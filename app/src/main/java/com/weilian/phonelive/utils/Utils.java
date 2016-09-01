package com.weilian.phonelive.utils;

import com.weilian.phonelive.AppContext;

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
        if (time - lastClickTime < 600) {
            AppContext.showToast("正在操作,请耐心等待...");
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 状态，检测头像是否更换！如果更换，则重新虚化背景
     */
    public static boolean IS_IMG_CHANGED = false;
    public static boolean IS_IMG_FIRST_LOAD = true;


}
