package com.weilian.phonelive.ui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.em.MainTab;
import com.weilian.phonelive.interf.BaseViewInterface;
import com.weilian.phonelive.utils.LoginUtils;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.UpdateManager;
import com.weilian.phonelive.viewpagerfragment.IndexPagerFragment;
import com.weilian.phonelive.widget.MyFragmentTabHost;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

public class MainActivity extends ActionBarActivity implements TabHost.OnTabChangeListener, BaseViewInterface,
        View.OnTouchListener {
    @InjectView(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initView();
        initData();
//        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public void initView() {
        //ShareUtils.shareFacebook(this, false, "www", "http://www.mob.com/mob/images/index/popwinimg.png",null);
        if (null == mTabHost) return;
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        getSupportActionBar().hide();
        initTabs();

        mTabHost.setCurrentTab(100);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setNoTabChangedTag("1");

    }

    public void setTab(int index) {
        if (mTabHost != null) {
            mTabHost.setCurrentTab(0);
        }
    }

    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        String[] titles = getResources().getStringArray(R.array.tab_titles);
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(String.valueOf(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            ImageView tabImg = (ImageView) indicator.findViewById(R.id.tab_img);
            TextView tabTitle = (TextView) indicator.findViewById(R.id.txt_tab_title);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            tabImg.setImageDrawable(drawable);
            tabTitle.setText(titles[i]);
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    @Override
    public void initData() {
        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //检查是否有最新版本
        checkNewVersion();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestStartLive();
                } else {
                    AppContext.showToast("您已拒绝使用摄像头权限,将无法正常直播,请去设置中修改");
                }
                return;
            }
        }
    }

    //请求服务端开始直播
    private void requestStartLive() {
        PhoneLiveApi.getLevelLimit(AppContext.getInstance().getLoginUid(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(MainActivity.this, "开始直播失败");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res) {
                    UIHelper.showStartLiveActivity(MainActivity.this);
                }
            }
        });
    }

    //登录环信即时聊天
    private void loginIM() {
        EMClient.getInstance().login(
                String.valueOf("pt" + AppContext.getInstance().getLoginUid()),
                "wl" + AppContext.getInstance().getLoginUid(), new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                TLog.log("主页登录聊天服务器成功" + "pt" + AppContext.getInstance().getLoginUid());
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        if (204 == code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.showToastAppMsg(MainActivity.this, "聊天服务器登录和失败,请重新登录");
                                }
                            });

                        }
                        TLog.log("主页登录聊天服务器失败" + "code:" + code + "    MESSAGE:" + message);
                    }
                });

    }

    /**
     * @dw 注册极光推送
     */
    private void registerJpush() {
        JPushInterface.setAlias(this, AppContext.getInstance().getLoginUid() + "PUSH",
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        TLog.log(i + "I" + s + "S");
                    }
                });
    }

    /**
     * @dw 检查token是否过期
     */
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, MainActivity.this);
                if (null == res) return;
                if (res.equals(ApiUtils.TOKEN_TIMEOUT)) {
                    AppContext.showToastAppMsg(MainActivity.this, "登陆过期,请重新登陆");
                    UIHelper.showLoginSelectActivity(MainActivity.this);
                    finish();
                    AppManager.getAppManager().finishAllActivity();
                    return;
                }

            }
        });
    }

    /**
     * @dw 检查是否有最新版本
     */
    private void checkNewVersion() {
        UpdateManager manager = new UpdateManager(this, false);
        manager.checkUpdate();

    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // 提示新消息
            for (EMMessage message : messages) {
                //DemoHelper.getInstance().getNotifier().onNewMsg(message);
            }
            //refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
        MobclickAgent.onPageStart("主页"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        if (mTabHost != null) {
            if (mTabHost.getCurrentTabTag().equals("1")) {
                mTabHost.setCurrentTab(0);
            }

        }

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("主页"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    //开始直播初始化
    public void startLive() {
        if (Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        5);
                return;
            }
        }
        requestStartLive();

    }

    @Override
    public void onTabChanged(String tabId) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

}
