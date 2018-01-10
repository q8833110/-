/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.xiaolian.amigo.ui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.prefs.ISharedPreferencesHelp;
import com.xiaolian.amigo.ui.base.intf.IBaseView;
import com.xiaolian.amigo.ui.base.swipeback.SwipeBackActivity;
import com.xiaolian.amigo.ui.login.LoginActivity;
import com.xiaolian.amigo.ui.main.HomeFragment2;
import com.xiaolian.amigo.ui.main.MainActivity;
import com.xiaolian.amigo.ui.widget.dialog.ActionSheetDialog;
import com.xiaolian.amigo.ui.widget.dialog.LoadingDialog;
import com.xiaolian.amigo.util.Log;
import com.xiaolian.amigo.util.NetworkUtil;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public abstract class BaseActivity extends SwipeBackActivity
        implements IBaseView {

    private final String TAG = this.getClass().getSimpleName();

    private static final int REQUEST_CODE_CAMERA = 0x1103;
    private static final int REQUEST_CODE_PICK = 0x1104;
    private static final int REQUEST_BLE = 0x1106;
    protected static final int REQUEST_LOCATION = 0x1107;

    private Uri mPhotoImageUri;
    private Uri mPickImageUri;
    private Uri mCropImageUri;

    protected RxPermissions rxPermissions;

    private LoadingDialog mProgressDialog;


    private Unbinder mUnBinder;

    ActionSheetDialog actionSheetDialog;
    // 申请蓝牙访问权限后的回调
    private Callback blePermissonCallback;

    @Inject
    ISharedPreferencesHelp sharedPreferencesHelp;
    private Toast toast;
    private CountDownTimer toastCountDown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rxPermissions = RxPermissions.getInstance(this);
    }


    private void selectPhoto() {
        mPickImageUri = getImageUri("pick");
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK);

    }

    private void takePhoto() {
        mPhotoImageUri = getImageUri("photo");
        //调用相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoImageUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }


    private Uri getImageUri(String fileName) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xiaolian/";
        File path = new File(filePath);
        if (!path.exists()) {
            boolean isPathSuccess = path.mkdirs();
            if (!isPathSuccess) {
                onError("没有SD卡权限");
                return null;
            }
        }
        Uri imageUri;
        File outputImage = new File(path, fileName + ".jpg");
        try {
            if (outputImage.exists()) {
                boolean isDeleteSuccess = outputImage.delete();
                if (!isDeleteSuccess) {
                    onError(R.string.no_sd_card_premission);
                    return null;
                }
            }
            if (!outputImage.createNewFile()) {
                onError(R.string.no_sd_card_premission);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        return imageUri;
    }

    private Uri getCropUri(String fileName) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xiaolian/";
        File path = new File(filePath);
        if (!path.exists() && !path.mkdirs()) {
            onError(R.string.no_sd_card_premission);
            return null;
        }
        File outputImage = new File(path, fileName + ".jpg");
        try {
            if (outputImage.exists() && !outputImage.delete()) {
                onError(R.string.no_sd_card_premission);
                return null;
            }
            if (!outputImage.createNewFile()) {
                onError(R.string.no_sd_card_premission);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return Uri.fromFile(outputImage);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                mCropImageUri = getCropUri("crop");
                UCrop.Options options = new UCrop.Options();
                int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
                options.setToolbarColor(colorPrimary);
                options.setActiveWidgetColor(colorPrimary);
                options.setStatusBarColor(colorPrimary);
                UCrop.of(mPhotoImageUri, mCropImageUri)
                        .withAspectRatio(1, 1)
//                        .withMaxResultSize(250 * 2, 170 * 2)
                        .withOptions(options)
                        .start(this);

            } else if (requestCode == UCrop.REQUEST_CROP) {
//                mImage.setImageDrawable(null);
//                mImage.setImageURI(mCropImageUri);
//                uploadImage(mCropImageUri);
                if (imageCallback != null) {
                    imageCallback.callback(mCropImageUri);
                }
            } else if (requestCode == REQUEST_CODE_PICK) {
                if (data != null && data.getData() != null) {
                    mPickImageUri = data.getData();
                    mCropImageUri = getCropUri("crop");
                    UCrop.Options options = new UCrop.Options();
                    int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
                    options.setToolbarColor(colorPrimary);
                    options.setActiveWidgetColor(colorPrimary);
                    options.setStatusBarColor(colorPrimary);
                    UCrop.of(mPickImageUri, mCropImageUri)
                            .withAspectRatio(1, 1)
//                            .withMaxResultSize(250 * 2, 170 * 2)
                            .withOptions(options)
                            .start(this);
                }
            } else if (requestCode == REQUEST_BLE) {
                if (isLocationEnable()) {
                    if (null != blePermissonCallback) {
                        blePermissonCallback.execute();
                    }
                } else {
                    try {
                        if (this instanceof MainActivity) {
                            ((MainActivity) this).showOpenLocationDialog();
                        }
                    } catch (ClassCastException e) {
                        Log.wtf(TAG, e);
                    } catch (Exception e) {
                        Log.wtf(TAG, e);
                    }
                }
            } else if (requestCode == REQUEST_LOCATION) {
                if (isLocationEnable()) {
                    if (null != blePermissonCallback) {
                        blePermissonCallback.execute();
                    }
                } else {
                    Log.i(TAG, "用户定位没有打开，无法进入设备用水");
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            if (emptyImageCallback != null) {
                emptyImageCallback.callback();
            }
            if (requestCode == UCrop.REQUEST_CROP) {
                showMessage("剪裁失败");
            }
        } else {
            if (emptyImageCallback != null) {
                emptyImageCallback.callback();
            }
        }
    }

    private ImageCallback imageCallback;
    protected EmptyImageCallback emptyImageCallback;

    public void getImage(ImageCallback callback) {
        imageCallback = callback;

        if (actionSheetDialog == null) {
            actionSheetDialog = new ActionSheetDialog(this)
                    .builder()
                    .setTitle("选择")
                    .addSheetItem("相机", ActionSheetDialog.SheetItemColor.Orange,
                            i -> rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                    .subscribe(granted -> {
                                        if (granted) {
                                            takePhoto();
                                        } else {
                                            showMessage("没有相机权限");
                                        }
                                    }))
                    .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Orange,
                            i -> rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    .subscribe(granted -> {
                                        if (granted) {
                                            selectPhoto();
                                        } else {
                                            showMessage("没有SD卡权限");
                                        }
                                    }));
        }
        actionSheetDialog.setOnCancalListener(dialog -> {
            if (emptyImageCallback != null) {
                emptyImageCallback.callback();
            }
        });
        actionSheetDialog.show();
    }

    public interface ImageCallback {
        void callback(Uri imageUri);
    }

    public interface EmptyImageCallback {
        void callback();
    }


    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(this);
        }
        try {
            hideLoading();
            mProgressDialog.show();
        } catch (Exception e) {
            Log.wtf(TAG, "showLoading出错", e);
        }
    }

    @Override
    public void hideLoading() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        } catch (Exception e) {
            Log.wtf(TAG, "hideLoading出错", e);
        }
    }

    private void showSuccessToast(String message) {
        if (toast == null) {
            toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        if (toastCountDown == null) {
            toastCountDown = new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    toast.cancel();
                }
            };
        }
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.view_toast, null, false);
        TextView tv_content = layout.findViewById(R.id.tv_content);
        tv_content.setText(message);
        toast.setView(layout);
        toast.show();
        toastCountDown.start();
    }

    @SuppressWarnings("unused")
    private void showSuccessToast(int message) {
        showSuccessToast(getString(message));
    }

    private void showErrorToast(String message) {
        if (toast == null) {
            toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
//        if (toastCountDown == null) {
//            toastCountDown = new CountDownTimer(800, 100) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    toast.cancel();
//                }
//            };
//        }
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.view_toast, null, false);
        TextView tv_content = layout.findViewById(R.id.tv_content);
        tv_content.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFullRed));
        tv_content.setText(message);
        toast.setView(layout);
        toast.show();
