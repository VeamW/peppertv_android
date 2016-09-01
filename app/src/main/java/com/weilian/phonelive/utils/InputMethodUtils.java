package com.weilian.phonelive.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

public class InputMethodUtils {

    /**
     * 关闭软键盘
     * @param activity
     */
    public static void closeSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        //如果软键盘已经开启
        if(inputMethodManager.isActive()&&activity.getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static InputMethodManager getInput(Activity activity){
        return (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }
    /**
    * 软键盘是否开启
    * @param activity
    * */
    public static boolean isShowSoft(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.isActive();
    }


    /**
     * 切换软键盘的状态
     * @param context
     */
    public static void toggleSoftKeyboardState(Context context){
        ((InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 解决InputMethodManager引发的内存泄漏的问题！
     *
     * @param context
     */
    public static void fixInputMethodManagerLeak(Context context) {
        if (context == null) {
            return;
        }
        try {
            // 对 mCurRootView mServedView mNextServedView 进行置空...
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }// author:sodino mail:sodino@qq.com


            Object obj_get = null;
            Field f_mCurRootView = imm.getClass().getDeclaredField("mCurRootView");
            Field f_mServedView = imm.getClass().getDeclaredField("mServedView");
            Field f_mNextServedView = imm.getClass().getDeclaredField("mNextServedView");
//            Field f_mLastSrvView = imm.getClass().getDeclaredField("mLastSrvView");

            if (f_mCurRootView.isAccessible() == false) {
                f_mCurRootView.setAccessible(true);
            }
            obj_get = f_mCurRootView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mCurRootView.set(imm, null);
            }

            if (f_mServedView.isAccessible() == false) {
                f_mServedView.setAccessible(true);
            }
            obj_get = f_mServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mServedView.set(imm, null);
            }

            if (f_mNextServedView.isAccessible() == false) {
                f_mNextServedView.setAccessible(true);
            }
            obj_get = f_mNextServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mNextServedView.set(imm, null);
            }

/*
            if (f_mLastSrvView.isAccessible() == false) {
                f_mLastSrvView.setAccessible(true);
            }
            obj_get = f_mLastSrvView.get(imm);
            if (obj_get != null) {
                f_mLastSrvView.set(imm, null);
            }*/
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
