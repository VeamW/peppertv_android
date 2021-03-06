package com.weilian.phonelive;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.devspark.appmsg.AppMsg;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.util.NetUtils;
import com.weilian.phonelive.base.BaseApplication;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.cache.DataCleanManager;
import com.weilian.phonelive.model.MyNotifier;
import com.weilian.phonelive.utils.CyptoUtils;
import com.weilian.phonelive.utils.MethodsCompat;
import com.weilian.phonelive.utils.StringUtils;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;

import org.kymjs.kjframe.Core;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import io.socket.client.IO;
import io.socket.client.Socket;

import static com.weilian.phonelive.AppConfig.KEY_FRITST_START;
import static com.weilian.phonelive.AppConfig.KEY_NIGHT_MODE_SWITCH;
import static com.weilian.phonelive.AppConfig.KEY_TWEET_DRAFT;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @version 1.0
 */
public class AppContext extends BaseApplication {


    private static AppContext instance;
    private int loginUid;
    private boolean login;
    private String Token;
    public static String address = "好像在黑洞";
    public static String province;
    private Socket mSocket;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    /*amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码*/
                    province = amapLocation.getProvince();
                    address = amapLocation.getCity();
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    TLog.log("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        initLogin();
//      Thread.setDefaultUncaughtExceptionHandler(AppException
//                .getAppExceptionHandler(this));
        UIHelper.sendBroadcastForNotice(this);
    }

    /**
     * 初始化操作
     */
    private void init() {

        // 初始化网络请求
        // Log控制器
        /*KJLoger.openDebutLog(true);
        TLog.DEBUG = BuildConfig.DEBUG;

        // Bitmap缓存地址
       */
        //环信初始化
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

        //初始化
        EMClient.getInstance().init(context(), options);


        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(false);
        setGlobalListeners();


        //初始化jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        try {
            mSocket = IO.socket(AppConfig.CHAT_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //高德定位初始化
        initAMap();
    }


    public Socket getSocket() {
        return mSocket;
    }

    private void initAMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //停止定位
    public void stopLocation() {
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();//销毁定位客户端。

    }

    protected void setGlobalListeners() {
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            TLog.log("连接聊天服务器成功！");
        }

        @Override
        public void onDisconnected(final int error) {

            if (error == EMError.USER_REMOVED) {
                // 显示帐号已经被移除
                TLog.log("显示帐号已经被移除");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                // 显示帐号在其他设备登陆
                TLog.log("显示帐号在其他设备登陆");
            } else {
                if (NetUtils.hasNetwork(AppContext.getInstance())) {
                    //连接不到聊天服务器
                    TLog.log("连接不到聊天服务器");
                } else {
                    //当前网络不可用，请检查网络设置
                    TLog.log("当前网络不可用，请检查网络设置");
                }
            }
        }
    }

    private EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            TLog.log("收到消息:" + messages);
            Intent broadcastIntent = new Intent("com.weilian.phonelive");
            broadcastIntent.putExtra("cmd_value", messages.get(0));
            sendBroadcast(broadcastIntent, null);
            MyNotifier notifier = new MyNotifier();
            notifier.init(AppContext.getInstance());
            notifier.sendNotification(messages.get(0),false);
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            TLog.log("收到透传消息:" + messages);
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
            TLog.log("收到已读回执:" + messages);
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
            TLog.log("收到已送达回执:" + message);
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            TLog.log("消息状态变动:" + message);
        }
    };

    private void initLogin() {
        UserBean user = getLoginUser();
        if (null != user && user.getId() > 0) {
            login = true;
            loginUid = user.getId();
            Token = user.getToken();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EMClient.getInstance().logout(true, new EMCallBack() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            }).start();
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final UserBean user) {
        this.loginUid = user.getId();
        this.Token = user.getToken();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getUser_nicename());
                setProperty("user.token", user.getToken());
                setProperty("user.sign", user.getSignature());
                setProperty("user.avatar", user.getAvatar());
                setProperty("user.pwd",
                        CyptoUtils.encode("PhoneLiveApp", user.getUser_pass()));


                setProperty("user.city", user.getCity());
                setProperty("user.coin", user.getCoin());
                setProperty("user.sex", String.valueOf(user.getSex()));
                setProperty("user.signature", user.getSignature());
                setProperty("user.avatar", user.getAvatar());
                setProperty("user.level", String.valueOf(user.getLevel()));

            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final UserBean user) {
        if (user == null) return;
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getUser_nicename());
                setProperty("user.token", user.getToken());
                setProperty("user.sign", user.getSignature());
                setProperty("user.avatar", user.getAvatar());
                setProperty("user.pwd",
                        CyptoUtils.encode("PhoneLiveApp", user.getUser_pass()));

                setProperty("user.city", user.getCity());
                setProperty("user.coin", user.getCoin());
                setProperty("user.sex", String.valueOf(user.getSex()));
                setProperty("user.signature", user.getSignature());
                setProperty("user.avatar", user.getAvatar());
                setProperty("user.level", String.valueOf(user.getLevel()));
            }
        });
    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public UserBean getLoginUser() {
        UserBean user = new UserBean();
        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
        user.setAvatar(getProperty("user.avatar"));
        user.setUser_nicename(getProperty("user.name"));
        user.setUser_pass(getProperty("user.pwd"));
        user.setSignature(getProperty("user.sign"));
        user.setToken(getProperty("user.token"));

        user.setCity(getProperty("user.city"));
        user.setCoin(getProperty("user.coin"));
        String sex = getProperty("user.sex");
        user.setSex(Integer.parseInt(sex == null ? "0" : sex));
        user.setSignature(getProperty("user.signature"));
        user.setAvatar(getProperty("user.avatar"));
        String level = getProperty("user.level");
        user.setLevel(Integer.parseInt(level == null ? "0" : level));

        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty("user.uid", "user.token", "user.name", "user.pwd", "user.avatar,user.sign,user.city,user.coin,user.sex,user.signature,user.signature,user.avatar,user.level");
    }

    public int getLoginUid() {
        return loginUid;
    }

    public String getToken() {
        return Token;
    }

    public boolean isLogin() {
        return login;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        cleanLoginInfo();
        //ApiHttpClient.cleanCookie();
        //this.cleanCookie();
        this.login = false;
        this.loginUid = 0;
        this.Token = "";
        //Intent intent = new Intent(this, MainActivity.class);
        //sendBroadcast(intent);
        //startActivity(intent);

    }

    /**
     * 清除保存的缓存
     *//*
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        Core.getKJBitmap().cleanCache();
    }

    /*public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
    }*/

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static String getTweetDraft() {
        return getPreferences().getString(
                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
    }

    // 设置夜间模式
    public static void setNightModeSwitch(boolean on) {
        set(KEY_NIGHT_MODE_SWITCH, on);
    }

    public static void showToastAppMsg(Activity context, String msg) {
        AppMsg.makeText(context, msg, new AppMsg.Style(1000, R.drawable.toast_background)).show();
    }

}
