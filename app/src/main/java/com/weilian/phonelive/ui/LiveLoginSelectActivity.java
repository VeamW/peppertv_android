package com.weilian.phonelive.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.LoginUtils;
import com.weilian.phonelive.utils.TDevice;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

public class LiveLoginSelectActivity extends BaseActivity implements PlatformActionListener {
    private String[] names = {QQ.NAME,Wechat.NAME, SinaWeibo.NAME};
    private String type;
    @InjectView(R.id.iv_select_login_bg)
    ImageView mBg;
    private Bitmap bmp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_login;
    }


    @Override
    public void initView() {
        getSupportActionBar().hide();
        bmp = null;
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.live_show_login_bg);

        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.live_show_login_bg, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, (int)TDevice.getScreenWidth(), (int)TDevice.getScreenHeight());
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        mBg.setImageBitmap(bmp);

    }

    @Override
    public void initData() {
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    @OnClick({R.id.iv_qqlogin,R.id.iv_sllogin,R.id.iv_wxlogin,R.id.iv_mblogin})
    public void onClick(View v) {
        int id = v.getId();
        //初始化shareSDK
        ShareSDK.initSDK(this);
        switch (id){
            case R.id.iv_qqlogin:
                type = "qq";
                otherLogin(names[0]);
                break;
            case R.id.iv_sllogin:
                type = "sina";
                AppContext.showToastAppMsg(this,"微博");
                otherLogin(names[2]);
                break;
            case R.id.iv_wxlogin:
                type = "wx";
                AppContext.showToastAppMsg(this,"微信");
                otherLogin(names[1]);
                break;
            case R.id.iv_mblogin:
                UIHelper.showMobilLogin(this);
                //finish();
                break;
        }
    }
    private void mobilLogin(){

    }
    private void otherLogin(String name){
        //AppContext.showToastAppMsg(LiveLoginSelectActivity.this,"正在准备登录..");
        Platform other = ShareSDK.getPlatform(name);
        other.showUser(null);//执行登录，登录后在回调里面获取用户资料
        other.SSOSetting(false);  //设置false表示使用SSO授权方式
        other.setPlatformActionListener(this);
        other.authorize();
        other.removeAccount(true);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppContext.showToastAppMsg(LiveLoginSelectActivity.this,"授权成功正在登录....");
            }
        });

        //用户资源都保存到res
        //通过打印res数据看看有哪些数据是你想要的
        if (i == Platform.ACTION_USER_INFOR) {
            //showWaitDialog("正在登录...");
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            PhoneLiveApi.otherLogin(type,platDB,callback);
        }

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        AppContext.showToastAppMsg(LiveLoginSelectActivity.this,"授权登录失败");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        AppContext.showToastAppMsg(LiveLoginSelectActivity.this,"授权已取消");
    }
    StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(LiveLoginSelectActivity.this,"登录失败");
            //hideWaitDialog();
        }

        @Override
        public void onResponse(String response) {
            //hideWaitDialog();
            String requestRes = ApiUtils.checkIsSuccess(response,LiveLoginSelectActivity.this);
            if(requestRes != null){
                Gson gson = new Gson();
                UserBean user = gson.fromJson(requestRes, UserBean.class);
                //友盟登录统计
                MobclickAgent.onProfileSignIn(type,String.valueOf(user.getId()));
                //保存用户信息
                AppContext.getInstance().saveUserInfo(user);
                UIHelper.showMainActivity(LiveLoginSelectActivity.this);
                LoginUtils.getInstance().OtherInit(LiveLoginSelectActivity.this);
                AppManager.getAppManager().finishAllActivity();
                System.gc();
            }
        }
    };
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bmp!=null)
        bmp.recycle();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("登录选择"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("登录选择"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
