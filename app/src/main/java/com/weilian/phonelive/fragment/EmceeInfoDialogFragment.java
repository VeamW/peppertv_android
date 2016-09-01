package com.weilian.phonelive.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tandong.bottomview.view.BottomView;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.ShowLiveActivityBase;
import com.weilian.phonelive.bean.PrivateChatUserBean;
import com.weilian.phonelive.bean.UserAlertInfoBean;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.ui.other.ChatServer;
import com.weilian.phonelive.widget.BottomMenuView;
import com.weilian.phonelive.widget.CircleImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.kymjs.kjframe.Core;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/6/3.
 */
public class EmceeInfoDialogFragment  extends DialogFragment{
    private UserBean mUser;
    private UserBean mToUser;
    private int mRoomNum;
    private ChatServer mChatServer;
    @InjectView(R.id.tv_show_dialog_u_fllow_num)
    TextView mTvFollowNum;

    @InjectView(R.id.tv_show_dialog_u_fans)
    TextView mTvFansNum;

    @InjectView(R.id.tv_show_dialog_u_send_num)
    TextView mTvSendNum;

    @InjectView(R.id.tv_show_dialog_u_ticket)
    TextView mTvTicketNum;

    @InjectView(R.id.bt_show_dialog_u_fllow_btn)
    Button mBtnFllow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emcee_info_dialog,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    private void initData() {

        PhoneLiveApi.getUserInfo(mToUser.getId(),mUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }
            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,getActivity());
                if(res != null){
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res,UserAlertInfoBean.class);
                    mTvFollowNum.setText("关注:" + u.getAttention());
                    mTvFansNum.setText("粉丝:" + u.getFans());
                    mTvSendNum.setText("送出:" + u.getConsumption());
                    mTvTicketNum.setText("辣度:" + u.getVotestotal());
                }

            }
        });//获取用户详细信息

        PhoneLiveApi.getIsFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,getActivity());
                if (null != res) {

                    if (res.equals("0")) {
                        mBtnFllow.setText(getString(R.string.follow2));
                        mBtnFllow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(mUser.getId(), mToUser.getId(),AppContext.getInstance().getToken(), null);
                                mBtnFllow.setEnabled(false);
                                mBtnFllow.setTextColor(getResources().getColor(R.color.gray));
                                mBtnFllow.setBackgroundResource(R.drawable.room_pop_up_graybutton);
                                mBtnFllow.setText(getString(R.string.alreadyfollow));
                            }
                        });
                    } else {
                        mBtnFllow.setText(getString(R.string.alreadyfollow));
                        mBtnFllow.setEnabled(false);
                        mBtnFllow.setTextColor(getResources().getColor(R.color.gray));
                        mBtnFllow.setBackgroundResource(R.drawable.room_pop_up_graybutton);
                    }


                }
            }
        });

    }

    private void initView(View view) {
        mUser = (UserBean) getArguments().getSerializable("MYUSERINFO");
        mToUser = (UserBean) getArguments().getSerializable("TOUSERINFO");
        mRoomNum = getArguments().getInt("ROOMNUM");
        mChatServer = ((ShowLiveActivityBase)getActivity()).mChatServer;

        view.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.bt_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showPrivateMessage((ShowLiveActivityBase) getActivity(),mUser.getId(),mToUser.getId());
            }
        });
        view.findViewById(R.id.tv_live_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneLiveApi.isManage(mUser.getId(),mToUser.getId(),new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response,getActivity());
                        if(null == res) return;
                        mToUser.setuType(Integer.parseInt(res));
                        showManageBottomMenu(getActivity(),mUser,mToUser,mChatServer,true);
                    }
                });
                dismiss();

            }
        });
        view.findViewById(R.id.bt_show_dialog_u_reply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShowLiveActivityBase)getActivity()).dialogReply(mToUser);
                dismiss();
            }
        });
        ((TextView) view.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView)view.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mToUser.getLevel()==0?0:mToUser.getLevel()-1]);
        ((ImageView)view.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView)view.findViewById(R.id.tv_show_dialog_u_sign)).setText(mToUser.getSignature());
        Core.getKJBitmap().display(((CircleImageView) view.findViewById(R.id.av_show_dialog_u_head)),mToUser.getAvatar());
        ((TextView) view.findViewById(R.id.tv_show_dialog_u_address))
                .setText(mUser.getCity() == null?"定位黑洞":mUser.getCity());


    }
    //显示管理弹窗
    public static void showManageBottomMenu(Activity activity, UserBean mUser, UserBean mToUser, ChatServer chatServer, boolean isEmcee) {
        BottomMenuView mBottomMenuView = new BottomMenuView(activity);
        BottomView mManageMenu = new BottomView(activity, R.style.BottomViewTheme_Transparent,mBottomMenuView);
        mBottomMenuView.setOptionData(mUser,mToUser,mUser.getId(),activity,chatServer,mManageMenu);
        mBottomMenuView.setIsEmcee(isEmcee);
        mManageMenu.setAnimation(R.style.BottomToTopAnim);
        mManageMenu.showBottomView(false);
    }
    //跳转私信
    public static void showPrivateMessage(final ShowLiveActivityBase activity, int uid, int touid){

        PhoneLiveApi.getPmUserInfo(uid, touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,activity);
                if(null == res) return;
                //UIHelper.showPrivateChatMessage(activity,new Gson().fromJson(res,PrivateChatUserBean.class));
                PrivateChatUserBean chatUserBean = new Gson().fromJson(res,PrivateChatUserBean.class);
                MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",chatUserBean);
                messageFragment.setArguments(bundle);
                messageFragment.setStyle(MessageDetailDialogFragment.STYLE_NO_TITLE,0);
                messageFragment.show(activity.getSupportFragmentManager(),"MessageDetailDialogFragment");
            }
        });
    }
}
