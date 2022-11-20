package cn.rongcloud.config;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.init.ModuleManager;
import cn.rongcloud.config.ryutiles.MyGiftConfig;
import io.rong.imkit.conversation.extension.RongExtensionManager;

public class AppConfig {
    private static class Holder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public final static String MODE_VOICE = "voice";
    public final static String MODE_RADIO = "radio";
    public final static String MODE_CALL = "call";
    public final static String MODE_LIVE = "live";
    public final static String MODE_GAME = "game";

    public static AppConfig get() {
        return Holder.INSTANCE;
    }

    public void init(String appKey,
                     String umengAppKey,
                     String baseServerAddress,
                     String businessToken,
                     boolean international,
                     String[] busiModes
    ) {
        this.appKey = appKey;
        this.umengAppKey = umengAppKey;
        this.baseServerAddress = baseServerAddress;
        this.businessToken = businessToken;
        this.international = international;
        if (null == modes) modes = new ArrayList();
        modes.clear();
        if (null != busiModes) {
            for (String mode : busiModes) {
                modes.add(mode);
            }
        }
        ModuleManager.manager().onInit();
        RongExtensionManager.getInstance().setExtensionConfig(new MyGiftConfig());
    }


    private String appKey = "";
    private String umengAppKey = "";
    private String baseServerAddress = "";
    private String businessToken = "";
    private boolean international = false;
    private List<String> modes;

    public String getAppKey() {
        return appKey;
    }

    public String getUmengAppKey() {
        return umengAppKey;
    }

    public String getBaseServerAddress() {
        return baseServerAddress;
    }

    public String getBusinessToken() {
        return businessToken;
    }

    public boolean isInternational() {
        return international;
    }

    /**
     * 判断mode是否配置
     *
     * @param modeName 功能模块
     * @return 是否配置
     */
    public boolean hasMode(String modeName) {
        return modes.contains(modeName);
    }

    public List<String> getModes() {
        return new ArrayList<>(modes);
    }

}
