package com.lalifa.utils;


public class ClickUtil {

    private static long sLastClickTime;

    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - sLastClickTime < 500) {
            return false;
        }
        sLastClickTime = curTime;
        return true;
    }

}
