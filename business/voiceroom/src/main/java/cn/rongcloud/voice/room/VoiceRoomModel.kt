package cn.rongcloud.voice.room

import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.RoomDetailBean
import cn.rongcloud.config.api.roomDetail
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.music.MusicControlManager
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager
import cn.rongcloud.voice.Constant
import cn.rongcloud.voice.model.UiRoomModel
import cn.rongcloud.voice.model.UiSeatModel
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener
import cn.rongcloud.voiceroom.model.RCPKInfo
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.logcat.LogCat.d
import com.drake.logcat.LogCat.e
import com.drake.net.utils.scopeNet
import com.lalifa.extension.noEN
import com.lalifa.ui.mvp.BaseModel
import com.lalifa.utils.GsonUtil
import com.lalifa.utils.KToast
import com.lalifa.utils.UIKit
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message

/**
 * 语聊房的逻辑处理
 */
class VoiceRoomModel(present: VoiceRoomPresenter?, lifecycle: Lifecycle?) :
    BaseModel<VoiceRoomPresenter?>(present, lifecycle!!), RCVoiceRoomEventListener {
    private val TAG = "NewVoiceRoomModel"

    //线程调度器
    var dataModifyScheduler = Schedulers.computation()

    //麦位信息变化监听器
    private val seatInfoChangeSubject = BehaviorSubject.create<UiSeatModel>()

    //座位数量订阅器,为了让所有订阅的地方都能回调回去
    private val seatListChangeSubject = BehaviorSubject.create<List<UiSeatModel>>()

    //房间信息发生改变订阅，比如房间被解散，上锁之类的
    private val roomInfoSubject = BehaviorSubject.create<UiRoomModel>()

    //房间事件监听（麦位 进入 踢出等等）
    private val roomEventSubject = BehaviorSubject.create<Pair<String?, ArrayList<String>?>>()

    /**
     * 申请和撤销上麦下麦的监听
     */
    private val obRequestSeatListChangeSuject = BehaviorSubject.create<List<User>>()

    /**
     * 可以被邀请的人员监听
     */
    private val obInviteSeatListChangeSuject = BehaviorSubject.create<List<User>?>()
    @JvmField
    var currentUIRoomInfo = UiRoomModel(roomInfoSubject)

    //本地麦克风的状态，默认是开启的
    var isRecordingStatus = true

    //麦位集合
    @Volatile
    var uiSeatModels = ArrayList<UiSeatModel>()

    //申请连麦的集合
    val requestSeats = ArrayList<User>()

    //可以被邀请的集合
    val inviteSeats: ArrayList<User>? = ArrayList()

    /**
     * 监听麦位数量变化
     */
    fun obSeatListChange(): Observable<List<UiSeatModel>> {
        return seatListChangeSubject.subscribeOn(dataModifyScheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取指定位置的麦位
     */
    fun obSeatInfoByIndex(index: Int): Observable<UiSeatModel> {
        return seatListChangeSubject.map { uiSeatModels -> uiSeatModels[index] }
            .subscribeOn(dataModifyScheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 麦位信息发生了变化
     */
    fun obSeatInfoChange(): Observable<UiSeatModel> {
        return seatInfoChangeSubject.subscribeOn(dataModifyScheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 监听房间的事件
     */
    fun obRoomEventChange(): Observable<Pair<String?, ArrayList<String>?>> {
        return roomEventSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    /**
     * 监听房间的信息改变，比如上锁，解散之类的信息
     */
    fun obRoomInfoChange(): Observable<UiRoomModel> {
        return roomInfoSubject.subscribeOn(dataModifyScheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 监听申请上麦和撤销申请的监听
     */
    fun obRequestSeatListChange(): Observable<List<User>> {
        return obRequestSeatListChangeSuject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    /**
     * 监听可以被邀请的人员
     */
    fun obInviteSeatListChange(): Observable<List<User>?> {
        return obInviteSeatListChangeSuject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    override fun onRoomKVReady() {}
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onRoomDestroy() {
        roomEventSubject.onNext(Pair<String?, ArrayList<String>?>(Constant.EVENT_ROOM_CLOSE, ArrayList<String>()))
    }

    override fun onRoomInfoUpdate(rcVoiceRoomInfo: RCVoiceRoomInfo) {
        Log.e(TAG, "onRoomInfoUpdate: ")
        currentUIRoomInfo.rcRoomInfo = rcVoiceRoomInfo
    }

    /**
     * 麦位信息发生了变化
     * 这里用同步锁，避免多线程操作的时候，影响麦位的显示
     *
     * @param list
     */
    override fun onSeatInfoUpdate(list: List<RCVoiceSeatInfo>) {
        synchronized(this) {
            val size = list?.size ?: 0
            val olds: MutableList<UiSeatModel> = ArrayList()
            olds.addAll(uiSeatModels)
            val oldCount = olds.size
            uiSeatModels.clear()
            for (i in 0 until size) {
                //构建一个集合返回去
                val rcVoiceSeatInfo = list[i]
                if (!TextUtils.isEmpty(rcVoiceSeatInfo.userId)
                    && rcVoiceSeatInfo.status == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                ) {
                    rcVoiceSeatInfo.status = RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing
                }
                val uiSeatModel = UiSeatModel(i, rcVoiceSeatInfo, seatInfoChangeSubject)
                //缓存giftcount
                if (i < oldCount) {
                    uiSeatModel.giftCount = olds[i].giftCount
                }
                uiSeatModels.add(uiSeatModel)
            }
            seatListChangeSubject.onNext(uiSeatModels)
        }
        present!!.refreshRoomMember()
    }

    /**
     * 用户加入麦位
     *
     * @param i
     * @param s
     */
    override fun onUserEnterSeat(i: Int, s: String) {
        Log.e(TAG, "onUserEnterSeat: ")
        //        present.refreshRoomMember();
    }

    /**
     * 用户离开麦位,并且保证该用户在房间里面 那么该用户应该能够被邀请才对
     *
     * @param i
     * @param s
     */
    override fun onUserLeaveSeat(i: Int, s: String) {
        Log.e(TAG, "onUserLeaveSeat: ")
        //如果是房主的话，那么去更新房主的信息
//        present.refreshRoomMember();
    }

    /**
     * 麦位被禁止
     *
     * @param i
     * @param b
     */
    override fun onSeatMute(i: Int, b: Boolean) {
        val uiSeatModel = uiSeatModels[i]
        uiSeatModel.isMute = b
        //        Log.e(TAG, "onSeatMute: ");
    }

    /**
     * 锁住当前座位
     *
     * @param i
     * @param b
     */
    override fun onSeatLock(i: Int, b: Boolean) {
        //锁住的位置，和状态
//        present.refreshRoomMember();
        Log.e(TAG, "onSeatLock: ")
    }

    /**
     * 观众加入
     *
     * @param s
     */
    override fun onAudienceEnter(s: String) {
        Log.e(TAG, "onAudienceEnter: ")
        present!!.refreshRoomMember()
    }

    /**
     * 观众退出
     *
     * @param s
     */
    override fun onAudienceExit(s: String) {
        Log.e(TAG, "onAudienceExit: ")
        //由于SDK内实现的是先发消息，才退的房间，导致此处立即刷新房间成员列表，偶像依然会拉取退房间前的列表
        UIKit.postDelayed({
            if (present != null) {
                present!!.refreshRoomMember()
            }
        }, 2000)
        //缓存立即刷新
        if (null != inviteSeats && !inviteSeats.isEmpty()) {
            val count = inviteSeats.size
            var exit: User? = null
            for (i in 0 until count) {
                val user = inviteSeats[i]
                if (TextUtils.equals(user.userId, s)) {
                    exit = user
                    break
                }
            }
            if (null != exit) {
                inviteSeats.remove(exit)
                obInviteSeatListChangeSuject.onNext(inviteSeats)
            }
        }
    }

    /**
     * 麦位的信息变化监听
     *
     * @param i
     * @param audioLevel
     */
    override fun onSpeakingStateChanged(i: Int, audioLevel: Int) {
        if (uiSeatModels.size > i) {
            val uiSeatModel = uiSeatModels[i]
            uiSeatModel.isSpeaking = audioLevel > 0
        }
    }

    override fun onUserSpeakingStateChanged(s: String, i: Int) {}

    /**
     * 消息接收
     *
     * @param message 收到的消息
     */
    override fun onMessageReceived(message: Message) {
        if (!TextUtils.isEmpty(present!!.roomId) && message.conversationType == Conversation.ConversationType.CHATROOM) {
            RCChatRoomMessageManager.onReceiveMessage(present!!.roomId, message.content)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onRoomNotificationReceived(name: String, content: String) {
        val contents = ArrayList<String>()
        contents.add(content)
        sendRoomEvent(Pair(name, contents))
    }

    /**
     * 收到上麦邀请
     *
     * @param userId
     */
    override fun onPickSeatReceivedFrom(userId: String) {
        if (userId == present!!.createUserId) {
            //当前是房主邀请的
            present!!.showPickReceivedDialog(true, userId)
        } else {
            //管理员邀请
            present!!.showPickReceivedDialog(false, userId)
        }
    }

    /**
     * 被抱下麦
     *
     * @param i
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onKickSeatReceived(i: Int) {
        sendRoomEvent(Pair<String?, ArrayList<String>?>(Constant.EVENT_KICK_OUT_OF_SEAT, ArrayList<String>()))
    }

    /**
     * 请求加入麦位被允许
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onRequestSeatAccepted() {
        sendRoomEvent(Pair<String?, ArrayList<String>?>(Constant.EVENT_REQUEST_SEAT_AGREE, ArrayList<String>()))
    }

    /**
     * 请求加入麦位被拒绝
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onRequestSeatRejected() {
        sendRoomEvent(Pair<String?, ArrayList<String>?>(Constant.EVENT_REQUEST_SEAT_REFUSE, ArrayList<String>()))
    }

    /**
     * 发送房间事件
     *
     * @param pair
     */
    fun sendRoomEvent(pair: Pair<String?, ArrayList<String>?>) {
        roomEventSubject.onNext(pair)
    }

    /**
     * 接收请求 撤销麦位
     */
    override fun onRequestSeatListChanged() {
        d(TAG, "onRequestSeatListChanged")
        requestSeatUserIds
    }//获取到当前房间所有用户,申请人需要在房间，并且不在麦位上

    /**
     * 获取到正在申请麦位的用户的信息
     */
    val requestSeatUserIds: Unit
        get() {
            //todo 111
//            RCVoiceRoomEngine.getInstance()
//                .getRequestSeatUserIds(object : RCVoiceRoomResultCallback<List<String>> {
//                    override fun onSuccess(requestUserIds: List<String>) {
//                        //获取到当前房间所有用户,申请人需要在房间，并且不在麦位上
//                        e(TAG, "requestUserIds = " + GsonUtil.obj2Json(requestUserIds))
//                        val users = MemberCache.getInstance().memberList.value!!
//                        requestSeats.clear()
//                        for (requestUserId in requestUserIds) {
//                            for (user in users) {
//                                if (user.userId == requestUserId && getSeatInfoByUserId(user.userId) == null) {
//                                    requestSeats.add(user)
//                                    break
//                                }
//                            }
//                        }
//                        obRequestSeatListChangeSuject.onNext(requestSeats)
//                    }
//
//                    override fun onError(i: Int, s: String) {}
//                })
        }

    /**
     * 收到邀请
     *
     * @param invitationId
     * @param userId
     * @param content
     */
    override fun onInvitationReceived(invitationId: String, userId: String, content: String) {
        Log.e(TAG, "onInvitationReceived: ")
    }

    /**
     * 同意邀请
     *
     * @param invitationId
     */
    override fun onInvitationAccepted(invitationId: String) {
        Log.e(TAG, "onInvitationAccepted: ")
    }

    /**
     * 拒绝邀请
     *
     * @param invitationId
     */
    override fun onInvitationRejected(invitationId: String) {
        Log.e(TAG, "onInvitationRejected: ")
    }

    /**
     * 取消邀请
     *
     * @param invitationId
     */
    override fun onInvitationCancelled(invitationId: String) {
        Log.e(TAG, "onInvitationCancelled: ")
    }

    /**
     * 用户收到被踢出房间 然后弹窗告知，然后退出房间等操作
     *
     * @param targetId 被踢用户的标识
     * @param userId   发起踢人用户的标识
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    override fun onUserReceiveKickOutRoom(targetId: String, userId: String) {
        Log.e(TAG, "onUserReceiveKickOutRoom: ")
        val strings = ArrayList<String>()
        strings.add(userId)
        strings.add(targetId)
        sendRoomEvent(Pair<String?, ArrayList<String>?>(Constant.EVENT_KICKED_OUT_OF_ROOM, strings))
    }

    /**
     * 网络信号监听
     *
     * @param i
     */
    override fun onNetworkStatus(i: Int) {
        if (present != null) present!!.onNetworkStatus(i)
    }

    override fun onPKGoing(rcpkInfo: RCPKInfo) {}
    override fun onPKFinish() {}
    override fun onReceivePKInvitation(s: String, s1: String) {}
    override fun onPKInvitationCanceled(s: String, s1: String) {}
    override fun onPKInvitationRejected(s: String, s1: String) {}
    override fun onPKInvitationIgnored(s: String, s1: String) {}

    /**
     * 获取房间信息
     */
    fun getRoomInfo(roomId: String?): Single<RoomDetailBean> {
        return Single.create {
            val roomBean = currentUIRoomInfo.roomBean
            if (roomBean != null) {
//                    currentUIRoomInfo.setRoomBean(voiceRoomBean);
            } else {
                //通过网络去获取
                queryRoomInfoFromServer(roomId)
            }
        }
    }

    /**
     * 通过网络去获取最新的房间信息
     * @param roomId
     * @return
     */
    private fun queryRoomInfoFromServer(roomId: String?) {
        scopeNet {
            val roomDetails = roomDetail(roomId!!.noEN())
            if(null!=roomDetails){
              //  currentUIRoomInfo.roomBean = roomDetails
            }
        }
    }
    /**
     * 音乐的所有操作
     * TODO =================================================================
     */
    /**
     * 音乐是否正在播放
     *
     * @return
     */
    val isPlayingMusic: Boolean
        get() = MusicControlManager.getInstance().isPlaying

    /**
     * 当房间人员变化的时候监听，当有人上麦或者下麦的时候也要监听
     *
     * @param users
     */
    fun onMemberListener(users: List<User>) {
        d(TAG, "onMemberListener")
        //房间观众发生变化
        requestSeatUserIds

        //只要不在麦位的人都可以被邀请
        inviteSeats!!.clear()
        for (user in users) {
            //是否在麦位上标识
            var isInSeat = false
            //当前用户在麦位上或者当前用户是房间创建者，那么不可以被邀请
            for (uiSeatModel in uiSeatModels) {
                if (!TextUtils.isEmpty(uiSeatModel.userId) && uiSeatModel.userId == user.userId
                    || user.userId == UserManager.get()!!.userId
                ) {
                    isInSeat = true
                    break
                }
            }
            if (!isInSeat) {
                inviteSeats.add(user)
            }
        }
        obInviteSeatListChangeSuject.onNext(inviteSeats)
    }
    /**
     * 关于到麦位的所有的操作
     * TODO =====================================================================
     */
    /**
     * 根据ID获取当前的麦位信息
     *
     * @param userId
     * @return
     */
    fun getSeatInfoByUserId(userId: String): UiSeatModel? {
        if (TextUtils.isEmpty(userId)) {
            return null
        }
        for (uiSeatModel in uiSeatModels) {
            if (!TextUtils.isEmpty(uiSeatModel.userId) && uiSeatModel.userId == userId) {
                return uiSeatModel
            }
        }
        return null
    }

    /**
     * 麦位断开链接
     *
     * @return
     */
    fun leaveSeat(): Completable {
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().leaveSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    emitter.onComplete()
                }

                override fun onError(i: Int, s: String) {
                    emitter.onError(Throwable(s))
                }
            })
        }.subscribeOn(dataModifyScheduler)
    }

    /**
     * 上麦
     *
     * @param userId 邀请人id
     */
    fun enterSeatIfAvailable(userId: String?) {
        RCVoiceRoomEngine.getInstance()
            .notifyVoiceRoom(Constant.EVENT_AGREE_MANAGE_PICK, userId, null)
        val availableIndex = availableIndex
        if (availableIndex > 0) {
            RCVoiceRoomEngine
                .getInstance()
                .enterSeat(availableIndex, object : RCVoiceRoomCallback {
                    override fun onSuccess() {
                        KToast.show("上麦成功")
                    }

                    override fun onError(code: Int, message: String) {
                        KToast.show(message)
                    }
                })
        } else {
            KToast.show("当前没有空余的麦位")
        }
    }

    /**
     * 自己是否已经在麦位上了
     *
     * @return
     */
    fun userInSeat(): Boolean {
        for (currentSeat in uiSeatModels) {
            if (currentSeat.userId != null && currentSeat.userId == UserManager.get()!!
                    .userId
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 位置是否有效
     *
     * @return
     */
    val availableIndex: Int
        get() {
            for (i in uiSeatModels.indices) {
                val uiSeatModel = uiSeatModels[i]
                if (uiSeatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty && i != 0) {
                    return i
                }
            }
            return -1
        }

    /**
     * 拒绝邀请
     *
     * @param userId 邀请人ID
     */
    fun refuseInvite(userId: String?) {
        RCVoiceRoomEngine.getInstance()
            .notifyVoiceRoom(Constant.EVENT_REJECT_MANAGE_PICK, userId, null)
    }

    /**
     * 房主控制自己上麦和下麦
     */
    fun creatorMuteSelf(): Completable {
        val seatInfoByUserId = getSeatInfoByUserId(
            UserManager.get()!!.userId
        )
        var extra = seatInfoByUserId!!.extra
        if (extra == null) {
            extra = UiSeatModel.UiSeatModelExtra()
        }
        extra.isDisableRecording = !extra.isDisableRecording
        seatInfoByUserId.extra = extra
        val finalExtra = extra
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().disableAudioRecording(finalExtra.isDisableRecording)
            RCVoiceRoomEngine.getInstance()
                .updateSeatInfo(0, GsonUtil.obj2Json(finalExtra), object : RCVoiceRoomCallback {
                    override fun onSuccess() {
                        emitter.onComplete()
                    }

                    override fun onError(code: Int, message: String) {
                        emitter.onError(Throwable(message))
                    }
                })
        }.subscribeOn(dataModifyScheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun creatorMuteSelf(disable: Boolean) {
        e(TAG, "creatorMuteSelf disable = $disable")
        RCVoiceRoomEngine.getInstance().disableAudioRecording(disable)
        val extra = UiSeatModel.UiSeatModelExtra()
        extra.isDisableRecording = disable
        RCVoiceRoomEngine.getInstance()
            .updateSeatInfo(0, GsonUtil.obj2Json(extra), object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    e(TAG, "creatorMuteSelf:updateSeatInfo success")
                }

                override fun onError(code: Int, message: String) {
                    e(TAG, "creatorMuteSelf:updateSeatInfo code =$code: $message")
                }
            })
    }

    fun muteSelf(index: Int, disable: Boolean) {
        e(TAG, "muteSelf disable = $disable index = $index")
        RCVoiceRoomEngine.getInstance().disableAudioRecording(disable)
        val extra = UiSeatModel.UiSeatModelExtra()
        extra.isDisableRecording = disable
        RCVoiceRoomEngine.getInstance()
            .updateSeatInfo(index, GsonUtil.obj2Json(extra), object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    e(TAG, "creatorMuteSelf:updateSeatInfo success")
                }

                override fun onError(code: Int, message: String) {
                    e(TAG, "creatorMuteSelf:updateSeatInfo code =$code: $message")
                }
            })
    }
}