package com.weilian.phonelive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.hyphenate.chat.EMClient;
import com.weilian.phonelive.base.BaseApplication;
import com.weilian.phonelive.ui.LiveLoginSelectActivity;
import com.weilian.phonelive.ui.MainActivity;
import com.weilian.phonelive.utils.InputMethodUtils;
import com.weilian.phonelive.utils.TDevice;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * 应用启动界面
 */
public class AppStart extends Activity {
    private View view = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAPP(BaseApplication
                .context());
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cacheVersion = PreferenceHelper.readInt(BaseApplication.context(), "first_install",
                "first_install", -1);
        int currentVersion = TDevice.getVersionCode();
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(BaseApplication.context(), "first_install", "first_install",
                    currentVersion);
            cleanImageCache();
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private void cleanImageCache() {
        final File folder = FileUtils.getSaveFolder("phoneLive/imagecache");
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
            }
        });
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        if (!AppContext.getInstance().isLogin()) {
            Intent intent = new Intent(this, LiveLoginSelectActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    int checkAPP(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            int hashcode = sign.hashCode();
            Log.i("myhashcode", "hashCode : " + hashcode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解决有inputMethodManager带来的leak问题
        InputMethodUtils.fixInputMethodManagerLeak(this);
    }


}
