package com.weilian.phonelive.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.ProfitBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 收益
 */
public class ProfitActivity extends BaseActivity {
    private TextView mTvTitle;
    @InjectView(R.id.iv_back)
    ImageView mIvBack;
    @InjectView(R.id.tv_text)
    TextView mSaveInfo;
    @InjectView(R.id.tv_votes)
    TextView mVotes;
    //@InjectView(R.id.tv_profit_canwithdraw)
    //TextView mCanwithDraw;
    @InjectView(R.id.tv_profit_withdraw)
    TextView mWithDraw;
    @InjectView(R.id.TextView)
    TextView text;
    private ProfitBean mProfitBean;
    private UserBean mUser;




    @Override
    protected int getLayoutId() {
        return R.layout.activity_profit;
    }

    @Override
    public void initView() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        mTvTitle.setText(getString(R.string.myprofit));
        mSaveInfo.setVisibility(View.GONE);//待开发功能提现记录
        mSaveInfo.setText("提现记录");

        Bundle bundle = getIntent().getBundleExtra("USERINFO");
        mVotes.setText(bundle.getString("votes"));
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,ProfitActivity.this);

                if(null != res){
                    mProfitBean = new Gson().fromJson(res,ProfitBean.class);
                    fillUI();
                }
            }
        };
        PhoneLiveApi.getWithdraw(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);

    }

    private void fillUI() {
        //mCanwithDraw.setText(mProfitBean.getCanwithdraw());
        mWithDraw.setText(mProfitBean.getWithdraw());
        mVotes.setText(mProfitBean.getVotes());
    }



    @OnClick({R.id.ll_profit_cash,R.id.TextView})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_profit_cash:
                PhoneLiveApi.requestCash(mUser.getId(),mUser.getToken(),
                        new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                String res = ApiUtils.checkIsSuccess(response,ProfitActivity.this);
                                if(null != res){
                                    AppContext.showToastAppMsg(ProfitActivity.this,res);
                                    initData();
                                }
                            }
                        });
                break;

            case R.id.TextView:  //添加  zxy 2016-04-19
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/appcmf/index.php?g=portal&m=page&a=newslist", "");
                break;
        }


    }
    @Override
    protected void initActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_actionbar_title);
        mTvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_actionBarTitle);

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的收益"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的收益"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
