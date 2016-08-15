package com.weilian.phonelive.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.LiveRecordBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends BaseActivity{
    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;
    ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.liverecord));
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(LiveRecordActivity.this,"获取直播纪录失败");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,LiveRecordActivity.this);
                if(null != res){
                    //TLog.log(res);
                    try {
                        JSONObject liveRecordJsonObj = new JSONObject(res);
                        JSONArray liveRecordJsonArray = liveRecordJsonObj.getJSONArray("list");
                        if(0 < liveRecordJsonArray.length()){
                            Gson g = new Gson();
                            for(int i = 0; i < liveRecordJsonArray.length(); i++){
                                mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i),LiveRecordBean.class));
                            }
                        }
                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        PhoneLiveApi.getLiveRecord(getIntent().getIntExtra("uid",0),callback);
    }

    private void fillUI() {
        mLiveRecordList.setAdapter(new LiveRecordAdapter());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }
    class LiveRecordAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mRecordList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(null == convertView){
                convertView = View.inflate(LiveRecordActivity.this,R.layout.item_live_record,null);
                viewHolder = new ViewHolder();
                viewHolder.mLiveNum = (TextView) convertView.findViewById(R.id.tv_item_live_record_num);
                viewHolder.mLiveTime = (TextView) convertView.findViewById(R.id.tv_item_live_record_time);
                viewHolder.mLiveTitle = (TextView) convertView.findViewById(R.id.tv_item_live_record_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            LiveRecordBean l = mRecordList.get(position);
            viewHolder.mLiveNum.setText(l.getNums());
            viewHolder.mLiveTitle.setText(l.getTitle());
            viewHolder.mLiveTime.setText(l.getDatetime());
            return convertView;
        }
        class ViewHolder{
            public TextView mLiveTime,mLiveNum,mLiveTitle;
        }
    }


}
