package com.weilian.phonelive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.LiveUserAdapter;
import com.weilian.phonelive.adapter.RecentlyVisitedAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.bean.VodBean;
import com.weilian.phonelive.ui.VideoPlayerActivity;
import com.weilian.phonelive.ui.VodVideoPlayerActivity;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by administrato on 2016/8/24.
 */
public class MsjFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.hot_private_chat)
    ImageView mHotPrivateChat;
    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvHotNewMessage;
    @InjectView(R.id.listview_visited)
    ListView mListviewVisited;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.lv_live_room)
    ListView mLvLiveRoom;
    @InjectView(R.id.rel_top)
    RelativeLayout mRelTop;
    @InjectView(R.id.count_visited)
    TextView mCountVisited;
    @InjectView(R.id.img_loading)
    ImageView mImgLoading;
    @InjectView(R.id.img_empty)
    ImageView mImgEmpty;
    @InjectView(R.id.img_error)
    ImageView mImgError;
    @InjectView(R.id.rel_loading_data)
    RelativeLayout mRelLoadingData;

    private View mView;
    private List<VodBean.DataBean.InfoBean.DataBeanb> mUserList = new ArrayList();
    List<UserBean> mAttentionUsers = new ArrayList<>();
    private RecentlyVisitedAdapter mVisitedAdapter;
    private BroadcastReceiver broadcastReceiver;
    private LiveUserAdapter mAdapter;

    private int uid = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        uid = AppContext.getInstance().getLoginUid();
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_msj, container, false);
        }
        initData();
        ButterKnife.inject(this, mView);
        initView();
        initEm();
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        return mView;
    }

    /**
     * init view
     */
    public void initView() {
        if (mRefreshLayout == null || mListviewVisited == null || mLvLiveRoom == null) return;
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mRefreshLayout.setOnRefreshListener(this);
        mListviewVisited.setOnItemClickListener(this);
        mLvLiveRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLvLiveRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (Utils.isFastClick()) return;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(VideoPlayerActivity.USER_INFO, mAttentionUsers.get(position));
                        UIHelper.showLookLiveActivity(getActivity(), bundle);
                    }
                });
            }
        });
    }


    @Override
    public void initData() {
        getData();
    }

    public void initEm() {
        //获取私信未读数量
        int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (count > 0) {
            if (mIvHotNewMessage == null) return;
            mIvHotNewMessage.setVisibility(View.VISIBLE);
        }
        IntentFilter intentFilter = new IntentFilter("com.weilian.phonelive");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIvHotNewMessage.setVisibility(View.VISIBLE);
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }


    /**
     * onclick listener
     *
     * @param v
     */
    @OnClick({R.id.hot_private_chat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot_private_chat:
                int uid = AppContext.getInstance().getLoginUid();
                if (0 < uid) {
                    if (mIvHotNewMessage != null) {
                        mIvHotNewMessage.setVisibility(View.GONE);
                    }
                    UIHelper.showPrivateChatSimple(getActivity(), uid);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (Utils.isFastClick()) return;
        VodBean.DataBean.InfoBean.DataBeanb user = mUserList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VodVideoPlayerActivity.USER_INFO, user);
        UIHelper.showVodActivity(getActivity(), bundle);

    }

    /**
     * callback for get data
     */
    StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            if (null != mRefreshLayout && mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(false);
            if (null != mRelLoadingData)
                mRelLoadingData.setVisibility(View.VISIBLE);
            if (null != mImgError)
                mImgError.setVisibility(View.VISIBLE);
            if (null != mImgLoading)
                mImgLoading.setVisibility(View.GONE);
            if (null != mImgEmpty)
                mImgEmpty.setVisibility(View.GONE);
        }

        @Override
        public void onResponse(String response) {
            if (null != mRefreshLayout && mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(false);
            if (null != mRelLoadingData)
                mRelLoadingData.setVisibility(View.GONE);

            try {
                if (null == response) return;
                JSONObject jsonObject = new JSONObject(response);
                if (null == jsonObject) return;
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                if (null == jsonObject1) return;
                VodBean.DataBean.InfoBean bean = new Gson().fromJson(jsonObject1.getString("info"), VodBean.DataBean.InfoBean.class);
                if (null == bean) return;
                mUserList = bean.getData();
                initUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != mCountVisited) {
                if (mUserList.size() <= 0) {
                    mCountVisited.setText("暂无访客记录");
                } else {
                    mCountVisited.setText(mUserList.size() + "个近期访客");
                }
            }
        }
    };

    private void initUI() {
        if (null == mListviewVisited) return;
        mVisitedAdapter = new RecentlyVisitedAdapter(mUserList, getActivity());
        mListviewVisited.setAdapter(mVisitedAdapter);
        int totalHeight = 0;
        for (int i = 0, len = mVisitedAdapter.getCount(); i < len; i++) {
            View listItem = mVisitedAdapter.getView(i, null, mListviewVisited);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListviewVisited.getLayoutParams();
        params.height = totalHeight + (mListviewVisited.getDividerHeight() * (mVisitedAdapter.getCount() - 1));
        mListviewVisited.setLayoutParams(params);
    }

    /**
     * get attentionlive data
     */
    StringCallback attentioncallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            if (null != mRefreshLayout && mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(false);
            if (null != mRelLoadingData)
                mRelLoadingData.setVisibility(View.VISIBLE);
            if (null != mImgError)
                mImgError.setVisibility(View.VISIBLE);
            if (null != mImgLoading)
                mImgLoading.setVisibility(View.GONE);
            if (null != mImgEmpty)
                mImgEmpty.setVisibility(View.GONE);
        }

        @Override
        public void onResponse(String response) {
            if (null != mRefreshLayout && mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(false);
            if (null != mRelLoadingData)
                mRelLoadingData.setVisibility(View.GONE);

            String res = ApiUtils.checkIsSuccess(response, getActivity());
            if (null != res) {
                mAttentionUsers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if (jsonObject.get("attentionlive") == null || jsonObject.get("attentionlive").toString().equals("{}") || jsonObject.get("attentionlive").toString().isEmpty() || jsonObject.get("attentionlive").toString().equals("[]"))
                        return;
                    JSONArray liveAndAttentionUserJson = jsonObject.getJSONArray("attentionlive");
                    if (0 == liveAndAttentionUserJson.length())
                        return;
                    Gson g = new Gson();
                    for (int i = 0; i < liveAndAttentionUserJson.length(); i++) {
                        mAttentionUsers.add(g.fromJson(liveAndAttentionUserJson.getString(i), UserBean.class));
                    }
                    refreshAttention();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * refresh attention
     */
    private void refreshAttention() {
        if (null == mLvLiveRoom || mRelTop == null || mAttentionUsers.size() <= 0) return;
        mAdapter = new LiveUserAdapter(getActivity(), mAttentionUsers);
        mLvLiveRoom.setAdapter(mAdapter);
        int totalHeight = 0;
        for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = mAdapter.getView(i, null, mLvLiveRoom);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mLvLiveRoom.getLayoutParams();
        params.height = totalHeight + (mLvLiveRoom.getDividerHeight() * (mAdapter.getCount() - 1));
        // listView.getDividerHeight()获
        mLvLiveRoom.setLayoutParams(params);
        if (null == mAdapter || mAttentionUsers.isEmpty() || mAttentionUsers.size() <= 0) {
            mLvLiveRoom.setVisibility(View.GONE);
            mRelTop.setVisibility(View.VISIBLE);
            return;
        } else {
            mLvLiveRoom.setVisibility(View.VISIBLE);
            mRelTop.setVisibility(View.GONE);
        }
        if (null != mAdapter)
            mAdapter.notifyDataSetChanged();
    }


    /**
     * get data
     */
    private void getData() {
        PhoneLiveApi.getVodList(0, uid, 20, callback);
        PhoneLiveApi.getAttentionLive(uid, attentioncallback);
    }


    @Override
    public void onRefresh() {
        getData();

    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = null;
        mVisitedAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
