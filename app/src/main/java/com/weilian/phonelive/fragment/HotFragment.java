package com.weilian.phonelive.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.VideoPlayerActivity;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.weilian.phonelive.viewpagerfragment.IndexPagerFragment;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * @author 魏鹏
 * @dw 首页热门
 */
public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.lv_live_room)
    ListView mListUserRoom;
    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.ll_hot_default_layout)
    LinearLayout mDefaultLayoutView;
    private List<UserBean> mUserList = new ArrayList<>();
    private LayoutInflater inflater;
    private HotUserListAdapter mHotUserListAdapter;
    private Handler handler;
    private View view;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_index_hot, null);
            ButterKnife.inject(this, view);
            initData();
        }
        initView();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }


    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mListUserRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) return;
                UserBean user = mUserList.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putSerializable(VideoPlayerActivity.USER_INFO, user);
                UIHelper.showLookLiveActivity(getActivity(), bundle);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initData() {
        if (mHotUserListAdapter == null) {
            mHotUserListAdapter = new HotUserListAdapter();
            mListUserRoom.addHeaderView(inflater.inflate(R.layout.view_hot_rollpic, null));
        }
        PhoneLiveApi.getIndexHotUserList(callback);
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 60000);

    }

    private void fillUI() {
        if (null != mDefaultLayoutView) {
            mDefaultLayoutView.setVisibility(View.GONE);
            mDefaultLayoutView = null;
        }
        if (null == mListUserRoom || null == mSwipeRefreshLayout || null == mHotUserListAdapter)
            return;
        mListUserRoom.setVisibility(View.VISIBLE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mHotUserListAdapter.notifyDataSetChanged();
        } else {
            mListUserRoom.setAdapter(mHotUserListAdapter);
        }

    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(getActivity(), "获取数据失败请刷新重试~");
            if (null == mSwipeRefreshLayout) return;
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onResponse(String s) {
            if (null == mSwipeRefreshLayout) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mUserList.clear();
            String res = ApiUtils.checkIsSuccess(s, getActivity());
            try {
                if (res == null) return;
                JSONArray resJa = new JSONArray(res);
                if (resJa == null) return;
                if (resJa.length() > 0) {
                    for (int i = 0; i < resJa.length(); i++) {
                        UserBean user = new Gson().fromJson(resJa.getJSONObject(i).toString(), UserBean.class);
                        mUserList.add(user);
                    }
                    fillUI();
                } else {
                    if (mDefaultLayoutView != null)
                        mDefaultLayoutView.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (IndexPagerFragment.mSex != 0 || !IndexPagerFragment.mArea.equals("")) {
            selectTermsScreen(IndexPagerFragment.mSex, IndexPagerFragment.mArea);
        }

    }

    public void selectTermsScreen(int sex, String area) {
        PhoneLiveApi.selectTermsScreen(sex, area, callback);
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        //PhoneLiveApi.getIndexHotUserList(callback);
        PhoneLiveApi.selectTermsScreen(IndexPagerFragment.mSex, IndexPagerFragment.mArea, callback);
    }

    private class HotUserListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_hot_user, null);
                viewHolder = new ViewHolder();
                viewHolder.mUserNick = (TextView) convertView.findViewById(R.id.tv_live_nick);
                viewHolder.mUserLocal = (TextView) convertView.findViewById(R.id.tv_live_local);
                viewHolder.mUserNums = (TextView) convertView.findViewById(R.id.tv_live_usernum);
                viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
                viewHolder.mUserPic = (LoadUrlImageView) convertView.findViewById(R.id.iv_live_user_pic);
                viewHolder.mRoomTitle = (TextView) convertView.findViewById(R.id.tv_hot_room_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserBean user = mUserList.get(position);
            viewHolder.mUserNick.setText(user.getUser_nicename());
            viewHolder.mUserLocal.setText(user.getCity());
            //viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());
            //用于加载图片可以平滑滚动
            Glide
                    .with(HotFragment.this)
                    .load(user.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .into(viewHolder.mUserPic);
//            viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());

        /*    Glide
                    .with(HotFragment.this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.null_blacklist)
                    .skipMemoryCache(false)
                    .into(viewHolder.mUserHead);*/
            viewHolder.mUserHead.setAvatarUrl(user.getAvatar());
            viewHolder.mUserNums.setText(String.valueOf(user.getNums() + "人正在观看"));
            if (null != user.getTitle()) {
                viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
                viewHolder.mRoomTitle.setText(user.getTitle());
            }
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView mUserNick, mUserLocal, mUserNums, mRoomTitle;
        public LoadUrlImageView mUserPic;
        public AvatarView mUserHead;
    }



}
