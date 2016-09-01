package com.weilian.phonelive.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.OrderBean;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserHomePageBean;
import com.weilian.phonelive.utils.FastBlur;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.weilian.phonelive.widget.AvatarView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 他人信息
 */
public class HomePageActivity extends BaseActivity {
    @InjectView(R.id.tv_home_page_send_num)
    TextView mSendNum;//送出钻
    @InjectView(R.id.tv_home_page_uname)
    TextView mUNice;//昵称
    @InjectView(R.id.iv_home_page_sex)
    ImageView mUSex;
    @InjectView(R.id.iv_home_page_level)
    ImageView mULevel;
    @InjectView(R.id.av_home_page_uhead)
    AvatarView mUHead;//头像
    @InjectView(R.id.tv_home_page_follow)
    TextView mUFollowNum;//关注数
    @InjectView(R.id.tv_home_page_fans)
    TextView mUFansNum;//粉丝数
    @InjectView(R.id.tv_home_page_sign)
    TextView mUSign;//个性签名
    @InjectView(R.id.tv_home_page_sign2)
    TextView mUSign2;
    @InjectView(R.id.tv_home_page_num)
    TextView mUNum;

    @InjectView(R.id.ll_home_page_index)
    LinearLayout mHomeIndexPage;

    @InjectView(R.id.ll_home_page_video)
    LinearLayout mHomeVideoPage;

    @InjectView(R.id.tv_home_page_index_btn)
    TextView mPageIndexBtn;

    @InjectView(R.id.tv_home_page_video_btn)
    TextView mPageVideoBtn;

    @InjectView(R.id.tv_home_page_menu_follow)
    TextView mFollowState;

    @InjectView(R.id.tv_home_page_black_state)
    TextView mTvBlackState;