//        toastCountDown.start();
    }

    @SuppressWarnings("unused")
    private void showErrorToast(int message) {
        showErrorToast(getString(message));
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            showErrorToast(message);
        } else {
            showErrorToast(getString(R.string.some_error));
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void onSuccess(@StringRes int resId) {
        onSuccess(getString(resId));
    }

    @Override
    public void onSuccess(String message) {
        if (message != null) {
            showSuccessToast(message);
        } else {
            showSuccessToast(getString(R.string.some_error));
        }
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    @Override
    public boolean isWifiConnected() {
        return NetworkUtil.isWifiConnected(getApplicationContext());
    }

    @Override
    public boolean isNetworkAvailable() {
        return NetworkUtil.isAvailable(getApplicationContext());
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void openActivityOnTokenExpire() {
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (actionSheetDialog != null && actionSheetDialog.getDialog() != null) {
            actionSheetDialog.getDialog().dismiss();
        }
        super.onDestroy();
    }

    protected abstract void setUp();

    /**
     * 启动activity完成跳转
     *
     * @param activity 当前activity
     * @param clazz    目标activity class
     */
    public void startActivity(AppCompatActivity activity, Class<?> clazz) {
        startActivity(activity, clazz, null);
    }

    /**
     * 启动activity完成跳转
     *
     * @param activity 当前activity
     * @param clazz    目标activity class
     * @param extraMap 捆绑参数
     */
    public void startActivity(AppCompatActivity activity, Class<?> clazz, Map<String, ? extends Serializable> extraMap) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        if (null != extraMap) {
            for (Map.Entry<String, ? extends Serializable> entry : extraMap.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        startActivity(intent);
    }

    // 单击回退按钮返回
    @OnClick(R.id.iv_back)
    @Optional
    void back() {
        hideLoading();
        super.onBackPressed();
//        finish();
    }

    @Override
    public void getBlePermission() {
        RxPermissions rxPermissions = RxPermissions.getInstance(this);
        rxPermissions.request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(bleIntent, REQUEST_BLE);
                        Log.i(TAG, "动态授权蓝牙操作成功！");
                    } else {
                        Log.e(TAG, "动态授权蓝牙操作失败！");
                        // enable首页按钮
                        EventBus.getDefault().post(new HomeFragment2.Event(HomeFragment2.Event.EventType.ENABLE_VIEW));
                    }
                });
    }

    @Override
    public boolean isBleOpen() {
        // 确保蓝牙适配器处于打开状态
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return null != adapter && adapter.isEnabled();
    }

    public void setBleCallback(Callback callback) {
        this.blePermissonCallback = callback;
    }


    @Override
    public void redirectToLogin() {
        startActivity(this, LoginActivity.class);
    }

    @Override
    public void post(Runnable task) {
        this.runOnUiThread(task);
    }

    private boolean isLocationEnable() {
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = false;
        boolean gpsProvider = false;
        if (locationManager != null) {
            networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return networkProvider || gpsProvider;
    }

    public interface Callback {
        void execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
