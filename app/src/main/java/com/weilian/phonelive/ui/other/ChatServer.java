package com.weilian.phonelive.ui.other;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.bean.ChatBean;
import com.weilian.phonelive.bean.SendGiftBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.interf.ChatServerInterface;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.utils.StringUtils;
import com.weilian.phonelive.utils.TDevice;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;

/**
 *直播间业务逻辑处理
 */
public class ChatServer {
    private Socket mSocket;
    private Context context;
    private int showid;
    private ChatServerInterface mChatServer;
    private static final String EVENT_NAME = "broadcast";
    private static final int SENDCHAT  = 2;
    private static final int SYSTEMNOT  = 1;
    private static final int NOTICE  = 0;
    private static final int PRIVELEGE = 4;
    private Gson gson;
    public static int LIVEUSERNUMS;
    public static final int[] heartImg = new int[]{R.drawable.plane_heart_cyan,R.drawable.plane_heart_pink,R.drawable.plane_heart_red,R.drawable.plane_heart_yellow};

    public ChatServer(ChatServerInterface chatServerInterface,Context context,int showid) throws URISyntaxException {
        this.mChatServer = chatServerInterface;
        this.context = context;
        this.showid = showid;
        gson = new Gson();
        mSocket = AppContext.getInstance().getSocket();
    }
    //服务器连接结果监听
    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(args[0].toString().equals("ok")){
                mChatServer.onConnect(true);
                PhoneLiveApi.getRoomUserList(showid, callback);
            }else {
                mChatServer.onConnect(false);
            }

        }
    };
    //服务器连接关闭监听
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            TLog.log("socket断开连接");
        }
    };
    //服务器连接失败监听
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mChatServer.onError();
            TLog.log("socket连接Error");
        }
    };
    //服务器消息监听
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                String res = args[0].toString();
                if(res.equals("stopplay")){
                    mChatServer.onSystemNot(1);
                    return;
                }
                JSONObject resJson = new JSONObject(res);
                JSONArray  msgArrayJson = resJson.getJSONArray("msg");
                JSONObject contentJson  = msgArrayJson.getJSONObject(0);
                int msgType = contentJson.getInt("msgtype");
                int action  = contentJson.getInt("action");
                switch (msgType){
                    case SENDCHAT://聊天
                        if(action == 0){//公聊
                            String ct = contentJson.getString("ct");

                            ChatBean c = new ChatBean();
                            c.setId(contentJson.getInt("uid"));
                            c.setSignature(contentJson.getString("usign"));
                            c.setLevel(contentJson.getInt("level"));
                            c.setUser_nicename(contentJson.getString("uname"));
                            c.setCity(contentJson.getString("city"));
                            c.setSex(contentJson.getInt("sex"));
                            c.setAvatar(contentJson.getString("uhead"));
                            int level = c.getLevel();
                            String uname = "_ "+c.getUser_nicename() + ":";

                            SpannableStringBuilder msg = new SpannableStringBuilder(ct);
                            SpannableStringBuilder name = new SpannableStringBuilder(uname);
                            //添加等级图文混合
                            Drawable levelDrawable = context.getResources().getDrawable(DrawableRes.LevelImg[(level!=0?level-1:0)]);
                            levelDrawable.setBounds(0,0, (int) TDevice.dpToPixel(30),(int) TDevice.dpToPixel(15));
                            ImageSpan levelImage = new ImageSpan(levelDrawable,ImageSpan.ALIGN_BASELINE);
                            name.setSpan(new ForegroundColorSpan(Color.rgb(215,126,23)), 1, name.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            name.setSpan(levelImage,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //获取被@用户id
                            String touid = contentJson.getString("touid");
                            //判断如果是@方式聊天,被@方用户显示粉色字体
                            if((!touid.equals("0") && (Integer.parseInt(touid) == AppContext.getInstance().getLoginUid()
                           /* || c.getId() == AppContext.getInstance().getLoginUid()*/))){
                                msg.setSpan(new ForegroundColorSpan(Color.rgb(232,109,130)),0,msg.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            //判断是否是点亮
                            if(res.indexOf("heart") > 0){
                                //int[] heartImg = new int[]{Color.rgb(30,190,204),Color.rgb(241,96,246),Color.rgb(218,53,42),Color.rgb(205,203,49)};
                                int index = contentJson.getInt("heart");
                                msg.append("❤");
                                //添加点亮图文混合
                                Drawable hearDrawable = context.getResources().getDrawable(heartImg[index]);
                                hearDrawable.setBounds(0,0,(int) TDevice.dpToPixel(20),(int) TDevice.dpToPixel(20));
                                ImageSpan hearImage = new ImageSpan(hearDrawable,ImageSpan.ALIGN_BASELINE);
                                msg.setSpan(hearImage,msg.length()-1,msg.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }
                            c.setSendChatMsg(msg);
                            c.setUserNick(name);

                            mChatServer.onMessageListen(c);
                        }
                        break;
                    case SYSTEMNOT://系统
                        if(action == 0){ // sendgift
                            ChatBean c = new ChatBean();
                            c.setId(contentJson.getInt("uid"));
                            c.setSignature(contentJson.getString("usign"));
                            c.setLevel(contentJson.getInt("level"));
                            c.setUser_nicename(contentJson.getString("uname"));
                            c.setCity(contentJson.getString("city"));
                            c.setSex(contentJson.getInt("sex"));
                            c.setAvatar(contentJson.getString("uhead"));
                            contentJson.getJSONObject("ct").put("evensend",contentJson.getString("evensend"));
                            SendGiftBean mSendGiftInfo = gson.fromJson(contentJson.getJSONObject("ct").toString(), SendGiftBean.class);//gift info

                            int level = c.getLevel();
                            String uname = "_ "+c.getUser_nicename() + ":";
                            SpannableStringBuilder msg = new SpannableStringBuilder("我送了" + mSendGiftInfo.getGiftcount() + "个" + mSendGiftInfo.getGiftname());
                            SpannableStringBuilder name = new SpannableStringBuilder(uname);

                            Drawable d = context.getResources().getDrawable(DrawableRes.LevelImg[(level!=0?level-1:0)]);
                            d.setBounds(0,0, (int) TDevice.dpToPixel(30),(int) TDevice.dpToPixel(15));
                            ImageSpan is = new ImageSpan(d,ImageSpan.ALIGN_BASELINE);
                            name.setSpan(new ForegroundColorSpan(Color.rgb(215,126,23)), 1, name.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            name.setSpan(is,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            msg.setSpan(new ForegroundColorSpan(Color.rgb(232,109,130)),0,msg.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            c.setSendChatMsg(msg);
                            c.setUserNick(name);

                            mChatServer.onShowSendGift(mSendGiftInfo,c);
                        }else if(action == 18){//close room
                            //房间关闭
                            mChatServer.onSystemNot(0);
                        }else if(action == 13){
                            //系统消息
                            SpannableStringBuilder msg = new SpannableStringBuilder(contentJson.getString("ct"));
                            SpannableStringBuilder name = new SpannableStringBuilder("系统消息:");
                            name.setSpan(new ForegroundColorSpan(Color.rgb(215,126,23)), 0, name.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ChatBean c = new ChatBean();
                            c.setType(13);
                            c.setSendChatMsg(msg);
                            c.setUserNick(name);
                           mChatServer.setManage(contentJson,c);
                        }
                        break;
                    case NOTICE://通知
                        if(action == 0){//上下线
                            JSONObject uInfo = contentJson.getJSONObject("ct");
                            ChatServer.LIVEUSERNUMS += 1;
                            mChatServer.onUserStateChange(gson.fromJson(uInfo.toString(), UserBean.class),true);
                        }else if(action == 1){
                            JSONObject uInfo = contentJson.getJSONObject("ct");
                            ChatServer.LIVEUSERNUMS -= 1;
                            mChatServer.onUserStateChange(gson.fromJson(uInfo.toString(), UserBean.class),false);
                        }else if(action == 2){//点亮
                            mChatServer.onLit();
                        }else if(action == 3){//僵尸粉丝推送
                            mChatServer.onAddZombieFans(contentJson.getString("ct"));
                        }
                        break;
                    case PRIVELEGE:
                        SpannableStringBuilder msg = new SpannableStringBuilder(contentJson.getString("ct"));
                        SpannableStringBuilder name = new SpannableStringBuilder("系统消息:");
                        name.setSpan(new ForegroundColorSpan(Color.rgb(215,126,23)), 0, name.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        msg.setSpan(new ForegroundColorSpan(Color.rgb(109,198,232)),0,msg.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ChatBean c = new ChatBean();
                        c.setType(13);
                        c.setSendChatMsg(msg);
                        c.setUserNick(name);
                        mChatServer.onPrivilegeAction(c,contentJson);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @dw 连接socket服务端
     * @param res 用户信息json格式
     * */
    public void connectSocketServer(String res, String token, int mNowRoomNum) {
        try {
            JSONObject resJson = new JSONObject(res);

            if(null != mSocket){
                mSocket.connect();
                JSONObject dataJson = new JSONObject();
                dataJson.put("uid",resJson.getString("id"));
                dataJson.put("token",token);
                dataJson.put("roomnum", mNowRoomNum);
                mSocket.emit("conn", dataJson);
                mSocket.on("conn",onConn);
                mSocket.on("broadcastingListen",onMessage);
                mSocket.on(mSocket.EVENT_DISCONNECT,onDisconnect);
                mSocket.on(mSocket.EVENT_ERROR,onError);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
    * @param handler
    * 定时发送心跳包,在连接成功后调用
    * */
    public void heartbeat(final Handler handler) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TLog.log("心跳包发送....");
                if(mSocket == null) return;
                mSocket.emit("heartbeat","heartbeat");
                handler.postDelayed(this,4000);
            }
        },4000);
    }

    /**
     * @dw 主播连接socket服务端
     * @param u 用户基本信息
     * @param mNowRoomNum 房间号码(主播id)
     * */
    public void connectSocketServer(UserBean u, int mNowRoomNum) {
        if(null == mSocket)return;
        try {
            mSocket.connect();
            JSONObject dataJson = new JSONObject();
            dataJson.put("uid",u.getId());
            dataJson.put("token",u.getToken());
            dataJson.put("roomnum",u.getId());
            mSocket.emit("conn", dataJson);
            mSocket.on("conn", onConn);
            mSocket.on("broadcastingListen",onMessage);
            mSocket.on(mSocket.EVENT_DISCONNECT,onDisconnect);
            mSocket.on(mSocket.EVENT_ERROR,onError);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //关闭房间
    public void closeLive() {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson = new JSONObject();
        JSONObject msgJson2 = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            msgJson2.put("_method_","StartEndLive");
            msgJson2.put("action","18");
            msgJson2.put("ct", context.getString(R.string.livestart));
            msgJson2.put("msgtype","1");
            msgJson2.put("timestamp",millisToStringDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
            msgJson2.put("tougood","");
            msgJson2.put("touid","");
            msgJson2.put("touname","");
            msgJson2.put("ugood","");

            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg","ok");

            mSocket.emit(EVENT_NAME,msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * @dw token 发送礼物凭证
     * @param mUser 用户信息
     * @param evensend 是否在连送规定时间内
     * */
    public void doSendGift(String token, UserBean mUser , String evensend) {
       if(null != mSocket){
           JSONObject msgJson  = new JSONObject();
           JSONObject msgJson2 = new JSONObject();
           JSONArray  msgArrJson = new JSONArray();
           try {
               msgJson2.put("_method_","SendGift");
               msgJson2.put("evensend",evensend);
               msgJson2.put("action","0");
               msgJson2.put("ct",token);
               msgJson2.put("msgtype","1");
               msgJson2.put("level",mUser.getLevel());
               msgJson2.put("uid",mUser.getId());
               msgJson2.put("sex",mUser.getSex());
               msgJson2.put("uname",mUser.getUser_nicename());
               msgJson2.put("uhead",mUser.getAvatar());
               msgJson2.put("usign",mUser.getSignature());
               msgJson2.put("city",AppContext.address);
               msgArrJson.put(0,msgJson2);

               msgJson.put("msg",msgArrJson);
               msgJson.put("retcode","000000");
               msgJson.put("retmsg", "ok");
               mSocket.emit(EVENT_NAME, msgJson);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }

    }
    /**
    * @dw 禁言
    * @param mUser 当前用户bean
    * @param mToUser 被操作用户bean
    * */
    public void doSetShutUp(UserBean mUser, UserBean mToUser) {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson  = new JSONObject();
        JSONObject msgJson2 = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            msgJson2.put("_method_","ShutUpUser");
            msgJson2.put("action","1");
            msgJson2.put("ct",mToUser.getUser_nicename() + "被禁言");
            msgJson2.put("msgtype","4");
            msgJson2.put("uid",mUser.getId());
            msgJson2.put("uname",mUser.getUser_nicename());
            msgJson2.put("touid",mToUser.getId());
            msgJson2.put("touname",mToUser.getUser_nicename());
            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg", "ok");
            mSocket.emit(EVENT_NAME, msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    //设为管理员
    public void doSetOrRemoveManage(UserBean user, UserBean touser,String content) {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson  = new JSONObject();
        JSONObject msgJson2 = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            msgJson2.put("_method_","SystemNot");
            msgJson2.put("action","13");
            msgJson2.put("ct",content);
            msgJson2.put("msgtype","1");
            msgJson2.put("uid",user.getId());
            msgJson2.put("uname",user.getUser_nicename());
            msgJson2.put("touid",touser.getId());
            msgJson2.put("touname",touser.getUser_nicename());
            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg", "ok");
            mSocket.emit(EVENT_NAME, msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
    * @dw 发言
    * @param sendMsg 发言内容
    * @param user 用户信息
    * */
    public void doSendMsg(String sendMsg, UserBean user,int reply) {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson    = new JSONObject();
        JSONObject msgJson2   = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            //json对象msgjson数组下数组下标为0的json对象
            msgJson2.put("_method_","SendMsg");
            msgJson2.put("action","0");
            msgJson2.put("ct",sendMsg);
            msgJson2.put("msgtype","2");
            msgJson2.put("timestamp",millisToStringDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
            msgJson2.put("tougood","");
            msgJson2.put("touid",reply);
            msgJson2.put("touname","");
            msgJson2.put("ugood","");
            msgJson2.put("city",AppContext.address);
            msgJson2.put("level",user.getLevel());
            msgJson2.put("uid",user.getId());
            msgJson2.put("sex",user.getSex());
            msgJson2.put("uname",user.getUser_nicename());
            msgJson2.put("uhead",user.getAvatar());
            msgJson2.put("usign",user.getSignature());
            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg","ok");
            mSocket.emit(EVENT_NAME,msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * @dw 我点亮了
     * @param index
     * @param user 用户信息
     * */
    public void doSendLitMsg(UserBean user,int index) {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson    = new JSONObject();
        JSONObject msgJson2   = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            //json对象msgjson数组下数组下标为0的json对象
            msgJson2.put("_method_","SendMsg");
            msgJson2.put("action","0");
            msgJson2.put("ct","我点亮了");
            msgJson2.put("msgtype","2");
            msgJson2.put("timestamp",millisToStringDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
            msgJson2.put("tougood","");
            msgJson2.put("touname","");
            msgJson2.put("heart",index);
            msgJson2.put("touid",0);
            msgJson2.put("ugood","");
            msgJson2.put("heart",index);
            msgJson2.put("city",AppContext.address);
            msgJson2.put("level",user.getLevel());
            msgJson2.put("uid",user.getId());
            msgJson2.put("sex",user.getSex());
            msgJson2.put("uname",user.getUser_nicename());
            msgJson2.put("uhead",user.getAvatar());
            msgJson2.put("usign",user.getSignature());
            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg","ok");
            mSocket.emit(EVENT_NAME,msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //获取僵尸粉丝
    public void getZombieFans() {
        if(null != mSocket){
            JSONObject msgJson    = new JSONObject();
            JSONObject msgJson2   = new JSONObject();
            JSONArray  msgArrJson = new JSONArray();
            try {
                //json对象msgjson数组下数组下标为0的json对象
                msgJson2.put("_method_","requestFans");
                msgJson2.put("timestamp",millisToStringDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));

                msgArrJson.put(0,msgJson2);

                msgJson.put("msg",msgArrJson);
                msgJson.put("retcode","000000");
                msgJson.put("retmsg","ok");
                mSocket.emit(EVENT_NAME,msgJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
    * @dw 点亮
    * @param index 点亮心在数组中的下标
    * */
    public void doSendLit(int index) {
        if(null == mSocket){
            return;
        }
        JSONObject msgJson    = new JSONObject();
        JSONObject msgJson2   = new JSONObject();
        JSONArray  msgArrJson = new JSONArray();
        try {
            //json对象msgjson数组下数组下标为0的json对象
            msgJson2.put("_method_","light");
            msgJson2.put("action","2");
            msgJson2.put("msgtype","0");
            msgJson2.put("timestamp",millisToStringDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
            msgJson2.put("tougood","");
            msgJson2.put("touname","");
            msgJson2.put("ugood","");
            msgArrJson.put(0,msgJson2);

            msgJson.put("msg",msgArrJson);
            msgJson.put("retcode","000000");
            msgJson.put("retmsg","ok");
            mSocket.emit(EVENT_NAME,msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param millis
     *            要转化的日期毫秒数。
     * @param pattern
     *            要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）。
     * @return 返回日期字符串。
     */
    public static String millisToStringDate(long millis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern,
                Locale.getDefault());
        return format.format(new Date(millis));
    }
    //用户列表回调
    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToast("获取观众列表失败");
        }

        @Override
        public void onResponse(String s) {

            String res = ApiUtils.checkIsSuccess(s);
            if(res != null){
                try {
                    JSONObject infoJsonObj = new JSONObject(res);
                    JSONArray uListJsonArray = infoJsonObj.getJSONArray("list");//观众列表
                    String votes = infoJsonObj.getString("votestotal");//票
                    SparseArray<UserBean> uList = new SparseArray<>();
                    for(int i = 0; i< uListJsonArray.length();i++){
                        UserBean u = gson.fromJson(uListJsonArray.getString(i), UserBean.class);
                        uList.put(u.getId(),u);
                    }
                    ChatServer.LIVEUSERNUMS = StringUtils.toInt(infoJsonObj.getString("nums"),0);
                    mChatServer.onUserList(uList,votes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    //释放资源
    public void close() {
        if(mSocket != null){
            mSocket.disconnect();
            mSocket.off("conn");
            mSocket.off("broadcastingListen");
            mSocket.close();
            mSocket = null;
        }

    }


}
