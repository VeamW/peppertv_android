package com.weilian.phonelive.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksy.recordlib.service.core.KSYStreamer;
import com.ksy.recordlib.service.core.KSYStreamerConfig;
import com.ksy.recordlib.service.streamer.OnStatusListener;
import com.ksy.recordlib.service.streamer.RecorderConstants;
import com.ksy.recordlib.service.util.audio.OnProgressListener;
import com.ksy.recordlib.service.view.CameraGLSurfaceView;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.UserListAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.ShowLiveActivityBase;
import com.weilian.phonelive.bean.ChatBean;
import com.weilian.phonelive.bean.SendGiftBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.fragment.SearchMusicDialogFragment;
import com.weilian.phonelive.interf.ChatServerInterface;
import com.weilian.phonelive.interf.DialogInterface;
import com.weilian.phonelive.ui.dialog.LiveCommon;
import com.weilian.phonelive.ui.other.ChatServer;
import com.weilian.phonelive.utils.DialogHelp;
import com.weilian.phonelive.utils.InputMethodUtils;
import com.weilian.phonelive.utils.MD5;
import com.weilian.phonelive.utils.ShareUtils;
import com.weilian.phonelive.utils.ThreadManager;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.viewpagerfragment.PrivateChatCorePagerDialogFragment;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.music.DefaultLrcBuilder;
import com.weilian.phonelive.widget.music.ILrcBuilder;
import com.weilian.phonelive.widget.music.LrcRow;
import com.weilian.phonelive.widget.music.LrcView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 直播页面
 * 本页面包括点歌 分享 直播 聊天 僵尸粉丝 管理 点亮 歌词等功能详细参照每个方法的注释
 * 本页面继承基类和观看直播属于同一父类
 */
public class StartLiveActivity extends ShowLiveActivityBase implements SearchMusicDialogFragment.SearchMusicFragmentInterface {

    //直播结束房间人数
    @InjectView(R.id.tv_live_end_num)
    TextView mTvLiveEndUserNum;

    //填写直播标题
    @InjectView(R.id.et_start_live_title)
    EditText mStartLiveTitle;

    //开始直播遮罩层
/*    @InjectView(R.id.rl_start_live_bg)
    RelativeLayout mStartLiveBg;*/

    //渲染视频
    @InjectView(R.id.camera_preview)
    CameraGLSurfaceView mCameraPreview;

    //开始直播btn
    @InjectView(R.id.btn_start_live)
    Button mStartLive;

    //歌词显示控件
    @InjectView(R.id.lcv_live_start)
    LrcView mLrcView;

    //歌词显示控件
    @InjectView(R.id.rl_live_music)
    LinearLayout mViewShowLiveMusicLrc;

    //结束直播收获映票数量
    @InjectView(R.id.tv_live_end_yp_num)
    TextView mTvLiveEndYpNum;
    @InjectView(R.id.iv_live_emcee_head)
    AvatarView mHead;


    //直播结束映票数量
    private int mLiveEndYpNum;

    private Timer mTimer;

    private TimerTask mTask;

    //是否开启直播
    private boolean IS_START_LIVE = true;

    private KSYStreamer mStreamer;

    private final static String TAG = "StartLiveActivity";

    private boolean mBeauty = false;

    //分享模式 7为不分享任何平台
    private int shareType = 7;

    private MediaPlayer mPlayer;

    private int mPlayTimerDuration = 1000;

    //如果分享回调未执行标记
    private int mark = 0;

    private int requestZombieFansNum = 0;


