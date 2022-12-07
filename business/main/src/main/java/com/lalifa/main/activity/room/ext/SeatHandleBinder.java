package com.lalifa.main.activity.room.ext;

import android.app.Activity;
import android.text.TextUtils;

import com.bcq.adapter.recycle.RcyHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.kit.utils.ImageLoader;
import com.lalifa.ext.Account;
import com.lalifa.main.R;

import java.lang.ref.WeakReference;

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 麦位信息数据的绑定器
 */
public class SeatHandleBinder {
    private RcyHolder holder;
    private Seat seatInfo;//麦位信息
    private int index = 0;//麦位索引
    private WeakReference<Activity> activity;

    public SeatHandleBinder(Activity activity, RcyHolder holder, int index) {
        this.activity = new WeakReference<>(activity);
        this.holder = holder;
        this.index = index;
    }

    public void bind(Seat seatInfo) {
        if (null == seatInfo) {
            return;
        }
        this.seatInfo = seatInfo;
        if (null == holder) {
            return;
        }
        bind();
    }

    private void bind() {
        boolean useing = seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing;
        boolean lock = seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking;
        //是否RCSeatStatusUsing
        ShapeableImageView iv_portrait = holder.getView(R.id.iv_portrait);
        if (useing) {
            Account accout = AccountManager.getAccount(seatInfo.getUserId());
            if (null != accout) {
                ImageLoader.loadUrl(iv_portrait,
                        accout.getAvatar(),
                        R.drawable.bg_seat_empty,
                        ImageLoader.Size.SZ_200);
            } else {
                iv_portrait.setImageResource(R.drawable.bg_seat_empty);
            }
        } else {
            iv_portrait.setImageResource(R.drawable.bg_seat_empty);
        }

        //是否锁定
        holder.setVisible(R.id.seat_locked, lock);
        //是否静音
        holder.setVisible(R.id.seat_mute, seatInfo.isMute());
        holder.setText(R.id.member_audio, seatInfo.getAudioLevel());
        //麦位上用户名称
        holder.setText(R.id.member_name, AccountManager.getAccountName(seatInfo.getUserId()));
        //扩展属性
        holder.setText(R.id.member_extra, TextUtils.isEmpty(seatInfo.getExtra()) ? "扩展：" : "扩展：" + seatInfo.getExtra());
        //api点击事件
        holder.itemView.setOnClickListener(view -> {
            ApiFunDialogHelper.helper().showSeatApiDialog(activity.get(),
                    (v, apiFun) -> onApi(apiFun, seatInfo, index));
//                if (QuickEventListener.get().isInSeat()) {
//                    VoiceRoomApi.getApi().jumpTo(index, null);
//                } else {
//                    VoiceRoomApi.getApi().enterSeat(index, null);
//                }

        });
    }

    /**
     * 处理api功能
     *
     * @param apiFun
     * @param seatInfo
     */
    public void onApi(ApiFun apiFun, RCVoiceSeatInfo seatInfo, int seatIndex) {
        VoiceRoomApi.getApi().handleSeatApi(apiFun, seatIndex, seatInfo.getUserId());
    }
}