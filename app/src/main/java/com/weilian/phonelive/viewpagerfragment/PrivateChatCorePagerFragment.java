package com.weilian.phonelive.viewpagerfragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.ViewPageFragmentAdapter;
import com.weilian.phonelive.base.BaseViewPagerFragment;
import com.weilian.phonelive.fragment.FollowPrivateChatFragment;
import com.weilian.phonelive.fragment.NotFollowPrivateChatFragment;

import butterknife.OnClick;


public class PrivateChatCorePagerFragment extends BaseViewPagerFragment {


    @Override
    protected void onSetupTabAdapter(View view,ViewPageFragmentAdapter adapter,ViewPager viewPager) {
        ((ImageView)view.findViewById(R.id.iv_private_chat_back)).setOnClickListener(this);
        initData();
        Bundle b1 = new Bundle();
        b1.putInt("ACTION",1);
        Bundle b2 = new Bundle();
        b2.putInt("ACTION",0);
        adapter.addTab(getString(R.string.friends), "friends", FollowPrivateChatFragment.class, b1);
        adapter.addTab(getString(R.string.nofollow), "nofollow", NotFollowPrivateChatFragment.class, b2);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //注册一个监听连接状态的listener
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_private_chat_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_private_chat_back:
                AppContext.showToastAppMsg(getActivity(), "7");
                getActivity().finish();
                break;
        }
    }
}
