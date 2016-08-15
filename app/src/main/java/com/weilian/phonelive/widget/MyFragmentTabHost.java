package com.weilian.phonelive.widget;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.ui.MainActivity;
import com.weilian.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * tabhost
 * 
 */

public class MyFragmentTabHost extends FragmentTabHost {
	
	private String mCurrentTag;
	
	private String mNoTabChangedTag = "1";
	private Context context;

	
	public MyFragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	public void onTabChanged(String tag) {

		if (tag.equals(mNoTabChangedTag)) {
			setCurrentTabByTag(mCurrentTag);
			((MainActivity)context).startLive();

		} else {
			super.onTabChanged(tag);
			mCurrentTag = tag;
		}


	}
	
	public void setNoTabChangedTag(String tag) {
		this.mNoTabChangedTag = tag;
	}
}
