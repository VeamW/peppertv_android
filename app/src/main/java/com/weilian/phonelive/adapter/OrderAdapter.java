package com.weilian.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.OrderBean;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.widget.CircleImageView;

import org.kymjs.kjframe.Core;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/20.
 */
public class OrderAdapter extends BaseAdapter {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public OrderAdapter(ArrayList<OrderBean> mOrderList, LayoutInflater mInflater, Context mContext) {
        this.mOrderList = mOrderList;
        this.mInflater = mInflater;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {

        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //if(convertView == null){
        if(position == 0){
            convertView = View.inflate(mContext, R.layout.view_order_top1,null);
        }else if(position == 1){
            convertView = View.inflate(mContext,R.layout.view_order_top2,null);
        }else if(position == 2){
            convertView = View.inflate(mContext,R.layout.view_order_top3,null);
        }else{
            convertView = View.inflate(mContext,R.layout.item_order_user,null);
        }

        viewHolder = new ViewHolder();
        viewHolder.mOrderUhead = (CircleImageView) convertView.findViewById(R.id.ci_order_item_u_head);
        viewHolder.mOrderULevel = (ImageView) convertView.findViewById(R.id.tv_order_item_u_level);
        viewHolder.mOrderUSex = (ImageView) convertView.findViewById(R.id.iv_order_item_u_sex);
        viewHolder.mOrderUname = (TextView) convertView.findViewById(R.id.tv_order_item_u_name);
        viewHolder.mOrderUGx = (TextView) convertView.findViewById(R.id.tv_order_item_u_gx);
        viewHolder.mOrderNo = (TextView) convertView.findViewById(R.id.tv_order_item_u_no);
        convertView.setTag(viewHolder);
            /*}else{
                viewHolder = (ViewHolder) convertView.getTag();
            }*/

        OrderBean o = mOrderList.get(position);
        Core.getKJBitmap().display(viewHolder.mOrderUhead,o.getAvatar());
        viewHolder.mOrderULevel.setImageResource(DrawableRes.LevelImg[o.getLevel() == 0?0:o.getLevel()-1]);
        viewHolder.mOrderUSex.setImageResource(o.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        viewHolder.mOrderUname.setText(o.getUser_nicename());
        viewHolder.mOrderUGx.setText("贡献:" + o.getTotal() + mContext.getResources().getString(R.string.yingpiao));
        if(position > 2){
            viewHolder.mOrderNo.setText("No." + (position+1));
        }
        return convertView;
    }
    class ViewHolder{
        public CircleImageView mOrderUhead;
        public ImageView mOrderUSex,mOrderULevel;
        public TextView mOrderUname,mOrderUGx,mOrderNo;
    }
}