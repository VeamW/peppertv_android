package com.weilian.phonelive.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.Core;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 最新
 */
public class NewestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    List<UserBean> mUserList = new ArrayList<>();
    @InjectView(R.id.gv_newest)
    GridView mNewestLiveView;
    @InjectView(R.id.sl_newest)
    SwipeRefreshLayout mRefresh;
    private int wh;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_newest,null);
            ButterKnife.inject(this,view);
        }
        initView(view);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            initData();
        }
    }


    @Override
    public void initData() {
        requestData();
    }

    private void requestData() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                if(null==mRefresh) return;
                mRefresh.setRefreshing(false);
                AppContext.showToastAppMsg(getActivity(),"获取最新直播失败");
            }

            @Override
            public void onResponse(String response) {
                if(null==mRefresh) return;
                mRefresh.setRefreshing(false);

                String res = ApiUtils.checkIsSuccess(response,getActivity());
                if(null != res){
                    try {
                        mUserList.clear();
                        JSONArray resUserListJsonArr = new JSONArray(res);
                        Gson g = new Gson();
                        for(int i=0;i<resUserListJsonArr.length(); i++){
                            mUserList.add(g.fromJson(resUserListJsonArr.getString(i),UserBean.class));
                        }
                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getNewestUserList(callback);
    }

    private void fillUI() {
        if(getActivity() != null ){
            int w = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            wh = w/3;
            mNewestLiveView.setColumnWidth(wh);
            mNewestLiveView.setAdapter(new NewestAdapter());
        }
    }

    @Override
    public void initView(View view) {
        if (null==mNewestLiveView||null==mRefresh) return;
        mNewestLiveView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBean user = mUserList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_INFO",user);
                UIHelper.showLookLiveActivity(getActivity(),bundle);
            }
        });
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        requestData();
    }


    class NewestAdapter extends BaseAdapter{

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
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.item_newest_user,null);
                convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,wh));
                viewHolder = new ViewHolder();
                viewHolder.mUHead = (LoadUrlImageView) convertView.findViewById(R.id.iv_newest_item_user);
                viewHolder.mUHead.getLayoutParams();
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            UserBean u = mUserList.get(position);
            //viewHolder.mUHead.setImageLoadUrl(u.getAvatar());
            Glide.with(NewestFragment.this)
                    .load(u.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .into(viewHolder.mUHead);
            return convertView;
        }
        class ViewHolder{
            public LoadUrlImageView mUHead;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("最新直播"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("最新直播"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
