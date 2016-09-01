package com.weilian.phonelive.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.weilian.phonelive.fragment.MessageDetailDialogFragment;
import com.weilian.phonelive.ui.DrawableRes;
import com.weilian.phonelive.ui.other.ChatServer;
import com.weilian.phonelive.utils.UIHelper;
import com.weilian.phonelive.widget.AvatarView;
import com.weilian.phonelive.widget.BottomMenuView;
import com.weilian.phonelive.widget.CircleImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.kymjs.kjframe.Core;

import okhttp3.Call;

/**
 * UI公共类
 */
public class LiveCommon {


    /**
     * pop window 提示
     * @param context
     * @param mUser
     * @param mToUser
     * @param roomnum
     * @param chatServer
     * @param v
     */
    public static void showUserInfoPop(final ShowLiveActivityBase context, final UserBean mUser, final UserBean mToUser, final int roomnum, final ChatServer chatServer, View v) {
        final View mDialogView = View.inflate(context, R.layout.dialog_show_user_info_detail, null);
        final PopupWindow p = new PopupWindow(mDialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mDialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        p.setOutsideTouchable(true);
        p.setAnimationStyle(R.style.mypopwindow_anim_style);
        final TextView mManageOrRePort = (TextView) mDialogView.findViewById(R.id.tv_live_manage_or_report);
        mDialogView.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.dismiss();
            }
        });
        mDialogView.findViewById(R.id.tv_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.dismiss();
                showPrivateMessage(context, mUser.getId(), mToUser.getId());
            }
        });
        mDialogView.findViewById(R.id.tv_show_dialog_u_reply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dialogReply(mToUser);
                p.dismiss();
            }
        });
        PhoneLiveApi.isManage(roomnum, mUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res == null) return;
                mUser.setuType(Integer.parseInt(res));
                mManageOrRePort.setText(mUser.getuType() == 40 ? "管理" : "举报");
                mManageOrRePort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser.getuType() == 40) {
                            showManageBottomMenu(context, mUser, mToUser, chatServer, false);
                            p.dismiss();
                        } else {
                            AppContext.showToastAppMsg(context, context.getString(R.string.reportsuccess));
                        }
                    }
                });
            }
        });

        //主页
        mDialogView.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(context, mToUser.getId());
            }
        });
        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_u_head)).setAvatarUrl(mToUser.getAvatar());
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mToUser.getLevel() == 0 ? 0 : mToUser.getLevel() - 1]);
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_sign)).setText(mToUser.getSignature());

        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_address))
                .setText(mUser.getCity() == null ? "定位黑洞" : mUser.getCity());
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res != null) {
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res, UserAlertInfoBean.class);
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_num))
                            .setText("关注:" + u.getAttention());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fans))
                            .setText("粉丝:" + u.getFans());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_send_num))
                            .setText("送出:" + u.getConsumption());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_ticket))
                            .setText("辣度:" + u.getVotestotal());

                    if (u.getVotestotal() > 0) {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setAvatarUrl(u.getContribute().getAvatar());
                    } else {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setVisibility(View.INVISIBLE);
                    }
                }

            }
        };
        PhoneLiveApi.getUserInfo(mToUser.getId(), mUser.getId(), callback);//获取用户详细信息


        //获取是否关注该用户
        PhoneLiveApi.getIsFollow(mUser.getId(), mToUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (null != res) {
                    final TextView mFollow = (TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_btn);
                    if (res.equals("0")) {
                        mFollow.setText(context.getString(R.string.follow2));
                        mFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(), AppContext.getInstance().getToken(), null);
                                mFollow.setEnabled(false);
                                mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                                mFollow.setText(context.getString(R.string.alreadyfollow));
                                if (mToUser.getId() == roomnum)
                                    chatServer.doSendMsg(mUser.getUser_nicename() + "关注了主播", mUser, 0);

                            }
                        });
                    } else {
                        mFollow.setText(context.getString(R.string.alreadyfollow));
                        mFollow.setEnabled(false);
                        mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                    }


                }
            }
        });

        int[] location = new int[2];
        v.getLocationOnScreen(location);
        p.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }


    /**
     * @param context 上下文
     * @param mToUser 点击用户信息
     * @dw 弹窗用户信息
     */
    public static void showUserInfoDialog(final ShowLiveActivityBase context, final UserBean mUser, final UserBean mToUser, final int roomnum, final ChatServer chatServer) {
        final View mDialogView = View.inflate(context, R.layout.dialog_show_user_info_detail, null);
        final Dialog d = new Dialog(context, R.style.dialog);
        final TextView mManageOrRePort = (TextView) mDialogView.findViewById(R.id.tv_live_manage_or_report);
        d.setContentView(mDialogView);
        mDialogView.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        mDialogView.findViewById(R.id.tv_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                showPrivateMessage(context, mUser.getId(), mToUser.getId());
            }
        });
        mDialogView.findViewById(R.id.tv_show_dialog_u_reply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dialogReply(mToUser);
                d.dismiss();
            }
        });
        PhoneLiveApi.isManage(roomnum, mUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res == null) return;
                mUser.setuType(Integer.parseInt(res));
                mManageOrRePort.setText(mUser.getuType() == 40 ? "管理" : "举报");
                mManageOrRePort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser.getuType() == 40) {
                            showManageBottomMenu(context, mUser, mToUser, chatServer, false);
                            d.dismiss();
                        } else {
                            AppContext.showToastAppMsg(context, context.getString(R.string.reportsuccess));
                        }
                    }
                });
            }
        });

        //主页
        mDialogView.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(context, mToUser.getId());
            }
        });
        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_u_head)).setAvatarUrl(mToUser.getAvatar());
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mToUser.getLevel() == 0 ? 0 : mToUser.getLevel() - 1]);
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_sign)).setText(mToUser.getSignature());

        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_address))
                .setText(mUser.getCity() == null ? "定位黑洞" : mUser.getCity());
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res != null) {
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res, UserAlertInfoBean.class);
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_num))
                            .setText("关注:" + u.getAttention());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fans))
                            .setText("粉丝:" + u.getFans());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_send_num))
                            .setText("送出:" + u.getConsumption());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_ticket))
                            .setText("辣度:" + u.getVotestotal());

                    if (u.getVotestotal() > 0) {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setAvatarUrl(u.getContribute().getAvatar());
                    } else {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setVisibility(View.INVISIBLE);
                    }
                }

            }
        };
        PhoneLiveApi.getUserInfo(mToUser.getId(), mUser.getId(), callback);//获取用户详细信息


        //获取是否关注该用户
        PhoneLiveApi.getIsFollow(mUser.getId(), mToUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (null != res) {
                    final TextView mFollow = (TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_btn);
                    if (res.equals("0")) {
                        mFollow.setText(context.getString(R.string.follow2));
                        mFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(), AppContext.getInstance().getToken(), null);
                                mFollow.setEnabled(false);
                                mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                                mFollow.setText(context.getString(R.string.alreadyfollow));
                                if (mToUser.getId() == roomnum)
                                    chatServer.doSendMsg(mUser.getUser_nicename() + "关注了主播", mUser, 0);

                            }
                        });
                    } else {
                        mFollow.setText(context.getString(R.string.alreadyfollow));
                        mFollow.setEnabled(false);
                        mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                    }


                }
            }
        });
        if (!d.isShowing()) {
            d.show();
        }

    }


    public static void showOwnInfoDialog(final ShowLiveActivityBase context, final UserBean mUser, int roomnum) {
        final View mDialogView = View.inflate(context, R.layout.dialog_show_own_info_detail, null);
        final Dialog d = new Dialog(context, R.style.dialog);

        d.setContentView(mDialogView);
        mDialogView.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        //主页
        mDialogView.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(context, mUser.getId());
            }
        });
        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_u_head)).setAvatarUrl(mUser.getAvatar());
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_name)).setText(mUser.getUser_nicename());
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mUser.getLevel() == 0 ? 0 : mUser.getLevel() - 1]);
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_sign)).setText(mUser.getSignature());

        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_address))
                .setText(mUser.getCity() == null ? "定位黑洞" : mUser.getCity());
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res != null) {
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res, UserAlertInfoBean.class);
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_num))
                            .setText("关注:" + u.getAttention());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fans))
                            .setText("粉丝:" + u.getFans());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_send_num))
                            .setText("送出:" + u.getConsumption());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_ticket))
                            .setText(AppContext.getInstance().getString(R.string.yingpiao) + ":" + u.getVotestotal());

                    if (u.getVotestotal() > 0) {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setAvatarUrl(u.getContribute().getAvatar());
                    } else {
                        ((AvatarView) mDialogView.findViewById(R.id.av_show_dialog_order_first_u_head)).setVisibility(View.INVISIBLE);
                    }
                }

            }
        };
        PhoneLiveApi.getUserInfo(mUser.getId(), mUser.getId(), callback);//获取用户详细信息
        if (!d.isShowing()) {
            d.show();
        }

    }


    /**
     * @param context 上下文
     * @param mToUser 点击用户信息
     * @dw 主播弹窗
     */
    public static void showUserInfoDialogEmcee(final ShowLiveActivityBase context, final UserBean mToUser, final UserBean mUser, final ChatServer chatServer) {
        final View mDialogView = View.inflate(context, R.layout.dialog_show_user_info_detail_emcee, null);
        final Dialog d = new Dialog(context, R.style.dialog);
        d.setContentView(mDialogView);
        mDialogView.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        mDialogView.findViewById(R.id.bt_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                showPrivateMessage(context, mUser.getId(), mToUser.getId());
            }
        });
        mDialogView.findViewById(R.id.tv_live_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneLiveApi.isManage(mUser.getId(), mToUser.getId(), new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response, context);
                        if (null == res) return;
                        mToUser.setuType(Integer.parseInt(res));
                        showManageBottomMenu(context, mUser, mToUser, chatServer, true);
                    }
                });
                d.dismiss();

            }
        });
        mDialogView.findViewById(R.id.bt_show_dialog_u_reply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dialogReply(mToUser);
                d.dismiss();
            }
        });
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mToUser.getLevel() == 0 ? 0 : mToUser.getLevel() - 1]);
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_sign)).setText(mToUser.getSignature());
        Core.getKJBitmap().display(((CircleImageView) mDialogView.findViewById(R.id.av_show_dialog_u_head)), mToUser.getAvatar());
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_address))
                .setText(mUser.getCity() == null ? "定位黑洞" : mUser.getCity());

        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res != null) {
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res, UserAlertInfoBean.class);
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_num))
                            .setText("关注:" + u.getAttention());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fans))
                            .setText("粉丝:" + u.getFans());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_send_num))
                            .setText("送出:" + u.getConsumption());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_ticket))
                            .setText("辣度:" + u.getVotestotal());
                }

            }
        };
        PhoneLiveApi.getUserInfo(mToUser.getId(), mUser.getId(), callback);//获取用户详细信息

        PhoneLiveApi.getIsFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (null != res) {
                    final Button mFollow = (Button) mDialogView.findViewById(R.id.bt_show_dialog_u_fllow_btn);

                    if (res.equals("0")) {
                        mFollow.setText(context.getString(R.string.follow2));
                        mFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(mUser.getId(), mToUser.getId(), AppContext.getInstance().getToken(), null);
                                mFollow.setEnabled(false);
                                mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                                mFollow.setBackgroundResource(R.drawable.room_pop_up_graybutton);
                                mFollow.setText(context.getString(R.string.alreadyfollow));
                            }
                        });
                    } else {
                        mFollow.setText(context.getString(R.string.alreadyfollow));
                        mFollow.setEnabled(false);
                        mFollow.setTextColor(context.getResources().getColor(R.color.gray));
                        mFollow.setBackgroundResource(R.drawable.room_pop_up_graybutton);
                    }


                }
            }
        });
        if (!d.isShowing()) {
            d.show();
        }
    }


    /**
     * @param context 上下文
     * @param mUser   点击用户信息
     * @dw 主播弹窗
     */
    public static void showOwnInfoDialogEmcee(final ShowLiveActivityBase context, final UserBean mUser) {
        final View mDialogView = View.inflate(context, R.layout.dialog_show_own_info_detail_emcee, null);
        final Dialog d = new Dialog(context, R.style.dialog);
        d.setContentView(mDialogView);
        mDialogView.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_name)).setText(mUser.getUser_nicename());
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_level)).setImageResource(DrawableRes.LevelImg[mUser.getLevel() == 0 ? 0 : mUser.getLevel() - 1]);
        ((ImageView) mDialogView.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_sign)).setText(mUser.getSignature());
        Core.getKJBitmap().display(((CircleImageView) mDialogView.findViewById(R.id.av_show_dialog_u_head)), mUser.getAvatar());

        ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_address)).setText(mUser.getCity() == null ? "定位黑洞" : mUser.getCity());

        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, context);
                if (res != null) {
                    //用户粉丝关注映票等信息
                    UserAlertInfoBean u = new Gson().fromJson(res, UserAlertInfoBean.class);
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fllow_num))
                            .setText("关注:" + u.getAttention());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_fans))
                            .setText("粉丝:" + u.getFans());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_send_num)).
                            setText("送出:" + u.getConsumption());
                    ((TextView) mDialogView.findViewById(R.id.tv_show_dialog_u_ticket))
                            .setText("辣度:" + u.getVotestotal());
                }

            }
        };
        PhoneLiveApi.getUserInfo(mUser.getId(), mUser.getId(), callback);//获取用户详细信息
        if (!d.isShowing()) {
            d.show();
        }
    }

    //显示管理弹窗
    public static void showManageBottomMenu(Activity activity, UserBean mUser, UserBean mToUser, ChatServer chatServer, boolean isEmcee) {
        BottomMenuView mBottomMenuView = new BottomMenuView(activity);
        BottomView mManageMenu = new BottomView(activity, R.style.BottomViewTheme_Transparent, mBottomMenuView);
        mBottomMenuView.setOptionData(mUser, mToUser, mUser.getId(), activity, chatServer, mManageMenu);
        mBottomMenuView.setIsEmcee(isEmcee);
        mManageMenu.setAnimation(R.style.BottomToTopAnim);
        mManageMenu.showBottomView(false);
    }

    //跳转私信
    public static void showPrivateMessage(final ShowLiveActivityBase activity, int uid, int touid) {
        PhoneLiveApi.getPmUserInfo(uid, touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response, activity);
                if (null == res) return;
                //UIHelper.showPrivateChatMessage(activity,new Gson().fromJson(res,PrivateChatUserBean.class));
                PrivateChatUserBean chatUserBean = new Gson().fromJson(res, PrivateChatUserBean.class);
                MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", chatUserBean);
                messageFragment.setArguments(bundle);
                messageFragment.setStyle(MessageDetailDialogFragment.STYLE_NO_TITLE, 0);
                messageFragment.show(activity.getSupportFragmentManager(), "MessageDetailDialogFragment");
            }
        });
    }
}
