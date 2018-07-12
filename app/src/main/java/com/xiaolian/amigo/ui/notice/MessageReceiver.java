package com.xiaolian.amigo.ui.notice;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.xiaolian.amigo.BuildConfig;
import com.xiaolian.amigo.data.network.model.notify.CustomDTO;
import com.xiaolian.amigo.ui.main.MainActivity;
import com.xiaolian.amigo.ui.repair.RepairDetailActivity;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zcd
 * @date 18/6/2
 */
public class MessageReceiver extends XGPushBaseReceiver {
    private static final String TAG = MessageReceiver.class.getSimpleName();
    private Gson mGson = new Gson();
//    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
        } else {
            text = message + "注册失败错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        String text = "收到消息:" + message.toString();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
//        if (customContent != null && customContent.length() != 0) {
//            try {
//                JSONObject obj = new JSONObject(customContent);
//                // key1为前台配置的key
//                if (!obj.isNull("key")) {
//                    String value = obj.getString("key");
//                    Log.d(TAG, "get custom value:" + value);
//                }
//                // ...
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        CustomDTO customDTO = mGson.fromJson(customContent, CustomDTO.class);
        Log.d("LC", "++++++++++++++++透传消息");
        // APP自主处理消息的过程...
        Log.d(TAG, text);
        switch (customDTO.getAction()) {
            case 1:
                if (BuildConfig.APPLICATION_ID.equalsIgnoreCase(CommonUtil.getRuningApp(context.getApplicationContext()))) {
                    CommonUtil.sendNotify(context.getApplicationContext(), (int) (Math.random() * 10000), message.getTitle(),
                            message.getTitle(), message.getContent(), null, null);
                } else {
                    CommonUtil.sendNotify(context.getApplicationContext(), (int) (Math.random() * 10000), message.getTitle(),
                            message.getTitle(), message.getContent(), MainActivity.class, null);
                }
                break;
            case 2:
                Bundle bundle = new Bundle();
                bundle.putLong(Constant.BUNDLE_ID, customDTO.getTargetId());
                CommonUtil.sendNotify(context.getApplicationContext(), (int) (Math.random() * 10000), message.getTitle(),
                        message.getTitle(), message.getContent(), RepairDetailActivity.class, bundle);
                break;
        }
    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击。此处不能做点击消息跳转，详细方法请参照官网的Android常见问题文档
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {

        Log.e("LC", "+++++++++++++++ 通知被点击 跳转到指定页面。");
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
//        Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
//                Toast.LENGTH_SHORT).show();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        Log.d(TAG, "custom: " + message.getCustomContent());
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(TAG, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(TAG, text);

    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
//        context.sendBroadcast(intent);
//        show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
        Log.d("LC", "+++++++++++++++++++++++++++++展示通知的回调");
    }
}