    @InjectView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;
    @InjectView(R.id.top_back)
    LinearLayout mTopBack;
    @InjectView(R.id.iv_home_page_back)
    ImageView mIvHomePageBack;
    @InjectView(R.id.home_page_top_back)
    LinearLayout mHomePageTopBack;
    @InjectView(R.id.av_home_page_order1)
    AvatarView mAvHomePageOrder1;
    @InjectView(R.id.av_home_page_order2)
    AvatarView mAvHomePageOrder2;
    @InjectView(R.id.av_home_page_order3)
    AvatarView mAvHomePageOrder3;
    @InjectView(R.id.rl_home_pager_yi_order)
    RelativeLayout mRlHomePagerYiOrder;
    @InjectView(R.id.ll_home_page_menu_follow)
    LinearLayout mLlHomePageMenuFollow;
    @InjectView(R.id.ll_home_page_menu_privatechat)
    LinearLayout mLlHomePageMenuPrivatechat;
    @InjectView(R.id.ll_home_page_menu_lahei)
    LinearLayout mLlHomePageMenuLahei;
    private int uid;
    AvatarView[] mOrderTopNoThree = new AvatarView[3];
    private UserHomePageBean mUserHomePageBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        mOrderTopNoThree[0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree[1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree[2] = (AvatarView) findViewById(R.id.av_home_page_order3);
    }

    @Override
    public void initData() {
        uid = getIntent().getIntExtra("uid", 0);
        if (uid == AppContext.getInstance().getLoginUid()) {
            mLLBottomMenu.setVisibility(View.GONE);
        }
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(HomePageActivity.this, "获取用户信息失败");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, HomePageActivity.this);
                if (res != null) {
                    mUserHomePageBean = new Gson().fromJson(res, UserHomePageBean.class);
                    fillUI();
                }
            }
        };
        //请求用户信息
        PhoneLiveApi.getHomePageUInfo(AppContext.getInstance().getLoginUid(), uid, callback);
    }


    private void fillUI() {//ui填充
        if (null == mHomePageTopBack || null == mUserHomePageBean) return;
        mHomePageTopBack.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isBurl) {
                    mMyTask = new MyTask();
                    mMyTask.execute("");
                }
                return true;
            }
        });
        if (null == mSendNum || null == mUHead || null == mUNice || null == mUSex || null == mULevel || null == mUFansNum || null == mUFollowNum || null == mUSign || null == mUSign2 || null == mUNum || null == mFollowState || null == mTvBlackState)
            return;
        mSendNum.setText(getString(R.string.send) + "" + mUserHomePageBean.getConsumption());
        mUHead.setAvatarUrl(mUserHomePageBean.getAvatar());
        mUNice.setText(mUserHomePageBean.getUser_nicename());
        mUSex.setImageResource(mUserHomePageBean.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        mULevel.setImageResource(DrawableRes.LevelImg[(mUserHomePageBean.getLevel() == 0 ? 0 : mUserHomePageBean.getLevel() - 1)]);
        mUFansNum.setText(getString(R.string.fans) + ":" + mUserHomePageBean.getFansnum());
        mUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.getAttentionnum());
        mUSign.setText(mUserHomePageBean.getSignature());
        mUSign2.setText(mUserHomePageBean.getSignature());
        mUNum.setText("ID: " + mUserHomePageBean.getId());
        mFollowState.setText(mUserHomePageBean.getIsattention() == 0 ? "关注TA" : getString(R.string.alreadyfollow));
        mTvBlackState.setText(mUserHomePageBean.getIsblack() == 0 ? getString(R.string.pullblack) : getString(R.string.relieveblack));
        List<OrderBean> os = mUserHomePageBean.getCoinrecord3();
        for (int i = 0; i < os.size(); i++) {
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }
    }

    @OnClick({R.id.top_back, R.id.ll_home_page_menu_lahei, R.id.ll_home_page_menu_privatechat, R.id.tv_home_page_menu_follow, R.id.rl_home_pager_yi_order, R.id.tv_home_page_follow, R.id.tv_home_page_index_btn, R.id.tv_home_page_video_btn, R.id.iv_home_page_back, R.id.tv_home_page_fans})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_back:
                if (mMyTask != null)
                    mMyTask.cancel(true);
                finish();
                break;
            case R.id.ll_home_page_menu_privatechat:
                if (Utils.isFastClick()) return;
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
                if (Utils.isFastClick()) return;
                pullTheBlack();
                break;
            case R.id.tv_home_page_menu_follow:
                if (Utils.isFastClick()) return;
                followUserOralready();
                break;
            case R.id.tv_home_page_index_btn:
                if (Utils.isFastClick()) return;
                mHomeIndexPage.setVisibility(View.VISIBLE);
                mHomeVideoPage.setVisibility(View.GONE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.global));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_home_page_video_btn:
                if (Utils.isFastClick()) return;
                mHomeIndexPage.setVisibility(View.GONE);
                mHomeVideoPage.setVisibility(View.VISIBLE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.black));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.global));
                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                if (Utils.isFastClick()) return;
                UIHelper.showFansActivity(this, uid);
                break;
            case R.id.tv_home_page_follow:
                if (Utils.isFastClick()) return;
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order://映票排行榜
                if (Utils.isFastClick()) return;
                UIHelper.showDedicateOrderActivity(this, uid);
                break;
        }

    }

    private void pullTheBlack() {// black list
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(), uid,
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        AppContext.showToastAppMsg(HomePageActivity.this, "操作失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response, HomePageActivity.this);
                        if (null == res || mTvBlackState == null) return;
                        if (mUserHomePageBean.getIsblack() == 0) {
                            //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                            try {
                                EMClient.getInstance().contactManager().addUserToBlackList("pt" + mUserHomePageBean.getId(), true);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                EMClient.getInstance().contactManager().removeUserFromBlackList("pt" + mUserHomePageBean.getId());
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                        AppContext.showToastAppMsg(HomePageActivity.this, mUserHomePageBean.getIsblack() == 0 ? "拉黑成功" : "解除拉黑");
                        mUserHomePageBean.setIsblack(mUserHomePageBean.getIsblack() == 0 ? 1 : 0);
                        mTvBlackState.setText(mUserHomePageBean.getIsblack() == 0 ? getString(R.string.pullblack) : getString(R.string.relieveblack));
                    }
                });
    }

    //私信
    private void openPrivateChat() {
        if (mUserHomePageBean.getIsblackto() == 1) {
            AppContext.showToastAppMsg(this, "你已被对方拉黑无法私信");
            return;
        }
        if (null != mUserHomePageBean) {
            PhoneLiveApi.getPmUserInfo(AppContext.getInstance().getLoginUid(), mUserHomePageBean.getId(), new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                }

                @Override
                public void onResponse(String response) {
                    String res = ApiUtils.checkIsSuccess(response, HomePageActivity.this);
                    if (null != res)
                        UIHelper.showPrivateChatMessage(HomePageActivity.this, new Gson().fromJson(res, PrivateChatUserBean.class));

                }
            });
        }
    }

    /**
     * 是否已经关注过该用户
     */
    private void followUserOralready() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                mUserHomePageBean.setIsattention(mUserHomePageBean.getIsattention() == 0 ? 1 : 0);
                mFollowState.setText(mUserHomePageBean.getIsattention() == 0 ? "关注TA" : getString(R.string.alreadyfollow));
            }
        };
        PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), uid, AppContext.getInstance().getToken(), callback);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    private boolean isBurl = false;
    private MyTask mMyTask;

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("个人主页"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    /**
     * blur top bac. >> Image
     */
    class MyTask extends AsyncTask<String, Integer, Integer> {
        private Bitmap mBitmap;

        @Override
        protected Integer doInBackground(String... params) {
            if (null == mUserHomePageBean) return null;
            mBitmap = ImageLoader.getInstance().loadImageSync(mUserHomePageBean.getAvatar());
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (null != mBitmap) {
                blur(mBitmap, mHomePageTopBack);
            }
        }
    }

    /**
     * start blur
     */
    private void blur(Bitmap back, View container) {
        isBurl = true;
        float radius = 15;
        long startMs = System.currentTimeMillis();
        if (null == container) return;
        Canvas canvas = null;
        Bitmap overlay = null;
        float scaleFactor = (float) container.getMeasuredWidth() / back.getWidth();  //选取比例
        float scaleFactorh = (float) container.getMeasuredHeight() / back.getHeight();
        overlay = Bitmap.createBitmap(
                (int) (container.getMeasuredWidth() / (scaleFactor)),
                (int) (container.getMeasuredHeight() / (scaleFactorh)),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(overlay);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(back, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        container.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        isBurl = false;

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人主页"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}