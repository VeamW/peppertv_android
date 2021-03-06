package com.weilian.phonelive.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.em.ChangInfo;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.AvatarView;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/3/9.
 */
public class MyInfoDetailActivity extends BaseActivity {

    private UserBean mUser;
    @InjectView(R.id.rl_userHead)
    RelativeLayout mRlUserHead;
    @InjectView(R.id.rl_userNick)
    RelativeLayout mRlUserNick;
    @InjectView(R.id.rl_userSign)
    RelativeLayout mRlUserSign;
    @InjectView(R.id.rl_userSex)
    RelativeLayout mRlUserSex;
    @InjectView(R.id.tv_userNick)
    TextView mUserNick;
    @InjectView(R.id.tv_userSign)
    TextView mUserSign;
    @InjectView(R.id.av_userHead)
    AvatarView mUserHead;
    @InjectView(R.id.iv_info_sex)
    ImageView mUserSex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {
        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        mUserHead.setOnClickListener(this);//增加头像事件监听
    }

    @Override
    public void initData() {
        setActionBarTitle(R.string.editInfo);
        sendRequiredData();
    }

    private void sendRequiredData() {
        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);
    }

    @Override
    public void onClick(View v) {
        if (mUser != null) {
            switch (v.getId()) {
                case R.id.rl_userNick:
                    UIHelper.showEditInfoActivity(
                            this, "修改昵称",
                            getString(R.string.editnickpromp),
                            mUser.getUser_nicename(),
                            ChangInfo.CHANG_NICK);
                    break;
                case R.id.rl_userSign:
                    UIHelper.showEditInfoActivity(
                            this, "修改签名",
                            getString(R.string.editsignpromp),
                            mUser.getSignature(),
                            ChangInfo.CHANG_SIGN);
                    break;
                case R.id.rl_userHead:
                    UIHelper.showSelectAvatar(this, mUser.getAvatar());
                    break;
                case R.id.rl_userSex:
                    UIHelper.showChangeSex(this, mUser.getSex());
                    break;
                case R.id.av_userHead:
                    UIHelper.showSelectAvatar(this, mUser.getAvatar()); //选择头像
                    break;
            }
        }

    }

    @Override
    protected void onRestart() {
        mUser = AppContext.getInstance().getLoginUser();
        fillUI();
        super.onRestart();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String s) {

            if (ApiUtils.checkIsSuccess(s, MyInfoDetailActivity.this) != null) {
                mUser = new Gson().fromJson(ApiUtils.checkIsSuccess(s, MyInfoDetailActivity.this), UserBean.class);
                fillUI();
            } else {

            }
        }
    };

    @Override
    protected void onStart() {
        if (mUser != null) {
            fillUI();
        }

        super.onStart();
    }

    private void fillUI() {
        if (null == mUserNick || null == mUserSign || null == mUserHead || null == mUserSex) return;
        mUserNick.setText(mUser.getUser_nicename());
        mUserSign.setText(mUser.getSignature());
        mUserHead.setAvatarUrl(mUser.getAvatar());
        mUserSex.setImageResource(mUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("个人中心"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人中心"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }


}
