package com.weilian.phonelive.ui;


import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.tandong.bottomview.view.BottomView;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.GridViewAdapter;
import com.weilian.phonelive.adapter.UserListAdapter;
import com.weilian.phonelive.adapter.ViewPageGridViewAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseApplication;
import com.weilian.phonelive.base.ShowLiveActivityBase;
import com.weilian.phonelive.bean.ChatBean;
import com.weilian.phonelive.bean.GiftBean;
import com.weilian.phonelive.bean.SendGiftBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.interf.ChatServerInterface;
import com.weilian.phonelive.interf.DialogInterface;
import com.weilian.phonelive.ui.dialog.LiveCommon;
import com.weilian.phonelive.ui.other.ChatServer;
import com.weilian.phonelive.utils.DialogHelp;
import com.weilian.phonelive.utils.QosObject;
import com.weilian.phonelive.utils.QosThread;
import com.weilian.phonelive.utils.ShareUtils;
import com.weilian.phonelive.utils.StringUtils;
import com.weilian.phonelive.utils.Strings;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.VideoSurfaceView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/*
* 直播播放页面
* */
public class VideoPlayerActivity extends ShowLiveActivityBase implements View.OnLayoutChangeListener {

    public final static String USER_INFO = "USER_INFO";
    @InjectView(R.id.video_view)
    VideoSurfaceView mVideoSurfaceView;

    @InjectView(R.id.rl_loading)
    RelativeLayout mLayoutLoading;

    //直播结束关注btn
    @InjectView(R.id.btn_follow)
    Button mFollowEmcee;

    @InjectView(R.id.view_live_content)
    RelativeLayout mLiveContent;

    private static final String TAG = "VideoPlayerActivity";

    public static final int UPDATE_SEEKBAR = 0;

    public static final int HIDDEN_SEEKBAR = 1;

    public static final int UPDATE_QOS = 2;

    private KSYMediaPlayer ksyMediaPlayer;

    private QosThread mQosThread;

    private Surface mSurface = null;

    private SurfaceHolder mSurfaceHolder = null;

    private boolean mPlayerPanelShow = false;

    private boolean mPause = false;

    private long mStartTime = 0;

    private long mPauseStartTime = 0;

    private long mPausedTime = 0;

    //视频流宽度
    private int mVideoWidth = 0;

    //视频流高度
    private int mVideoHeight = 0;

    private List<GiftBean> mGiftList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpGiftAdapter;

    //礼物view
    private ViewPager mVpGiftView;

    //礼物服务端返回数据
    private String mGiftResStr;

    //当前选中的礼物
    private GiftBean mSelectedGiftItem;

    //赠送礼物按钮
    private Button mSendGiftBtn;

    private int mShowGiftSendOutTime = 5;

    private RelativeLayout mSendGiftLian;

    private TextView mUserCoin;

    //主播信息
    private UserBean mEmceeInfo;

    private BottomView mGiftSelectView;

    //是否是禁言状态
    private boolean mIsShutUp = false;

    private long mLitLastTime = 0;

    private View mLoadingView;

    private int lastX;
    private int lastY;