    @Override
    protected void onStart() {
        super.onStart();
        if (mark == 1) {
            readyStart();
        }
        mark += 1;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("直播"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        mWl.acquire();
        if (mStreamer != null) {
            mStreamer.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("直播"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
        mWl.release();
        if (mStreamer != null) {
            mStreamer.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        mChatServer.close();
        if (mStreamer != null) {
            mStreamer.stopStream();
            stopLrcPlay();
            mStreamer.stopMixMusic();
            mStreamer.onDestroy();
        }
        //解除广播
        try {
            unregisterReceiver(broadCastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_show;
    }

    @Override
    public void initView() {
        super.initView();
        if (null == mButtonMenu) return;
        mButtonMenu.setVisibility(View.GONE);
        if (null == mEmceeHead) return;
        mEmceeHead.setEnabled(false);
        //默认新浪微博share
//        ImageView mSinnaWeiBoShare = (ImageView) findViewById(R.id.iv_live_share_weibo);
//        ImageView mBack = (ImageView) findViewById(R.id.iv_startlive_back);   //返回按钮
//        LinearLayout lBack = (LinearLayout) findViewById(R.id.ll_startlive_back);
        /*mSinnaWeiBoShare.post(new Runnable() {
            @Override
            public void run() {
                startLiveShare(mSinnaWeiBoShare,0);
                shareType = 0 == shareType?7:0;
            }
        });*/

        /**
         * 修改
         */
        if (null == mHead) return;
        mHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
       /* lBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartLiveActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });*/
       /* mSinnaWeiBoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v, 0);
                shareType = 0 == shareType ? 7 : 0;
            }
        });
        findViewById(R.id.iv_live_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v, 1);
                shareType = 1 == shareType ? 7 : 1;
            }
        });
        findViewById(R.id.iv_live_share_timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v, 2);
                shareType = 2 == shareType ? 7 : 2;
            }
        });

        findViewById(R.id.iv_live_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v, 3);
                shareType = 3 == shareType ? 7 : 3;
            }
        });
        findViewById(R.id.iv_live_share_qqzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v, 4);
                shareType = 4 == shareType ? 7 : 4;
            }
        });*/

        if (null == mRoot) return;
        //防止聊天软键盘挤压屏幕
        mRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > oldBottom && InputMethodUtils.isShowSoft(StartLiveActivity.this)) {
                    //mCameraPreview.setBottom(mScreenHeight);
                    hideEditText();
                }
            }
        });
        InputMethodUtils.closeSoftKeyboard(this);
    }

    /**
     * @dw 当每个聊天被点击显示该用户详细信息弹窗
     */
    public void chatListItemClick(ChatBean chat) {
        if (chat.getId() == mUser.getId()) {
            LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, chat);
        } else {
            LiveCommon.showUserInfoDialogEmcee(StartLiveActivity.this, chat, mUser, mChatServer);
        }
    }

    /**
     * @param v    点击按钮
     * @param type 分享平台
     * @dw 开始直播分享
     */
    private void startLiveShare(View v, int type) {
        String titleStr = "";
        if (type == shareType) {
            String titlesClose[] = getResources().getStringArray(R.array.live_start_share_close);
            titleStr = titlesClose[type];
        } else {
            String titlesOpen[] = getResources().getStringArray(R.array.live_start_share_open);
            titleStr = titlesOpen[type];
        }

        View popView = getLayoutInflater().inflate(R.layout.pop_view_share_start_live, null);
        TextView title = (TextView) popView.findViewById(R.id.tv_pop_share_start_live_prompt);
        title.setText(titleStr);
        PopupWindow pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        pop.setFocusable(false);
        pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.getWidth() / 2 - popView.getMeasuredWidth() / 2, location[1] - popView.getMeasuredHeight());

    }

    /**
     * @param num 倒数时间
     * @dw 开始直播倒数计时
     */
    private void startAnimation(final int num) {
        final TextView tvNum = new TextView(this);
        tvNum.setTextColor(getResources().getColor(R.color.white));
        tvNum.setText(num + "");
        tvNum.setTextSize(30);
        mRoot.addView(tvNum);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNum.setLayoutParams(params);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvNum, "scaleX", 5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvNum, "scaleY", 5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRoot == null) return;
                mRoot.removeView(tvNum);
                if (num == 1) {
                    mStreamer.startStream();
                    mStreamer.setEnableReverb(true);
                    return;
                }
                startAnimation(num == 3 ? 2 : 1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();

    }


    @Override
    public void initData() {
        super.initData();
        mHandler = new Handler();
        PowerManager mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWl = mPm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        mUser = AppContext.getInstance().getLoginUser();
        mRoomNum = mUser.getId();
        if (mTvLiveNumber != null) {
            //主播id
            mTvLiveNumber.setText("ID号:" + mUser.getId());
        }

        //主播昵称
        mTvEmceeName.setText(mUser.getUser_nicename());
        //封面
      /* ((LoadUrlImageView) findViewById(R.id.iv_live_start_cover)).setImageLoadUrl(mUser.getAvatar());

        ((AvatarView) findViewById(R.id.av_live_mask_emcee_head)).setAvatarUrl(mUser.getAvatar());*/
        //连接聊天服务器
        initChatConnection();
        initLivePlay();
        //获取僵尸粉丝
        getZombieFans();

//        createRoom();

        if (mCameraPreview != null) mCameraPreview.setVisibility(View.VISIBLE);
        if (mButtonMenu != null) mButtonMenu.setVisibility(View.VISIBLE);
       /* o*/

//        readyStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppManager.getAppManager().addActivity(this);
    }

