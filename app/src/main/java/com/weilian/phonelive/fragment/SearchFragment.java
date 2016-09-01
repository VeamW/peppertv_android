package com.weilian.phonelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.UserBaseInfoAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 搜索
 */
public class SearchFragment extends BaseFragment {
    @InjectView(R.id.et_search_input)
    EditText mSearchKey;
    @InjectView(R.id.lv_search)
    ListView mLvSearch;
    private List<UserBean> mUserList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_index, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        if (null == mLvSearch) return;
        mLvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) return;
                UIHelper.showHomePageActivity(getActivity(), mUserList.get(position).getId());
            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.iv_private_chat_back, R.id.tv_search_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_private_chat_back:
                getActivity().finish();
                break;
            case R.id.tv_search_btn:
                search();
                break;

        }
    }

    private void search() {
        showWaitDialog();
        String screenKey = mSearchKey.getText().toString().trim();
        if (!screenKey.equals("")) {
            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    hideWaitDialog();
                }

                @Override
                public void onResponse(String response) {
                    hideWaitDialog();
                    String res = ApiUtils.checkIsSuccess(response, getActivity());

                    if (null != res) {
                        Gson g = new Gson();
                        try {
                            JSONArray searchUserJsonArray = new JSONArray(res);
                            mUserList.clear();
                            for (int i = 0; i < searchUserJsonArray.length(); i++) {
                                mUserList.add(g.fromJson(searchUserJsonArray.getString(i), UserBean.class));
                            }
                            fillUI();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            PhoneLiveApi.search(screenKey, callback, AppContext.getInstance().getLoginUid());
        }
    }

    private void fillUI() {
        if (null == mLvSearch) return;
        mLvSearch.setAdapter(new UserBaseInfoAdapter(mUserList));

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("搜索"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("搜索"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
