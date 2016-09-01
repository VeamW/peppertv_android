package com.weilian.phonelive.WxPay;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.ui.MyDiamondsActivity;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WChatPay {
    IWXAPI msgApi;
    //appid
    //wx17eb1de2b376294f
    final String appId = "wx17eb1de2b376294f";
    private MyDiamondsActivity context;
    String nums= "";
    public WChatPay(MyDiamondsActivity context) {
        this.context = context;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信】
        msgApi.registerApp(appId);
    }



    /**
     * @param price 价格
     * @param num   数量
     * @dw 初始化微信支付
     */
    public void initPay(String price, String num) {
        nums = num;
        PhoneLiveApi.wxPay(AppContext.getInstance().getLoginUid(), price, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (null == res) return;
                callWxPay(res);

            }
        });
    }

    private void callWxPay(String res) {
        try {
            JSONObject signInfo = new JSONObject(res);
            PayReq req = new PayReq();
            req.appId = signInfo.getString("appid");
            req.partnerId = signInfo.getString("partnerid");
            req.prepayId = signInfo.getString("prepayid");//预支付会话ID
            req.packageValue = "Sign=WXPay";
            req.nonceStr = signInfo.getString("noncestr");
            req.timeStamp = signInfo.getString("timestamp");
            req.sign = signInfo.getString("sign");
            if (msgApi.sendReq(req)) {
                AppContext.showToastAppMsg(context, "微信支付");
            } else {
                AppContext.showToastAppMsg(context, "请查看您是否安装微信");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
