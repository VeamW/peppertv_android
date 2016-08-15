package com.weilian.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.LiveUserAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.VideoPlayerActivity;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 首页左边关注
 */
public class AttentionFragment extends BaseFragment {
    @InjectView(R.id.lv_attentions)
    ListView mLvAttentions;
    @InjectView(R.id.rl_attention_no_live)
    RelativeLayout mAttentionNoLiveShowView;
    List<UserBean> mUserList = new ArrayList<>();
    @InjectView(R.id.mSwipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    private View view;
    private LiveUserAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_attention, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserList.clear();
                initData();
            }
        });
    }

    @Override
    public void initData() {
        int uid = AppContext.getInstance().getLoginUid();
        if (!(0 < uid))
            return;
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (mRefresh.isRefreshing()) {
                    mRefresh.setRefreshing(false);
                }
                String res = ApiUtils.checkIsSuccess(response, getActivity());
                if (null != res) {

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        TLog.error(jsonObject.toString());
                        JSONArray liveAndAttentionUserJson = jsonObject.getJSONArray("playback");
                        if (0 == liveAndAttentionUserJson.length())
                            return;
                        Gson g = new Gson();
                        for (int i = 0; i < liveAndAttentionUserJson.length(); i++) {
                            mUserList.add(g.fromJson(liveAndAttentionUserJson.getString(i), UserBean.class));
                        }

                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getAttentionLive(AppContext.getInstance().getLoginUid(), callback);


    }

    private void fillUI() {
        mAttentionNoLiveShowView.setVisibility(View.GONE);
        mLvAttentions.setVisibility(View.VISIBLE);
        if (mAdapter == null) {
            mAdapter = new LiveUserAdapter(this, getActivity().getLayoutInflater(), mUserList);
        }
        mLvAttentions.setAdapter(mAdapter);
        mLvAttentions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(VideoPlayerActivity.USER_INFO, mUserList.get(position));
                UIHelper.showLookLiveActivity(getActivity(), bundle);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }
}
