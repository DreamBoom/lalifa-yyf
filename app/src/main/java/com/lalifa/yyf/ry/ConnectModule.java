package com.lalifa.yyf.ry;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.drake.logcat.LogCat;
import com.lalifa.ext.Config;
import com.lalifa.ext.User;
import com.lalifa.ext.UserManager;
import com.lalifa.utils.GsonUtil;
import com.lalifa.utils.KToast;
import com.lalifa.utils.UIKit;
import com.lalifa.yyf.ry.shumei.RCDeviceMessage;
import com.lalifa.yyf.ry.shumei.RCSMMessage;

import java.util.Arrays;

import io.rong.imkit.IMCenter;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReceivedProfile;

/**
 * im连接模块，默认注册
 */
public class ConnectModule implements IModule {
    private final static String TAG = "ConnectModule";
    private OnRegisterMessageTypeListener listener;

    protected ConnectModule(OnRegisterMessageTypeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onInit() {
        // 连接监听
        IMCenter.getInstance().addConnectionStatusListener(status -> {
            LogCat.d(TAG, "onInit: ConnectionStatusListener");
            if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                LogCat.d(TAG, "onInit: KICKED_OFFLINE_BY_OTHER_CLIENT");
                KToast.show("当前账号已在其他设备登录，请重新登录");
                //222 UserManager.logout();
            }
        });

        // 监听通用业务消息
        // 1. 数美审核消息
        // 2. 设备登录消息
        RongCoreClient.addOnReceiveMessageListener(new OnReceiveMessageWrapperListener() {
            @Override
            public void onReceivedMessage(Message message, ReceivedProfile profile) {
                if (null != message) {
                    try {
                        MessageContent content = message.getContent();
                        LogCat.e(TAG, "objectName:" + message.getObjectName()
                                + "  messageId:" + message.getMessageId()
                                + "  uid:" + message.getUId()
                                + "  content:" + GsonUtil.obj2Json(content));
                        //如果status级别：1: 弹窗提示  2: 则为用户跳到登录页面
                        if (content instanceof RCSMMessage) {
                            RCSMMessage rcs = (RCSMMessage) content;
                            if (2 == rcs.getStatus()) {
                                UserManager.logout();
                            }
                            KToast.show(rcs.getMessage());
                        } else if (content instanceof RCDeviceMessage) {
                            RCDeviceMessage rds = (RCDeviceMessage) content;
                            // mobile web 和 desktop
                            // 注意 android 和 ios 可以通过连接状态监听实现互踢，此处主要针对和web互踢功能
                            String platform = rds.getPlatform();
                            if (!TextUtils.equals(platform, "mobile")) {
                                UserManager.logout();
                                KToast.show(platform + " 端已登录");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 添加自定义消息
        if (null != listener) listener.onRegisterMessageType();

        User user = UserManager.get();
        String imToken = null == user ? "" : user.getImToken();
        if (TextUtils.isEmpty(imToken)) {
            return;
        }
    }

    @Override
    public void onUnInit() {
    }

    @Override
    public void onRegisterMessageType() {
        RongIMClient.registerMessageType(Arrays.asList(RCDeviceMessage.class, RCSMMessage.class));
    }
}
