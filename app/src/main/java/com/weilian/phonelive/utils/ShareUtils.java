package com.weilian.phonelive.utils;

import android.app.Activity;
import android.content.Context;

import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.bean.UserBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ShareUtils {

    public static String url = "http://yy.yunbaozhibo.com/public/wxshare/Phone/index?roomnum=";

    public static void share(final Activity context, final int id, final UserBean user) {
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    String res = ApiUtils.checkIsSuccess(response);
                    if (res == null) {
                        return;
                    }
                    JSONObject object = new JSONObject(res);
                    if (object == null) return;
                    url = object.getString("wx_siteurl");
                    switch (id) {
                        case R.id.ll_live_shar_qq:
                            share(context, 3, user, null);
                            break;
                        case R.id.ll_live_shar_pyq:
                            share(context, 2, user, null);
                            break;
                        case R.id.ll_live_shar_qqzone:
                            share(context, 4, user, null);
                            break;
                        case R.id.ll_live_shar_sinna:
                            share(context, 0, user, null);
                            break;
                        case R.id.ll_live_shar_wechat:
                            share(context, 1, user, null);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void share(Context context, int index, UserBean user, PlatformActionListener listener) {
        String content = user.getUser_nicename() + "正在直播,快来一起看吧~";
        String[] names = new String[]{SinaWeibo.NAME, Wechat.NAME, WechatMoments.NAME, QQ.NAME, QZone.NAME};
        switch (index) {
            case 0: {
                share(context, names[0], false, content, user, listener);
                break;
            }
            case 1: {
                share(context, names[1], false, content, user, listener);
                break;
            }
            case 2: {
                share(context, names[2], false, content, user, listener);
                break;
            }
            case 3: {
                share(context, names[3], false, content, user, listener);
                break;
            }
            case 4: {
                share(context, names[4], false, content, user, listener);
                break;
            }
        }
    }

    public static void share(Context context, String name, boolean showContentEdit, String content, UserBean user, PlatformActionListener listener) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(true);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(name);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(context.getString(R.string.shartitle));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用

        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        oks.setImageUrl(user.getAvatar());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用


       /* if (name.equals(Wechat.NAME) || name.equals(WechatMoments.NAME)) {
            oks.setUrl(url + user.getId());
            oks.setSiteUrl(url + user.getId());
            oks.setTitleUrl(url + user.getId());
        } else {
            oks.setUrl("https://www.pgyer.com");
            oks.setSiteUrl("https://www.pgyer.com");
            oks.setTitleUrl("https://www.pgyer.com");
        }*/

        oks.setUrl(url+"Phone/index?roomnum=" + user.getId());
        oks.setSiteUrl(url+"Phone/index?roomnum=" + user.getId());
        oks.setTitleUrl(url+"Phone/index?roomnum=" + user.getId());

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment(context.getString(R.string.shartitle));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        oks.setCallback(listener);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用

        // 启动分享GUI
        oks.show(context);
    }
}
