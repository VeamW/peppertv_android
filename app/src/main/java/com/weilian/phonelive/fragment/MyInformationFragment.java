package com.weilian.phonelive.fragment;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.OrderBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.ui.SelectAvatarActivity;
import com.weilian.phonelive.utils.FastBlur;
import com.weilian.phonelive.utils.LoginUtils;
import com.weilian.phonelive.utils.StringUtils;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 登录用户中心页面
 */
public class MyInformationFragment extends BaseFragment {

    @InjectView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    @InjectView(R.id.tv_name)
    TextView mTvName;
    @InjectView(R.id.iv_gender)
    ImageView mIvGender;
    @InjectView(R.id.tv_signature)
    TextView mTvSignature;
    @InjectView(R.id.iv_editInfo)
    ImageView mIvEditInfo;
    @InjectView(R.id.ll_user_container)
    View mUserContainer;
    @InjectView(R.id.rl_user_unlogin)
    View mUserUnLogin;
    @InjectView(R.id.ll_loginout)
    LinearLayout mLoginOut;
    @InjectView(R.id.tv_info_u_follow_num)
    TextView mFollowNum;
    @InjectView(R.id.tv_info_u_fans_num)
    TextView mFansNum;
    @InjectView(R.id.tv_send)
    TextView mSendNum;
    @InjectView(R.id.tv_id)
    TextView mUId;
    @InjectView(R.id.iv_info_private_core)
    ImageView mPrivateCore;
    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;
    @InjectView(R.id.top_view)
    FrameLayout topBack;
    @InjectView(R.id.iv_live_start_cover)
    LoadUrlImageView mLoadUrlImageView;
    //等级
    @InjectView(R.id.iv_info_level)
    ImageView mTvInfoLevel;


    private boolean mIsWatingLogin;

    private UserBean mInfo;
    private AvatarView[] mOrderTopThree;
    //    private BroadcastReceiver broadcastReceiver;
    private boolean isBurl = false;
    private MyTask mMyTask;

