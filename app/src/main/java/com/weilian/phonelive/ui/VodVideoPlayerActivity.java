package com.weilian.phonelive.ui;


import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.base.BaseApplication;
import com.weilian.phonelive.bean.VodBean;
import com.weilian.phonelive.ui.other.ChatServer;
import com.weilian.phonelive.utils.QosObject;
import com.weilian.phonelive.utils.QosThread;
import com.weilian.phonelive.utils.Strings;
import com.weilian.phonelive.utils.TDevice;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.VideoSurfaceView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/*
* 直播播放页面
* */

public class VodVideoPlayerActivity extends BaseActivity implements View.OnLayoutChangeListener {

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

    private static final String TAG = "VodVideoPlayerActivity";

    public static final int UPDATE_SEEKBAR = 0;

    public static final int HIDDEN_SEEKBAR = 1;

    public static final int UPDATE_QOS = 2;
    @InjectView(R.id.iv_live_emcee_head)
    AvatarView mIvLiveEmceeHead;
    @InjectView(R.id.tv_live_start_emcee_name)
    TextView mTvLiveStartEmceeName;
    @InjectView(R.id.tv_live_number)
    TextView mTvLiveNumber;

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
    //屏幕宽度
    protected int mScreenWidth;
    //屏幕高度
    protected int mScreenHeight;

    //主播信息
    private VodBean.DataBean.InfoBean.DataBeanb mEmceeInfo;

    private View mLoadingView;

    private int lastX;
    private int lastY;

    private ExecutorService mService;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("点播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.start();
            mPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("点播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPause(this);          //统计时长
        if (ksyMediaPlayer != null) {
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
        return R.layout.activity_vod_video;
    }

    @Override
    public void initView() {
        mScreenWidth = (int) TDevice.getScreenWidth();
        mScreenHeight = (int) TDevice.getScreenHeight();
        startLoadingAnimation();
        mVideoSurfaceView.addOnLayoutChangeListener(this);
        mService = Executors.newFixedThreadPool(3);
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
    public void initData() {
        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        //获取用户登陆信息
        mEmceeInfo = (VodBean.DataBean.InfoBean.DataBeanb) bundle.getSerializable(USER_INFO);
        //主播昵称
        mTvLiveStartEmceeName.setText(mEmceeInfo.getUser_nicename());
        mTvLiveNumber.setText("ID号:" + mEmceeInfo.getId() + "");
        //初始化直播播放器参数配置
        initLive();
        mIvLiveEmceeHead.setAvatarUrl(mEmceeInfo.getAvatar());
    }


    protected Handler mHandler;

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
        String mrl = mEmceeInfo.getVideo_url();//getIntent().getStringExtra("path");
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
                ApiUtils.checkIsSuccess(response, VodVideoPlayerActivity.this);
                if (mFollowEmcee.getText().toString().equals(getResources().getString(R.string.follow))) {
                    mFollowEmcee.setText(getResources().getString(R.string.alreadyfollow));
                } else {
                    mFollowEmcee.setText(getResources().getString(R.string.follow));
                }

            }
        };
        PhoneLiveApi.showFollow(uid, touid, AppContext.getInstance().getToken(), callback);
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    //直播结束判断当前主播是否关注过
    private void showEndIsFollowEmcee() {

        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, VodVideoPlayerActivity.this);//0：未关注1:关注
                if (res != null) {
                    if (res.equals("0")) {
                        mFollowEmcee.setText(getString(R.string.follow));
                    } else {
                        mFollowEmcee.setText(getString(R.string.alreadyfollow));
                    }
                }

            }
        };

        PhoneLiveApi.getIsFollow(Integer.parseInt(mEmceeInfo.getId()), Integer.parseInt(mEmceeInfo.getId()), callback);//判断当前主播是否已经关注
    }


    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            ksyMediaPlayer.start();
            //直播开始
            if (null != mLoadingView) {
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
        /*    mButtonMenuFrame.setVisibility(View.GONE);
            mlayout.setVisibility(View.VISIBLE);*/
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
                if (ksyMediaPlayer != null) {
                    ksyMediaPlayer.release();
                    ksyMediaPlayer = null;
                }
                if (null != mHandler) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (mQosThread != null) {
                    mQosThread.stopThread();
                    mQosThread = null;
                }
            }
        });
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //dealTouchEvent(v, event);
            return false;
        }
    };


    private final Callback mSurfaceCallback = new Callback() {
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
  @OnClick({ R.id.iv_live_back})
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
          case R.id.iv_live_back:
              finish();
              break;
      }
    }


    private class UIHandler extends Handler {

        VodVideoPlayerActivity mActivity;

        public UIHandler(VodVideoPlayerActivity activty) {
            mActivity = activty;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEKBAR:
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
