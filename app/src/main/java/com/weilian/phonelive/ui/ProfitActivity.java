package com.weilian.phonelive.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.ProfitBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

/**
 * 收益
 */
public class ProfitActivity extends BaseActivity implements PlatformActionListener {
    private TextView mTvTitle;
    @InjectView(R.id.iv_back)
    LinearLayout mIvBack;
    @InjectView(R.id.tv_text)
    TextView mSaveInfo;
    @InjectView(R.id.tv_votes)
    TextView mVotes;
    @InjectView(R.id.txt_profit_bind_wechat)
    TextView mTxtIsBind;
    //@InjectView(R.id.tv_profit_canwithdraw)
    //TextView mCanwithDraw;
    @InjectView(R.id.tv_profit_withdraw)
    TextView mWithDraw;
    /*@InjectView(R.id.TextView)
    TextView text;*/
    private ProfitBean mProfitBean;
    private UserBean mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
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
        checkIfBind();
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, ProfitActivity.this);
                if (null != res) {
                    mProfitBean = new Gson().fromJson(res, ProfitBean.class);
                    fillUI();
                }
            }
        };
        PhoneLiveApi.getWithdraw(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);

    }


    /**
     * check
     */
    private void checkIfBind() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, ProfitActivity.this);
                if (null == mTxtIsBind) return;
                if (null != res) {
                    if (res.equals("0")) {  // not bound yet\

                        mTxtIsBind.setText("绑定微信");
                    } else {  //already bound
                        mTxtIsBind.setText("提现");
                    }
                }
            }
        };

        PhoneLiveApi.checkIfBind(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);
    }

    private void fillUI() {
        //mCanwithDraw.setText(mProfitBean.getCanwithdraw());
        if (null==mWithDraw||null==mVotes||null==mProfitBean) return;
        mWithDraw.setText(mProfitBean.getWithdraw());
        mVotes.setText(mProfitBean.getVotes());
    }


    @OnClick({R.id.txt_profit_bind_wechat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.ll_profit_cash:
                PhoneLiveApi.requestCash(mUser.getId(), mUser.getToken(),
                        new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                String res = ApiUtils.checkIsSuccess(response, ProfitActivity.this);
                                if (null != res) {
                                    AppContext.showToastAppMsg(ProfitActivity.this, res);
                                    initData();
                                }
                            }
                        });
                break;

 case R.id.TextView:  //添加  zxy 2016-04-19
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/appcmf/index.php?g=portal&m=page&a=newslist", "");
                break;*/

            case R.id.txt_profit_bind_wechat:
                if (mTxtIsBind.getText().equals("绑定微信")) {

                    bindWeChat();
                } else if (mTxtIsBind.getText().equals("提现")) {
                    AppContext.showToastAppMsg(ProfitActivity.this, "请关注微信公众号 '辣椒直播' 领取红包");
                } else {
                    AppContext.showToastAppMsg(ProfitActivity.this, "请稍等...");
                }
                break;
        }

    }


    /**
     * bind wechat
     */
    private void bindWeChat() {
        ShareSDK.initSDK(this);
        Platform other = ShareSDK.getPlatform(Wechat.NAME);
        other.showUser(null);//执行登录，登录后在回调里面获取用户资料
        other.SSOSetting(false);  //设置false表示使用SSO授权方式
        other.setPlatformActionListener(this);
        other.authorize();
        other.removeAccount(true);
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

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppContext.showToastAppMsg(ProfitActivity.this, "正在绑定");
            }
        });

        PhoneLiveApi.bindWeChat(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), hashMap.get("unionid").toString(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                TLog.error("绑定微信失败");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, ProfitActivity.this);
                if (res.equals("2")) {
                    AppContext.showToastAppMsg(ProfitActivity.this, "绑定成功");
                    mTxtIsBind.setText("提现");
                }
            }
        });
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        AppContext.showToastAppMsg(ProfitActivity.this, "授权失败");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        AppContext.showToastAppMsg(ProfitActivity.this, "取消绑定");
    }
}
