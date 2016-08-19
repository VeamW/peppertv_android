package com.weilian.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.MessageAdapter;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.other.PhoneLivePrivateChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MessageDetailDialogFragment extends DialogFragment {
    @InjectView(R.id.tv_private_chat_title)
    TextView mTitle;

    @InjectView(R.id.iv_private_chat_send)
    Button mSendChatBtn;

    @InjectView(R.id.et_private_chat_message)
    EditText mMessageInput;

    @InjectView(R.id.iv_private_chat_gift)
    ImageView mShowGiftBtn;

    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;


    private List<EMMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_private_chat_message, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @OnClick({R.id.iv_private_chat_send, R.id.et_private_chat_message, R.id.iv_close})

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_private_chat_send:
                if (mMessageInput.getText().toString().equals(""))
                    AppContext.showToastAppMsg(getActivity(), "内容为空");
                updateChatList(PhoneLivePrivateChat.sendChatMessage(
                        mMessageInput.getText().toString()
                        , mToUser
                        , mUser));
                mMessageInput.setText("");
                break;
            case R.id.et_private_chat_message:

                break;
        }

    }

    public void initData() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        mUser = AppContext.getInstance().getLoginUser();
        mToUser = (PrivateChatUserBean) getArguments().getSerializable("user");
        mTitle.setText(mToUser.getUser_nicename());
        getUnreadRecord();
        mMessageAdapter = new MessageAdapter(AppContext.getInstance().getLoginUid(), getActivity());
        mMessageAdapter.setChatList(mChats);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

    }

    private void getUnreadRecord() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation( String.valueOf(mToUser.getId()));
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        //获取此会话的所有消息
        try {
            mChats = conversation.getAllMessages();
        } catch (Exception e) {
            //无历史消息纪录
        }

    }

    public void initView(View view) {
        mMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!mMessageInput.getText().toString().equals("")) {
                    mSendChatBtn.setVisibility(View.VISIBLE);
                    // mShowGiftBtn.setVisibility(View.GONE);
                } else {
                    // mSendChatBtn.setVisibility(View.GONE);
                    //   mShowGiftBtn.setVisibility(View.VISIBLE);   zxy  0419  礼物按钮不显示
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateChatList(EMMessage message) {
        mMessageAdapter.addMessage(message);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mMessageAdapter.getCount() - 1);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (final EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateChatList(message);
                        }
                    });
                }

                // 如果是当前会话的消息，刷新聊天页面
               /* if (username.equals(toChatUsername)) {
                    messageList.refreshSelectLast();
                    // 声音和震动提示有新消息
                    EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    // 如果消息不是和当前聊天ID的消息
                    EaseUI.getInstance().getNotifier().onNewMsg(message);
                }*/
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            /*if(isMessageListInited) {
                messageList.refresh();
            }*/
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            /*if(isMessageListInited) {
                messageList.refresh();
            }*/
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            /*if(isMessageListInited) {
                messageList.refresh();
            }*/
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("私信聊天页面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
        /*if(isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());*/
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        // 把此activity 从foreground activity 列表里移除
        //EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != msgListener) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("私信聊天页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
