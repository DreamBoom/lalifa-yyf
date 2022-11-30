package cn.rongcloud.voice.room

import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.RoomDetailBean
import cn.rongcloud.config.api.roomDetail
import cn.rongcloud.config.api.roomGift
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.music.MusicApi
import cn.rongcloud.music.MusicBean
import cn.rongcloud.music.MusicControlManager
import cn.rongcloud.roomkit.intent.IntentWrap
import cn.rongcloud.roomkit.manager.AllBroadcastManager
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager
import cn.rongcloud.roomkit.message.*
import cn.rongcloud.roomkit.provider.VoiceRoomProvider
import cn.rongcloud.roomkit.ui.OnItemClickListener
import cn.rongcloud.roomkit.ui.RoomListIdsCache
import cn.rongcloud.roomkit.ui.RoomOwnerType
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield
import cn.rongcloud.roomkit.ui.room.fragment.BackgroundSettingFragment.OnSelectBackgroundListener
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment.OnMemberSettingClickListener
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment.OnSendGiftListener
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.*
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun.BaseFun
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.EmptySeatFragment
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.ICommonDialog
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.RevokeSeatRequestFragment
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.SeatOperationViewPagerFragment
import cn.rongcloud.roomkit.ui.room.model.Member
import cn.rongcloud.roomkit.ui.room.model.MemberCache
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar
import cn.rongcloud.roomkit.widget.InputPasswordDialog
import cn.rongcloud.voice.Constant
import cn.rongcloud.voice.model.UiSeatModel
import cn.rongcloud.voice.room.dialogFragment.CreatorSettingFragment
import cn.rongcloud.voice.room.dialogFragment.SelfSettingFragment
import cn.rongcloud.voice.room.helper.VoiceEventHelper
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.logcat.LogCat
import com.drake.logcat.LogCat.d
import com.drake.logcat.LogCat.e
import com.drake.net.utils.scopeNet
import com.lalifa.extension.noEN
import com.lalifa.ui.mvp.BasePresenter
import com.lalifa.utils.GsonUtil
import com.lalifa.utils.KToast
import com.lalifa.utils.UIKit
import com.lalifa.wapper.IResultBack
import com.lalifa.wapper.IRoomCallBack
import com.lalifa.widget.dialog.dialog.VRCenterDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.rong.imlib.model.MessageContent
import io.rong.message.CommandMessage
import io.rong.message.TextMessage
import java.util.*

/**
 * 语聊房present
 */
