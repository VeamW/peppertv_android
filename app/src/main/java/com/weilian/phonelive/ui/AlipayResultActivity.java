package com.weilian.phonelive.ui;

import android.view.View;
import android.widget.TextView;

import com.weilian.phonelive.R;
import com.weilian.phonelive.base.BaseActivity;

import butterknife.InjectView;


public class AlipayResultActivity extends BaseActivity {
    @InjectView(R.id.tv_alipaypay_result)
    TextView mAliPayResult;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_alipay_result;
    }

    @Override
    public void initView() {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.payresult));
        if(getIntent().getIntExtra("result",0) == 1){
            mAliPayResult.setText("ok");
        }else{
            mAliPayResult.setText("no");
        }
    }

    @Override
    public void onClick(View v) {

    }
}
