package com.xptschool.teacher.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.imsdroid.NativeService;
import com.xptschool.teacher.push.MyPushIntentService;
import com.xptschool.teacher.push.UpushTokenHelper;
import com.xptschool.teacher.server.ServerManager;

import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;

/**
 * Created by dexing on 2017/6/5.
 * No1
 */

public class BaseMainActivity extends BaseActivity {

    //login video chat server
    private INgnSipService mSipService;
    private INgnConfigurationService mConfigurationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //依据手机类型，注册不同推送平台
        String model = android.os.Build.MODEL;
        String carrier = android.os.Build.MANUFACTURER;
        Log.i(TAG, "onCreate: " + model + "  " + carrier);
        if (carrier.toUpperCase().equals("XIAOMI")) {

            MiPushClient.registerPush(this, XPTApplication.APP_MIID, XPTApplication.APP_KEY);
            LoggerInterface newLogger = new LoggerInterface() {

                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d(TAG, content, t);
                }

                @Override
                public void log(String content) {
                    Log.d(TAG, content);
                }
            };
            Logger.setLogger(this, newLogger);
            //推送可用
            MiPushClient.enablePush(this);
        } else {
            //友盟
            final PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.setDebugMode(false);
            //使用自定义消息
            mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

            Log.i(TAG, "startServer: register ");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "startServer: register start");
                    //注册推送服务，每次调用register方法都会回调该接口
                    mPushAgent.register(new IUmengRegisterCallback() {

                        @Override
                        public void onSuccess(String deviceToken) {
                            //注册成功会返回device token
                            Log.i(TAG, "onSuccess: deviceToken " + deviceToken);
                            UpushTokenHelper.uploadDevicesToken(deviceToken, "UPush");
                        }

                        @Override
                        public void onFailure(String s, String s1) {
                            Log.i(TAG, "onFailure: " + s + "---" + s1);
                        }
                    });
                }
            }).start();

            //接收通知
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "PushAgent enable onSuccess: ");
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.i(TAG, "PushAgent enable onFailure: " + s + " s1 " + s1);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerManager.getInstance().startServer(this);
    }
}