    private ExecutorService mService;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("直播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        mWl.acquire();
        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.start();
            mPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("直播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPause(this);          //统计时长
        mWl.release();
        if (ksyMediaPlayer != null) {
            //ksyMediaPlayer.pause();
            mPause = true;
        }
    }

    @Override
    protected void onDestroy() {//释放
        videoPlayEnd();
        super.onDestroy();
        if (mService != null && !mService.isShutdown()) {
            mService.shutdownNow();
        }
        mService = null;
        finish();
        System.gc();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_look;
    }

    @Override
    public void initView() {
        super.initView();
        mLiveChat.setVisibility(View.VISIBLE);
        startLoadingAnimation();
        mVideoSurfaceView.addOnLayoutChangeListener(this);
        mRoot.addOnLayoutChangeListener(this);
        mLlinfo.setClickable(true);
        mEmceeHead.setClickable(true);
        mTvEmceeName.setClickable(true);
        mLiveNum.setClickable(true);
        mService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (v.getId() == R.id.video_view) {
            if (bottom != 0) {
                //防止聊天软键盘挤压屏幕导致视频变形
                mVideoSurfaceView.setVideoDimension(mScreenWidth, mScreenHeight);
            }
        } else if (v.getId() == R.id.rl_live_root) {
            if (bottom > oldBottom) {
                //如果聊天窗口开启,收起软键盘时关闭聊天输入框
                hideEditText();
            }
        }

    }

    /**
     * @dw 当每个聊天被点击显示该用户详细信息弹窗
     */
    public void chatListItemClick(ChatBean chat) {
        if (chat.getId() == mUser.getId()) {
            LiveCommon.showOwnInfoDialog(VideoPlayerActivity.this, chat, mEmceeInfo.getId());
        } else {
            LiveCommon.showUserInfoDialog(VideoPlayerActivity.this, mUser, chat, mEmceeInfo.getId(), mChatServer);
        }
    }


    //加载中animation
    private void startLoadingAnimation() {
        mLoadingView = getLayoutInflater().inflate(R.layout.view_live_loading, null);
        ImageView loading = (ImageView) mLoadingView.findViewById(R.id.iv_live_loading_img);
        ImageView loading2 = (ImageView) mLoadingView.findViewById(R.id.iv_live_loading_img2);
        mLayoutLoading.addView(mLoadingView);
        LayoutParams pa = loading.getLayoutParams();
        pa.height = mScreenHeight / 2;
        loading.setLayoutParams(pa);
        LayoutParams pa2 = loading2.getLayoutParams();
        pa2.height = mScreenHeight / 2;
        loading2.setLayoutParams(pa2);
        loading2.setX(mScreenWidth);
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(loading, "translationX", -mScreenWidth);
        oa1.setDuration(1500);
        oa1.setRepeatMode(ObjectAnimator.RESTART);
        oa1.setRepeatCount(ObjectAnimator.INFINITE);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(loading2, "translationX", 0);
        oa2.setDuration(1500);
        oa2.setRepeatMode(ObjectAnimator.RESTART);
        oa2.setRepeatCount(ObjectAnimator.INFINITE);
        oa1.start();
        oa2.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int index = mRandom.nextInt(4);
            if (mLitLastTime == 0 || (System.currentTimeMillis() - mLitLastTime) > 500) {
                if (mLitLastTime == 0) {
                    //第一次点亮请求服务端纪录
                    PhoneLiveApi.showLit(mUser.getId(), mUser.getToken(), mEmceeInfo.getId());
                    mChatServer.doSendLitMsg(mUser, index);
                }
                mLitLastTime = System.currentTimeMillis();
                mChatServer.doSendLit(index);
            } else {
                showLit(mRandom.nextInt(4));
            }

        }
        //屏幕侧滑隐藏页面功能
        int rowX = (int) event.getRawX();
        int rowY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = rowX;
                lastY = rowY;
                break;
            case MotionEvent.ACTION_MOVE:

                if (!(mLiveContent.getLeft() < 0)) {
                    int offsetX = rowX - lastX;
                    int offsetY = rowY - lastY;
                    if (Math.abs(offsetX) > 10) {
                        mLiveContent.layout(mLiveContent.getLeft() + offsetX,
                                mLiveContent.getTop(),
                                mLiveContent.getRight(),
                                mLiveContent.getBottom()
                        );
                    }

                    lastX = rowX;
                    lastY = rowY;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mLiveContent.getLeft() > mScreenWidth / 2) {//如果left>当前屏幕宽度/2 则将该内容view隐藏
                    mLiveContent.layout(mScreenWidth,
                            mLiveContent.getTop(),
                            mLiveContent.getRight(),
                            mLiveContent.getBottom()
                    );
                } else {//否则还原view位置
                    mLiveContent.layout(0,
                            mLiveContent.getTop(),
                            mLiveContent.getRight(),
                            mLiveContent.getBottom()
                    );
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void initData() {
        super.initData();
        mGson = new Gson();

        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        //获取用户登陆信息
        mUser = AppContext.getInstance().getLoginUser();
        mEmceeInfo = (UserBean) bundle.getSerializable(USER_INFO);

        //主播昵称
        mTvEmceeName.setText(mEmceeInfo.getUser_nicename());

        mRoomNum = mEmceeInfo.getId();
        mTvLiveNumber.setText("ID号:" + mEmceeInfo.getId() + "");
        //初始化直播播放器参数配置
        initLive();
        mEmceeHead.setAvatarUrl(mEmceeInfo.getAvatar());
        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), BaseApplication.context(), mEmceeInfo.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            TLog.log("connect error");
        }
        PowerManager mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWl = mPm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        getRoomInfo();
    }

