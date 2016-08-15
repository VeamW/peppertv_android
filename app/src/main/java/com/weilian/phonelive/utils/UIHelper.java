package com.weilian.phonelive.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.SimpleBackPage;
import com.weilian.phonelive.ui.AttentionActivity;
import com.weilian.phonelive.em.ChangInfo;
import com.weilian.phonelive.ui.ChangeSexActivity;
import com.weilian.phonelive.ui.DedicateOrderActivity;
import com.weilian.phonelive.ui.EditInfoActivity;
import com.weilian.phonelive.ui.FansActivity;
import com.weilian.phonelive.ui.ActionBarSimpleBackActivity;
import com.weilian.phonelive.ui.HomePageActivity;
import com.weilian.phonelive.ui.LiveLoginSelectActivity;
import com.weilian.phonelive.ui.LiveRecordActivity;
import com.weilian.phonelive.fragment.ManageListDialogFragment;
import com.weilian.phonelive.ui.PhoneLoginActivity;
import com.weilian.phonelive.ui.MainActivity;
import com.weilian.phonelive.ui.MyDiamondsActivity;
import com.weilian.phonelive.ui.MyInfoDetailActivity;
import com.weilian.phonelive.ui.MyLevelActivity;
import com.weilian.phonelive.ui.ProfitActivity;
import com.weilian.phonelive.ui.SelectAvatarActivity;
import com.weilian.phonelive.ui.SettingActivity;
import com.weilian.phonelive.ui.SimpleBackActivity;
import com.weilian.phonelive.ui.StartLiveActivity;
import com.weilian.phonelive.ui.VideoPlayerActivity;
import com.weilian.phonelive.ui.WebViewActivity;

/**
 * 界面帮助类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 * 
 */

public class UIHelper {
    /**
     * 发送通知广播
     *
     * @param context
     */
    public static void sendBroadcastForNotice(Context context) {
        /*Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
        context.sendBroadcast(intent);*/
    }
    /**
     * 手机登录
     *
     * @param context
     */

    public static void showMobilLogin(Context context) {
        Intent intent = new Intent(context, PhoneLoginActivity.class);
        context.startActivity(intent);
    }
    /**
     * 登陆选择
     *
     * @param context
     */
    public static void showLoginSelectActivity(Context context) {
        Intent intent = new Intent(context, LiveLoginSelectActivity.class);
        context.startActivity(intent);

    }

    /**
     * 首页
     *
     * @param context
     */
    public static void showMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }
    /**
     * 我的详细资料
     *
     * @param context
     */
    public static void showMyInfoDetailActivity(Context context) {
        Intent intent = new Intent(context, MyInfoDetailActivity.class);
        context.startActivity(intent);
    }
    /**
     * 编辑资料
     *
     * @param context
     */
    public static void showEditInfoActivity(MyInfoDetailActivity context, String action,
                                            String prompt, String defaultStr, ChangInfo changInfo) {
        Intent intent = new Intent(context, EditInfoActivity.class);
        intent.putExtra(EditInfoActivity.EDITACTION,action);
        intent.putExtra(EditInfoActivity.EDITDEFAULT,defaultStr);
        intent.putExtra(EditInfoActivity.EDITPROMP,prompt);
        intent.putExtra(EditInfoActivity.EDITKEY, changInfo.getAction());
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    public static void showSelectAvatar(MyInfoDetailActivity context, String avatar) {
        Intent intent = new Intent(context, SelectAvatarActivity.class);
        intent.putExtra("uhead",avatar);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //showUrlRedirect(view.getContext(), url);
                return true;
            }
        };
    }

    /**
     * 我的等级
     *
     * @return
     */
    public static void showLevel(Context context, int loginUid) {
        Intent intent = new Intent(context, MyLevelActivity.class);
        intent.putExtra("USER_ID",String.valueOf(loginUid));
        context.startActivity(intent);
    }
    /**
     * 我的钻石
     *
     * @return
     */
    public static void showMyDiamonds(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MyDiamondsActivity.class);
        intent.putExtra("USERINFO",bundle);
        context.startActivity(intent);
    }
    /**
     * 我的收益
     *
     * @return
     */
    public static void showProfitActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ProfitActivity.class);
        intent.putExtra("USERINFO",bundle);
        context.startActivity(intent);
    }
    /**
     * 设置
     *
     * @return
     */
    public static void showSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
    /**
     * 看直播
     *
     * @return
     */
    public static void showLookLiveActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.USER_INFO,bundle);
        context.startActivity(intent);
    }
    /**
     * 直播
     *
     * @return
     */
    public static void showStartLiveActivity(Context context) {
        Intent intent = new Intent(context, StartLiveActivity.class);
        context.startActivity(intent);
    }
    /*
    * 其他用户个人信息
    * */
    public static void showHomePageActivity(Context context,int id) {
        Intent intent = new Intent(context, HomePageActivity.class);
        intent.putExtra("uid",id);
        context.startActivity(intent);
    }
    /*
    * 粉丝列表
    * */
    public static void showFansActivity(Context context, int uid) {
        Intent intent = new Intent(context, FansActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    /*
    * 关注列表
    * */
    public static void showAttentionActivity(Context context, int uid) {
        Intent intent = new Intent(context, AttentionActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //映票贡献榜
    public static void showDedicateOrderActivity(Context context, int uid) {

        Intent intent = new Intent(context, DedicateOrderActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //直播记录
    public static void showLiveRecordActivity(Context context, int uid) {
        Intent intent = new Intent(context, LiveRecordActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //私信页面
    public static void showPrivateChatSimple(Context context, int uid) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE.getValue());
        context.startActivity(intent);
    }
    //私信详情
    public static void showPrivateChatMessage(Context context, PrivateChatUserBean user) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("user",user);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE_MESSAGE.getValue());
        context.startActivity(intent);

    }
    //地区选择
    public static void showSelectArea(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.AREA_SELECT.getValue());
        context.startActivity(intent);
    }
    //搜索
    public static void showScreen(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.INDEX_SECREEN.getValue());
        context.startActivity(intent);
    }
    //打开网页
    public static void showWebView(Context context,String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("title",title);
        intent.putExtra("URL_INFO",bundle);
        context.startActivity(intent);
    }
    //黑名单
    public static void showBlackList(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_BLACK_LIST.getValue());
        context.startActivity(intent);
    }
    //推送管理
    public static void showPushManage(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_PUSH_MANAGE.getValue());
        context.startActivity(intent);
    }
    //搜索歌曲
    public static void showSearchMusic(Activity context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.LIVE_START_MUSIC.getValue());
        context.startActivityForResult(intent,1);
    }
    //管理员列表
    public static void shoManageListActivity(Context context) {
        Intent intent = new Intent(context,ManageListDialogFragment.class);
        context.startActivity(intent);
    }

    public static void showChangeSex(Context context, int sex) {
        Intent intent = new Intent(context, ChangeSexActivity.class);
        intent.putExtra("SEX",sex);
        context.startActivity(intent);

    }
}
