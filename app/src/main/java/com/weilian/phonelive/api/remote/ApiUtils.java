package com.weilian.phonelive.api.remote;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.AppManager;
import com.weilian.phonelive.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtils {
    public final static int SUCCESS_CODE = 200;//成功请求到服务端
    public final static String TOKEN_TIMEOUT = "700";

    /**
     * @return JsonObject
     * @dw 检测服务端请求是否有异常, 如果成功返回结果中的info json对象
     */
    public static String checkIsSuccess(String res, Activity context) {
        try {
            if (res == null || res.isEmpty()) {
                AppContext.showToastAppMsg(context, "网络异常");
                return null;
            }
            JSONObject resJson = new JSONObject(res);
            if (Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE) {
                JSONObject dataJson = resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if (code.equals(TOKEN_TIMEOUT)) {
                    AppManager.getAppManager().finishAllActivity();
                    UIHelper.showLoginSelectActivity(context);
                    return null;
                } else if (!code.equals("0")) {
                    AppContext.showToastAppMsg(context, dataJson.getString("msg"));
                    return null;
                } else {
                    return dataJson.get("info").toString();
                }
            } else {
                AppContext.showToastAppMsg(context, resJson.getString("msg"));
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String checkIsSuccess(String res) {
        try {
            if (res==null||res.isEmpty()|| TextUtils.isEmpty(res)) return null;
            JSONObject resJson = new JSONObject(res);

            if (Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE) {
                JSONObject dataJson = resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if (code.equals(TOKEN_TIMEOUT)) {
                    AppManager.getAppManager().finishAllActivity();
                    UIHelper.showLoginSelectActivity(AppContext.getInstance());
                    return null;
                } else if (!code.equals("0")) {
                    Toast.makeText(AppContext.getInstance(), dataJson.get("info").toString(), Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    return dataJson.get("info").toString();
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String checkIsSuccess2(String res) {
        try {
            JSONObject resJson = new JSONObject(res);

            if (Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE) {
                JSONObject dataJson = resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if (code.equals(TOKEN_TIMEOUT)) {
                    AppManager.getAppManager().finishAllActivity();
                    UIHelper.showLoginSelectActivity(AppContext.getInstance());
                    return null;
                } else if (!code.equals("0")) {
                    return null;
                } else {
                    return dataJson.get("info").toString();
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}
