package com.weilian.phonelive.ui;

import android.view.View;
import android.widget.ImageView;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 修改性别
 */
public class ChangeSexActivity extends BaseActivity {
    @InjectView(R.id.iv_change_sex_male)
    ImageView mIvMale;
    @InjectView(R.id.iv_change_sex_female)
    ImageView mIvFemale;

    private int sex = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_sex;
    }


    @Override
    public void initView() {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @OnClick({R.id.iv_change_sex_male,R.id.iv_change_sex_female})
    public void OnClick(View v){
        switch (v.getId()){
            case R.id.iv_change_sex_male:
                sex = 1;
                changeSex();
                break;
            case R.id.iv_change_sex_female:
                sex = 2;
                changeSex();
                break;
        }
    }
    private void changeSex(){
        mIvMale.setImageResource(sex == 1?R.drawable.choice_sex_male:R.drawable.choice_sex_un_male);
        mIvFemale.setImageResource(sex == 1?R.drawable.choice_sex_un_femal:R.drawable.choice_sex_femal);
        PhoneLiveApi.saveInfo("sex", String.valueOf(sex), AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(ChangeSexActivity.this,"修改失败");
            }

            @Override
            public void onResponse(String response) {
                UserBean mUser = AppContext.getInstance().getLoginUser();
                mUser.setSex(sex);
                AppContext.getInstance().saveUserInfo(mUser);
                finish();
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle("性别");
        sex = getIntent().getIntExtra("SEX",0);
        mIvMale.setImageResource(sex == 1?R.drawable.choice_sex_male:R.drawable.choice_sex_un_male);
        mIvFemale.setImageResource(sex == 1?R.drawable.choice_sex_un_femal:R.drawable.choice_sex_femal);
    }

    @Override
    public void onClick(View v) {

    }
}
