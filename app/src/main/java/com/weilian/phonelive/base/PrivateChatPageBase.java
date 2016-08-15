package com.weilian.phonelive.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.weilian.phonelive.R;
import com.weilian.phonelive.base.BaseFragment;

import butterknife.ButterKnife;


public abstract class PrivateChatPageBase extends BaseFragment {
    private BroadcastReceiver broadCastReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_private_chat,null);
        initCreateView(inflater,container,savedInstanceState);
        ButterKnife.inject(this,v);
        initBroadCast();
        initView(v);
        initData();
        return v;
    }

    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.weilian.phonelive");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    onNewMessage((EMMessage)intent.getParcelableExtra("cmd_value"));
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }


    protected abstract void initCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void onNewMessage(EMMessage messages);

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadCastReceiver);
    }
}
