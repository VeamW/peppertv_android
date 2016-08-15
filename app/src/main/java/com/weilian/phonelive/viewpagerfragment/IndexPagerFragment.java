package com.weilian.phonelive.viewpagerfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.ViewPageFragmentAdapter;
import com.weilian.phonelive.base.BaseFragment;
import com.weilian.phonelive.fragment.AttentionFragment;
import com.weilian.phonelive.fragment.HotFragment;
import com.weilian.phonelive.fragment.NewestFragment;
import com.weilian.phonelive.interf.PagerSlidingInterface;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IndexPagerFragment extends BaseFragment{

    private View view;
    @InjectView(R.id.mviewpager)
    ViewPager pager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.iv_hot_select_region)
    ImageView mRegion;
    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;
    private ViewPageFragmentAdapter viewPageFragmentAdapter;
    public static int mSex = 0;
    public static String mArea = "";
    private BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null){
            view = inflater.inflate(R.layout.fragment_hot,null);
            ButterKnife.inject(this,view);
            initView();
            initData();
        }else{
            tabs.setViewPager(pager);
        }

        return view;
    }

    @Override
    public void initData() {
        //获取私信未读数量
        int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if(count > 0){
            mIvNewMessage.setVisibility(View.VISIBLE);
        }
        IntentFilter intentFilter = new IntentFilter("com.weilian.phonelive");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIvNewMessage.setVisibility(View.VISIBLE);
            }
        };
        getActivity().registerReceiver(broadcastReceiver,intentFilter);

    }

    @OnClick({R.id.iv_hot_private_chat,R.id.iv_hot_search})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_hot_private_chat:
                int uid = AppContext.getInstance().getLoginUid();
                if(0 < uid){
                    mIvNewMessage.setVisibility(View.GONE);
                    UIHelper.showPrivateChatSimple(getActivity(),uid);
                }

                break;
            case R.id.iv_hot_search:
                UIHelper.showScreen(getActivity());
                break;
        }
    }

    private void initView() {

        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getFragmentManager(),pager);
        viewPageFragmentAdapter.addTab(getString(R.string.attention), "gz", AttentionFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.hot), "rm", HotFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.daren), "dr", NewestFragment.class, getBundle());

        pager.setAdapter(viewPageFragmentAdapter);

        pager.setOffscreenPageLimit(2);
        tabs.setViewPager(pager);
        tabs.setUnderlineColor(getResources().getColor(R.color.gray2));
        tabs.setDividerColor(getResources().getColor(R.color.gray2));
        tabs.setIndicatorColor(getResources().getColor(R.color.gray2));
        tabs.setTextColor(Color.BLACK);
        tabs.setTextSize((int) getResources().getDimension(R.dimen.text_size_6));
        tabs.setSelectedTextColor(getResources().getColor(R.color.global));
        tabs.setIndicatorHeight(2);
        tabs.setZoomMax(0f);
        tabs.setIndicatorColorResource(R.color.global);
        tabs.setPagerSlidingListen(new PagerSlidingInterface() {
            @Override
            public void onItemClick(View v,int currentPosition,int position) {

                if(currentPosition == position && position == 1){
                    UIHelper.showSelectArea(getActivity());
                }
            }
        });

        pager.setCurrentItem(1);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mRegion.setVisibility(1 == position? View.VISIBLE:View.GONE);
                //tabs.setIndicatorColorResource(1 == position? R.color.white:R.color.global);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();

    }

    private Bundle getBundle( ) {
       Bundle bundle = new Bundle();

       return bundle;
   }
}
