package com.weilian.phonelive.ui.other;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserBean;

/**
 * Created by Administrator on 2016/4/12.
 */
public class PhoneLivePrivateChat {
    public static EMMessage sendChatMessage(String content, PrivateChatUserBean toUser,UserBean mUser){
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, String.valueOf(toUser.getId()));
        message.setAttribute("uhead",mUser.getAvatar());
        message.setAttribute("isfollow",toUser.getIsattention2());
        message.setAttribute("uname",toUser.getUser_nicename());
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }
}