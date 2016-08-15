package com.weilian.phonelive.ui;

import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import org.kymjs.kjframe.Core;

import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.setting));
    }

    @OnClick({R.id.ll_clearCache,R.id.ll_push_manage,R.id.ll_about,R.id.ll_feedback,R.id.ll_blank_list})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_clearCache:
                clearCache();
                break;
            case R.id.ll_push_manage:
                UIHelper.showPushManage(this);
                break;
            case R.id.ll_about:
                UIHelper.showWebView(this,AppConfig.MAIN_URL + "/appcmf/index.php?g=portal&m=page&a=lists","服务条款");
                break;
            case R.id.ll_feedback:
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/appcmf/index.php?g=portal&m=page&a=newslist&uid=" + AppContext.getInstance().getLoginUid(),"");
                break;
            case R.id.ll_blank_list:
                UIHelper.showBlackList(SettingActivity.this);
                break;

        }
    }

    private void clearCache() {
        AppContext.showToastAppMsg(this,"缓存清理成功");
        Core.getKJBitmap().cleanCache();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("设置"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("设置"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
