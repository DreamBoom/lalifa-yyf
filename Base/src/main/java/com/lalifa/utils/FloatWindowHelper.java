package com.lalifa.utils;

/**
 * 悬浮窗辅助类
 */
public class FloatWindowHelper {

    private static ActionListener sActionListener;

    public static void setActionListener(ActionListener actionListener) {
        sActionListener = actionListener;
    }

    public static boolean checkVoice(boolean enterLive) {
        if (sActionListener != null) {
            return sActionListener.checkVoice(enterLive);
        }
        return true;
    }

    public static void setFloatWindowVisible(boolean visible) {
        if (sActionListener != null) {
            sActionListener.setFloatWindowVisible(visible);
        }
    }

    public interface ActionListener {
        /**
         * 是否可以播放声音
         */
        boolean checkVoice(boolean enterLive);

        /**
         * 设置隐藏和显示
         */
        void setFloatWindowVisible(boolean visible);
    }
}