    /**
     * @dw 获取礼物列表
     */
    private void getGiftList() {
        PhoneLiveApi.getGiftList(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(VideoPlayerActivity.this, "获取礼物信息失败");
            }

            @Override
            public void onResponse(String s) {
                mGiftResStr = ApiUtils.checkIsSuccess(s, VideoPlayerActivity.this);
            }
        });
    }

    private void initLive() {
        mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mVideoSurfaceView.setOnTouchListener(mTouchListener);
        mVideoSurfaceView.setKeepScreenOn(true);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mHandler = new UIHandler(this);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mQosThread = new QosThread(activityManager, mHandler);

        //视频流播放地址
        String mrl = AppConfig.RTMP_URL + mEmceeInfo.getId();//getIntent().getStringExtra("path");
        Log.e(getLocalClassName(), mrl);
        //视频播放器init
        ksyMediaPlayer = new KSYMediaPlayer.Builder(BaseApplication.context()).build();
        ksyMediaPlayer.setBufferTimeMax(5);
        try {
            ksyMediaPlayer.setDataSource(mrl);
            //rtmp://live.hkstv.hk.lxdns.com/live/hks
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        * 参数：OnBufferingUpdateListener
        * 功能：设置Buffering的监听器，当播放器在Buffering时会发出此回调，通知外界Buffering的进度
        * 返回值：无
        * */
        ksyMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        /*
        * 参数：OnCompletionListener
        * 功能：设置Completion的监听器，在视频播放完成后会发出此回调
        * 返回值：无
        * */
        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        /*
        * 参数：OnPreparedListener
        * 功能：设置Prepared状态的监听器，在调用prepare()/prepareAsync()之后，正常完成解析后会通过此监听器通知外界。
        * 返回值：无
        * */
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);

        /*
        *参数：OnInfoListener
        *功能：设置Info监听器，播放器可通过此回调接口将消息通知开发者
        *返回值：无
        * */
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        /*参数：OnVideoSizeChangedListener
        功能：设置VideoSizeChanged的监听器，当视频的宽度或高度发生变化时会发出次回调，通知外界视频的最新宽度和高度*/
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        /*参数：OnErrorListener
        功能：设置Error监听器，当播放器遇到error时，会发出此回调并送出error code
        返回值：无*/
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        /*参数：OnSeekCompleteListener
        功能：设置Seek Complete的监听器，Seek操作完成后会有此回调
        返回值：无*/
        ksyMediaPlayer.setOnSeekCompleteListener(mOnSeekCompletedListener);
        /*参数：screenOn 值为true时，播放时屏幕保持常亮，反之则否
        功能：使用SurfaceHolder控制播放期间屏幕是否保持常亮。须调用接口setDisplay设置SurfaceHolder，此接口才有效
        返回值：无*/
        ksyMediaPlayer.setScreenOnWhilePlaying(true);
        /*参数：直播音频缓存最大值，单位为秒
        功能：设置直播音频缓存上限，由此可控制追赶功能的阈值。该值为负数时，关闭直播追赶功能。此接口只对直播有效
        返回值：无*/


    }


    @OnClick({R.id.tv_live_num, R.id.tv_live_start_emcee_name, R.id.iv_live_shar, R.id.iv_live_emcee_head, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.ll_live_room_info, R.id.iv_live_chat, R.id.btn_back_index, R.id.rl_loading, R.id.bt_send_chat, R.id.iv_live_gift, R.id.btn_follow})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_live_num:
                LiveCommon.showUserInfoDialog(this, mUser, mEmceeInfo, mEmceeInfo.getId(), mChatServer);
                break;
            case R.id.tv_live_start_emcee_name:
                LiveCommon.showUserInfoDialog(this, mUser, mEmceeInfo, mEmceeInfo.getId(), mChatServer);
                break;
            case R.id.iv_live_emcee_head:
                LiveCommon.showUserInfoDialog(this, mUser, mEmceeInfo, mEmceeInfo.getId(), mChatServer);
                break;
            case R.id.iv_live_shar:
                showSharePopWindow(v);
                break;
            case R.id.iv_live_privatechat:
                mIvNewPrivateChat.setVisibility(View.GONE);
                UIHelper.showPrivateChatSimple(VideoPlayerActivity.this, mUser.getId());
                break;
            case R.id.iv_live_back:
                finish();
                break;
            case R.id.ll_yp_labe:
                UIHelper.showDedicateOrderActivity(this, mEmceeInfo.getId());
                break;
            case R.id.iv_live_chat:
                if (mIsShutUp)
                    isShutUp();
                else
                    showEditText();
                break;
            case R.id.btn_back_index:
                videoPlayEnd();
                finish();
                break;
            case R.id.bt_send_chat:
                sendChat();
                break;
            case R.id.rl_loading:
                hideEditText();
                break;
            case R.id.iv_live_gift:
                showGiftList();
                break;
            case R.id.btn_follow://直播结束关注按钮
                showFollow(mUser.getId(), mEmceeInfo.getId());
                break;
            case R.id.ll_live_room_info://左上角点击主播信息
                LiveCommon.showUserInfoDialog(this, mUser, mEmceeInfo, mEmceeInfo.getId(), mChatServer);
                break;
        }
    }


    //分享操作
    public void share(View v) {
        ShareUtils.share(this, v.getId(), mEmceeInfo);
    }


    //分享pop弹窗
    private void showSharePopWindow(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.pop_view_share, null);
        PopupWindow p = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        p.setBackgroundDrawable(new BitmapDrawable());
        p.setOutsideTouchable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        p.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.getWidth() / 2 - view.getMeasuredWidth() / 2, location[1] - view.getMeasuredHeight());

    }


    /**
     * @param uid   当前用户id
     * @param touid 关注用户id
     * @dw 关注
     */
    private void showFollow(int uid, int touid) {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                ApiUtils.checkIsSuccess(response, VideoPlayerActivity.this);
                if (mFollowEmcee.getText().toString().equals(getResources().getString(R.string.follow))) {
                    mFollowEmcee.setText(getResources().getString(R.string.alreadyfollow));
                } else {
                    mFollowEmcee.setText(getResources().getString(R.string.follow));
                }

            }
        };
        PhoneLiveApi.showFollow(uid, touid, AppContext.getInstance().getToken(), callback);
    }

    //连送按钮隐藏
    private void recoverySendGiftBtnLayout() {
        ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText("");
        mSendGiftLian.setVisibility(View.GONE);
        mSendGiftBtn.setVisibility(View.VISIBLE);
        mShowGiftSendOutTime = 5;
    }

    //展示礼物列表
    private void showGiftList() {
        if (mYpNum == null) {
            return;
        }
        mGiftSelectView = new BottomView(this, R.style.BottomViewTheme_Transparent, R.layout.view_show_viewpager);
        mGiftSelectView.setAnimation(R.style.BottomToTopAnim);
        mGiftSelectView.showBottomView(true);
        View mGiftView = mGiftSelectView.getView();
        mUserCoin = (TextView) mGiftView.findViewById(R.id.tv_show_select_user_coin);
        mUserCoin.setText(mUser.getCoin());
        //点击底部跳转充值页面
        mGiftView.findViewById(R.id.rl_show_gift_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("diamonds", mUser.getCoin());
                UIHelper.showMyDiamonds(VideoPlayerActivity.this, bundle);
            }
        });
        mVpGiftView = (ViewPager) mGiftView.findViewById(R.id.vp_gift_page);
        mSendGiftLian = (RelativeLayout) mGiftView.findViewById(R.id.iv_show_send_gift_lian);

        mSendGiftLian.setOnClickListener(new View.OnClickListener() {//礼物连送
            @Override
            public void onClick(View v) {
                sendGift("y");//礼物发送
                mShowGiftSendOutTime = 5;
                ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
            }
        });
        mSendGiftBtn = (Button) mGiftView.findViewById(R.id.btn_show_send_gift);
        mSendGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendGift(v);
            }
        });
        if (mSelectedGiftItem != null) {
            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.global));
        }
        if (mGiftViews != null) { //表示已经请求过数据不再向下执行
            fillGift();
            return;
        }

    }

    /**
     * @param v btn
     * @dw 点击赠送礼物按钮
     */
    private void onClickSendGift(View v) {//赠送礼物
        if (!mConnectionState) {//没有连接ok
            return;
        }
        if ((mSelectedGiftItem != null) && (mSelectedGiftItem.getType() == 1)) {//连送礼物
            v.setVisibility(View.GONE);
            if (mHandler == null) return;
            mHandler.postDelayed(new Runnable() {//开启连送定时器
                @Override
                public void run() {
                    if (mShowGiftSendOutTime == 1) {
                        recoverySendGiftBtnLayout();
                        mHandler.removeCallbacks(this);
                        return;
                    }
                    mHandler.postDelayed(this, 1000);
                    mShowGiftSendOutTime--;
                    ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));

                }
            }, 1000);
            sendGift("y");//礼物发送
        } else {
            sendGift("n");//礼物发送
        }
    }

    //礼物列表填充
    private void fillGift() {
        if (null == mVpGiftAdapter && null != mGiftResStr) {
            if (mGiftList.size() == 0) {
                try {
                    JSONArray giftListJson = new JSONArray(mGiftResStr);
                    for (int i = 0; i < giftListJson.length(); i++) {
                        mGiftList.add(mGson.fromJson(giftListJson.getString(i), GiftBean.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //礼物item填充
            List<View> mGiftViewList = new ArrayList<>();
            int index = 0;

            for (int i = 0; i < 3; i++) {
                View v = getLayoutInflater().inflate(R.layout.view_show_gifts_gv, null);
                mGiftViewList.add(v);
                List<GiftBean> l = new ArrayList<>();
                for (int j = 0; j < 8; j++) {
                    if (index >= mGiftList.size()) {
                        break;
                    }
                    l.add(mGiftList.get(index));
                    index++;
                }
                mGiftViews.add((GridView) v.findViewById(R.id.gv_gift_list));
                mGiftViews.get(i).setAdapter(new GridViewAdapter(l));
                mGiftViews.get(i).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        giftItemClick(parent, view, position);
                    }
                });
            }
            mVpGiftAdapter = new ViewPageGridViewAdapter(mGiftViewList);
        }
        mVpGiftView.setAdapter(mVpGiftAdapter);
        //mVpGiftView.setCurrentItem(0);
    }

    //赠送礼物单项被选中
    private void giftItemClick(AdapterView<?> parent, View view, int position) {
        if ((GiftBean) parent.getItemAtPosition(position) != mSelectedGiftItem) {
            recoverySendGiftBtnLayout();
            mSelectedGiftItem = (GiftBean) parent.getItemAtPosition(position);
            //点击其他礼物
            changeSendGiftBtnStatue(true);
            for (int i = 0; i < mGiftViews.size(); i++) {
                for (int j = 0; j < mGiftViews.get(i).getChildCount(); j++) {
                    if (((GiftBean) mGiftViews.get(i).getItemAtPosition(j)).getType() == 1) {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
                    } else {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
                    }

                }
            }
            view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift_chosen);

        } else {
            if (((GiftBean) parent.getItemAtPosition(position)).getType() == 1) {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
            } else {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
            }
            mSelectedGiftItem = null;
            changeSendGiftBtnStatue(false);

        }
    }

    /**
     * @param statue 开启or关闭
     * @dw 赠送礼物按钮状态修改
     */
    private void changeSendGiftBtnStatue(boolean statue) {
        if (statue) {
            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.global));
            mSendGiftBtn.setEnabled(true);
        } else {
            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.light_gray2));
            mSendGiftBtn.setEnabled(false);
        }
    }

    /**
     * @param isOutTime 是否连送超时(如果是连送礼物的情况下)
     * @dw 赠送礼物请求服务端获取数据扣费
     */
    private void sendGift(final String isOutTime) {
        if (mSelectedGiftItem != null) {
            if (mSelectedGiftItem.getType() == 1) {
                mSendGiftLian.setVisibility(View.VISIBLE);
            } else {
                changeSendGiftBtnStatue(true);
            }
            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    AppContext.showToastAppMsg(VideoPlayerActivity.this, getString(R.string.senderror));
                }

                @Override
                public void onResponse(String response) {
                    //TLog.log(response);
                    String s = ApiUtils.checkIsSuccess(response, VideoPlayerActivity.this);
                    if (s != null) {
                        try {
                            ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
                            JSONObject tokenJson = new JSONObject(s);
                            mUser.setCoin(String.valueOf(
                                    StringUtils.toInt(mUserCoin.getText().toString())
                                            - mSelectedGiftItem.getNeedcoin()));
                            mUserCoin.setText(mUser.getCoin());//重置余额
                            mUser.setLevel(tokenJson.getInt("level"));
                            mChatServer.doSendGift(tokenJson.getString("gifttoken"), mUser, isOutTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            PhoneLiveApi.sendGift(mUser, mSelectedGiftItem, mEmceeInfo.getId(), callback);
        }
    }

    /**
     * @dw 获取房间信息
     */
    private void getRoomInfo() {
        //请求服务端获取房间基本信息
        PhoneLiveApi.initRoomInfo(AppContext.getInstance().getLoginUid()
                , mEmceeInfo.getId()
                , AppContext.getInstance().getToken()
                , AppContext.address
                , new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(VideoPlayerActivity.this, getString(R.string.initDataError));
            }

            @Override
            public void onResponse(String s) {
                String res = ApiUtils.checkIsSuccess(s, VideoPlayerActivity.this);
                if (res != null) {
                    UserBean u = mGson.fromJson(res, UserBean.class);
                    mUser.setCoin(u.getCoin());
                    mUser.setLevel(u.getLevel());
                    mChatServer.connectSocketServer(res, AppContext.getInstance().getToken(), mEmceeInfo.getId());//连接到socket服务端
                    fillUI();
                }
            }
        });
        //禁言状态初始化
        PhoneLiveApi.isShutUp(mUser.getId(), mEmceeInfo.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res && res.equals("1")) {
                    mIsShutUp = true;
                }
            }
        });
        //获取礼物列表
        getGiftList();

    }

    //更新ui
    private void fillUI() {
        if (mLvChatList != null && mChatListAdapter != null) {
            mLvChatList.setAdapter(mChatListAdapter);
        }
    }

    //socket客户端事件监听处理start
    private class ChatListenUIRefresh implements ChatServerInterface {

        @Override
        public void onMessageListen(final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessage(c);
                }
            });
        }

        @Override
        public void onConnect(final boolean res) {
            //连接结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectRes(res);
                }
            });
        }

        @Override
        public void onUserList(SparseArray<UserBean> uList, String votes) {//用户列表
            mUsers = uList;
            if (mUserList != null) {
                mLiveNum.setText(ChatServer.LIVEUSERNUMS + "");
                mYpNum.setText(votes);
                sortUserList();
                mUserListAdapter.setOnItemClickListener(new UserListAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //展示用户详细信息弹窗
                        if (mUser.getId() == mUsers.valueAt(position).getId()) {
                            LiveCommon.showOwnInfoDialog(VideoPlayerActivity.this, mUsers.valueAt(position), mEmceeInfo.getId());
                            return;
                        }
                        LiveCommon.showUserInfoDialog(VideoPlayerActivity.this, mUser, mUsers.valueAt(position), mEmceeInfo.getId(), mChatServer);
                    }
                });
            }
        }

        //用户状态改变
        @Override
        public void onUserStateChange(final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        //主播关闭直播
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {
                        DialogHelp.showPromptDialog(getLayoutInflater(), VideoPlayerActivity.this, "直播内容涉嫌违规", new DialogInterface() {

                            @Override
                            public void cancelDialog(View v, Dialog d) {

                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                d.dismiss();
                            }
                        });
                    }
                    if (mLayoutLiveStop != null) {
                        mLayoutLiveStop.setVisibility(View.VISIBLE);
                    }
                    showEndIsFollowEmcee();
                    videoPlayEnd();
                }
            });

        }

        //送礼物展示
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        //设置为管理员
        @Override
        public void setManage(final JSONObject contentJson, final ChatBean chat) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (contentJson == null || contentJson.length() <= 0 || !contentJson.has("touid"))
                            return;
                        if (contentJson.getInt("touid") == mUser.getId()) {
                            AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被设为管理员");
                        }
                        addChatMessage(chat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //特权操作
        @Override
        public void onPrivilegeAction(final ChatBean c, final JSONObject contentJson) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TLog.error(contentJson.toString());
                        if (contentJson == null || contentJson.length() <= 0 || !contentJson.has("touid"))
                            return;
                        if (contentJson.getInt("touid") == mUser.getId()) {
                            AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被禁言");
                            mIsShutUp = true;
                            hideEditText();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addChatMessage(c);
                }
            });
        }

        //点亮
        @Override
        public void onLit() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLit(mRandom.nextInt(4));
                }
            });

        }

        //添加僵尸粉丝
        @Override
        public void onAddZombieFans(final String ct) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addZombieFans(ct);
                }
            });
        }

        //服务器连接错误
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppContext.showToastAppMsg(VideoPlayerActivity.this, "服务器连接错误");
                }
            });
        }
    }

    //直播结束判断当前主播是否关注过
    private void showEndIsFollowEmcee() {

        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, VideoPlayerActivity.this);//0：未关注1:关注
                if (res != null) {
                    if (res.equals("0")) {
                        mFollowEmcee.setText(getString(R.string.follow));
                    } else {
                        mFollowEmcee.setText(getString(R.string.alreadyfollow));
                    }
                }

            }
        };

        PhoneLiveApi.getIsFollow(mUser.getId(), mEmceeInfo.getId(), callback);//判断当前主播是否已经关注
    }
    //socket客户端事件监听处理end

    public void dialogReply(UserBean toUser) {
        if (mIsShutUp) {
            isShutUp();
        } else {
            ACE_TEX_TO_USER = toUser.getId();
            mChatInput.setText("@" + toUser.getUser_nicename() + " ");
            mChatInput.setSelection(mChatInput.getText().length());
            showEditText();
        }

    }


    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            ksyMediaPlayer.start();
            TLog.error("加载完成" + mp.getDataSource());
            //直播开始
            if (null != mLoadingView && null != mRoot) {
                mRoot.removeView(mLoadingView);
                mLoadingView = null;

                mLayoutLoading.setVisibility(View.GONE);
                mLayoutLoading = null;
            }


        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            long duration = ksyMediaPlayer.getDuration();
            long progress = duration * percent / 100;
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    // maybe we could call scaleVideoView here.
                    if (mVideoSurfaceView != null) {

                        mVideoSurfaceView.setVideoDimension(mVideoWidth, mVideoHeight);
                        mVideoSurfaceView.requestLayout();
                    }
                }
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.e(TAG, "onSeekComplete...............");
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            //直播完成 直播结束
            mButtonMenuFrame.setVisibility(View.GONE);
            mLayoutLiveStop.setVisibility(View.VISIBLE);
            videoPlayEnd();
            showEndIsFollowEmcee();
        }
    };
    //错误异常监听
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "OnErrorListener, Error Unknown:" + what + ",extra:" + extra);
                    break;
                default:
                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra + ",source:" + mp.getDataSource());
            }

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "onInfo, what:" + i + ",extra:" + i1);
            return false;
        }
    };


    /**
     * 检测状态栏
     *
     * @param keyCode 返回键
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            videoPlayEnd();
            ButterKnife.reset(this);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //视频尺寸规格
    private void scaleVideoView() {
        if (ksyMediaPlayer == null || ksyMediaPlayer.getVideoHeight() <= 0 || mVideoSurfaceView == null)
            return;

        WindowManager wm = this.getWindowManager();
        int sw = wm.getDefaultDisplay().getWidth();
        int sh = wm.getDefaultDisplay().getHeight();
        int videoWidth = mVideoWidth;
        int videoHeight = mVideoHeight;
        int visibleWidth = 0;
        int visibleHeight = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            visibleWidth = sw > sh ? sh : sw;
            visibleHeight = (int) Math.ceil(visibleWidth * videoHeight / videoWidth);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (videoHeight * sw > videoWidth * sh) {
                visibleHeight = sh;
                visibleWidth = (int) Math.ceil(videoWidth * visibleHeight / videoHeight);
            } else {
                visibleWidth = sw;
                visibleHeight = (int) Math.ceil(visibleWidth * videoHeight / videoWidth);
            }
        }

        LayoutParams lp = mVideoSurfaceView.getLayoutParams();
        lp.width = visibleWidth;
        lp.height = visibleHeight;
        mVideoSurfaceView.setLayoutParams(lp);

        mVideoSurfaceView.invalidate();




    }

    //进度条更新
    public int setVideoProgress(int currentProgress) {

        if (ksyMediaPlayer == null)
            return -1;

        long time = currentProgress > 0 ? currentProgress : ksyMediaPlayer.getCurrentPosition();
        long length = ksyMediaPlayer.getDuration();

        // Update all view elements

        if (time >= 0) {
            String progress = Strings.millisToString(time) + "/" + Strings.millisToString(length);
        }

        Message msg = new Message();
        msg.what = UPDATE_SEEKBAR;

        if (mHandler != null)
            mHandler.sendMessageDelayed(msg, 1000);
        return (int) time;
    }

    private void updateQosInfo(QosObject obj) {

        if (ksyMediaPlayer != null) {
            long bits = ksyMediaPlayer.getDecodedDataSize() * 8 / (mPause ? mPauseStartTime - mPausedTime - mStartTime : System.currentTimeMillis() - mPausedTime - mStartTime);

        }

    }



    //直播结束释放资源
    private void videoPlayEnd() {

        mService.execute(new Runnable() {
            @Override
            public void run() {
                if (mButtonMenuFrame != null) {
                    mButtonMenuFrame.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mButtonMenuFrame != null)
                                mButtonMenuFrame.setVisibility(View.GONE);//隐藏菜单栏
                        }
                    });
                }
                if (mLvChatList != null) {
                    mLvChatList.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mLvChatList != null)
                                mLvChatList.setVisibility(View.GONE);
                        }
                    });
                }
                if (mChatServer != null) {
                    mChatServer.close();
                }

                if (ksyMediaPlayer != null) {
                    ksyMediaPlayer.release();
                    ksyMediaPlayer = null;
                }
                if (null != mHandler) {
                    mHandler.removeCallbacksAndMessages(null);
                    //mHandler = null;
                }

                if (mQosThread != null) {
                    mQosThread.stopThread();
                    mQosThread = null;
                }
            }
        });



    }

    private int mVideoProgress = 0;
    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mVideoProgress = progress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ksyMediaPlayer.seekTo(mVideoProgress);
            setVideoProgress(mVideoProgress);
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideEditText();
            //dealTouchEvent(v, event);
            return false;
        }
    };


    //获取当前用户是否被禁言
    private void isShutUp() {
        if (mIsShutUp) {
            PhoneLiveApi.isShutUp(mUser.getId(), mEmceeInfo.getId(),
                    new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(String response) {
                            String res = ApiUtils.checkIsSuccess(response, VideoPlayerActivity.this);

                            if (res == null) return;
                            if (Integer.parseInt(res) == 0) {
                                mIsShutUp = false;
                                showEditText();
                            } else {
                                AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被禁言");
                            }
                        }
                    });
        }
    }


    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            if (ksyMediaPlayer != null) {
                final Surface newSurface = holder.getSurface();
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                }
                ksyMediaPlayer.setDisplay(holder);
                ksyMediaPlayer.setSurface(mSurface);
                ksyMediaPlayer.setScreenOnWhilePlaying(true);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            TLog.error("holder被创建---->" + holder.getSurface().toString());

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            if (ksyMediaPlayer != null) {
                ksyMediaPlayer.setDisplay(null);

                mSurface = null;
            }
        }
    };


    private class UIHandler extends Handler {

        VideoPlayerActivity mActivity;

        public UIHandler(VideoPlayerActivity activty) {
            mActivity = activty;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEKBAR:
                    if (mActivity != null)
                        mActivity.setVideoProgress(0);
                    break;
                case HIDDEN_SEEKBAR:
                    if (mActivity != null) {
                        mActivity.mPlayerPanelShow = false;
                    }
                    break;
                case UPDATE_QOS:
                    if (mActivity != null && msg.obj instanceof QosObject) {
                        mActivity.updateQosInfo((QosObject) msg.obj);
                    }
                    break;
            }
        }
    }

}
