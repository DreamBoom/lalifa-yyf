package com.lalifa.main.activity.room.ext;

import android.app.Activity;
import android.util.Log;

import com.drake.logcat.LogCat;
import com.kit.cache.GsonUtil;
import com.kit.utils.KToast;
import com.kit.wapper.IResultBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.voiceroom.api.IRCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.PKResponse;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Message;

public class QuickEventListener implements RCVoiceRoomEventListener {

    public interface SeatListObserver {
        void onSeatList(List<Seat> seatInfos);
        void requestSeatAccepted(int index);
        void seatSpeak(int index, int audioLevel);
    }

    public interface RoomInfoObserver {
        void onUserIn(String userId);
        //void onOnLineCount(int userNumber);
        void onReady();
        void onMessage(Message message);
        void onOut(String userId);
    }

    public interface SeatObserver {
        void onSeat(int index, Seat info);
    }

    private final static Object obj = new Object();
    private SeatListObserver seatListObserver;
    private RoomInfoObserver roomInfoObserver;
    private SeatObserver seatObserver;
    private static String TAG = "======";
    private static QuickEventListener listener = new QuickEventListener();
    //设置监听标识
    private WeakReference<IRCVoiceRoomEngine> reference;
    private WeakReference<Activity> activity;
    public static List<Seat> mSeatInfos;
    private List<String> mAudienceIds;

    private QuickEventListener() {
    }

    public static QuickEventListener get() {
        return listener;
    }

    public boolean isInialized() {
        return null != reference && null != reference.get();
    }

    private boolean isOwner = false;

    public QuickEventListener register(Activity activity, boolean isOwner) {
        this.activity = new WeakReference<>(activity);
        this.isOwner = isOwner;
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(this);
        mSeatInfos = new ArrayList<>();
        mAudienceIds = new ArrayList<>();
        return this;
    }

    /**
     * 监听麦位列表变化
     *
     * @param observer
     */
    public void observeSeatList(SeatListObserver observer) {
        this.seatListObserver = observer;
    }

    /**
     * 监听麦位变化
     *
     * @param observer
     */
    public void observeSeat(SeatObserver observer) {
        this.seatObserver = observer;
    }


    /**
     * 监听房间信息变化
     *
     * @param observer
     */
    public void observeRoomInfo(RoomInfoObserver observer) {
        this.roomInfoObserver = observer;
    }

    /**
     * 获取当前用户进入房间后 加入的观众列表
     *
     * @return
     */
    public List<String> getAudienceIds() {
        return mAudienceIds;
    }

    public boolean isInSeat() {
        return null != getSeatInfo(RongCoreClient.getInstance().getCurrentUserId());
    }