    /**
     * @dw 准备直播
     * 弹框提示输入标题！
     */
    private void readyStart() {
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入标题(不输使用默认标题)").setView(inputServer).setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    //避免开播没有进入分享页面,然后点击其他页面后返回当前页面重复执行start方法
                mark += 1;
                //请求服务端存储开播纪录
                String title = "美食家";
                PhoneLiveApi.createLive(mUser.getId(), title,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                                AppContext.showToastAppMsg(StartLiveActivity.this, "开启直播失败,请退出重试- -!");
                                mStartLive.setVisibility(View.GONE);
                            }
                            @Override
                            public void onResponse(String s) {
                                String res = ApiUtils.checkIsSuccess(s, StartLiveActivity.this);
                                if (res != null) {
//                            mCameraPreview.setVisibility(View.VISIBLE);
                                    //初始化直播
//                            mButtonMenu.setVisibility(View.VISIBLE);
//                            mStartLive.setVisibility(View.VISIBLE);
                                    createRoom();
                                }
                            }
                        }, mUser.getToken());
            }
        }).setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                //避免开播没有进入分享页面,然后点击其他页面后返回当前页面重复执行start方法
                mark += 1;
                //请求服务端存储开播纪录
                String title = "美食家";
                if (inputServer != null && !inputServer.getText().toString().replace(" ", "").equals("")) {
                    title = inputServer.getText().toString().replace("\n", "");
                }
                PhoneLiveApi.createLive(mUser.getId(), title,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                                AppContext.showToastAppMsg(StartLiveActivity.this, "开启直播失败,请退出重试- -!");
                                mStartLive.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResponse(String s) {
                                String res = ApiUtils.checkIsSuccess(s, StartLiveActivity.this);
                                if (res != null) {
//                            mCameraPreview.setVisibility(View.VISIBLE);
                                    //初始化直播
//                            mButtonMenu.setVisibility(View.VISIBLE);
//                            mStartLive.setVisibility(View.VISIBLE);
                                    createRoom();
                                }
                            }
                        }, mUser.getToken());
            }
        });

        builder.show();
    }

    /**
     * @param fileName 歌词文件目录
     * @dw 获取歌词字符串
     */
    public String getFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputReader = new InputStreamReader(fileInputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                Result += line + "\r\n";
            }
            fileInputStream.close();
            inputReader.close();
            bufReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @dw 获取僵尸粉丝
     * 每次获取20个僵尸粉丝,最多为220位僵尸粉
     */
    private void getZombieFans() {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (requestZombieFansNum > 10) {
                            mHandler.removeCallbacks(this);
                            return;
                        }
                        mHandler.postDelayed(this, 2000);
                        mChatServer.getZombieFans();
                        requestZombieFansNum++;
                    }
                }, 2000);
            }
        });

    }

    /**
     * @dw 初始化连接聊天服务器
     */
    private void initChatConnection() {
        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), this, mUser.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     * @dw 初始化直播播放器
     */
    private void initLivePlay() {
        //直播参数配置start
        KSYStreamerConfig.Builder builder = new KSYStreamerConfig.Builder();
        String url = AppConfig.RTMP_PUSH_URL + mUser.getId();
        builder.setmUrl(url);
        String timeSec = String.valueOf(System.currentTimeMillis() / 1000);
        String skSign = MD5.md5sum("s6539d4f91a16ed6ba27db3ea863b943" + timeSec);
        builder.setAppId("QYA0ABFDF283A98F4837");
        builder.setAccessKey("a7164872c44510ae32ffbe7c3b589e35");
        builder.setSecretKeySign(skSign);
        builder.setTimeSecond(timeSec);
        //builder.setDefaultLandscape(false);
        mStreamer = new KSYStreamer(this);
        mStreamer.setConfig(builder.build());
        mStreamer.setOnStatusListener(mOnErrorListener);
        //mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);
        mStreamer.setDisplayPreview(mCameraPreview);
        //直播参数配置end
        mEmceeHead.setAvatarUrl(mUser.getAvatar());
    }

    //分享pop弹窗
    private void showSharePopWindow(View v) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_view_share, null);
        PopupWindow p = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        p.setBackgroundDrawable(new BitmapDrawable());
        p.setOutsideTouchable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        p.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.getWidth() / 2 - view.getMeasuredWidth() / 2, location[1] - view.getMeasuredHeight());

    }


    /**
     * 分享
     *
     * @param v
     */
    //分享操作
    public void share(View v) {
        ShareUtils.share(this, v.getId(), mUser);
    }

    @OnClick({R.id.iv_live_shar, R.id.btn_live_end_music, R.id.iv_live_music, R.id.iv_live_meiyan, R.id.iv_live_switch_camera, R.id.camera_preview, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.btn_start_live, R.id.iv_live_chat, R.id.bt_send_chat, R.id.btn_back_index})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.tv_live_num:
                LiveCommon.showOwnInfoDialogEmcee(this, mUser);
                break;
            case R.id.tv_live_start_emcee_name:
                LiveCommon.showOwnInfoDialogEmcee(this, mUser);
                break;*/
            /*case R.id.iv_live_emcee_head:
                LiveCommon.showOwnInfoDialogEmcee(this, mUser);
                break;*/
            /*case R.id.ll_live_room_info:
                LiveCommon.showOwnInfoDialogEmcee(this, mUser);
                break;*/
            case R.id.iv_live_shar:
                ImageView imageView = (ImageView) findViewById(R.id.iv_live_shar);
                if (imageView == null) {
                    AppContext.showToast("请稍后");
                    return;
                }

                showSharePopWindow(imageView);
                break;
            case R.id.iv_live_music:
                showSearchMusicDialog();
                //UIHelper.showSearchMusic(this);
                break;
            case R.id.iv_live_meiyan:
                if (!mBeauty) {
                    mBeauty = true;
                    mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);
                } else {
                    mBeauty = false;
                    mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DISABLE);
                }
                break;
            case R.id.iv_live_switch_camera://摄像头反转
                mStreamer.switchCamera();
                break;
            case R.id.camera_preview:
                hideEditText();
                break;
            case R.id.iv_live_privatechat://私信
                showPrivateChat();
                //UIHelper.showPrivateChatSimple(StartLiveActivity.this,mUser.getId());
                break;
            case R.id.iv_live_back:
                clickBack();
                break;
            case R.id.ll_yp_labe:
                showDedicateOrder();
                break;
            case R.id.btn_start_live://创建房间
                //请求服务端存储记录

                if (mStartLive != null) mStartLive.setVisibility(View.GONE);
                if (mStartLiveTitle != null) mStartLiveTitle.setVisibility(View.GONE);
                readyStart();
