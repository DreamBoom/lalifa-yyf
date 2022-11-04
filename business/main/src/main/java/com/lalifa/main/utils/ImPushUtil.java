package com.lalifa.main.utils;

import android.content.Context;

/**
 * Created by cxf on 2017/8/3.
 * 极光推送相关
 */

public class ImPushUtil {

    public static final String TAG = "极光推送";
    private static ImPushUtil sInstance;
    private boolean mClickNotification;
    private int mNotificationType;

    private ImPushUtil() {

    }

    public static ImPushUtil getInstance() {
        if (sInstance == null) {
            synchronized (ImPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new ImPushUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
      //  JPushInterface.setDebugMode(true);
      //  JPushInterface.init(context);
      //  L.e(TAG, "regID------>" + JPushInterface.getRegistrationID(context));
    }


    public void logout() {
        stopPush();
    }

    public void resumePush() {
    //    if (JPushInterface.isPushStopped(CommonAppContext.getInstance())) {
   //         JPushInterface.resumePush(CommonAppContext.getInstance());
   //     }
    }

    public void stopPush() {
        //JPushInterface.stopPush(CommonAppContext.getInstance());
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
       //return JPushInterface.getRegistrationID(CommonAppContext.getInstance());
        return "";
    }
}