    public int getIndexByUserId(String userId) {
        synchronized (obj) {
            int count = mSeatInfos.size();
            for (int i = 0; i < count; i++) {
                RCVoiceSeatInfo s = mSeatInfos.get(i);
                if (userId.equals(s.getUserId())) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 根据用户id获取麦位信息
     *
     * @param userId
     * @return 麦位信息
     */
    public static RCVoiceSeatInfo getSeatInfo(String userId) {
        synchronized (obj) {
            int count = mSeatInfos.size();
            for (int i = 0; i < count; i++) {
                RCVoiceSeatInfo s = mSeatInfos.get(i);
                if (userId.equals(s.getUserId())) {
                    return s;
                }
            }

            return null;
        }
    }

    public static Seat getSeatInfo(int index) {
        synchronized (obj) {
            int count = mSeatInfos.size();
            if (index > -1 && index < count) {
                return mSeatInfos.get(index);
            }
            return null;
        }
    }

    /**
     * 获取可用麦位
     *
     * @return 麦位是否可用
     */
    private boolean seatIsEmpty(int index) {
        synchronized (obj) {
            RCVoiceSeatInfo seat = mSeatInfos.get(index);
            if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                return true;
            }
            return false;
        }
    }

    /**
     * 获取可用麦位索引
     *
     * @return 可用麦位索引
     */
    private int getAvailableSeatIndex() {
        synchronized (obj) {
            int availableIndex = -1;
            for (int i = 0; i < mSeatInfos.size(); i++) {
                RCVoiceSeatInfo seat = mSeatInfos.get(i);
                if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                    availableIndex = i;
                    break;
                }
            }
            return availableIndex;
        }
    }

    @Override
    public void onRoomKVReady() {
        if (null != roomInfoObserver) {
            roomInfoObserver.onReady();
        }
    }

    @Override
    public void onRoomDestroy() {
        KToast.show("房间已销毁");
        if (activity != null && activity.get() != null) {
            activity.get().setResult(Activity.RESULT_OK);
            activity.get().finish();
        }
    }

    /**
     * 房间信息跟新回调
     *
     * @param room
     */
    @Override
    public void onRoomInfoUpdate(RCVoiceRoomInfo room) {
//        RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
//        roomInfo.setRoomName(room.getRoomName());
//        roomInfo.setMuteAll(room.isMuteAll());
//        roomInfo.setLockAll(room.isLockAll());
//        roomInfo.setSeatCount(room.getSeatCount());
    }

    /**
     * 麦位列表跟新回调
     *
     * @param list
     */
    @Override
    public void onSeatInfoUpdate(List<RCVoiceSeatInfo> list) {
        LogCat.e("11111=====麦位列表跟新回调");
        int count = list.size();
        synchronized (obj) {
            mSeatInfos.clear();
            for (int i = 0; i < count; i++) {
                mSeatInfos.add(new Seat(list.get(i)));
            }
            if (null != seatListObserver) seatListObserver.onSeatList(mSeatInfos);
        }

    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onUserEnterSeat(int index, String userId) {
      //  Log.d(TAG, "onUserEnterSeat: index = " + index + " userId = " + userId);
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onUserLeaveSeat(int index, String userId) {
      //  Log.d(TAG, "onUserLeaveSeat: index = " + index + " userId = " + userId);
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onSeatMute(int index, boolean mute) {
        Log.d(TAG, "onSeatMute: index = " + index + " mute = " + mute);
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onSeatLock(int index, boolean locked) {
        Log.d(TAG, "onSeatLock: index = " + index + " locked = " + locked);
    }

    /**
     * 观众进入
     *
     * @param userId
     */
    @Override
    public void onAudienceEnter(String userId) {
        Log.d(TAG, "onAudienceEnter: userId = " + userId);
        //添加关注id
        if (null != mAudienceIds && !mAudienceIds.contains(userId)) {
            mAudienceIds.add(userId);
        }
        if (null != roomInfoObserver) {
          //  roomInfoObserver.onOnLineCount(mAudienceIds.size());
            roomInfoObserver.onUserIn(userId);
        }
    }

    /**
     * 观众离开房间
     *
     * @param userId
     */
    @Override
    public void onAudienceExit(String userId) {
        Log.d(TAG, "onAudienceExit: userId = " + userId);
        // 移除观众id
        if (null != mAudienceIds) mAudienceIds.remove(userId);
//        if (null != roomInfoObserver) {
//            roomInfoObserver.onOnLineCount(mAudienceIds.size());
//        }
    }

    /**
     * 说话状态回调 比较频繁
     *
     * @param index      麦位索引
     * @param audioLevel 是否正在语音
     */
    @Override
    public void onSpeakingStateChanged(int index, int audioLevel) {
      //  Log.d(TAG, "onSpeakingStateChanged: index = " + index + " audioLevel = " + audioLevel);
     //   if (null != seatListObserver) seatListObserver.seatSpeak(index,audioLevel);
//        Seat seat = getSeatInfo(index);
//        if (null != seat) {
//            seat.setAudioLevel(audioLevel);
//            if (null != seatListObserver) seatListObserver.onSeatList(mSeatInfos);
//        }
    }

    @Override
    public void onUserSpeakingStateChanged(String userId, int audioLevel) {
//        Log.d(TAG, "onUserSpeakingStateChanged: userId = " + userId + " audioLevel = " + audioLevel);
    }

    @Override
    public void onMessageReceived(Message message) {
        if (null != roomInfoObserver) {
            roomInfoObserver.onMessage(message);
        }
    }

    /**
     * 房间通知回调
     *
     * @param name
     * @param content
     */
    @Override
    public void onRoomNotificationReceived(String name, String content) {
        Log.v(TAG, "onRoomNotificationReceived: name = " + name + " content = " + content);
    }

    /**
     * 当前用户被抱上麦回调
     *
     * @param userId 发起邀请的用户id
     */
    @Override
    public void onPickSeatReceivedFrom(String userId) {
        // 房主邀请别人，不接收邀请
        if (isOwner) return;
        Log.d(TAG, "onPickSeatReceivedFrom: userId = " + userId);
        if (null != activity && null != activity.get()) {
            String name = Member.Companion.getMemberName(userId);
            ApiFunDialogHelper.helper().showTipDialog(activity.get(), "邀请", "'" + name + "'邀请您上麦，是否接收", new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        //同意
                        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(Api.EVENT_AGREE_PICK, Member.Companion.getCurrentId(), null);
                        //获取可用麦位索引
                        int availableIndex = getAvailableSeatIndex();
                        if (availableIndex > -1) {
                            VoiceRoomApi.getApi().enterSeat(availableIndex, false,null);
                        } else {
                            KToast.show("当前没有空余的麦位");
                        }
                    } else {//拒绝
                        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(Api.EVENT_REJECT_PICK,Member.Companion.getCurrentId(), null);
                    }

                }
            });
        }
    }

    /**
     * 被踢下麦回调
     *
     * @param index 麦位索引
     */
    @Override
    public void onKickSeatReceived(int index) {
        Log.d(TAG, "onPickSeatReceivedFrom: index = " + index);
        KToast.show("您被抱下麦");
    }

    /**
     * 房主或管理员同意当前用户的排麦申请的回调
     * 1. enterSeat
     */
    @Override
    public void onRequestSeatAccepted() {
        Log.d(TAG, "onRequestSeatAccepted: ");
        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(Api.EVENT_AGREE_PICK,
                Member.Companion.getCurrentId(), null);
        if (seatIsEmpty(Tool.Companion.getCurrentSeatIndex())) {
            if (null != seatListObserver) seatListObserver.requestSeatAccepted(Tool.Companion.getCurrentSeatIndex());
            KToast.show("您的上麦申请被同意啦");
            VoiceRoomApi.getApi().enterSeat(Tool.Companion.getCurrentSeatIndex(),false, null);
        } else {
            if (null != seatListObserver) seatListObserver.requestSeatAccepted(-1);
            KToast.show("当前没有空余的麦位");
        }
    }

    /**
     * 发送的排麦请求被房主或管理员拒绝
     */
    @Override
    public void onRequestSeatRejected() {
        Log.d(TAG, "onRequestSeatRejected: ");
        KToast.show("您的上麦申请被拒绝啦");
    }

    /**
     * 排麦列表发生变化
     * 1、获取申请排麦id列表
     * 2、过滤已经在房间的用户
     * 3、弹框提 同意、拒绝
     */
    @Override
    public void onRequestSeatListChanged() {
        // 非房主不接受处理排麦
        if (!isOwner) return;
        Log.d(TAG, "onRequestSeatListChanged: ");
        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(new RCVoiceRoomResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                Log.e(TAG, "getRequestSeatUserIds: ids = " + GsonUtil.obj2Json(strings));
                List<String> requestIds = new ArrayList<>();
                for (String id : strings) {
                    if (null == getSeatInfo(id)) {//过滤 不再麦位上
                        requestIds.add(id);
                    }
                }
                if (!requestIds.isEmpty()) {
                    String userId = requestIds.get(0);
                    String name = Member.Companion.getMemberName(userId);
                    ApiFunDialogHelper.helper().showTipDialog(activity.get(), "申请上麦", "'" + name + "'申请上麦 是否同意？", new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if (result) {
                                //同意
                                int index = getAvailableSeatIndex();
                                if (index > -1) {
                                    RCVoiceRoomEngine.getInstance().acceptRequestSeat(userId, null);
                                } else {
                                    KToast.show("当前没有空余的麦位");
                                }
                            } else {//拒绝
                                RCVoiceRoomEngine.getInstance().rejectRequestSeat(userId, null);
                            }
                        }
                    });
                } else {//申请被取消
                    ApiFunDialogHelper.helper().dismissDialog();
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "onError: code:" + i + " ,message = " + s);
            }
        });

    }

    /**
     * 收到邀请
     *
     * @param invitationId 邀请标识 Id
     * @param userId       发送邀请用户的标识
     * @param content      邀请内容 （用户可以自定义）
     */
    @Override
    public void onInvitationReceived(String invitationId, String userId, String content) {
        Log.d(TAG, "onInvitationReceived: invitationId = " + invitationId + " userId = " + userId + " content = " + content);
    }

    /**
     * 邀请被接受回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationAccepted(String invitationId) {
        Log.d(TAG, "onInvitationAccepted: invitationId = " + invitationId);
    }

    /**
     * 邀请被拒绝回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationRejected(String invitationId) {
        Log.d(TAG, "onInvitationRejected: invitationId = " + invitationId);
    }

    /**
     * 邀请被取消回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationCancelled(String invitationId) {
        Log.d(TAG, "onInvitationCancelled: invitationId = " + invitationId);
    }

    /**
     * 被踢出房间回调
     *
     * @param targetId 被踢用户的标识
     * @param userId   发起踢人用户的标识
     */
    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        if (null != roomInfoObserver) {
            roomInfoObserver.onOut(targetId);
        }
        Log.d(TAG, "onUserReceiveKickOutRoom: targetId = " + targetId);
    }

    @Override
    public void onNetworkStatus(int delayMs) {
//        Log.d(TAG, "onNetworkStatus: delayMs = " + delayMs);
    }

    @Override
    public void onPKGoing(RCPKInfo rcpkInfo) {

    }

    @Override
    public void onPKFinish() {

    }

    @Override
    public void onReceivePKInvitation(String inviterRoomId, String inviterUserId) {
        Log.d(TAG, "onReveivePKInvitation: inviterRoomId = " + inviterRoomId + " inviterUserId = " + inviterUserId);
        String name = Member.Companion.getMemberName(inviterUserId);
        ApiFunDialogHelper.helper().showTipDialog(activity.get(), "PK邀请", "'" + name + "'向您发起PK申请，是否同意？", new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                RCVoiceRoomEngine.getInstance().responsePKInvitation(inviterRoomId, inviterUserId, result ? PKResponse.accept : PKResponse.reject, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show(result ? "同意PK成功" : "拒绝PK成功");
                    }

                    @Override
                    public void onError(int code, String message) {
                        KToast.show((result ? "同意PK失败" : "拒绝PK失败") + " code = " + code + " message = " + message);
                    }
                });
            }
        });
    }

    @Override
    public void onPKInvitationCanceled(String inviterRoomId, String inviterUserId) {

    }

    @Override
    public void onPKInvitationRejected(String inviteeRoomId, String inviteeUserId) {

    }

    @Override
    public void onPKInvitationIgnored(String inviteeRoomId, String inviteeUserId) {

    }

    @Override
    public void onUserAudioRecordingDisable(String roomId, String userId, boolean disable) {

    }
}