//                createRoom();
                break;
            case R.id.iv_live_chat://chat gone or visble
                showEditText();
                break;
            case R.id.bt_send_chat://send chat
                sendChat();
                break;
            case R.id.btn_back_index:
                finish();
                break;
            case R.id.btn_live_end_music:
                stopMusic();
                break;
        }
    }

    //打开映票排行
    private void showDedicateOrder() {
        DialogHelp.showPromptDialog(getLayoutInflater(), StartLiveActivity.this, "正在直播点击排行会影响直播,是否继续", new DialogInterface() {

            @Override
            public void cancelDialog(View v, Dialog d) {
                d.dismiss();
            }

            @Override
            public void determineDialog(View v, Dialog d) {
                d.dismiss();
                UIHelper.showDedicateOrderActivity(StartLiveActivity.this, mUser.getId());
            }
        });
    }

    /**
     * @dw 显示私信页面
     */
    private void showPrivateChat() {
        mIvNewPrivateChat.setVisibility(View.GONE);
        PrivateChatCorePagerDialogFragment privateChatFragment = new PrivateChatCorePagerDialogFragment();
        privateChatFragment.setStyle(PrivateChatCorePagerDialogFragment.STYLE_NO_TITLE, 0);
        privateChatFragment.show(getSupportFragmentManager(), "PrivateChatCorePagerDialogFragment");
    }

    /**
     * @dw 显示搜索音乐弹窗
     */
    private void showSearchMusicDialog() {
        SearchMusicDialogFragment musicFragment = new SearchMusicDialogFragment();
        musicFragment.setStyle(SearchMusicDialogFragment.STYLE_NO_TITLE, 0);
        musicFragment.show(getSupportFragmentManager(), "SearchMusicDialogFragment");
    }

    /**
     * @dw 创建直播房间
     * 请求服务端添加直播记录,分享直播
     */
    private void createRoom() {
        mEmceeHead.setEnabled(true);
        mRlbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
        mEmceeHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
        mLlinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
        mLiveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
        mTvEmceeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUser);
            }
        });
    /*    if (shareType != 7) {
            ShareUtils.share(this, shareType, mUser,
                    new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            readyStart();
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            readyStart();
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            readyStart();
                        }
                    });
        } else {
            readyStart();
        }*/
        InputMethodUtils.closeSoftKeyboard(this);
        mStartLive.setEnabled(false);
        mStartLive.setTextColor(getResources().getColor(R.color.white));
        startLive();
    }


    /**
     * @param toUser 被@用户
     * @dw @艾特用户
     */
    @Override
    public void dialogReply(UserBean toUser) {
        ACE_TEX_TO_USER = toUser.getId();
        mChatInput.setText("@" + toUser.getUser_nicename() + " ");
        mChatInput.setSelection(mChatInput.getText().length());
        showEditText();
    }

    //当主播选中了某一首歌,开始播放
    @Override
    public void onSelectMusic(Intent data) {
        startMusic(data);
    }

    //socket客户端事件监听处理
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

        //用户列表
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
                        if (mUser.getId() == mUsers.valueAt(position).getId()) {
                            LiveCommon.showOwnInfoDialogEmcee(StartLiveActivity.this, mUsers.valueAt(position));
                        } else {
                            LiveCommon.showUserInfoDialogEmcee(StartLiveActivity.this, mUsers.valueAt(position), mUser, mChatServer);
                        }
                    }
                });

            }
        }


        //用户状态改变
        @Override
        public void onUserStateChange(final UserBean user, final boolean state) {//用户状态改变
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        //系统通知
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {//后台关闭直播
                        DialogHelp.showPromptDialog(getLayoutInflater(), StartLiveActivity.this, "直播内容涉嫌违规", new DialogInterface() {

                            @Override
                            public void cancelDialog(View v, Dialog d) {

                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                d.dismiss();
                            }
                        });
                        stopLive();
                    }
                }
            });

        }

        //送礼物
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {//送礼物展示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLiveEndYpNum += giftInfo.getTotalcoin();
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        //设置管理员
        @Override
        public void setManage(final JSONObject contentJson, final ChatBean chat) {//设置为管理员
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (contentJson.getInt("touid") == mUser.getId()) {
                            AppContext.showToastAppMsg(StartLiveActivity.this, "您已被设为管理员");
                        }
                        addChatMessage(chat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

        //特权动作
        @Override
        public void onPrivilegeAction(final ChatBean c, final JSONObject contentJson) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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

        //添加僵尸粉
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
                    AppContext.showToastAppMsg(StartLiveActivity.this, "服务器连接错误");
                }
            });
        }
    }

    private void fillUI() {
        mLvChatList.setAdapter(mChatListAdapter);
        mStartLive.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*switch (resultCode){
            case 1:{//歌曲选择完毕开始播放
                startMusic(data);
                break;
            }
        }*/

        super.onActivityResult(requestCode, resultCode, data);
    }

    //播放音乐
    private void startMusic(Intent data) {
        if (null != mPlayer) {
            mPlayer.stop();
        }
        mStreamer.stopMixMusic();
        mViewShowLiveMusicLrc.setVisibility(View.VISIBLE);
        String musicPath = data.getStringExtra("filepath");
        mStreamer.startMixMusic(musicPath, onProgressListener, true);
        mStreamer.setHeadsetPlugged(true);
        String lrcStr = getFromFile(musicPath.substring(0, musicPath.length() - 3) + "lrc");

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrcStr);

        mLrcView.setLrc(rows);
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(musicPath);
            mPlayer.setLooping(true);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new LrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPlayTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //停止歌词滚动
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //停止播放音乐
    private void stopMusic() {
        if (null != mPlayer && null != mStreamer && mViewShowLiveMusicLrc != null) {
            mPlayer.stop();
            mPlayer = null;
            mStreamer.stopMixMusic();
            mViewShowLiveMusicLrc.setVisibility(View.GONE);
        }
    }

    class LrcTask extends TimerTask {

        long beginTime = -1;

        @Override
        public void run() {
            if (beginTime == -1) {
                beginTime = System.currentTimeMillis();
            }
            if (null != mPlayer) {
                final long timePassed = mPlayer.getCurrentPosition();
                StartLiveActivity.this.runOnUiThread(new Runnable() {

                    public void run() {
                        mLrcView.seekLrcToTime(timePassed);
                    }
                });
            }


        }
    }


    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!IS_START_LIVE) /*|| ((mStartLiveBg.getVisibility() + 7) == 7)*/) {
                /*Intent intent = new Intent(StartLiveActivity.this, MainActivity.class);
                startActivity(intent);*/
                finish();
                return super.onKeyDown(keyCode, event);
            } else {
                clickBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //主播点击退出
    private void clickBack() {
        DialogHelp.showDialog(getLayoutInflater(), this, getString(R.string.iscloselive), new DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {
                d.dismiss();
            }

            @Override
            public void determineDialog(View v, Dialog d) {
                d.dismiss();
                stopLive();
            }
        });
    }

    //关闭直播
    private void stopLive() {
        IS_START_LIVE = false;
        if (null == mLvChatList) return;
        mLvChatList.setVisibility(View.GONE);
        PhoneLiveApi.closeLive(mUser.getId(), mUser.getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(StartLiveActivity.this, "关闭直播失败");
            }

            @Override
            public void onResponse(String response) {
                mChatServer.closeLive();
            }
        });
        //停止直播
        mStreamer.stopStream();
        //停止播放音乐
        stopMusic();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mLayoutLiveStop.setVisibility(View.VISIBLE);
        mLiveChatEdit.setVisibility(View.VISIBLE);
        mTvLiveEndUserNum.setText(ChatServer.LIVEUSERNUMS + "人观看");
        mTvLiveEndYpNum.setText("共收获:" + mLiveEndYpNum + "辣度");
        mButtonMenuFrame.setVisibility(View.GONE);
        mStartLive.setVisibility(View.GONE);

    }

    //开始直播
    private void startLive() {
        startAnimation(3);
        mChatServer.connectSocketServer(mUser, mUser.getId());//连接到socket服务端
        fillUI();
    }

    public OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onMusicProgress(long l, long l1) {

        }

        @Override
        public void onMusicStopped() {

        }

        @Override
        public void onMusicError(int i) {

        }
    };


    public OnStatusListener mOnErrorListener = new OnStatusListener() {
        @Override
        public void onStatus(int what, int arg1, int arg2, String msg) {
            // msg may be null
            switch (what) {
                case RecorderConstants.KSYVIDEO_OPEN_STREAM_SUCC:
                    // 推流成功
                    Log.d("TAG", "KSYVIDEO_OPEN_STREAM_SUCC");
                    mHandler.obtainMessage(what, "start stream succ")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_THRESHOLD:
                    //认证失败且超过编码上限
                    Log.d(TAG, "KSYVIDEO_ENCODED_FRAME_THRESHOLD");
                    mHandler.obtainMessage(what, "KSYVIDEO_ENCODED_FRAME_THRESHOLD")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_AUTH_FAILED:
                    //认证失败
                    Log.d(TAG, "KSYVIDEO_AUTH_ERROR");
                    break;
                case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_FAILED:
                    //编码失败
                    Log.e(TAG, "---------KSYVIDEO_ENCODED_FRAMES_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_FRAME_DATA_SEND_SLOW:
                    //网络状况不佳
                    if (mHandler != null) {
                        mHandler.obtainMessage(what, "网太搓~").sendToTarget();
                    }
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_DROP:
                case RecorderConstants.KSYVIDEO_EST_BW_RAISE:
                case RecorderConstants.KSYVIDEO_AUDIO_INIT_FAILED:

                    break;
                case RecorderConstants.KSYVIDEO_INIT_DONE:
                    mHandler.obtainMessage(what, "init done")
                            .sendToTarget();
                    break;
                default:
                    if (msg != null) {
                        // 可以在这里处理断网重连的逻辑
                    }
                    if (mHandler != null) {
                        mHandler.obtainMessage(what, msg).sendToTarget();
                    }
            }
        }

    };


}
