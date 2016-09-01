package com.weilian.phonelive.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.widget.CircleImageView;

import org.kymjs.kjframe.Core;

import java.util.ArrayList;
import java.util.List;

public class UserBaseInfoPrivateChatAdapter extends BaseAdapter {
    private List<PrivateChatUserBean> users;
    public UserBaseInfoPrivateChatAdapter(List<PrivateChatUserBean> users) {
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
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_private_chat,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (TextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mULastMsg = (TextView) convertView.findViewById(R.id.tv_item_last_msg);
            viewHolder.mUnread = (ImageView) convertView.findViewById(R.id.iv_unread_dot);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrivateChatUserBean u = users.get(position);
        Core.getKJBitmap().display(viewHolder.mUHead, u.getAvatar());

        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel()-1<0?0:u.getLevel()-1]);
        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mULastMsg.setText(u.getLastMessage());
        viewHolder.mUnread.setVisibility(u.isUnreadMessage()?View.VISIBLE:View.GONE);

        return convertView;
    }



    public void setPrivateChatUserList(ArrayList<PrivateChatUserBean> privateChatUserList) {
        if (this.users != null) {
            this.users.clear();
            if (privateChatUserList != null) {
                this.users.addAll(privateChatUserList);
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder{
        public CircleImageView mUHead;
        public ImageView mUSex,mULevel,mUnread;
        public TextView mUNice,mULastMsg;
    }
}
