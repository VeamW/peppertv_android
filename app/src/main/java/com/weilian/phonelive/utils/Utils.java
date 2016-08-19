package com.weilian.phonelive.utils;

/**
 * Created by administrato on 2016/8/19.
 */
public class Utils {


    private static long lastClickTime;
    /**
     * 防止在短时间内重复点击
     */
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
