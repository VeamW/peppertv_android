package com.weilian.phonelive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.WxPay.WChatPay;
import com.weilian.phonelive.alipay.AliPay;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.utils.Utils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 我的钻石
 */
public class MyDiamondsActivity extends BaseActivity {
    private RelativeLayout mWxPay;
    private RelativeLayout mAliPay;
    private List<RechargeBean> rechanList;
    @InjectView(R.id.lv_select_num_list)
    ListView mSelectNumListItem;
    private TextView mTvDiamondsPayWx;
    private TextView mTvDiamondsPayAliPay;
    private TextView mPayName;
    private TextView mCoin;
    private final int WXPAY = 1;
    private final int ALIPAY = 2;
    private int PAYMODE = WXPAY;
    private View mHeadView;
    private AliPay mAliPayUtils;
    private int[] price;
    private int[] diamondsNum;
    private WChatPay mWChatPay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_diamonds;
    }

    @Override
    public void initView() {
        mHeadView = getLayoutInflater().inflate(R.layout.view_diamonds_head, null);
        mWxPay = (RelativeLayout) mHeadView.findViewById(R.id.rl_wxpay);
        mAliPay = (RelativeLayout) mHeadView.findViewById(R.id.rl_alipay);
        mPayName = (TextView) mHeadView.findViewById(R.id.tv_payname);
        mCoin = (TextView) mHeadView.findViewById(R.id.tv_coin);
        mTvDiamondsPayAliPay = (TextView) mHeadView.findViewById(R.id.tv_diamonds_pay_alipay);
        mTvDiamondsPayWx = (TextView) mHeadView.findViewById(R.id.tv_diamonds_pay_wx);
        mSelectNumListItem.addHeaderView(mHeadView);

        getImageView(mWxPay, View.VISIBLE);
        getImageView(mAliPay, View.GONE);
        selected(mWxPay);
        //微信支付
        mWxPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAYMODE = WXPAY;
                selected(mWxPay);
            }
        });
        //支付宝
        mAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAYMODE = ALIPAY;
                selected(mAliPay);
            }
        });
        mSelectNumListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) return;
                if (PAYMODE == ALIPAY)
                    mAliPayUtils.initPay(String.valueOf(price[position - 1]), String.valueOf(diamondsNum[position - 1]));
                else
                    mWChatPay.initPay(String.valueOf(price[position - 1]), String.valueOf(diamondsNum[position - 1]));
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void initData() {
        requestData();
        mAliPayUtils = new AliPay(this);
        mWChatPay = new WChatPay(this);
        setActionBarTitle(getString(R.string.mydiamonds));
        diamondsNum = new int[]{20, 60, 300, 980, 2980, 5880, 15980};
        String explain[] = {"新人礼包,仅1次机会", "", "", "赠送10钻石", "赠送90钻石", "赠送300钻石", "赠送1120钻石"};
        price = new int[]{1, 6, 30, 98, 298, 588, 1598};
        rechanList = new ArrayList<>();
        for (int i = 0; i < price.length; i++) {
            rechanList.add(new RechargeBean(price[i], explain[i], diamondsNum[i], price[i] + ".00"));
        }
        mSelectNumListItem.setAdapter(new MyAdapter());
        Bundle bundle = getIntent().getBundleExtra("USERINFO");

        mCoin.setText(bundle.getString("diamonds"));

    }

    private void requestData() {
        PhoneLiveApi.getUserDiamondsNum(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response, MyDiamondsActivity.this);
                        if (res == null) return;
                        fillUI(res);
                    }
                });
    }

    private void fillUI(String res) {
        try {
            JSONObject jsonObj = new JSONObject(res);
            mCoin.setText(jsonObj.getString("coin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void selected(RelativeLayout rl) {
        rl.setBackgroundColor(getResources().getColor(R.color.actionbarbackground));
        rl.getChildAt(1).setVisibility(View.VISIBLE);
        mPayName.setText(getString(R.string.paymode) + (PAYMODE == WXPAY ? getString(R.string.wxpay) : getString(R.string.alipay)));
        if (PAYMODE == WXPAY) {
            mAliPay.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            mWxPay.setBackgroundColor(getResources().getColor(R.color.white));
        }
        mTvDiamondsPayWx.setTextColor(getResources().getColor(PAYMODE == WXPAY ? R.color.white : R.color.black));
        mTvDiamondsPayAliPay.setTextColor(getResources().getColor(PAYMODE == WXPAY ? R.color.black : R.color.white));
    }

    private ImageView getImageView(RelativeLayout rl, int displayMode) {
        ImageView imageView = new ImageView(this);
        rl.addView(imageView);
        imageView.setVisibility(displayMode);
        imageView.setImageResource(R.drawable.pay_choose);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = 60;
        params.height = 60;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    //充值结果
    public void rechargeResult(boolean isOk, String rechargeMoney) {
        if (isOk) {
            if (mCoin!=null)
            mCoin.setText((Integer.parseInt(mCoin.getText().toString()) + Integer.parseInt(rechargeMoney) + ""));
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rechanList.size();
        }

        @Override
        public Object getItem(int position) {
            return rechanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RechargeBean rechargeBean = rechanList.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_select_num, null);
                holder = new ViewHolder();
                holder.mDiamondsNum = (TextView) convertView.findViewById(R.id.tv_diamondsnum);
                holder.mPrieExplain = (TextView) convertView.findViewById(R.id.tv_price_explain);
                holder.mPriceText = (TextView) convertView.findViewById(R.id.bt_preice_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mDiamondsNum.setText(rechargeBean.getRecharDiamondsNum() + "");
            holder.mPrieExplain.setText(rechargeBean.getPriceExplain());
            holder.mPriceText.setText(rechargeBean.getPriceText());
            return convertView;
        }

        private class ViewHolder {
            TextView mDiamondsNum, mPrieExplain;
            TextView mPriceText;

        }

    }

    private class RechargeBean {
        public int price;//人民币
        public String priceExplain;//充值说明
        public int recharDiamondsNum;//充值钻石数量
        public String priceText;//充值人民币组成字符

        private RechargeBean(int price, String priceExplain, int recharDiamondsNum, String priceText) {
            this.price = price;
            this.priceExplain = priceExplain;
            this.recharDiamondsNum = recharDiamondsNum;
            this.priceText = priceText;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getPriceExplain() {
            return priceExplain;
        }

        public void setPriceExplain(String priceExplain) {
            this.priceExplain = priceExplain;
        }

        public int getRecharDiamondsNum() {
            return recharDiamondsNum;
        }

        public void setRecharDiamondsNum(int recharDiamondsNum) {
            this.recharDiamondsNum = recharDiamondsNum;
        }

        public String getPriceText() {
            return priceText;
        }

        public void setPriceText(String priceText) {
            this.priceText = priceText;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的钻石"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        requestData();

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的钻石"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
