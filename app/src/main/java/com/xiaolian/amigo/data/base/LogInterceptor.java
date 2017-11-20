package com.xiaolian.amigo.data.base;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.xiaolian.amigo.util.Log;

import com.xiaolian.amigo.data.prefs.ISharedPreferencesHelp;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nullable;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Log拦截器
 *
 * @author zcd
 */

public class LogInterceptor implements Interceptor {
    private final static String TAG = LogInterceptor.class.getSimpleName();
    private final static boolean DEBUG = true;

    private long lastTime = 0;
    private Request lastRequest;
    private final static long NETWORK_INTERVAL = 500;

    private ISharedPreferencesHelp sharedPreferencesHelp;

    public LogInterceptor(ISharedPreferencesHelp sharedPreferencesHelp) {
        this.sharedPreferencesHelp = sharedPreferencesHelp;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;
        String token = sharedPreferencesHelp.getToken();
        if (token == null) {
            token = "";
        }

        if (request.url().url().getPath().startsWith("/trade")) {
            String deviceToken = sharedPreferencesHelp.getCurrentDeviceToken();
            if (deviceToken == null) {
                deviceToken = "";
            }

            newRequest = request.newBuilder()
                    // 添加token
                    .addHeader("token", token)
                    .addHeader("deviceToken", deviceToken)
                    .build();

        } else {
            newRequest = request.newBuilder()
                    // 添加token
                    .addHeader("token", token)
                    .build();

        }

        if (lastTime == 0 || lastRequest == null) {
            lastTime = Calendar.getInstance().getTimeInMillis();
            lastRequest = newRequest;
        } else {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastTime < NETWORK_INTERVAL) {
                if (isRequestEqual(newRequest, lastRequest)) {
                    Log.w(TAG, "请求间隔过短 url:" + request.url());
                    return new Response.Builder()
                            .code(600) //Simply put whatever value you want to designate to aborted request.
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .message("ignore message")
                            .body(ResponseBody.create(null, new byte[0]))
                            .build();
                }
            }
            lastRequest = newRequest;
            lastTime = Calendar.getInstance().getTimeInMillis();
        }
        String url = newRequest.url().toString();
        String header = newRequest.headers().toString();
        okhttp3.Response response;
        try {
            response = chain.proceed(newRequest);
        } catch (Exception e) {
            Log.wtf(TAG, "网络请求错误: " + newRequest.url(), e);
            return null;
        }
        if (null != response.header("deviceToken")) {
            // 有device_token,一定配对一个macAddress
            String macAddress = response.header("macAddress");
            sharedPreferencesHelp.setDeviceToken(macAddress, response.header("deviceToken"));
        }
        String content;
        okhttp3.MediaType mediaType;
        if (response.body() != null) {
            content = response.body().string();
            mediaType = response.body().contentType();
        } else {
            content = "";
            mediaType = MediaType.parse("application/json;charset=UTF-8");
        }
        int code = response.code();
        if (DEBUG) {
            if (!newRequest.method().equals("GET")) {
                String requestContent = bodyToString(newRequest.body());
                Log.d(TAG, "url: " + url + "\n" + "code: " + code + "\n" + "request header: " + header + "\n" + "request body: " + requestContent + "\n" + "response header: "
                        + response.headers().toString() + "\n" + "response body: " + content);
            } else {
                Log.d(TAG, "url: " + url + "\n" + "code: " + code + "\n" + "request header: " + header + "\n" + "response body: " + content);
            }
        }

        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

    private static class EmptyBody extends ResponseBody {

        @Nullable
        @Override
        public MediaType contentType() {
            return MediaType.parse("application/json;charset=UTF-8");
        }

        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public BufferedSource source() {
            return null;
        }
    }

    private boolean isRequestEqual(Request request, Request lastRequest) {
        if (!TextUtils.equals(request.url().toString(), lastRequest.url().toString())) {
            return false;
        }
        if (request.headers().size() != lastRequest.headers().size()) {
            return false;
        }
        if (!TextUtils.equals(request.headers().get("token"), lastRequest.headers().get("token"))) {
            return false;
        }
        if (!TextUtils.equals(request.headers().get("deviceToken"), lastRequest.headers().get("deviceToken"))) {
            return false;
        }
        if (!TextUtils.equals(request.method(), lastRequest.method())) {
            return false;
        }
        if (request.method().equals("POST")) {
            if (!TextUtils.equals(bodyToString(request.body()), bodyToString(lastRequest.body()))) {
                return false;
            }
        }
        return true;
    }

    private String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
