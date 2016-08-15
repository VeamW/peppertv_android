package com.weilian.phonelive.viewpagerfragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.ViewPageFragmentAdapter;
import com.weilian.phonelive.fragment.FollowPrivateChatFragment;
import com.weilian.phonelive.fragment.NotFollowPrivateChatFragment;

import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/26.
 */
public class PrivateChatCorePagerDialogFragment extends DialogFragment implements View.OnClickListener {
    private PagerSlidingTabStrip mTabStrip;
    private ViewPager mViewPager;
    private ViewPageFragmentAdapter mTabsAdapter;
    private void onSetupTabAdapter(View view, ViewPageFragmentAdapter adapter, ViewPager viewPager) {
        ((ImageView)view.findViewById(R.id.iv_private_chat_back)).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initData();
        Bundle b1 = new Bundle();
        b1.putInt("ACTION",1);
        Bundle b2 = new Bundle();
        b2.putInt("ACTION",0);
        adapter.addTab(getString(R.string.friends), "friends", FollowPrivateChatFragment.class, b1);
        adapter.addTab(getString(R.string.nofollow), "nofollow", NotFollowPrivateChatFragment.class, b2);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);

    }
    public void initData() {

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_viewpage_dialog_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mTabStrip = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(), mViewPager);
        setScreenPageLimit();
        onSetupTabAdapter(view,mTabsAdapter, mViewPager);
        mTabStrip.setViewPager(mViewPager);
        mTabStrip.setDividerColor(getResources().getColor(R.color.global));
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.backgroudcolor));
        mTabStrip.setTextColor(Color.WHITE);
        mTabStrip.setTextSize((int) getResources().getDimension(R.dimen.text_size_15));
        mTabStrip.setIndicatorHeight(10);
        mTabStrip.setUnderlineColorResource(R.color.global);

        // if (savedInstanceState != null) {
        // int pos = savedInstanceState.getInt("position");
        // mViewPager.setCurrentItem(pos, true);
        // }
    }

    protected void setScreenPageLimit() {
    }

    // @Override
    // public void onSaveInstanceState(Bundle outState) {
    // //No call for super(). Bug on API Level > 11.
    // if (outState != null && mViewPager != null) {
    // outState.putInt("position", mViewPager.getCurrentItem());
    // }
    // //super.onSaveInstanceState(outState);
    // }


}