    private void steupUser() {
        if (mIsWatingLogin) {
            mUserContainer.setVisibility(View.GONE);
            mUserUnLogin.setVisibility(View.VISIBLE);
        } else {
            mUserContainer.setVisibility(View.VISIBLE);
            mUserUnLogin.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        setNotice();
    }


    public void setNotice() {
        if (null == topBack) return;
//        initData();
        topBack.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isBurl) {
                    mMyTask = new MyTask();
                    mMyTask.execute("");
                }
                return true;
            }
        });
        sendRequestData();
    }


    View view;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_my_information,
                    container, false);
        }
        ButterKnife.inject(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData(true);
    }

    @Override
    public void onStart() {
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();
        super.onStart();
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.ll_live).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);
        view.findViewById(R.id.ll_fans).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_level).setOnClickListener(this);
        view.findViewById(R.id.ll_diamonds).setOnClickListener(this);
        view.findViewById(R.id.rl_info_order_btn).setOnClickListener(this);
        view.findViewById(R.id.ll_authenticate).setOnClickListener(this);
        view.findViewById(R.id.ll_gx).setOnClickListener(this);
        mUserUnLogin.setOnClickListener(this);
        mLoginOut.setOnClickListener(this);
        mIvEditInfo.setOnClickListener(this);
        mPrivateCore.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
        mOrderTopThree = new AvatarView[3];
        mOrderTopThree[0] = (AvatarView) view.findViewById(R.id.iv_info_order_no1);
        mOrderTopThree[1] = (AvatarView) view.findViewById(R.id.iv_info_order_no2);
        mOrderTopThree[2] = (AvatarView) view.findViewById(R.id.iv_info_order_no3);
    }

    private void fillUI() {
        if (mInfo == null)
            return;
        if (Utils.IS_IMG_CHANGED == true || Utils.IS_IMG_FIRST_LOAD == true) {
        topBack.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isBurl) {
                    mMyTask = new MyTask();
                    mMyTask.execute("");
                }
                return true;
            }
        });
        }
        if (null == mLoadUrlImageView || null == mIvAvatar || null == mTvName || null == mIvGender || null == mTvSignature || null == mUId)
            return;
        mLoadUrlImageView.setImageLoadUrl(mInfo.getAvatar());
        //头像
        mIvAvatar.setAvatarUrl(mInfo.getAvatar());
        //昵称
        mTvName.setText(mInfo.getUser_nicename());
        //性别
        mIvGender.setImageResource(
                StringUtils.toInt(mInfo.getSex()) == 1 ? R.drawable.global_male : R.drawable.global_female);
        //签名
        mTvSignature.setText(mInfo.getSignature().equals("") ? getString(R.string.defaultsign) : mInfo.getSignature());
        //id
        mUId.setText("ID:" + mInfo.getId());
        //等级
        mTvInfoLevel.setImageResource(DrawableRes.LevelImg[mInfo.getLevel() == 0 ? 0 : mInfo.getLevel() - 1]);


    }

    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            sendRequestData();
        } else {
            mIsWatingLogin = true;
        }
        steupUser();
    }

    private void sendRequestData() {
        int uid = AppContext.getInstance().getLoginUid();
        String token = AppContext.getInstance().getToken();
        PhoneLiveApi.getMyUserInfo(uid, token, stringCallback);
    }

    private String getCacheKey() {
        return "my_information" + AppContext.getInstance().getLoginUid();
    }

    StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String s) {
            String res = ApiUtils.checkIsSuccess(s, getActivity());
            if (null == mFollowNum || null == mFansNum || null == mSendNum) return;
            if (res == null || res.isEmpty()) {
                UIHelper.showLoginSelectActivity(getActivity());
                getActivity().finish();
                return;
            }
            mInfo = new Gson().fromJson(res, UserBean.class);
            if (mInfo == null) return;
            AppContext.getInstance().updateUserInfo(mInfo);
            mFollowNum.setText("关注:" + mInfo.getAttentionnum());
            mFansNum.setText("粉丝:" + mInfo.getFansnum());
            mSendNum.setText(mInfo.getConsumption());
            if (!(0 < mInfo.getCoinrecord3().size()))
                return;
            List<OrderBean> coinrecord3 = mInfo.getCoinrecord3();
            for (int i = 0; i < coinrecord3.size(); i++) {
                mOrderTopThree[i].setAvatarUrl(coinrecord3.get(i).getAvatar());
            }
        }
    };


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.ll_authenticate://申请认证
                UIHelper.showWebView(getActivity(), AppConfig.MAIN_URL + "/appcmf/index.php?g=User&m=Rz&a=index&uid=" + AppContext.getInstance().getLoginUid(), "申请认证");
                break;
            case R.id.iv_info_private_core:
                mIvNewMessage.setVisibility(View.GONE);
                UIHelper.showPrivateChatSimple(getActivity(), mInfo.getId());
                break;
            case R.id.ll_gx:
                UIHelper.showDedicateOrderActivity(getActivity(), mInfo.getId());
                break;

            case R.id.iv_avatar:
                Intent intent = new Intent(getActivity(), SelectAvatarActivity.class);
                intent.putExtra("uhead", mInfo.getAvatar());
                getActivity().startActivity(intent);
                break;
            case R.id.ll_live:
                UIHelper.showLiveRecordActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_following:
                UIHelper.showAttentionActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_fans:
                UIHelper.showFansActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds://我的钻石
                Bundle dBundle = new Bundle();
                dBundle.putString("diamonds", mInfo.getCoin());
                UIHelper.showMyDiamonds(getActivity(), dBundle);
                break;
            case R.id.ll_level://我的等级
                UIHelper.showLevel(getActivity(), AppContext.getInstance().getLoginUid());
                break;

            case R.id.rl_user_unlogin:
                AppManager.getAppManager().finishAllActivity();
                UIHelper.showLoginSelectActivity(getActivity());
                getActivity().finish();

                break;
            case R.id.ll_loginout:
                LoginUtils.outLogin(getActivity());
                getActivity().finish();
                break;
            case R.id.iv_editInfo://编辑资料
                UIHelper.showMyInfoDetailActivity(getActivity());
                break;
            case R.id.ll_profit://收益
                Bundle pBundle = new Bundle();
                pBundle.putString("votes", mInfo.getVotes());
                UIHelper.showProfitActivity(getActivity(), pBundle);
                break;
            default:
                break;
        }
    }


    @Override
    public void initData() {
        if (mIvNewMessage == null) {
            return;
        }
        //获取私信未读数量
     /*    int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (count > 0) {
            mIvNewMessage.setVisibility(View.VISIBLE);
        }
        IntentFilter intentFilter = new IntentFilter("com.weilian.phonelive");
       broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIvNewMessage.setVisibility(View.VISIBLE);
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);*/
    }


    /**
     * blur top bac. >> Image
     */
    class MyTask extends AsyncTask<String, Integer, Integer> {
        private Bitmap mBitmap;

        @Override
        protected Integer doInBackground(String... params) {
            mBitmap = ImageLoader.getInstance().loadImageSync(mInfo.getAvatar());
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (null != mBitmap) {
                blur(mBitmap, topBack);
            }
        }
    }

    /**
     * start blur
     *
     * @param back
     * @param container
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap back, View container) {
        isBurl = true;
        float radius = 5;
        if (null == container) return;
        Canvas canvas = null;
        Bitmap overlay = null;
        if (isAdded()) {
            float scaleFactor = (float) container.getMeasuredWidth() / back.getWidth();  //选取比例
            float scaleFactorh = (float) container.getMeasuredHeight() / back.getHeight();
            overlay = Bitmap.createBitmap(
                    (int) (container.getMeasuredWidth() / (scaleFactor)),
                    (int) (container.getMeasuredHeight() / (scaleFactor)),
                    Bitmap.Config.ARGB_8888);
            canvas = new Canvas(overlay);
             canvas.translate(-container.getLeft() / scaleFactor, -container.getTop() / scaleFactor);
//             canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(back, 0, 0, paint);
            overlay = FastBlur.doBlur(overlay, (int) radius, true);
            container.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
            isBurl = false;
        }

        Utils.IS_IMG_FIRST_LOAD = false;
        Utils.IS_IMG_CHANGED = false;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
       /*

       try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            broadcastReceiver.setDebugUnregister(true);
            broadcastReceiver = null;
        }*/
    }
}
