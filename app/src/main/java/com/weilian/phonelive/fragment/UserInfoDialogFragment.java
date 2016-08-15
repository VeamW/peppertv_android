package com.weilian.phonelive.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.BottomMenuView;
import com.weilian.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoDialogFragment extends DialogFragment {

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

    @InjectView(R.id.av_show_dialog_order_first_u_head)
    LoadUrlImageView mFirstHead;

    @InjectView(R.id.tv_show_dialog_u_fllow_btn)
    TextView mTvFollowBtn;

    @InjectView(R.id.tv_live_manage_or_report)
    TextView mTvReportBtn;
    public UserInfoDialogFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_dialog,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        PhoneLiveApi.isManage(mRoomNum, mUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,getActivity());
                if(res == null) return;
                mUser.setuType(Integer.parseInt(res));
                mTvReportBtn.setText(mUser.getuType() == 40?"管理":"举报");
                mTvReportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mUser.getuType() == 40){
                            showManageBottomMenu(getActivity(),mUser,mToUser,mChatServer,false);
                            dismiss();
                        }else {
                            AppContext.showToastAppMsg(getActivity(),getString(R.string.reportsuccess));
                        }
                    }
                });
            }
        });
        //获取用户详细信息
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
                    mTvTicketNum.setText("映票:" + u.getVotestotal());

                    if(u.getVotestotal() > 0 ){
                        mFirstHead.setImageLoadUrl(u.getContribute().getAvatar());
                    }else{
                        mFirstHead.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
        //获取是否关注该用户
        PhoneLiveApi.getIsFollow(mUser.getId(), mToUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response,getActivity());
                if (null != res) {

                    if (res.equals("0")) {
                        mTvFollowBtn.setText(getString(R.string.follow2));
                        mTvFollowBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(),AppContext.getInstance().getToken(), null);
                                mTvFollowBtn.setEnabled(false);
                                mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                                mTvFollowBtn.setText(getString(R.string.alreadyfollow));
                                if(mToUser.getId() == mRoomNum)
                                    mChatServer.doSendMsg(mUser.getUser_nicename() + "关注了主播",mUser,0);

                            }
                        });
                    } else {
                        mTvFollowBtn.setText(getString(R.string.alreadyfollow));
                        mTvFollowBtn.setEnabled(false);
                        mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                    }


                }
            }
        });
    }

    private void initView(final View view) {
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
        view.findViewById(R.id.tv_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showPrivateMessage((ShowLiveActivityBase) getActivity(),mUser.getId(),mToUser.getId());
            }
        });
        view.findViewById(R.id.tv_show_dialog_u_reply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShowLiveActivityBase)getActivity()).dialogReply(mToUser);
                dismiss();
            }
        });


        //主页
        view.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(getActivity(),mToUser.getId());
            }
        });
        ((LoadUrlImageView) view.findViewById(R.id.av_show_dialog_u_head)).setImageLoadUrl(mToUser.getAvatar());
        ((TextView) view.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView)view.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mToUser.getLevel()==0?0:mToUser.getLevel()-1]);
        ((ImageView)view.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView)view.findViewById(R.id.tv_show_dialog_u_sign)).setText(mToUser.getSignature());
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
