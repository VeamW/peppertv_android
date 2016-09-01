package com.weilian.phonelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.BlackInfoAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.BlackBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/4/19.
 */
public class BlackListFragment extends BaseFragment {

    @InjectView(R.id.img_loading)
    ImageView mImgLoading;
    @InjectView(R.id.img_empty)
    ImageView mImgEmpty;
    @InjectView(R.id.img_error)
    ImageView mImgError;
    @InjectView(R.id.rel_loading_data)
    RelativeLayout mRelLoadingData;
    private List<BlackBean> mBlackList = new ArrayList<>();
    @InjectView(R.id.lv_black_list)
    ListView mLvBlackList;
    private BlackInfoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_black_list, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        AppContext.showToastAppMsg(getActivity(), "长按可解除拉黑");
        mAdapter = new BlackInfoAdapter(mBlackList);
        if (null == mLvBlackList) return;
        mLvBlackList.setAdapter(mAdapter);
        mLvBlackList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                relieveBlack(position);
                return false;
            }
        });
        requestData(false);
    }

    /**
     * 移除黑名单
     *
     * @param position
     */
    private void relieveBlack(final int position) {
        try {
            PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(), mBlackList.get(position).getId(),
                    AppContext.getInstance().getToken(),
                    new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e) {
                            AppContext.showToastAppMsg(getActivity(), "解除拉黑失败");
                        }

                        @Override
                        public void onResponse(String response) {
                            AppContext.showToastAppMsg(getActivity(), "解除拉黑成功");

                            try {
                                EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mBlackList.get(position).getId()));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                            mBlackList.remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.getBlackList(AppContext.getInstance().getLoginUid(), callback);
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
          if (mRelLoadingData==null||mImgError==null||mImgEmpty==null||mImgLoading==null) return;
            mRelLoadingData.setVisibility(View.VISIBLE);
            mImgError.setVisibility(View.VISIBLE);
            mImgLoading.setVisibility(View.GONE);
            mImgEmpty.setVisibility(View.GONE);
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response, getActivity());
            if (mRelLoadingData==null||mImgError==null||mImgEmpty==null||mImgLoading==null) return;
            if (null == res){
                mRelLoadingData.setVisibility(View.VISIBLE);
                mImgError.setVisibility(View.GONE);
                mImgLoading.setVisibility(View.GONE);
                mImgEmpty.setVisibility(View.VISIBLE);
                return;
            }
            try {
                JSONArray blackJsonArray = new JSONArray(res);
                if (blackJsonArray.length() <= 0) {
                    mRelLoadingData.setVisibility(View.VISIBLE);
                    mImgError.setVisibility(View.GONE);
                    mImgLoading.setVisibility(View.GONE);
                    mImgEmpty.setVisibility(View.VISIBLE);
                }else{
                    mRelLoadingData.setVisibility(View.GONE);
                }
                if (blackJsonArray.length() > 0) {
                    Gson g = new Gson();
                    for (int i = 0; i < blackJsonArray.length(); i++) {
                        mBlackList.add(g.fromJson(blackJsonArray.getString(i), BlackBean.class));
                    }
                    fillUI();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void fillUI() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
