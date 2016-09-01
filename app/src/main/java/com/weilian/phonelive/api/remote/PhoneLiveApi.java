package com.weilian.phonelive.api.remote;

import android.annotation.TargetApi;
import android.os.Build;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.BasicApi;
import com.weilian.phonelive.bean.GiftBean;
import com.weilian.phonelive.bean.UserBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.LinkedHashMap;

import cn.sharesdk.framework.PlatformDb;

/**
 * 接口获取
 */
public class PhoneLiveApi {
    //最初
    //public final static String API_URL = "http://yy.yunbaozhibo.com/public/";
    //test.haodidao.cc   139.224.18.21

    //测试
//    public final static String API_URL = "http://139.224.18.21/public/";

    //正式
    public final static String API_URL = "http://test.haoyidao.cc/public/";

    /**
     * 登陆
     *
     * @param phone
     * @param code
     */
    public static void login(String phone, String code, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "user.userlogin")
                .addParams("user_login", phone)
                .addParams("code", code)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "user.userlogin");
        map.put("user_login", phone);
        map.put("code", code);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * 获取用户信息
     *
     * @param token    appkey
     * @param uid      用户id
     * @param callback 回调
     */
    public static void getMyUserInfo(int uid, String token, StringCallback callback) {
    /*    OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getBaseInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getBaseInfo");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * 获取其他用户信息
     *
     * @param uid      用户id
     * @param callback 回调
     */
    public static void getOtherUserInfo(int uid, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getUserInfo")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getUserInfo");
        map.put("uid", String.valueOf(uid));
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param key   参数
     * @param value 修改
     * @dw 修改用户信息
     */
    public static void saveInfo(String key, String value, int uid, String token, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.userUpdate")
                .addParams("field", key)
                .addParams("value", value)
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.userUpdate");
        map.put("field", key);
        map.put("value", value);
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 获取热门首页轮播
     */
    public static void getIndexHotRollpic(StringCallback callback) {
   /*     OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getSlide")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getSlide");
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 获取热门首页主播
     */
    public static void getIndexHotUserList(StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.searchArea")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.searchArea");
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid    当前用户id
     * @param showId 主播id
     * @param token  token
     * @dw 进入直播间初始化信息
     */
    public static void initRoomInfo(int uid, int showId, String token, String address, StringCallback callback) {
  /*      OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setNodejsInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("showid", String.valueOf(showId))
                .addParams("token", token)
                .addParams("city", address)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setNodejsInfo");
        map.put("uid", String.valueOf(uid));
        map.put("showid", String.valueOf(showId));
        map.put("token", token);
        map.put("city", address);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param showid 房间号码
     * @dw 获取用户列表
     */
    public static void getRoomUserList(int showid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getRedislist")
                .addParams("size", "0")
                .addParams("showid", String.valueOf(showid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getRedislist");
        map.put("size", "0");
        map.put("showid", String.valueOf(showid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param loginUid 主播id
     * @param title    开始直播标题
     * @param token
     * @dw 开始直播
     */
    public static void createLive(int loginUid, String title, StringCallback callback, String token) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.createRoom")
                .addParams("uid", String.valueOf(loginUid))
                .addParams("title", title)
                .addParams("city", AppContext.address)
                .addParams("province", AppContext.province)
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.createRoom");
        map.put("uid", String.valueOf(loginUid));
        map.put("title", title);
        map.put("city", AppContext.address);
        map.put("province", AppContext.province);
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param token 用户的token
     * @dw 关闭直播
     */
    public static void closeLive(int id, String token, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.stopRoom")
                .addParams("uid", String.valueOf(id))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.stopRoom");
        map.put("uid", String.valueOf(id));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param callback
     * @dw 获取礼物列表
     */
    public static void getGiftList(StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getGifts")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getGifts");
        BasicApi.doGet(map, callback);
    }

    /**
     * @param g           赠送礼物信息
     * @param mUser       用户信息
     * @param mNowRoomNum 房间号(主播id)
     * @dw 赠送礼物
     */
    public static void sendGift(UserBean mUser, GiftBean g, int mNowRoomNum, StringCallback callback) {
   /*     OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.sendGift")
                .addParams("token", mUser.getToken())
                .addParams("uid", String.valueOf(mUser.getId()))
                .addParams("touid", String.valueOf(mNowRoomNum))
                .addParams("giftid", String.valueOf(g.getId()))
                .addParams("giftcount", "1")
                .build()
                .execute(callback);*/

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.sendGift");
        map.put("token", mUser.getToken());
        map.put("uid", String.valueOf(mUser.getId()));
        map.put("touid", String.valueOf(mNowRoomNum));
        map.put("giftid", String.valueOf(g.getId()));
        map.put("giftcount", "1");
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid   其他用户id
     * @param ucuid 当前用户自己的id
     * @dw 获取其他用户信息
     */
    public static void getUserInfo(int uid, int ucuid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getPopup")
                .addParams("uid", String.valueOf(uid))
                .addParams("ucuid", String.valueOf(ucuid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getPopup");
        map.put("uid", String.valueOf(uid));
        map.put("ucuid", String.valueOf(ucuid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param touid 当前主播id\
     * @param uid   当前用户uid
     * @dw 判断是否关注
     */
    public static void getIsFollow(int uid, int touid, StringCallback callback) {
        /*OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.isAttention")
                .addParams("uid", String.valueOf(uid))
                .addParams("touid", String.valueOf(touid))
                .build()
                .execute(callback);
*/

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.isAttention");
        map.put("uid", String.valueOf(uid));
        map.put("touid", String.valueOf(touid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid   当前用户id
     * @param touid 关注用户id
     * @dw 关注
     */
    public static void showFollow(int uid, int touid, String token, StringCallback callback) {
   /*     OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setAttention")
                .addParams("uid", String.valueOf(uid))
                .addParams("showid", String.valueOf(touid))
                .addParams("token", token)
                .build()
                .execute(callback);*/

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setAttention");
        map.put("uid", String.valueOf(uid));
        map.put("showid", String.valueOf(touid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid 查询用户id
     * @dw 获取homepage中的用户信息
     */
    public static void getHomePageUInfo(int uid, int ucuid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getUserHome")
                .addParams("uid", String.valueOf(uid))
                .addParams("ucuid", String.valueOf(ucuid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getUserHome");
        map.put("uid", String.valueOf(uid));
        map.put("ucuid", String.valueOf(ucuid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param ucid 自己的id
     * @param uid  查询用户id
     * @dw 获取homepage用户的fans
     */
    public static void getFansList(int uid, int ucid, StringCallback callback) {
    /*    OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getFans")
                .addParams("uid", String.valueOf(uid))
                .addParams("ucuid", String.valueOf(ucid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getFans");
        map.put("uid", String.valueOf(uid));
        map.put("ucuid", String.valueOf(ucid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param ucid 自己的id
     * @param uid  查询用户id
     * @dw 获取homepage用户的关注用户列表
     */
    public static void getAttentionList(int uid, int ucid, StringCallback callback) {
      /*  OkHttpUtils.get()

                .url(API_URL)
                .addParams("service", "User.getAttention")
                .addParams("uid", String.valueOf(uid))
                .addParams("ucuid", String.valueOf(ucid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getAttention");
        map.put("uid", String.valueOf(uid));
        map.put("ucuid", String.valueOf(ucid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid 查询用户id
     * @dw 获取映票排行
     */
    public static void getYpOrder(int uid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getCoinRecord")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getCoinRecord");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


    /**
     * @param uid   查询用户id
     * @param token token
     * @dw 获取收益信息
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void getWithdraw(int uid, String token, StringCallback callback) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getWithdraw");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * 检查是否已经绑定过微信
     *
     * @param uid
     * @param token
     * @param callback
     */
    public static void checkIfBind(int uid, String token, StringCallback callback) {
      /*  OkHttpUtils.get().url(API_URL)
                .addParams("service", "User.IsBindWxUnionId")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.IsBindWxUnionId");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


    /**
     * bind wechat
     *
     * @param uid
     * @param token
     * @param callback
     */
    public static void bindWeChat(int uid, String token, String unionId, StringCallback callback) {
    /*    OkHttpUtils.get().url(API_URL)
                .addParams("service", "User.BindWxUnionId")
                .addParams("uid", String.valueOf(uid))
                .addParams("unionid", unionId)
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.BindWxUnionId");
        map.put("uid", String.valueOf(uid));
        map.put("unionid", unionId);
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


    /**
     * unbind wechat
     *
     * @param uid
     * @param token
     * @param callback
     */
    public static void unbindWeChat(int uid, String token, StringCallback callback) {
     /*   OkHttpUtils.get().url(API_URL)
                .addParams("service", "User.DelWxUnionId")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.DelWxUnionId");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 获取最新
     */
    public static void getNewestUserList(StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getNew")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getNew");
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid          用户id
     * @param token
     * @param protraitFile 图片文件
     * @dw 更新头像
     */
    public static void updatePortrait(int uid, String token, File protraitFile, StringCallback callback) {
        OkHttpUtils.post()
                .addFile("file", "wp.png", protraitFile)
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .url(API_URL + "appapi/?service=User.upload")
                .build()
                .execute(callback);

    }

    /**
     * @param uid   用户id
     * @param token
     * @dw 提现
     */
    public static void requestCash(int uid, String token, StringCallback callback) {
    /*    OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.userCash")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.userCash");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid 用户id
     * @dw 直播记录
     */
    public static void getLiveRecord(int uid, StringCallback callback) {
   /*     OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getLiveRecord")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getLiveRecord");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid 用户id
     * @dw 支付宝下订单
     */
    public static void getAliPayOrderNum(int uid, String money, String token, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getAliOrderId")
                .addParams("uid", String.valueOf(uid))
                .addParams("money", "1")
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getAliOrderId");
        map.put("uid", String.valueOf(uid));
        map.put("money", money);
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    //定位
    public static void getAddress(StringCallback callback) {
        OkHttpUtils.get()
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .build()
                .execute(callback);
    }

    /**
     * @param screenKey 搜索关键词
     * @param uid       用户id
     * @dw 搜索
     */
    public static void search(String screenKey, StringCallback callback, int uid) {
       /* OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.search")
                .addParams("key", screenKey)
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.search");
        map.put("key", screenKey);
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 获取地区列表
     */
    public static void getAreaList(StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getArea")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getArea");
        BasicApi.doGet(map, callback);
    }

    /**
     * @param sex  性别
     * @param area 地区
     * @dw 地区检索
     */

    public static void selectTermsScreen(int sex, String area, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.searchArea")
                .addParams("sex", String.valueOf(sex))
                .addParams("key", area)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.searchArea");
        map.put("sex", String.valueOf(sex));
        map.put("key", area);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uidList 用户id字符串 以逗号分割
     * @dw 批量获取用户信息
     */
    public static void getMultiBaseInfo(int action, int uid, String uidList, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getMultiBaseInfo")
                .addParams("uids", uidList)
                .addParams("type", String.valueOf(action))
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getMultiBaseInfo");
        map.put("uids", uidList);
        map.put("type", String.valueOf(action));
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid 用户id
     * @dw 获取已关注正在直播的用户
     */
    public static void getAttentionLive(int uid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.attentionLive")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.attentionLive");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid   当前用户id
     * @param ucuid to uid
     * @dw 获取用户信息私聊专用
     */

    public static void getPmUserInfo(int uid, int ucuid, StringCallback callback) {
    /*    OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getPmUserInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("ucuid", String.valueOf(ucuid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getPmUserInfo");
        map.put("uid", String.valueOf(uid));
        map.put("ucuid", String.valueOf(ucuid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid   用户id
     * @param price 价格
     * @dw 微信支付
     */
    public static void wxPay(int uid, String price, StringCallback callback) {
 /*       OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getAliOrderId")
                .addParams("uid", String.valueOf(uid))
                .addParams("money", "1")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.GetWxOrderId");
        map.put("uid", String.valueOf(uid));
        map.put("money", price);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param platDB 用户信息
     * @param type   平台
     * @dw 第三方登录
     */
    public static void otherLogin(String type, PlatformDb platDB, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.userLoginByThird")
                .addParams("openid", platDB.getUserId())
                .addParams("nicename", platDB.getUserName())
                .addParams("type", type)
                .addParams("avatar", platDB.getUserIcon())
                .addParams("sign", MD5.parseStrToMd5L32(platDB.getUserId() + System.currentTimeMillis()))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.userLoginByThird");
        map.put("openid", platDB.getUserId());
        map.put("nicename", platDB.getUserName());
        map.put("type", type);
        map.put("avatar", platDB.getUserIcon());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param roomnum 房间号码
     * @param touid   操作id
     * @param token   用户登录token
     * @dw 设为管理员
     */
    public static void setManage(int roomnum, int touid, String token, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setAdmin")
                .addParams("showid", String.valueOf(roomnum))
                .addParams("uid", String.valueOf(touid))
                .addParams("token", token)
                .build()
                .execute(callback);
*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setAdmin");
        map.put("showid", String.valueOf(roomnum));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid    用户id
     * @param showid 房间号码
     * @dw 判断是否为管理员
     */

    public static void isManage(int showid, int uid, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getIsAdmin")
                .addParams("showid", String.valueOf(showid))
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getIsAdmin");
        map.put("showid", String.valueOf(showid));
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);

    }

    /**
     * @param showid 房间id
     * @param touid  被禁言用户id
     * @param token  用户登录token
     * @dw 禁言
     */
    public static void setShutUp(int showid, int touid, int uid, String token, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setShutUp")
                .addParams("showid", String.valueOf(showid))
                .addParams("touid", String.valueOf(touid))
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setShutUp");
        map.put("showid", String.valueOf(showid));
        map.put("touid", String.valueOf(touid));
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    //是否禁言解除
    public static void isShutUp(int uid, int showid, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.isShutUp")
                .addParams("showid", String.valueOf(showid))
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.isShutUp");
        map.put("showid", String.valueOf(showid));
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    //token是否过期
    public static void tokenIsOutTime(int uid, String token, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.iftoken")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.iftoken");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 拉黑
     */
    public static void pullTheBlack(int uid, int touid, String token, StringCallback callback) {
        /*OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setBlackList")
                .addParams("uid", String.valueOf(uid))
                .addParams("touid", String.valueOf(touid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setBlackList");
        map.put("uid", String.valueOf(uid));
        map.put("touid", String.valueOf(touid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);

    }

    /**
     * @dw 黑名单列表
     */
    public static void getBlackList(int uid, StringCallback callback) {
       /* OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getBlackList")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getBlackList");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    public static void getMessageCode(String phoneNum) {
   /*     OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getCode")
                .addParams("mobile", phoneNum)
                .addParams("sign", MD5.parseStrToMd5L32(phoneNum + System.currentTimeMillis()))
                .build()
                .execute(null);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getCode");
        map.put("mobile", phoneNum);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, null);
    }

    /**
     * @param uid 用户id
     * @dw 获取用户余额
     */
    public static void getUserDiamondsNum(int uid, String token, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getUserPrivateInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getUserPrivateInfo");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param uid    用户id
     * @param token  用户登录token
     * @param showid 房间号
     * @dw 点亮
     */
    public static void showLit(int uid, String token, int showid) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.setLight")
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .addParams("showid", String.valueOf(showid))
                .build()
                .execute(null);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.setLight");
        map.put("uid", String.valueOf(uid));
        map.put("token", token);
        map.put("showid", String.valueOf(showid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, null);
    }

    /**
     * @param keyword 歌曲关键词
     * @dw 百度接口搜索音乐
     */
    public static void searchMusic(String keyword, StringCallback callback) {
        //baidu music interface (http://tingapi.ting.baidu.com/v1/restserver/ting)
        OkHttpUtils.get()
                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.catalogSug&query=" + keyword)
                .build()
                .execute(callback);

    }

    /**
     * @param songid 歌曲id
     * @dw 获取music信息
     */
    public static void getMusicFileUrl(String songid, StringCallback callback) {
        OkHttpUtils.get()
                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.downWeb&songid=" + songid + "&bit=24&_t=" + System.currentTimeMillis())
                .build()
                .execute(callback);
    }

    /**
     * @param musicUrl 下载地址
     * @dw 下载音乐文件
     */
    public static void downloadMusic(String musicUrl, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicUrl)
                .build()
                .execute(fileCallBack);
    }

    /**
     * @dw 开播等级限制
     */
    public static void getLevelLimit(int uid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getLevelLimit")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getLevelLimit");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @dw 检查新版本
     */
    public static void checkUpdate(StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getVersion")
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getVersion");
        BasicApi.doGet(map, callback);
    }

    public static void downloadLrc(String musicLrc, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicLrc)
                .build()
                .execute(fileCallBack);
    }

    /**
     * @param apkUrl 下载地址
     * @dw 下载最新apk
     */
    public static void getNewVersionApk(String apkUrl, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(apkUrl)
                .build()
                .execute(fileCallBack);
    }

    /**
     * @param uid 用户id
     * @dw 获取管理员列表
     */
    public static void getManageList(int uid, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getAdminList")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getAdminList");
        map.put("uid", String.valueOf(uid));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }

    /**
     * @param mRoomNum 房间号码
     * @param size     当前人数
     * @dw 获取更多用户列表
     */
    public static void loadMoreUserList(int size, int mRoomNum, StringCallback callback) {
      /*  OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.getRedislist")
                .addParams("size", String.valueOf(size))
                .addParams("showid", String.valueOf(mRoomNum))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.getRedislist");
        map.put("size", String.valueOf(size));
        map.put("showid", String.valueOf(mRoomNum));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


    /**
     * 获取点播列表
     *
     * @param id  请求起始ID
     * @param uid 用户ID
     * @param len 数据条数
     */
    public static void getVodList(int id, int uid, int len, StringCallback callback) {
     /*   OkHttpUtils.get()
                .url(API_URL)
                .addParams("service", "User.GetVodList")
                .addParams("id", "")
                .addParams("uid", "")
                .addParams("len", String.valueOf(len))
                .build()
                .execute(callback);*/
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.GetVodList");
        map.put("id", "");
        map.put("uid", "");
        map.put("len", String.valueOf(len));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


    /**
     * 获取扩展信息
     *
     * @param callback
     */
    public static void getConfig(StringCallback callback) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("service", "User.GetConfig");
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", ApiUtils.createSign(map));
        BasicApi.doGet(map, callback);
    }


}
