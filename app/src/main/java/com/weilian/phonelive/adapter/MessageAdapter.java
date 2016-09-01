package com.weilian.phonelive.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.weilian.phonelive.widget.PhoneLiveChatRow;
import com.weilian.phonelive.widget.PhoneLiveChatRowText;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private List<EMMessage> mChats = new ArrayList<>();
    private LayoutInflater inflate;
    private Context context;
    private int uid;
    public MessageAdapter(int uid, Context context) {
        this.uid = uid;
        this.context = context;
        this.inflate = LayoutInflater.from(context);

    }
    public void setChatList(List<EMMessage> mChats){
        this.mChats = mChats;
    }
    public void addMessage(EMMessage emMessage){
        this.mChats.add(emMessage);
    }


    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        EMMessage e = mChats.get(position);

        /*if(convertView == null){
            convertView = createChatRow(context, e, position);
        }*/
        convertView = createChatRow(context, e, position);
        //缓存的view的message很可能不是当前item的，传入当前message和position更新ui
        ((PhoneLiveChatRow)convertView).setUpView(e, position);
        return convertView;
    }

    private View createChatRow(Context context, EMMessage e, int position) {
        PhoneLiveChatRow chatRow = null;
        if (e==null) return chatRow;
        switch (e.getType()) {
            case TXT:
                chatRow = new PhoneLiveChatRowText(context, e, position, this);
                break;

        }
        return chatRow;
    }


}
