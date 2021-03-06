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
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
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
import com.xiaolian.amigo.ui.wallet.WithdrawalActivity;
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

import static com.xiaolian.amigo.util.Constant.ANOTHER_DEVICE_LOGIN;
import static com.xiaolian.amigo.util.Constant.SHOW_VERSION_UPDATE;

public abstract class BaseActivity extends SwipeBackActivity
        implements IBaseView {

    protected final String TAG = this.getClass().getSimpleName();

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
    public Callback blePermissonCallback;


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
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoImageUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    File outputImage ;
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
         outputImage = new File(path, fileName +System.currentTimeMillis()+ ".jpg");
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

    private File getCropFile(String fileName) {
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
        return outputImage;
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }

        return imagePath ;
    }


    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (imageCallback != null) {
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
                }


                if (imageCallback2 != null) {
                    imageCallback2.callback(outputImage.getAbsolutePath());
                }
//                    File cropFile = getCropFile("crop");

//                    if (FileUtils.copyFile(outputImage.getAbsoluteFile(), cropFile, new FileUtils.OnReplaceListener() {
//                        @Override
//                        public boolean onReplace() {
//                            return true;
//                        }
//                    })){
//                        imageCallback2.callback(cropFile.getPath());
//                    }else{
//                        onError("上传失败");
//                    }




            } else if (requestCode == UCrop.REQUEST_CROP) {
//                mImage.setImageDrawable(null);
//                mImage.setImageURI(mCropImageUri);
//                uploadImage(mCropImageUri);
                if (imageCallback != null) {
                    imageCallback.callback(mCropImageUri);
                }
            } else if (requestCode == REQUEST_CODE_PICK) {
                if (data != null && data.getData() != null) {
                    if (imageCallback != null){
                        mPickImageUri  =data.getData();
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

//

                    if (imageCallback2 != null){
                        String pickImagePath = getRealPathFromUri(this ,data.getData());
                        imageCallback2.callback(pickImagePath);
                    }
//
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

    /**
     * 测试
     */


    /**
     * 根据图片的Uri获取图片的绝对路径。@uri 图片的uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        if(context == null || uri == null) {
            return null;
        }
        if("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context,uri);
        } else if("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context,uri);
        }
//        int sdkVersion = Build.VERSION.SDK_INT;
//        if (sdkVersion < 11) {
//            // SDK < Api11
//            return getRealPathFromUri_BelowApi11(context, uri);
//        }
////        if (sdkVersion < 19) {
////             SDK > 11 && SDK < 19
////            return getRealPathFromUri_Api11To18(context, uri);
//            return getRealPathFromUri_ByXiaomi(context, uri);
////        }
//        // SDK > 19
        return getRealPathFromUri_AboveApi19(context, uri);//没用到
    }

    //针对图片URI格式为Uri:: file:///storage/emulated/0/DCIM/Camera/IMG_20170613_132837.jpg
    private static String getRealPathFromUri_Byfile(Context context,Uri uri){
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;

        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * //适配api11-api18,根据uri获取图片的绝对路径。
     * 针对图片URI格式为Uri:: content://media/external/images/media/1028
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null && cursor.getCount() >= 1) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }


    /**
     * 测试
     */

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

    ImageCallback2 imageCallback2 ;
    public void getImage2(ImageCallback2 callback) {
        imageCallback2 = callback;

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


    public interface ImageCallback2{
        void callback(String imagePath);
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

    /**
     * 获取startActivity中传递的值
     */
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
    public void redirectToLogin(boolean showAnotherDeviceLogin) {

        Intent intent = new Intent(this , LoginActivity.class);
        intent.putExtra(ANOTHER_DEVICE_LOGIN ,showAnotherDeviceLogin);
        intent.putExtra(SHOW_VERSION_UPDATE , true);
        startActivity(intent);
        finish();
    }


    @Override
    public void redirectToLogin(boolean showAnotherDeviceLogin , boolean canShowVersionUpdate) {

        Intent intent = new Intent(this , LoginActivity.class);
        intent.putExtra(ANOTHER_DEVICE_LOGIN ,showAnotherDeviceLogin);
        intent.putExtra(SHOW_VERSION_UPDATE , canShowVersionUpdate);
        startActivity(intent);
        finish();
    }

    @Override
    public void post(Runnable task) {
        this.runOnUiThread(task);
    }

    public boolean isLocationEnable() {
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