class VoiceRoomPresenter(mView: IVoiceRoomFragmentView?, lifecycle: Lifecycle?) :
    BasePresenter<IVoiceRoomFragmentView?>(mView, lifecycle!!),
    OnItemClickListener<MutableLiveData<BaseFun?>?>, IVoiceRoomPresent,
    OnMemberSettingClickListener, OnSelectBackgroundListener, OnSendGiftListener,
    RoomTitleBar.OnFollowClickListener, ICommonDialog {
    private val TAG = "NewVoiceRoomPresenter"

    /**
     * 语聊房model
     */
    private val voiceRoomModel: VoiceRoomModel?

    /**
     * 房间信息
     */
    private var mVoiceRoomBean: RoomDetailBean? = null
    private var confirmDialog: VRCenterDialog? = null
    var roomOwnerType: RoomOwnerType? = null
        private set
    private var inputPasswordDialog: InputPasswordDialog? = null
    @JvmField
    var currentStatus = STATUS_NOT_ON_SEAT

    //    private List<Shield> shields = new ArrayList<>();
    //监听事件全部用集合管理,所有的监听事件需要在离开当前房间的时候全部取消注册
    private val disposableList: MutableList<Disposable> = ArrayList()
    private var emptySeatFragment: EmptySeatFragment? = null
    private var isInRoom = false
    var notice: String? = null
        private set

    /**
     * 初始化
     *
     * @param roomId
     * @param isCreate
     */
    fun init(roomId: String, isCreate: Boolean) {
        isInRoom = TextUtils.equals(VoiceEventHelper.helper().roomId, roomId)
        // TODO 请求数据
        getRoomInfo(roomId,isCreate)
    }

    /**
     * 获取房间信息
     */
    private fun getRoomInfo(roomId: String, isCreate: Boolean) {
        mView!!.showLoading("")
        scopeNet {
            val roomDetail = roomDetail(roomId.noEN())
            if(null!=roomDetail){
                mVoiceRoomBean = roomDetail
                if (isInRoom) {
                    //如果已经在房间里面了,那么需要重新设置监听
                    initListener(roomId)
                    currentStatus = VoiceEventHelper.helper().currentStatus
                    mView!!.changeStatus(currentStatus)
                    voiceRoomModel!!.currentUIRoomInfo.isMute =
                        VoiceEventHelper.helper().muteAllRemoteStreams
                    //todo
                   // voiceRoomModel.onSeatInfoUpdate(VoiceEventHelper.helper().rcVoiceSeatInfoList)
                    setCurrentRoom(roomDetail)
                    mView!!.dismissLoading()
                    if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true)
                    }
                } else {
                    leaveRoom(roomId, isCreate, true)
                }
            }else{
                mView!!.dismissLoading()
                //房间不存在了
                mView!!.showFinishView()
                leaveRoom(roomId, isCreate, false)
            }
        }
    }

    private fun leaveRoom(roomId: String, isCreate: Boolean, isExit: Boolean) {
        // 先退出上个房间
        RCVoiceRoomEngine.getInstance().leaveRoom(object : RCVoiceRoomCallback {
            override fun onSuccess() {
                LogCat.i("==============leaveRoom onSuccess")
                VoiceEventHelper.helper().changeUserRoom(roomId)
                if (isExit) {
                    UIKit.postDelayed({ joinRoom(roomId, isCreate) }, 0)
                }
            }

            override fun onError(code: Int, message: String) {
                LogCat.e("==============leaveRoom onError,code:$code,message:$message")
                if (isExit) {
                    joinRoom(roomId, isCreate)
                }
            }
        })
    }

    private fun joinRoom(roomId: String, isCreate: Boolean) {
        //设置界面监听
        VoiceEventHelper.helper().register(roomId)
        VoiceEventHelper.helper().setRoomBean(mVoiceRoomBean)
        initListener(roomId)
        //重置底部状态
        currentStatus = STATUS_NOT_ON_SEAT
        mView!!.changeStatus(currentStatus)
        if (isCreate) {
            val rcVoiceRoomInfo = RCVoiceRoomInfo()
            rcVoiceRoomInfo.roomName = mVoiceRoomBean!!.title
            rcVoiceRoomInfo.seatCount = 9
            rcVoiceRoomInfo.isFreeEnterSeat = false
            rcVoiceRoomInfo.isLockAll = false
            rcVoiceRoomInfo.isMuteAll = false
            RCVoiceRoomEngine.getInstance()
                .createAndJoinRoom(roomId, rcVoiceRoomInfo, object : RCVoiceRoomCallback {
                    override fun onSuccess() {
                        d("==============createAndJoinRoom onSuccess")
                        VoiceEventHelper.helper().changeUserRoom(roomId)
                        setCurrentRoom(mVoiceRoomBean!!)
                        if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                            refreshMusicView(true)
                        }
                        mView!!.dismissLoading()
                    }

                    override fun onError(code: Int, message: String) {
                        e("==============createAndJoinRoom onError,code:$code,message:$message")
                        mView!!.dismissLoading()
                        KToast.show("创建房间失败")
                        closeRoom()
                    }
                })
        } else {
            LogCat.e("==============joinRoom start")
            RCVoiceRoomEngine.getInstance().joinRoom(roomId, object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    d("==============joinRoom onSuccess")
                    VoiceEventHelper.helper().changeUserRoom(roomId)
                    setCurrentRoom(mVoiceRoomBean!!)
                    if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true)
                    }
                    mView!!.dismissLoading()
                }

                override fun onError(code: Int, message: String) {
                    e("==============joinRoom onError,code:$code,message:$message")
                    mView!!.dismissLoading()
                }
            })
        }
    }

    /**
     * 进入房间后发送默认的消息
     */
    private fun sendSystemMessage() {
        if (mVoiceRoomBean != null) {
            mView!!.showMessage(null, true)
            // 默认消息
            val welcome = RCChatroomLocationMessage()
            welcome.content = String.format("欢迎来到 %s", mVoiceRoomBean!!.title)
            RCChatRoomMessageManager.sendLocationMessage(mVoiceRoomBean!!.Chatroom_id, welcome)
            val tips = RCChatroomLocationMessage()
            tips.content = "感谢使用随心语音 语音房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。"
            RCChatRoomMessageManager.sendLocationMessage(mVoiceRoomBean!!.Chatroom_id, tips)
            d("=================发送了默认消息")
            // 广播消息
            val enter = RCChatroomEnter()
            enter.userId = UserManager.get()!!.userId
            enter.userName = UserManager.get()!!.userName
            RCChatRoomMessageManager.sendChatMessage(
                mVoiceRoomBean!!.Chatroom_id, enter, false,
                { integer: Any? -> null }) { coreErrorCode: Any?, integer: Any? -> null }
        }
    }

    val roomId: String
        get() = if (mVoiceRoomBean != null) {
            mVoiceRoomBean!!.Chatroom_id
        } else ""
    val createUserId: String
        get() = if (mVoiceRoomBean != null) {
            mVoiceRoomBean!!.uid.toString()
        } else ""
    val themePictureUrl: String
        get() = if (mVoiceRoomBean != null) {
            //todo 这里是主题背景，暂时使用房间图片代替，后期联系后台添加
            mVoiceRoomBean!!.image
        } else ""

    /**
     * 设置当前的voiceBean
     *
     * @param mVoiceRoomBean
     */
    override fun setCurrentRoom(mVoiceRoomBean: RoomDetailBean?) {
        roomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(mVoiceRoomBean)
        // 房主进入房间，如果不在麦位上那么自动上麦
        if (roomOwnerType == RoomOwnerType.VOICE_OWNER && !voiceRoomModel!!.userInSeat() && !isInRoom) {
            roomOwnerEnterSeat(true)
        } else if (voiceRoomModel!!.userInSeat()) {
            // 第一次进房间 房主的disableRecord状态置为false
            if (null == voiceRoomModel) return
            RCVoiceRoomEngine.getInstance().disableAudioRecording(!voiceRoomModel.isRecordingStatus)
            //            UiSeatModel seatModel = voiceRoomModel.getUiSeatModels().get(0);
//            if (null != seatModel && null != seatModel.getExtra()) {
//                boolean disable = seatModel.getExtra().isDisableRecording();
//                voiceRoomModel.creatorMuteSelf(disable);
//            }
        }
        if (isInRoom) {
            //恢复一下当前信息就可以了
            val messageList = VoiceEventHelper.helper().messageList
            mView!!.showMessageList(messageList, true)
        } else {
            // 发送默认消息
            sendSystemMessage()
        }
        //界面初始化成功的时候，要去请求网络
        voiceRoomModel.getRoomInfo(mVoiceRoomBean!!.Chatroom_id).subscribe()
        //刷新房间信息
        MemberCache.Companion.get().fetchData(mVoiceRoomBean.Chatroom_id)
        //监听房间里面的人
        MemberCache.Companion.get().memberList.observe((mView as VoiceRoomFragment).viewLifecycleOwner) {
                users -> //人数
            mView!!.setOnlineCount(users.size)
            voiceRoomModel.onMemberListener(users)
        }
        MemberCache.Companion.get().adminList.observe((mView as VoiceRoomFragment).viewLifecycleOwner) {
            mView!!.refreshMessageList() }
        MemberCache.Companion.get().adminList.observe((mView as VoiceRoomFragment).viewLifecycleOwner) {
            mView!!.refreshSeat() }
        //获取屏蔽词
        shield
        giftCount
        mView!!.setRoomData(mVoiceRoomBean)
    }

    override fun getmVoiceRoomBean(): RoomDetailBean? {
        return mVoiceRoomBean!!
    }

    override fun initListener(roomId: String) {
        //注册model关于房间的监听
        VoiceEventHelper.helper().setRCVoiceRoomEventListener(voiceRoomModel)
        setObSeatListChange()
        setObRoomEventChange()
        setRequestSeatListener()
        setObSeatInfoChange()
        setObRoomInfoChange()
        setObShieldListener()
        setObMessageListener()
    }

    /**
     * 刷新音乐播放的小窗UI
     *
     * @param show 是否显示
     */
    private fun refreshMusicView(show: Boolean) {
        if (show) {
            MusicApi.getPlayingMusic(roomId,object : IResultBack<MusicBean>{
                override fun onResult(musicBean: MusicBean?) {
                    if (musicBean != null) {
                        mView!!.refreshMusicView(
                            true,
                            musicBean.nameAndAuthor,
                            musicBean.backgroundUrl
                        )
                    } else {
                        mView!!.refreshMusicView(false, "", "")
                    }
                }

            })
        } else {
            mView!!.refreshMusicView(false, "", "")
        }
    }

    /**
     * 监听自己删除或者添加屏蔽词
     */
    private fun setObShieldListener() {
//        EventBus.get().on(UPDATE_SHIELD, new EventBus.EventCallback() {
//            @Override
//            public void onEvent(String tag, Object... args) {
//                getShield();
//            }
//        });
    }

    /**
     * 监听房间的信息
     */
    private fun setObRoomInfoChange() {
        disposableList.add(
            voiceRoomModel!!.obRoomInfoChange()
                .subscribe({ uiRoomModel ->
                    var extra: String? = ""
                    if (uiRoomModel.rcRoomInfo != null) {
                        extra = uiRoomModel.rcRoomInfo.extra
                        mView!!.setVoiceName(uiRoomModel.rcRoomInfo.roomName)
                    }
                    if (mVoiceRoomBean != null) {
                        notice = if (TextUtils.isEmpty(extra)) String.format(
                            "欢迎来到 %s",
                            mVoiceRoomBean!!.title
                        ) else extra
                        mView!!.setNotice(notice!!)
                    }
                }) { throwable -> Log.e(TAG, "setObRoomInfoChange: $throwable") })
    }

    /**
     * 麦位信息改变监听
     */
    private fun setObSeatInfoChange() {
        disposableList.add(voiceRoomModel!!.obSeatInfoChange().subscribe { uiSeatModel ->
            //根据位置去刷新波纹
            val index = uiSeatModel.index
            if (index == 0) {
                mView!!.refreshRoomOwner(uiSeatModel)
            } else {
                //刷新别的地方的波纹
                mView!!.onSeatListChange(voiceRoomModel.uiSeatModels)
            }
        })
    }

    /**
     * 设置请求上麦监听
     */
    private fun setRequestSeatListener() {
        voiceRoomModel!!.obRequestSeatListChange()
            .subscribe { users -> //申请的，通知底部弹窗的刷新
                if (mView != null) {
                    mView!!.showUnReadRequestNumber(users.size)
                }
            }
    }

    /**
     * 设置接收消息的监听（包括自己发送的，以及外部发送过来的）
     * TODO 多次订阅导致了多次回调，导致消息重复
     */
    private fun setObMessageListener() {
        disposableList.add(RCChatRoomMessageManager.obMessageReceiveByRoomId(mVoiceRoomBean!!.Chatroom_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { messageContent ->
                Log.v(TAG, "===>>>>>>: " + GsonUtil.obj2Json(messageContent.toString()))
                //将消息显示到列表上
                if (VoiceEventHelper.helper().isShowingMessage(messageContent)) {
                    // fix：悬浮框 接收pk邀请
                    if (null != mView) mView!!.showMessage(messageContent, false)
                }
                if (messageContent is RCChatroomGift || messageContent is RCChatroomGiftAll) {
                    if (null != mView) mView!!.showVideoGift()
                    giftCount
                } else if (messageContent is RCChatroomLike) {
                    if (null != mView) mView!!.showLikeAnimation()
                    return@Consumer
                } else if (messageContent is RCAllBroadcastMessage) {
                    AllBroadcastManager.getInstance().addMessage(messageContent)
                } else if (messageContent is RCChatroomSeats) {
                    refreshRoomMember()
                } else if (messageContent is RCChatroomLocationMessage) {
                    VoiceEventHelper.helper().addMessage(messageContent)
                } else if (messageContent is CommandMessage) {
                    val show = !TextUtils.isEmpty(messageContent.data)
                    refreshMusicView(show)
                }
            })
        )
    }

    /**
     * 空座位被点击
     *
     * @param position 指的是麦位 Recyclerview的位置，真是的麦位应该是Position+1
     */
    override fun enterSeatViewer(position: Int) {
        //判断是否在麦位上
        if (voiceRoomModel!!.userInSeat()) {
            //在麦位上
            RCVoiceRoomEngine.getInstance()
                .switchSeatTo(position + 1, object : RCVoiceRoomCallback {
                    override fun onSuccess() {}
                    override fun onError(code: Int, message: String) {
                        mView!!.showToast(message)
                    }
                })
        } else {
            //不在麦位上
            requestSeat(position + 1)
        }
    }

    /**
     * 申请连麦
     *
     * @param position
     */
    fun requestSeat(position: Int) {
        if (currentStatus == STATUS_ON_SEAT) {
            //如果是麦位上
            return
        }
        //如果当前正在等待并且不可以自有上麦的模式
        if (currentStatus == STATUS_WAIT_FOR_SEAT && !voiceRoomModel!!.currentUIRoomInfo.isFreeEnterSeat) {
            mView!!.showRevokeSeatRequest()
            return
        }
        //如果是自由上麦模式
        if (voiceRoomModel!!.currentUIRoomInfo.isFreeEnterSeat) {
            var index = position
            if (index == -1) {
                index = voiceRoomModel.availableIndex
            }
            if (index == -1) {
                mView!!.showToast("当前麦位已满")
                return
            }
            RCVoiceRoomEngine.getInstance().enterSeat(index, object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    mView!!.showToast("上麦成功")
                }

                override fun onError(code: Int, message: String) {
                    mView!!.showToast(message)
                }
            })
        } else {
            RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    currentStatus = STATUS_WAIT_FOR_SEAT
                    mView!!.changeStatus(STATUS_WAIT_FOR_SEAT)
                    mView!!.showToast("已申请连线，等待房主接受")
                }

                override fun onError(code: Int, message: String) {
                    mView!!.showToast("请求连麦失败")
                }
            })
        }
    }

    /**
     * 空座位被点击 房主
     */
    override fun enterSeatOwner(seatModel: UiSeatModel) {
        if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty || seatModel.seatStatus ==
            RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
        ) {
            //如果当前是空座位或者是上锁的座位
            if (emptySeatFragment == null) {
                emptySeatFragment = EmptySeatFragment()
            }
            val seatStatus =
                if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) 1 else 0
            emptySeatFragment!!.setData(seatModel.index, seatStatus, seatModel.isMute, this)
            emptySeatFragment!!.setSeatActionClickListener(this)
            emptySeatFragment!!.show((mView as VoiceRoomFragment).childFragmentManager)
        } else if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            //如果座位正在使用中
        }
    }

    /**
     * 监听麦位改变
     */
    private fun setObSeatListChange() {
        disposableList.add(
            voiceRoomModel!!.obSeatListChange()
                .subscribe({ uiSeatModels ->
                    mView!!.onSeatListChange(uiSeatModels)
                    refreshCurrentStatus(uiSeatModels)
                    giftCount
                }) { throwable -> Log.e(TAG, "setObSeatListChange: $throwable") })
    }

    /**
     * 刷新当前的状态
     *
     * @param uiSeatModels
     */
    @Synchronized
    private fun refreshCurrentStatus(uiSeatModels: List<UiSeatModel>) {
        try {
            var inseat = false
            for (uiSeatModel in uiSeatModels) {
                if (!TextUtils.isEmpty(uiSeatModel.userId) && !TextUtils.isEmpty(
                        UserManager.get()!!
                            .userId
                    ) && uiSeatModel.userId == UserManager.get()!!
                        .userId
                ) {
                    //说明在麦位上
                    inseat = true
                    break
                }
            }
            if (inseat) {
                //当前用户已经在麦位上
                currentStatus = STATUS_ON_SEAT
                mView!!.changeStatus(currentStatus)
            } else {
                //当前用户不在麦位上
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //说明已经申请了上麦,那么等着对方给判断就好，不用做特定的操作
                } else {
                    //当前用户不在麦位上
                    currentStatus = STATUS_NOT_ON_SEAT
                }
                // 房主的话不在麦位上就关掉音乐悬浮窗
                if (TextUtils.equals(
                        createUserId, UserManager.get()!!
                            .userId
                    )
                ) {
                    refreshMusicView(false)
                }
            }
            mView!!.changeStatus(currentStatus)
        } catch (e: Exception) {
            Log.e(TAG, "refreshCurrentStatus: $e")
        }
    }

    /**
     * 监听房间的改变
     */
    private fun setObRoomEventChange() {
        disposableList.add(
            voiceRoomModel!!.obRoomEventChange()
                .subscribe({ stringArrayListPair ->
                    val first = stringArrayListPair.first
                    if (TextUtils.equals(first, Constant.EVENT_ADD_SHIELD)) {
                        val shield = Shield()
                        val name = stringArrayListPair.second?.get(0)
                        shield.name = name
                        VoiceEventHelper.helper().shield.add(shield)
                    } else if (TextUtils.equals(first, Constant.EVENT_DELETE_SHIELD)) {
                        val iterator = VoiceEventHelper.helper().shield.iterator()
                        val shile = stringArrayListPair.second?.get(0)
                        while (iterator.hasNext()) {
                            val x = iterator.next()
                            if (x.name == shile) {
                                iterator.remove()
                            }
                        }
                    } else if (TextUtils.equals(first, Constant.EVENT_REQUEST_SEAT_AGREE)) {
                        //请求麦位被允许
                        currentStatus = STATUS_ON_SEAT
                        //加入麦位
                        voiceRoomModel.enterSeatIfAvailable("")
                        //去更改底部的状态显示按钮
                        mView!!.changeStatus(currentStatus)
                    } else if (TextUtils.equals(first, Constant.EVENT_REQUEST_SEAT_REFUSE)) {
                        //请求麦位被拒绝
                        mView!!.showToast("您的上麦请求被拒绝")
                        currentStatus = STATUS_NOT_ON_SEAT
                        //去更改底部的状态显示按钮
                        mView!!.changeStatus(currentStatus)
                    } else if (TextUtils.equals(first, Constant.EVENT_KICK_OUT_OF_SEAT)) {
                        //被抱下麦
                        mView!!.showToast("您已被抱下麦位")
                    } else if (TextUtils.equals(first, Constant.EVENT_REQUEST_SEAT_CANCEL)) {
                        //撤销麦位申请
                        currentStatus = STATUS_NOT_ON_SEAT
                        mView!!.changeStatus(currentStatus)
                    } else if (TextUtils.equals(first, Constant.EVENT_MANAGER_LIST_CHANGE)) {
                        //管理员列表发生了变化
                        MemberCache.Companion.get().refreshAdminData(getmVoiceRoomBean()!!.Chatroom_id)
                        Log.e(TAG, "accept: " + "EVENT_MANAGER_LIST_CHANGE")
                    } else if (TextUtils.equals(first, Constant.EVENT_KICKED_OUT_OF_ROOM)) {
                        //被踢出了房间
                        val second = stringArrayListPair.second
                        if (second!![1] == UserManager.get()!!.userId) {
                            KToast.show("你已被踢出房间")
                            leaveRoom()
                        }
                    } else if (TextUtils.equals(first, Constant.EVENT_ROOM_CLOSE)) {
                        //当前房间被关闭
                        val confirmDialog =
                            VRCenterDialog((mView as VoiceRoomFragment).requireActivity(),
                                null)
                        confirmDialog.replaceContent(
                            "当前直播已结束",
                            "",
                            null,
                            "确定",
                            { leaveRoom() },
                            null
                        )
                        confirmDialog.setCancelable(false)
                        confirmDialog.show()
                    } else if (TextUtils.equals(first, Constant.EVENT_BACKGROUND_CHANGE)) {
                        mView!!.setRoomBackground(stringArrayListPair.second!![0])
                    } else if (TextUtils.equals(first, Constant.EVENT_AGREE_MANAGE_PICK)) {
                        if (stringArrayListPair.second!![0] == UserManager.get()!!
                                .userId
                        ) {
                            KToast.show("用户连线成功")
                        }
                    } else if (TextUtils.equals(first, Constant.EVENT_REJECT_MANAGE_PICK)) {
                        if (stringArrayListPair.second!![0] == UserManager.get()!!
                                .userId
                        ) {
                            KToast.show("用户拒绝邀请")
                        }
                    }
                }, Consumer { throwable -> Log.e(TAG, "setObRoomEventChange: $throwable") })
        )
    }

    override fun onNetworkStatus(i: Int) {
        mView!!.onNetworkStatus(i)
    }

    /**
     * 麦位上点击自己的头像
     *
     * @param seatModel
     * @return
     */
    fun showNewSelfSettingFragment(seatModel: UiSeatModel?): SelfSettingFragment {
        return SelfSettingFragment(
            seatModel, mVoiceRoomBean!!.Chatroom_id, voiceRoomModel, UserManager.get()
        )
    }

    /**
     * 房间所有者点击自己的头像
     */
    fun onClickRoomOwnerView(fragmentManager: FragmentManager?) {
        if (voiceRoomModel!!.uiSeatModels.size > 0) {
            val uiSeatModel = voiceRoomModel.uiSeatModels[0]
            if (uiSeatModel != null) {
                if (!TextUtils.isEmpty(uiSeatModel.userId) && uiSeatModel.userId == UserManager.get()!!
                        .userId
                ) {
                    //如果在麦位上
                    val creatorSettingFragment = CreatorSettingFragment(
                        voiceRoomModel,
                        uiSeatModel,
                        mVoiceRoomBean!!.userInfo
                    )
                    creatorSettingFragment.show(fragmentManager!!)
                } else {
                    //如果不在麦位上，直接上麦
                    roomOwnerEnterSeat(false)
                }
            }
        }
    }

    /**
     * 发送消息
     *
     * @param messageContent
     */
    fun sendMessage(messageContent: MessageContent?) {
        VoiceEventHelper.helper().sendMessage(messageContent)
    }

    /**
     * 获取屏蔽词
     */
    val shield: Unit
        get() {
            //todo 222
//            OkApi.get(VRApi.getShield(mVoiceRoomBean!!.Chatroom_id),
//                null, object : WrapperCallBack() {
//                override fun onResult(result: Wrapper) {
//                    if (result.ok()) {
//                        val list = result.getList(Shield::class.java)
//                        VoiceEventHelper.helper().shield.clear()
//                        if (list != null) {
//                            VoiceEventHelper.helper().shield.addAll(list)
//                        }
//                    }
//                }
//            })
        }

    /**
     * 设置管理员
     *
     * @param user
     * @param callback
     */
    override fun clickSettingAdmin(user: User?, callback: ClickCallback<Boolean>?) {
        if (mVoiceRoomBean == null) {
            return
        }
        val isAdmin = !MemberCache.Companion.get().isAdmin(user!!.userId)
        //todo 222
//        val params = OkParams()
//            .add("roomId", mVoiceRoomBean!!.Chatroom_id)
//            .add("userId", user.userId)
//            .add("isManage", isAdmin)
//            .build()
//        // 先请求 设置/取消 管理员
//        OkApi.put(VRApi.ADMIN_MANAGE, params, object : WrapperCallBack() {
//            override fun onResult(result: Wrapper) {
//                if (result.ok()) {
//                    val admin = RCChatroomAdmin()
//                    admin.isAdmin = isAdmin
//                    admin.userId = user.userId
//                    admin.userName = user.userName
//                    // 成功后发送管理变更的消息
//                    sendMessage(admin)
//                    callback.onResult(true, "")
//                } else {
//                    KToast.show(result.message)
//                    callback.onResult(true, result.message)
//                }
//            }
//        })
    }

    override fun clickInviteSeat(seatIndex: Int, user: User, callback: ClickCallback<Boolean>) {
        VoiceEventHelper.helper().pickUserToSeat(user.userId, callback)
    }

    override fun acceptRequestSeat(userId: String, callback: ClickCallback<Boolean>) {
        VoiceEventHelper.helper().acceptRequestSeat(userId, callback)
    }

    override fun rejectRequestSeat(userId: String, callback: ClickCallback<Boolean>) {}
    override fun cancelRequestSeat(callback: ClickCallback<Boolean>?) {
        VoiceEventHelper.helper().cancelRequestSeat(object : ClickCallback<Boolean?> {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            override fun onResult(result: Boolean?, msg: String) {
                if (callback == null) return
                callback.onResult(result, msg)
                if (result!!) {
                    KToast.show("已撤回连线申请")
                    voiceRoomModel!!.sendRoomEvent(
                        Pair(
                            Constant.EVENT_REQUEST_SEAT_CANCEL,
                            ArrayList()
                        )
                    )
                } else {
                    //撤销失败，判断是否已经被同意了在麦位上了
                    if (voiceRoomModel!!.userInSeat()) {
                        KToast.show("您已经在麦上了哦")
                    } else {
                        KToast.show(msg)
                    }
                }
            }
        })
    }

    override fun cancelInvitation(userId: String, callback: ClickCallback<Boolean>) {}
    override fun clickKickRoom(user: User?, callback: ClickCallback<Boolean>?) {
        VoiceEventHelper.helper().kickUserFromRoom(user!!, callback!!)
    }

    override fun clickKickSeat(user: User, callback: ClickCallback<Boolean>) {
        VoiceEventHelper.helper().kickUserFromSeat(user, callback)
    }

    override fun clickMuteSeat(seatIndex: Int, isMute: Boolean, callback: ClickCallback<Boolean>) {
        VoiceEventHelper.helper().muteSeat(seatIndex, isMute, callback)
    }

    override fun clickCloseSeat(seatIndex: Int, isLock: Boolean, callback: ClickCallback<Boolean>) {
        VoiceEventHelper.helper().lockSeat(seatIndex, isLock, callback)
    }

    override fun switchToSeat(seatIndex: Int, callback: ClickCallback<Boolean>) {}

    /**
     * 点击底部送礼物，礼物可以送给麦位和房主，无论房主是否在房间
     */
    fun sendGift() {
        val memberArrayList = ArrayList<Member?>()
        //房间内所有人
        val uiSeatModels = voiceRoomModel!!.uiSeatModels
        e(TAG, "uiSeatModels:" + uiSeatModels.size)
        for (uiSeatModel in uiSeatModels) {
            e(TAG, "uiSeatModel:" + GsonUtil.obj2Json(uiSeatModel.member))
            if (uiSeatModel.index == 0) {
                //如果是房主麦位，不用管房主是否存在
                val user = MemberCache.Companion.get().getMember(
                    mVoiceRoomBean!!.uid.toString()
                )
                var member: Member? = null
                if (user == null) {
                    member = Member()
                    member.userName = mVoiceRoomBean!!.userInfo!!.userName
                    member.userId = mVoiceRoomBean!!.userInfo!!.userId
                } else {
                    member = Member.fromUser(user)
                }
                member!!.seatIndex = uiSeatModel.index
                memberArrayList.add(member)
                continue
            }
            //            if (uiSeatModel != null && uiSeatModel.getSeatStatus() ==
            //            RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
//                //当前用户在麦位上
//                Member member = new Member().toMember(mVoiceRoomBean.getCreateUser());
//                member.setSeatIndex(uiSeatModel.getIndex());
//                memberArrayList.add(member);
//            }
            val userId = uiSeatModel.userId
            if (!TextUtils.isEmpty(userId)) {
                val user = MemberCache.Companion.get().getMember(userId)
                val member = Member.fromUser(user)
                member.seatIndex = uiSeatModel.index
                memberArrayList.add(member)
            }
        }
        //按照麦位从小到大排序
        memberArrayList.sortWith(Comparator { o1, o2 -> o1!!.seatIndex - o2!!.seatIndex })
        e(TAG, "getCreateUserId:$createUserId")
        e(TAG, "memberArrayList:" + GsonUtil.obj2Json(memberArrayList))
        //todo 测试
       // mView!!.showSendGiftDialog(mVoiceRoomBean!!, createUserId, memberArrayList!!)
    }

    /**
     * 发送礼物 底部发送礼物的按钮
     *
     * @param user
     */
    override fun clickSendGift(user: User?) {
        val userId = user!!.userId
        val member = Member.fromUser(user)
        if (!TextUtils.isEmpty(userId)) {
            e(TAG, "clickSendGift: userId = $userId")
            val uiSeatModels = voiceRoomModel!!.uiSeatModels
            val count = uiSeatModels.size
            for (i in 0 until count) {
                val m = uiSeatModels[i]
                if (userId == m.userId) {
                    member.seatIndex = i
                }
            }
        }
        mView!!.showSendGiftDialog(mVoiceRoomBean!!, user.userId, listOf(member))
    }

    /**
     * 发送关注消息
     *
     * @param followMsg
     */
    override fun clickFollow(isFollow: Boolean, followMsg: RCFollowMsg?) {
        if (isFollow) {
            sendMessage(followMsg!!)
        }
        mView!!.setTitleFollow(isFollow)
    }

    /**
     * 展示邀请和接受邀请fragment
     *
     * @param index
     */
    override fun showSeatOperationViewPagerFragment(index: Int, seatIndex: Int) {
        val seatOperationViewPagerFragment = SeatOperationViewPagerFragment(
            roomOwnerType, null
        )
        seatOperationViewPagerFragment.setRequestSeats(voiceRoomModel!!.requestSeats)
        seatOperationViewPagerFragment.setInviteSeats(voiceRoomModel.inviteSeats)
        seatOperationViewPagerFragment.setIndex(index)
        seatOperationViewPagerFragment.setObInviteSeatListChangeSuject(voiceRoomModel.obInviteSeatListChange())
        seatOperationViewPagerFragment.setObRequestSeatListChangeSuject(voiceRoomModel.obRequestSeatListChange())
        seatOperationViewPagerFragment.seatActionClickListener = this
        seatOperationViewPagerFragment.show((mView as VoiceRoomFragment).childFragmentManager)
    }

    /**
     * 展示撤销麦位申请
     */
    override fun showRevokeSeatRequestFragment() {
        val revokeSeatRequestFragment = RevokeSeatRequestFragment()
        revokeSeatRequestFragment.setSeatActionClickListener(this)
        revokeSeatRequestFragment.show((mView as VoiceRoomFragment).childFragmentManager)
    }

    /**
     * 弹窗收到上麦邀请弹窗
     *
     * @param isCreate 是否是房主
     * @param userId   邀请人的ID
     */
    fun showPickReceivedDialog(isCreate: Boolean, userId: String?) {
        val pickName = if (isCreate) "房主" else "管理员"
        confirmDialog = VRCenterDialog((mView as VoiceRoomFragment).activity, null)
        confirmDialog!!.replaceContent("您被" + pickName + "邀请上麦，是否同意?",
            "拒绝", {
                confirmDialog!!.dismiss()
                voiceRoomModel!!.refuseInvite(userId)
            }, "同意", {
                //同意
                voiceRoomModel!!.enterSeatIfAvailable(userId)
                confirmDialog!!.dismiss()
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //被邀请上麦了，并且同意了，如果该用户已经申请了上麦，那么主动撤销掉申请
                    cancelRequestSeat(null)
                }
            }, null
        )
        confirmDialog!!.show()
    }

    /**
     * 调用离开房间
     */
    @JvmOverloads
    fun leaveRoom(callback: IRoomCallBack? = null) {
        mView!!.showLoading("")
        VoiceEventHelper.helper().leaveRoom(object : IRoomCallBack {
            override fun onSuccess() {
                d("==============leaveRoom onSuccess")
                mView!!.dismissLoading()
                mView!!.finish()
                callback?.onSuccess()
            }

            override fun onError(code: Int, message: String) {
                e("==============leaveRoom onError")
                mView!!.dismissLoading()
                mView!!.showToast(message)
            }
        })
    }

    /**
     * 房主关闭房间
     */
    fun closeRoom() {
        mView!!.showLoading("正在关闭房间")
        MusicControlManager.getInstance().release()
        //        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_ROOM_CLOSE, "", null);
        VoiceEventHelper.helper().leaveRoom(object : IRoomCallBack {
            override fun onSuccess() {
                deleteRoom()
            }

            override fun onError(code: Int, message: String) {
                mView!!.dismissLoading()
                mView!!.showToast(message)
                deleteRoom()
            }
        })
    }

    private fun deleteRoom() {
        //房主关闭房间，调用删除房间接口
        //todo 222
//        if (mVoiceRoomBean != null) OkApi.get(VRApi.deleteRoom(
//            mVoiceRoomBean!!.Chatroom_id
//        ), null, object : WrapperCallBack() {
//            override fun onResult(result: Wrapper) {
//                if (result.ok()) {
//                    mView!!.finish()
//                    mView!!.dismissLoading()
//                } else {
//                    mView!!.dismissLoading()
//                }
//            }
//
//            override fun onError(code: Int, msg: String) {
//                super.onError(code, msg)
//                mView!!.dismissLoading()
//                mView!!.showToast(msg)
//            }
//        })
    }

    /**
     * 房主上麦
     */
    fun roomOwnerEnterSeat(fromJoinRoom: Boolean) {
//        RCVoiceRoomEngine.getInstance().enterSeat(0, object : RCVoiceRoomCallback {
//            override fun onSuccess() {
//                mView!!.enterSeatSuccess()
//                //                if (!fromJoinRoom) {
//                if (null == voiceRoomModel) return
//                val seatModel = voiceRoomModel.uiSeatModels[0]
//                if (null != seatModel && null != seatModel.extra) {
//                    val disable = seatModel.extra.isDisableRecording
//                    voiceRoomModel.creatorMuteSelf(disable)
//                }
                //                } else {
//                    if (null == voiceRoomModel) return;
//                    // 第一次进房间 房主的disableRecord状态置为false
//                    boolean disable = RCVoiceRoomEngine.getInstance().isDisableAudioRecording();
//                    UiSeatModel seatModel = voiceRoomModel.getUiSeatModels().get(0);
//                    UiSeatModel.UiSeatModelExtra extra = new UiSeatModel.UiSeatModelExtra();
//                    extra.setDisableRecording(disable);
//                    seatModel.setExtra(extra);
//                    voiceRoomModel.creatorMuteSelf(disable);
//                }
    //        }

//            override fun onError(i: Int, message: String) {
//                mView!!.showToast(message)
//            }
//        })
    }

    /**
     * 修改房间公告
     *
     * @param notice
     */
    fun modifyNotice(notice: String?) {
        //判断公告是否有显示
        val currentUIRoomInfo = voiceRoomModel!!.currentUIRoomInfo
        val rcRoomInfo = currentUIRoomInfo.rcRoomInfo
        rcRoomInfo.extra = notice
        RCVoiceRoomEngine.getInstance().setRoomInfo(rcRoomInfo, object : RCVoiceRoomCallback {
            override fun onSuccess() {
                sendNoticeModifyMessage()
                //公告更新成功
                Log.d(TAG, "onSuccess: ")
            }

            override fun onError(i: Int, s: String) {
                //公告更新失败
                Log.e(TAG, "onError: ")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //界面销毁，回收监听
        unInitListener()
    }

    /**
     * 回收掉对房间的各种监听
     */
    fun unInitListener() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
        VoiceEventHelper.helper().removeRCVoiceRoomEventListener(voiceRoomModel)
        //        EventBus.get().off(UPDATE_SHIELD, null);
    }

    /**
     * 发送公告更新的
     */
    private fun sendNoticeModifyMessage() {
        val tips = TextMessage.obtain("房间公告已更新!")
        sendMessage(tips)
    }

    /**
     * 弹出设置弹窗
     */
    fun showSettingDialog() {
        val funList = Arrays.asList(
            MutableLiveData<BaseFun>(RoomLockFun(if (mVoiceRoomBean!!.password_type==1) 1 else 0)),
            MutableLiveData<BaseFun>(RoomNameFun(0)),
            MutableLiveData<BaseFun>(RoomNoticeFun(0)),
            MutableLiveData<BaseFun>(RoomBackgroundFun(0)),
            MutableLiveData<BaseFun>(RoomSeatModeFun(if (voiceRoomModel!!.currentUIRoomInfo.isFreeEnterSeat) 1 else 0)),
            MutableLiveData<BaseFun>(RoomMuteAllFun(if (voiceRoomModel.currentUIRoomInfo.isMuteAll) 1 else 0)),
            MutableLiveData<BaseFun>(RoomLockAllSeatFun(if (voiceRoomModel.currentUIRoomInfo.isLockAll) 1 else 0)),
            MutableLiveData<BaseFun>(RoomMuteFun(if (voiceRoomModel.currentUIRoomInfo.isMute) 1 else 0)),
            MutableLiveData<BaseFun>(RoomSeatSizeFun(if (voiceRoomModel.currentUIRoomInfo.seatCount == 5) 1 else 0)),
            MutableLiveData<BaseFun>(RoomShieldFun(0)),
            MutableLiveData<BaseFun>(RoomMusicFun(0))
        )
        mView!!.showSettingDialog(funList)
    }

    /**
     * 点击设置的
     *
     * @param item
     * @param position
     */
    override fun clickItem(item: MutableLiveData<BaseFun?>?, position: Int) {
        val `fun` = item!!.value
        if (`fun` is RoomNoticeFun) {
            mView!!.showNoticeDialog(true)
        } else if (`fun` is RoomLockFun) {
            if (`fun`.getStatus() == 1) {
                setRoomPassword(false, "", item)
            } else {
                mView!!.showSetPasswordDialog(item)
            }
        } else if (`fun` is RoomNameFun) {
            mView!!.showSetRoomNameDialog(mVoiceRoomBean!!.title)
        } else if (`fun` is RoomBackgroundFun) {
            mView!!.showSelectBackgroundDialog(mVoiceRoomBean!!.background)
        } else if (`fun` is RoomShieldFun) {
            mView!!.showShieldDialog(mVoiceRoomBean!!.Chatroom_id)
        } else if (`fun` is RoomSeatModeFun) {
            if (`fun`.getStatus() == 1) {
                //申请上麦
                setSeatMode(false)
            } else {
                //自由上麦
                setSeatMode(true)
            }
        } else if (`fun` is RoomMuteAllFun) {
            if (`fun`.getStatus() == 1) {
                //解锁全麦
                setAllSeatLock(false)
            } else {
                //全麦锁麦
                setAllSeatLock(true)
            }
        } else if (`fun` is RoomLockAllSeatFun) {
            if (`fun`.getStatus() == 1) {
                //解锁全座
                lockOtherSeats(false)
            } else {
                //全麦锁座
                lockOtherSeats(true)
            }
        } else if (`fun` is RoomMuteFun) {
            if (`fun`.getStatus() == 1) {
                //取消静音
                muteAllRemoteStreams(false)
            } else {
                //静音
                muteAllRemoteStreams(true)
            }
        } else if (`fun` is RoomSeatSizeFun) {
            if (`fun`.getStatus() == 1) {
                //设置8个座位
                setSeatCount(9)
            } else {
                //设置4个座位
                setSeatCount(5)
            }
        } else if (`fun` is RoomMusicFun) {
            //音乐 判断房主是否在麦位上
            val seatInfoByUserId = voiceRoomModel!!.getSeatInfoByUserId(
                UserManager.get()!!.userId
            )
            if (seatInfoByUserId != null && seatInfoByUserId.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                //在座位上，可以播放音乐
                mView!!.showMusicDialog()
            } else {
                mView!!.showToast("请先上麦之后再播放音乐")
            }
        }
    }

    /**
     * 设置房间座位
     *
     * @param seatCount
     */
    private fun setSeatCount(seatCount: Int) {
        voiceRoomModel!!.currentUIRoomInfo.seatCount = seatCount
        RCVoiceRoomEngine.getInstance()
            .setRoomInfo(voiceRoomModel.currentUIRoomInfo.rcRoomInfo, object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    //更换模式成功
                    val rcChatroomSeats = RCChatroomSeats()
                    rcChatroomSeats.count = seatCount - 1
                    RCChatRoomMessageManager.sendChatMessage(
                        getmVoiceRoomBean()!!.Chatroom_id,
                        rcChatroomSeats,
                        true,
                        { integer: Any? -> null }) {
                            coreErrorCode: Any?, integer: Any? -> null }
                }

                override fun onError(i: Int, s: String) {}
            })
    }

    /**
     * 静音 取消静音
     *
     * @param isMute
     */
    private fun muteAllRemoteStreams(isMute: Boolean) {
        RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute)
        voiceRoomModel!!.currentUIRoomInfo.isMute = isMute
        VoiceEventHelper.helper().muteAllRemoteStreams = isMute
        if (isMute) {
            KToast.show("扬声器已静音")
        } else {
            KToast.show("已取消静音")
        }
        //此时要将当前的状态同步到服务器，下次进入的时候可以同步
    }

    /**
     * 全麦锁座
     */
    private fun lockOtherSeats(isLockAll: Boolean) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll, null)
        if (isLockAll) {
            KToast.show("全部座位已锁定")
        } else {
            KToast.show("已解锁全座")
        }
    }

    /**
     * 全麦锁麦
     *
     * @param isMuteAll
     */
    private fun setAllSeatLock(isMuteAll: Boolean) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll, null)
        if (isMuteAll) {
            KToast.show("全部麦位已静音")
        } else {
            KToast.show("已解锁全麦")
        }
    }

    /**
     * 设置上麦的模式
     */
    fun setSeatMode(isFreeEnterSeat: Boolean) {
        voiceRoomModel!!.currentUIRoomInfo.isFreeEnterSeat = isFreeEnterSeat
        RCVoiceRoomEngine.getInstance()
            .setRoomInfo(voiceRoomModel.currentUIRoomInfo.rcRoomInfo, object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    if (isFreeEnterSeat) {
                        KToast.show("当前观众可自由上麦")
                    } else {
                        KToast.show("当前观众上麦要申请")
                    }
                }

                override fun onError(i: Int, s: String) {
                    KToast.show(s)
                }
            })
    }

    /**
     * 设置房间密码
     *
     * @param isPrivate
     * @param password
     * @param item
     */
    fun setRoomPassword(isPrivate: Boolean, password: String?, item: MutableLiveData<BaseFun?>) {
        val p = if (isPrivate) 1 else 0
        //todo 222
//        OkApi.put(VRApi.ROOM_PASSWORD,
//            OkParams()
//                .add("roomId", getmVoiceRoomBean()!!.Chatroom_id)
//                .add("isPrivate", p)
//                .add("password", password).build(),
//            object : WrapperCallBack() {
//                override fun onResult(result: Wrapper) {
//                    if (result.ok()) {
//                        KToast.show(if (isPrivate) "设置成功" else "取消成功")
//                       // mVoiceRoomBean!!.setIsPrivate(if (isPrivate) 1 else 0)
//                        mVoiceRoomBean!!.password = password!!
//                        val `fun` = item.value
//                        `fun`!!.status = p
//                        item.setValue(`fun`)
//                    } else {
//                        KToast.show(if (isPrivate) "设置失败" else "取消失败")
//                    }
//                }
//            })
    }

    /**
     * 修改房间名称
     *
     * @param name
     */
    fun setRoomName(name: String?) {
    //todo 222
//        OkApi.put(VRApi.ROOM_NAME,
//            OkParams()
//                .add("roomId", getmVoiceRoomBean()!!.Chatroom_id)
//                .add("name", name)
//                .build(),
//            object : WrapperCallBack() {
//                override fun onResult(result: Wrapper) {
//                    if (result.ok()) {
//                        KToast.show("修改成功")
//                        mView!!.setVoiceName(name!!)
//                        mVoiceRoomBean!!.title = name
//                        val rcRoomInfo = voiceRoomModel!!.currentUIRoomInfo.rcRoomInfo
//                        rcRoomInfo.roomName = name
//                        RCVoiceRoomEngine.getInstance()
//                            .setRoomInfo(rcRoomInfo, object : RCVoiceRoomCallback {
//                                override fun onSuccess() {
//                                    Log.e(TAG, "onSuccess: ")
//                                }
//
//                                override fun onError(i: Int, s: String) {
//                                    Log.e(TAG, "onError: ")
//                                }
//                            })
//                    } else {
//                        val message = result.message
//                        KToast.show(if (!TextUtils.isEmpty(message)) message else "修改失败")
//                    }
//                }
//
//                override fun onError(code: Int, msg: String) {
//                    super.onError(code, msg)
//                    KToast.show(if (!TextUtils.isEmpty(msg)) msg else "修改失败")
//                }
//            })
    }

    override fun selectBackground(url: String) {
        //todo 222
//        OkApi.put(VRApi.ROOM_BACKGROUND, OkParams()
//            .add("roomId", mVoiceRoomBean!!.Chatroom_id)
//            .add("backgroundUrl", url)
//            .build(), object : WrapperCallBack() {
//            override fun onResult(result: Wrapper) {
//                if (result.ok()) {
//                    mVoiceRoomBean!!.background = url
//                    mView!!.setRoomBackground(url)
//                    //通知外部更改
//                    RCVoiceRoomEngine.getInstance()
//                        .notifyVoiceRoom(Constant.EVENT_BACKGROUND_CHANGE, url, null)
//                    KToast.show("设置成功")
//                } else {
//                    KToast.show("设置失败")
//                }
//            }
//
//            override fun onError(code: Int, msg: String) {
//                super.onError(code, msg)
//                KToast.show("设置失败")
//            }
//        })
    }

    override fun onSendGiftSuccess(messages: List<MessageContent>) {
        if (messages != null && !messages.isEmpty()) {
            for (message in messages) {
                sendMessage(message)
            }
            giftCount
        }
    }

    /**
     * 获取房间内礼物列表 ,刷新列表
     */
    val giftCount: Unit
        get() {
            scopeNet {
                val roomGift = roomGift()
                roomGift!!.gift.size
            }
//            if (mVoiceRoomBean != null && !TextUtils.isEmpty(mVoiceRoomBean!!.Chatroom_id))
//                OkApi.get(VRApi.getGiftList(
//                    mVoiceRoomBean!!.Chatroom_id
//                ),
//                null,
//                object : WrapperCallBack() {
//                    override fun onResult(result: Wrapper) {
//                        if (result.ok()) {
//                            val map = result.map
//                            e("================" + map.toString())
//                            for (userId in map!!.keys) {
//                                val uiSeatModel = voiceRoomModel!!.getSeatInfoByUserId(userId)
//                                val gifCount = map[userId]
//                                if (uiSeatModel != null) uiSeatModel.giftCount = gifCount!!.toInt()
//                            }
//                        }
//                    }
//                })
        }

    /**
     * 根据id获取用户信息
     */
    //todo 根据id获取用户信息
    fun getUserInfo(userId: String) {
//        OkApi.post(
//            VRApi.GET_USER,
//            OkParams().add("userIds", arrayOf(userId)).build(),
//            object : WrapperCallBack() {
//                override fun onResult(result: Wrapper) {
//                    if (result.ok()) {
//                        val members = result.getList(
//                            Member::class.java
//                        )
//                        if (members != null && members.size > 0) {
//                            val uiSeatModel =
//                                voiceRoomModel!!.getSeatInfoByUserId(members[0].userId)
//                            mView!!.showUserSetting(members[0], uiSeatModel!!)
//                        }
//                    }
//                }
//            })
    }

    /**
     * 请求房间用户人数
     */
    fun refreshRoomMember() {
        MemberCache.Companion.get().fetchData(roomId)
    }

    /**
     * 点击全局广播后跳转到相应的房间
     *
     * @param message
     */
    fun jumpRoom(message: RCAllBroadcastMessage?) {
        // 当前房间不跳转
        if (message == null || TextUtils.isEmpty(message.roomId) || TextUtils.equals(
                message.roomId,
                roomId
            )
            || voiceRoomModel!!.getSeatInfoByUserId(UserManager.get()!!.userId) != null
            || TextUtils.equals(
                UserManager.get()!!.userId, mVoiceRoomBean!!.userInfo!!.userId
            )
        ) return
        scopeNet {
            val roomBean = roomDetail(message.roomId.noEN())
            if (roomBean != null) {
                // 房间有密码需要弹框验证密码
                if (roomBean.password_type==1) {
                    inputPasswordDialog = InputPasswordDialog((mView as VoiceRoomFragment)
                        .requireContext(), false,
                        object : InputPasswordDialog.OnClickListener {
                            override fun clickCancel() {}
                            override fun clickConfirm(password: String) {
                                if (TextUtils.isEmpty(password)) {
                                    return
                                }
                                if (password.length < 4) {
                                    mView!!.showToast("请输入 4 位密码")
                                    return
                                }
                                if (TextUtils.equals(password, roomBean.password)) {
                                    inputPasswordDialog!!.dismiss()
                                    exitRoom(roomBean.Chatroom_id)
                                } else {
                                    mView!!.showToast("密码错误")
                                }
                            }
                        })
                    inputPasswordDialog!!.show()
                } else {
                    exitRoom(roomBean.Chatroom_id)
                }
            } else {
            mView!!.dismissLoading()
            //房间不存在了
            mView!!.showToast("房间不存在了")
        }
    }
    }

    private fun exitRoom(roomId: String) {
        // 房间类表包含roomId，则直接切换，否则跳转
        if (RoomListIdsCache.get().contains(roomId)) {
            mView!!.switchOtherRoom(roomId)
        } else {
            leaveRoom(object : IRoomCallBack {
                override fun onSuccess() {
                    IntentWrap.launchRoom((mView as VoiceRoomFragment).requireContext(), roomId)
                }

                override fun onError(code: Int, message: String) {}
            })
        }
    }

    companion object {
        const val STATUS_ON_SEAT = 0
        const val STATUS_NOT_ON_SEAT = 1
        const val STATUS_WAIT_FOR_SEAT = 2
    }

    init {
        voiceRoomModel = VoiceRoomModel(this, lifecycle)
    }
}