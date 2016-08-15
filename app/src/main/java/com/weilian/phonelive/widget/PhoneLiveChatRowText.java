package com.weilian.phonelive.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.weilian.phonelive.R;
import com.weilian.phonelive.utils.TLog;

/**
 * Created by Administrator on 2016/4/12.
 */
public class PhoneLiveChatRowText extends PhoneLiveChatRow {
    private TextView contentView;
    private AvatarView mUhead;
    public PhoneLiveChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.item_message_left : R.layout.item_message_right, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_message_text);
        mUhead = (AvatarView) findViewById(R.id.av_message_head);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();

        // 设置内容
        contentView.setText(txtBody.getMessage());
        try {
            mUhead.setAvatarUrl(message.getStringAttribute("uhead"));
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        handleTextMessage();
    }
    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    // 发送消息
//                sendMsgInBackground(message);
                    break;
                case SUCCESS: // 发送成功
                    break;
                case FAIL: // 发送失败
                    break;
                case INPROGRESS: // 发送中
                    break;
                default:
                    break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onBubbleClick() {

    }
}
