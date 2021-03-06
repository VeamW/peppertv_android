package com.weilian.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.base.ShowLiveActivityBase;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.widget.AvatarView;

/**
 * 用户列表adapter
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolderUserList> {
    private SparseArray<UserBean> mUsers = new SparseArray<>();
    private LayoutInflater mLayoutInflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public UserListAdapter(LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
    }

    public void setUserList(SparseArray<UserBean> users){
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderUserList onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderUserList holderUserList = new ViewHolderUserList(mLayoutInflater.inflate(R.layout.item_live_user_list,parent,false));

        return holderUserList;
    }

    @Override
    public void onBindViewHolder(ViewHolderUserList holder, final int position) {
        holder.mUhead.setAvatarUrl(mUsers.valueAt(position).getAvatar());
        //holder.mUhead.setTag(position);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.mUhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOnItemClickListener){
                    mOnItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    protected class ViewHolderUserList extends RecyclerView.ViewHolder {
        public AvatarView mUhead;

        public ViewHolderUserList(View itemView) {
            super(itemView);
            mUhead = (AvatarView) itemView.findViewById(R.id.av_userHead);

        }

    }
    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view,int data);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
