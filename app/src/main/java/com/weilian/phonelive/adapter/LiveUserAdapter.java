package com.weilian.phonelive.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.transcode.GlideBitmapDrawableTranscoder;
import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.LoadUrlImageView;

import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;


public class LiveUserAdapter extends BaseAdapter {
    private List<UserBean> mUserList;
    private LayoutInflater inflater;
    private Fragment mFragment;

    public LiveUserAdapter(Fragment fragment,LayoutInflater inflater, List<UserBean> mUserList) {
        this.mUserList = mUserList;
        this.inflater = inflater;
        this.mFragment = fragment;
    }

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
            convertView = inflater.inflate(R.layout.item_hot_user,null);
            viewHolder = new ViewHolder();
            viewHolder.mUserNick = (TextView) convertView.findViewById(R.id.tv_live_nick);
            viewHolder.mUserLocal = (TextView) convertView.findViewById(R.id.tv_live_local);
            viewHolder.mUserNums = (TextView) convertView.findViewById(R.id.tv_live_usernum);
            viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
            viewHolder.mUserPic = (LoadUrlImageView) convertView.findViewById(R.id.iv_live_user_pic);
            viewHolder.mRoomTitle = (TextView) convertView.findViewById(R.id.tv_hot_room_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserBean user = mUserList.get(position);
        viewHolder.mUserNick.setText(user.getUser_nicename());
        viewHolder.mUserLocal.setText(user.getCity());
        //viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());
        viewHolder.mUserHead.setAvatarUrl(user.getAvatar());
        viewHolder.mUserNums.setText(String.valueOf(user.getNums()));
        //用于平滑加载图片
        Glide
                .with(mFragment)
                .load(user.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.null_blacklist)
                .crossFade()
                .into(viewHolder.mUserPic);
        if(null !=user.getTitle()){
            viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
            viewHolder.mRoomTitle.setText(user.getTitle());
        }
        return convertView;
    }
    private class ViewHolder{
        public TextView mUserNick,mUserLocal,mUserNums,mRoomTitle;
        public LoadUrlImageView mUserPic;
        public AvatarView mUserHead;
    }

}


