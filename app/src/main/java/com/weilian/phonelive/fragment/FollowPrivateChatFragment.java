package com.weilian.phonelive.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.UserBaseInfoPrivateChatAdapter;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.PrivateChatPageBase;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.TLog;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.utils.Utils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class FollowPrivateChatFragment extends PrivateChatPageBase {


    private int action;
    private ArrayList<PrivateChatUserBean> mPrivateChatListData = new ArrayList<>();
    private ListView mPrivateListView;
    private int mPosition;
    private UserBaseInfoPrivateChatAdapter mUserInfoPrivateAdapter;
    private UserBean mUser;
    private Map<String, EMConversation> emConversationMap;
    private Gson mGson = new Gson();


    @Override
    public void initView(View view) {
        mPrivateListView = (ListView) view.findViewById(R.id.lv_privatechat);
        mUserInfoPrivateAdapter = new UserBaseInfoPrivateChatAdapter(mPrivateChatListData);
        mPrivateListView.setAdapter(mUserInfoPrivateAdapter);

        mPrivateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) return;
                mPosition = position;
                mPrivateChatListData.get(position).setUnreadMessage(false);
                if (getParentFragment() instanceof DialogFragment) {
                    MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", mPrivateChatListData.get(position));
                    messageFragment.setArguments(bundle);
                    messageFragment.setStyle(MessageDetailDialogFragment.STYLE_NO_TITLE, 0);
                    messageFragment.show(getFragmentManager(), "MessageDetailDialogFragment");
                } else {
                    UIHelper.showPrivateChatMessage(getActivity(), mPrivateChatListData.get(position));
                }
            }
        });
    }


    @Override
    protected void initCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    protected void onNewMessage(final EMMessage messages) {
        //收到消息
        try {
            if ((messages.getIntAttribute("isfollow") != 1)) return;
            if (!emConversationMap.containsKey(messages.getFrom())) {
                emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
                PhoneLiveApi.getPmUserInfo(mUser.getId(), Integer.parseInt(messages.getFrom().replace("pt", "")), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        TLog.error(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response, getActivity());
                        if (null == res) return;
                        PrivateChatUserBean privateChatUserBean = mGson.fromJson(res, PrivateChatUserBean.class);
                        privateChatUserBean.setLastMessage(((EMTextMessageBody) (messages.getBody())).getMessage());
                        privateChatUserBean.setUnreadMessage(true);
                        mPrivateChatListData.add(privateChatUserBean);
                        mUserInfoPrivateAdapter.notifyDataSetChanged();
                        TLog.error(privateChatUserBean.getId() + "");
                    }
                });

            } else {
                if (mPrivateChatListData == null) return;
                for (int i = 0; i < mPrivateChatListData.size(); i++) {
                    PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(i);
                    if (privateChatUserBean.getId() == Integer.parseInt(messages.getFrom().replace("pt", ""))) {
                        privateChatUserBean.setLastMessage(((EMTextMessageBody) (messages.getBody())).getMessage());
                        privateChatUserBean.setUnreadMessage(true);
                        mPrivateChatListData.add(privateChatUserBean);
                        mUserInfoPrivateAdapter.notifyDataSetChanged();
                        continue;
                    }
                }


            }

        } catch (HyphenateException e) {
            //没有传送是否关注标记
            e.printStackTrace();
        }

    }

    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        action = getArguments().getInt("ACTION");
        initConversationList();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("关注私信"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长

        if (null == mPrivateChatListData || mPrivateChatListData.size() == 0) return;

        try {
            EMConversation conversation = EMClient.getInstance()
                    .chatManager()
                    .getConversation("pt" + String.valueOf(mPrivateChatListData.get(mPosition).getId()));
            conversation.markAllMessagesAsRead();
            mPrivateChatListData.get(mPosition).setLastMessage(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
        } catch (Exception e) {
            //无最后一条消息
        }
  /*      mPrivateListView.setAdapter(mUserInfoPrivateAdapter);
        mUserInfoPrivateAdapter.notifyDataSetChanged();*/
    }

    private void initConversationList() {
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
        StringBuilder keys = new StringBuilder();
        for (String key : emConversationMap.keySet()) {
            keys.append(key.replace("pt","") + ",");
        }
        if (keys.toString().length() == 0) return;
        String uidList = keys.toString().substring(0, keys.length() - 1);
        PhoneLiveApi.getMultiBaseInfo(action, mUser.getId(), uidList, new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {

                String res = ApiUtils.checkIsSuccess(response, getActivity());
                if (null != res) {
                    try {
                        mPrivateChatListData.clear();
                        JSONArray fansJsonArr = new JSONArray(res);
                        if (fansJsonArr.length() > 0) {
                            Gson gson = new Gson();
                            for (int i = 0; i < fansJsonArr.length(); i++) {
                                PrivateChatUserBean chatUserBean = gson.fromJson(fansJsonArr.getString(i), PrivateChatUserBean.class);
                                EMConversation conversation = EMClient.getInstance()
                                        .chatManager()
                                        .getConversation("pt" + String.valueOf(chatUserBean.getId()));
                                try {
                                    chatUserBean.setLastMessage(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
                                    chatUserBean.setUnreadMessage(conversation.getUnreadMsgCount() > 0 ? true : false);
                                } catch (Exception e) {
                                    //无最后一条消息纪录
                                }
                                mPrivateChatListData.add(chatUserBean);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                fillUI();
            }
        });
    }

    private void fillUI() {
        if (mPrivateChatListData == null || mPrivateChatListData.isEmpty()) {
            return;
        }
        mUserInfoPrivateAdapter.notifyDataSetChanged();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("关注私信"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
