package com.weilian.phonelive.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.ViewPageFragmentAdapter;
import com.weilian.phonelive.widget.PagerSlidingTabStrip;


/**
 * 带有导航条的基类
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ViewPageFragmentAdapter mTabsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_viewpage_fragment, null);
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
        mTabStrip.setDividerColor(getResources().getColor(R.color.gray2));
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.global));
        mTabStrip.setTextColor(Color.BLACK);
        mTabStrip.setTextSize((int) getResources().getDimension(R.dimen.text_size_6));
        mTabStrip.setIndicatorHeight(2);
        mTabStrip.setZoomMax(0f);
        mTabStrip.setSelectedTextColor(getResources().getColor(R.color.global));
        mTabStrip.setUnderlineColorResource(R.color.gray2);

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

    protected abstract void onSetupTabAdapter(View view,ViewPageFragmentAdapter adapter,ViewPager viewPager);
}