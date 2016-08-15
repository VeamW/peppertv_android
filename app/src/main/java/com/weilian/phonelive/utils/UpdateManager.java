package com.weilian.phonelive.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.interf.DialogInterface;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

/**
 * 更新管理类
 */

public class UpdateManager {

    private Context mContext;

    private boolean isShow = false;

    private ProgressDialog _waitDialog;
    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess2(response);
            if(null != res){
                try {
                    JSONObject versionInfoObj = new JSONObject(res);
                    if(!String.valueOf(AppContext.getInstance().getPackageInfo().versionName).equals(
                            versionInfoObj.getString("apk_ver"))){
                        showUpdateInfo(versionInfoObj.getString("apk_url"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };
    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
    }


    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }
        PhoneLiveApi.checkUpdate(callback);
    }


    private void showCheckDialog() {
        if (_waitDialog == null) {
            _waitDialog = DialogHelp.getWaitDialog((Activity) mContext, "正在获取新版本信息...");
        }
        _waitDialog.show();
    }

    private void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.dismiss();
        }
    }

    private void showUpdateInfo(final String apiUrl) {
        DialogHelp.showDialog(mContext, "发现新版本是否更新", new DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {
                d.dismiss();
            }

            @Override
            public void determineDialog(View v,final Dialog d) {
                View view = View.inflate(mContext,R.layout.dialog_show_download_view,null);
                final NumberProgressBar progressBar = (NumberProgressBar) view.findViewById(R.id.np_download);
                d.setContentView(view);
                PhoneLiveApi.getNewVersionApk(apiUrl,new FileCallBack(AppConfig.DEFAULT_SAVE_FILE_PATH,"app.apk"){

                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(mContext,"安装包下载失败!",Toast.LENGTH_SHORT).show();
                        d.dismiss();
                    }

                    @Override
                    public void onResponse(File response) {
                        installApk();
                        d.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total) {
                        progressBar.setProgress((int) (progress*100));
                    }
                });
            }
        });

    }

    private void installApk() {
        File file = new File(AppConfig.DEFAULT_SAVE_FILE_PATH + "app.apk");
        if(!file.exists()){
            return;
        }
        TDevice.installAPK(mContext,file);
    }

    private void showLatestDialog() {
        DialogHelp.getMessageDialog(mContext, "已经是新版本了").show();
    }

    private void showFaileDialog() {
        DialogHelp.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
    }
}
