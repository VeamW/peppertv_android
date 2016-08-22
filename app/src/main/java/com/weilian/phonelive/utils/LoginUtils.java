package com.weilian.phonelive.utils;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.callback.StringCallback;


public class LoginUtils {
    private static LoginUtils loginUtils = null;

    public static LoginUtils getInstance(){
        if(null == loginUtils){
            loginUtils = new LoginUtils();
        }
        return loginUtils;
    }
    public void  OtherInit(Context context){
        new Thread(){
            @Override
            public void run() {
                int uid = AppContext.getInstance().getLoginUid();
                try {
                    EMClient.getInstance().createAccount("pt"+ String.valueOf(uid), "wl" + uid);//同步方法
                    TLog.log("注册成功");
                } catch (HyphenateException e) {
                    int errorCode=e.getErrorCode();
                    TLog.log("注册失败"+errorCode);
                    e.printStackTrace();
                }

            }
        }.start();
        SharedPreUtil.put(context,"isOpenPush",true);
}

    public static void outLogin(Context context) {
        EMClient.getInstance().logout(true);
        AppContext.getInstance().Logout();
        AppManager.getAppManager().finishAllActivity();
        UIHelper.showLoginSelectActivity(context);
    }

    public static void tokenIsOutTime(StringCallback callback){
        PhoneLiveApi.tokenIsOutTime(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),callback);
    }
}
