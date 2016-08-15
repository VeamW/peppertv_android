package com.weilian.phonelive.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.UserBaseInfoAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 粉丝列表
 */
public class FansActivity extends BaseActivity {
    @InjectView(R.id.lv_fans)
    ListView mFansView;
    @InjectView(R.id.sr_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.ll_fans_default)
    LinearLayout mLlDefaultBg;
    private List<UserBean> mFanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fans;
    }

    @Override
    public void initView() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFanList.clear();
                initData();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle(getResources().getString(R.string.fans));
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,FansActivity.this);
                if(res != null){
                    try {
                        JSONArray fansJsonArr = new JSONArray(res);
                        if(fansJsonArr.length() > 0){
                            Gson gson = new Gson();
                            for(int i =0;i<fansJsonArr.length(); i++){
                                mFanList.add(gson.fromJson(fansJsonArr.getString(i), UserBean.class));
                            }

                            fillUI();
                        }else {
                            mFansView.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getFansList(getIntent().getIntExtra("uid",0), AppContext.getInstance().getLoginUid(),callback);
    }

    private void fillUI() {
        mLlDefaultBg.setVisibility(View.GONE);
        mFansView.setVisibility(View.VISIBLE);
        mFansView.setAdapter(new UserBaseInfoAdapter(mFanList));
        mFansView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(FansActivity.this, mFanList.get(position).getId());
            }
        });
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("粉丝列表"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("粉丝列表"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
