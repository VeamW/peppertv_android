package com.weilian.phonelive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.VodBean;
import com.weilian.phonelive.utils.TimeFormater;

import org.kymjs.kjframe.Core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrato on 2016/8/24.
 */
public class RecentlyVisitedAdapter extends BaseAdapter {


    private List<VodBean.DataBean.InfoBean.DataBeanb> mDataBeanbList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public RecentlyVisitedAdapter(List<VodBean.DataBean.InfoBean.DataBeanb> list, Context context) {
        this.mDataBeanbList = list;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mDataBeanbList.size();
    }

    @Override
    public VodBean.DataBean.InfoBean.DataBeanb getItem(int position) {
        return mDataBeanbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_vod, null);
            holder.mImgBg = (ImageView) convertView.findViewById(R.id.item_vod_img);
            holder.mTxtContent = (TextView) convertView.findViewById(R.id.item_vod_content);
            holder.mTxtVisitCount = (TextView) convertView.findViewById(R.id.item_vod_visit_count);
            holder.mTxtVisitPost = (TextView) convertView.findViewById(R.id.item_vod_time_post);
            holder.mTxtVisitTime = (TextView) convertView.findViewById(R.id.item_vod_time_visited);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VodBean.DataBean.InfoBean.DataBeanb bean = getItem(position);
        holder.mTxtContent.setText(bean.getTitle());
        try {
            holder.mTxtVisitTime.setText(TimeFormater.getminTime(bean.getStarttime(), bean.getEndtime()));
            holder.mTxtVisitPost.setText(TimeFormater.getInterval(bean.getStarttime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mTxtVisitCount.setText(bean.getNums() + "人已观看");
        Glide.with(mContext).load(bean.getVideo_pic()).centerCrop().skipMemoryCache(false)
                .crossFade().into(holder.mImgBg);
        return convertView;
    }


    private  class ViewHolder {
        TextView mTxtVisitCount, mTxtVisitTime, mTxtVisitPost, mTxtContent;
        ImageView mImgBg;
    }


    private void setData(List<VodBean.DataBean.InfoBean.DataBeanb> list) {
        this.mDataBeanbList = list;
        notifyDataSetChanged();
    }


}
