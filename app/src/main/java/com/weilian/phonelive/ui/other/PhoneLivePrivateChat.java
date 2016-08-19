package com.weilian.phonelive.ui.other;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.TLog;

/**
 * Created by Administrator on 2016/4/12.
 */
public class PhoneLivePrivateChat {
    public static EMMessage sendChatMessage(String content, PrivateChatUserBean toUser, UserBean mUser) {
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        if (toUser == null || toUser.getId() <= 0) {
            return null;
        }
        EMMessage message = null;
        try {
            TLog.error("私聊发送的消息：fromId：" + mUser.getId() + "   toId     pt" + toUser.getId());
            message = EMMessage.createTxtSendMessage(content, "pt" + toUser.getId());
            if (message != null) {
                message.setAttribute("uhead", mUser.getAvatar());
                message.setAttribute("isfollow", toUser.getIsattention2());
                message.setAttribute("uname", toUser.getUser_nicename());
            }

            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}
