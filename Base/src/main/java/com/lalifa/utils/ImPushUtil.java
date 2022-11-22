package com.lalifa.utils;

import android.content.Context;


import com.lalifa.base.BaseApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cxf on 2017/8/3.
 * 极光推送相关
 */

public class ImPushUtil {

    public static final String TAG = "极光推送";
    private static ImPushUtil sInstance;
    private boolean mClickNotification;
    private int mNotificationType;
    private Context context;
    private ImPushUtil(Context context) {
        this.context = context;
    }

    public static ImPushUtil getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ImPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new ImPushUtil(context);
                }
            }
        }
        return sInstance;
    }

    public void init() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
    }


    public void logout() {
        stopPush();
    }

    public void resumePush() {
        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
    }

    public void stopPush() {
        JPushInterface.stopPush(context);
    }

    public boolean isClickNotification() {
        return mClickNotification;
    }

    public void setClickNotification(boolean clickNotification) {
        mClickNotification = clickNotification;
    }

    public int getNotificationType() {
        return mNotificationType;
    }

    public void setNotificationType(int notificationType) {
        mNotificationType = notificationType;
    }

    /**
     * 获取极光推送 RegistrationID
     */
    public String getPushID() {
       return JPushInterface.getRegistrationID(context);
    }
}
