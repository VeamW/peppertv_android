package com.weilian.phonelive.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;

public class NotFollowPrivateChatFragment extends PrivateChatPageBase {


    private int action;
    private ArrayList<PrivateChatUserBean> mPrivateChatListData = new ArrayList<>();
    private ListView mPrivateListView;
    private int mPosition;
    private UserBaseInfoPrivateChatAdapter mUserInfoPrivateAdapter;
    private UserBean mUser;
    private Map<String, EMConversation> emConversationMap;
    private Gson mGson = new Gson();


    @Override
    protected void initCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    public void initView(View view) {
        mPrivateListView = (ListView) view.findViewById(R.id.lv_privatechat);
        mUserInfoPrivateAdapter = new UserBaseInfoPrivateChatAdapter(mPrivateChatListData);
        mPrivateListView.setAdapter(mUserInfoPrivateAdapter);


        mPrivateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
    protected void onNewMessage(final EMMessage message) {
        //收到消息
        try {

            if ((message.getIntAttribute("isfollow") != 0)) return;
            if (!emConversationMap.containsKey(message.getFrom())) {
                TLog.log("not in conversations not follow");
                emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
                PhoneLiveApi.getPmUserInfo(mUser.getId(), Integer.parseInt(message.getFrom().replace("pt", "")), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response, getActivity());
                        if (null == res) return;
                        PrivateChatUserBean privateChatUserBean = mGson.fromJson(res, PrivateChatUserBean.class);
                        privateChatUserBean.setLastMessage(((EMTextMessageBody) (message.getBody())).getMessage());
                        privateChatUserBean.setUnreadMessage(true);
                        mPrivateChatListData.add(privateChatUserBean);
                        mPrivateListView.setAdapter(mUserInfoPrivateAdapter);
                        mUserInfoPrivateAdapter.notifyDataSetChanged();
                    }
                });

            } else {
                //TLog.log("in conversations not follow");
                if (mPrivateChatListData == null) return;
                TLog.log("not null");
                for (int i = 0; i < mPrivateChatListData.size(); i++) {
                    PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(i);
                    TLog.log("uid:" + privateChatUserBean.getId() + "fromid:" + message.getFrom());
                    if (privateChatUserBean.getId() == Integer.parseInt(message.getFrom().replace("pt", ""))) {
                        privateChatUserBean.setLastMessage(((EMTextMessageBody) (message.getBody())).getMessage());
                        privateChatUserBean.setUnreadMessage(true);
//                        mPrivateChatListData.set(i, privateChatUserBean);
                        mPrivateChatListData.add(privateChatUserBean);
                        mUserInfoPrivateAdapter.notifyDataSetChanged();
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            //没有传送是否关注标记
            TLog.log("没有传送是否关注标记");
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
        MobclickAgent.onPageStart("未关注私信"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
        if (null == mPrivateChatListData || mPrivateChatListData.size() == 0) return;

        try {
            EMConversation conversation = EMClient.getInstance()
                    .chatManager()
                    .getConversation(String.valueOf("pt" + mPrivateChatListData.get(mPosition).getId()));
            conversation.markAllMessagesAsRead();
            mPrivateChatListData.get(mPosition).setLastMessage(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
        } catch (Exception e) {
            //无最后一条消息
        }

        mUserInfoPrivateAdapter.notifyDataSetChanged();

    }

    private void initConversationList() {
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
        StringBuilder keys = new StringBuilder();
        for (String key : emConversationMap.keySet()) {
            keys.append(key.replace("pt", "") + ",");
            TLog.error(key);
        }
        if (keys.toString().length() == 0) return;
        Log.e("keys", keys.toString());
        final String uidList = keys.toString().substring(0, keys.length() - 1);
        PhoneLiveApi.getMultiBaseInfo(action, mUser.getId(), uidList, new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToast("获取列表失败");
            }

            @Override
            public void onResponse(String response) {
                TLog.error("未关注：" + response);
                String res = ApiUtils.checkIsSuccess(response, getActivity());
                if (null != res) {
                    try {
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
        if (mPrivateChatListData == null || mPrivateChatListData.isEmpty()) return;
        mUserInfoPrivateAdapter.notifyDataSetChanged();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("未关注私信"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
