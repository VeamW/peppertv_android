package com.weilian.phonelive.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.bean.BlackBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.widget.CircleImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.kymjs.kjframe.Core;

import java.util.List;

import okhttp3.Call;

/**
 *
 */
public class BlackInfoAdapter extends BaseAdapter {
    private List<BlackBean> users;
    public BlackInfoAdapter(List<BlackBean> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_black_info,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (TextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mUSign = (TextView) convertView.findViewById(R.id.tv_item_usign);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BlackBean u = users.get(position);
        Core.getKJBitmap().display(viewHolder.mUHead, u.getAvatar());
        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel()-1]);
        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mUSign.setText(u.getSignature());
        return convertView;
    }
    private class ViewHolder{
        public CircleImageView mUHead;
        public ImageView mUSex,mULevel;
        public TextView mUNice,mUSign;
    }
}
