package com.weilian.phonelive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 修改资料
 */
public class EditInfoActivity extends BaseActivity {
    @InjectView(R.id.edit_input)
    EditText mInPutText;
    @InjectView(R.id.tv_prompt)
    TextView mPrompt;
    @InjectView(R.id.tv_text)
    TextView mSaveInfo;
    @InjectView(R.id.iv_back)
    LinearLayout mIvBack;
    @InjectView(R.id.iv_editInfo_clean)
    ImageView mInfoClean;
    public final static String EDITKEY     = "EDITKEY";
    public final static String EDITACTION  = "EDITACTION";
    public final static String EDITPROMP   = "EDITPROMP";
    public final static String EDITDEFAULT = "EDITDEFAULT";
    private TextView mTvTitle;
    private Intent intent;
    private String key;
    private String value;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        intent = getIntent();
    }

    @Override
    public void initView() {
        mSaveInfo.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mInfoClean.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if(intent != null){

            mTvTitle.setText(intent.getStringExtra(EDITACTION));
            mPrompt.setText(intent.getStringExtra(EDITPROMP));
            mInPutText.setText(intent.getStringExtra(EDITDEFAULT));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_text:
                saveInfo();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_editInfo_clean:
                mInPutText.setText("  ");
                //TODO
                break;
        }
    }
    /**
     * @dw 提交修改数据
     * */
    private void saveInfo() {
        key = intent.getStringExtra(EDITKEY);
        value = mInPutText.getText().toString();

        // zxy 0418  判断输入长度
        if(key.equals("user_nicename") && value.length()>15){
            AppContext.showToastAppMsg(EditInfoActivity.this,"昵称长度超过限制");
            return;
        }else if(key.equals("signature") && value.length()>20){
            AppContext.showToastAppMsg(EditInfoActivity.this,"签名长度超过限制");
            return;
        }else if(key.equals("user_nicename") && value.length() < 6){
            AppContext.showToastAppMsg(EditInfoActivity.this,"昵称长度不符合要求");
            return;
        }


        // zxy 0418  判断输入长度
        PhoneLiveApi.saveInfo(key, value,
                AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                callback);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_actionbar_title);
        mTvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_actionBarTitle);

    }
    StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            showToast2(getString(R.string.editfail));
        }

        @Override
        public void onResponse(String s) {
            String res = ApiUtils.checkIsSuccess(s,EditInfoActivity.this);
            if(null != res){
                AppContext.showToastAppMsg(EditInfoActivity.this,getString(R.string.editsuccess));
                UserBean u =  AppContext.getInstance().getLoginUser();
                if(key.equals("user_nicename")){
                    u.setUser_nicename(value);
                }else if(key.equals("signature")){
                    u.setSignature(value);
                }
                AppContext.getInstance().updateUserInfo(u);
                finish();
            }

        }
    };
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("编辑资料"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("编辑资料"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}