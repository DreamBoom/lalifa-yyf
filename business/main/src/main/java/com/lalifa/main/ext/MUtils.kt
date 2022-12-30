package com.lalifa.main.ext

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.ext.Config
import com.lalifa.ext.showInputDialog
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.AdActivity
import com.lalifa.main.activity.room.RankActivity
import com.lalifa.main.activity.room.RoomActivity
import com.lalifa.main.activity.room.ext.*
import com.lalifa.main.activity.room.message.*
import com.lalifa.main.activity.room.widght.GiftDialog
import com.lalifa.main.activity.room.widght.InputBar
import com.lalifa.main.activity.room.widght.InputBarDialog
import com.lalifa.main.api.*
import com.lalifa.main.ext.MUtils.Companion.callTalk
import com.lalifa.main.ext.MUtils.Companion.clickSend
import com.lalifa.main.ext.MUtils.Companion.offset
import com.lalifa.main.fragment.RoomListFragment.Companion.closeRoomByService
import com.lalifa.main.fragment.adapter.roomRankAdapter
import com.lalifa.utils.SPUtil
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.MessageContent
import java.net.URL

class MUtils {
    companion object {
        //语音房用户进入展示动画
        var inUserIdList = arrayListOf<String>()
        fun AppCompatActivity.showUserCar(
            roomId: String,
            userId: String?,
            view: SVGAImageView
        ) {
            if (!TextUtils.isEmpty(userId)) {
                inUserIdList.add(userId!!)
            }
            if (inUserIdList.size > 0) {
                scopeNetLife {
                    val userInfo = userInfo(inUserIdList[0], roomId)
                    Member.setMember(userInfo)
                    //展示进场动画
                    if (!TextUtils.isEmpty(userInfo!!.car)) {
                        loadSvg(view, userInfo.car) {
                            inUserIdList.removeAt(0)
                            showUserCar(roomId, "", view)
                        }
                    } else {
                        inUserIdList.removeAt(0)
                        showUserCar(roomId, "", view)
                    }
                }
            }
        }

        //语音房消费排行布局覆盖设置
        fun RecyclerView.offset(list: List<RoomRankBean>) {
            this.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    if (parent.getChildPosition(view) !== list.size - 1) {
                        outRect.right = -10
                    }
                }
            })
        }

        //语音房礼物弹窗展示
        fun AppCompatActivity.showGiftPop(
            roomId: String, isPrivate: Boolean,
            listener: GiftDialog.OnSendGiftListener
        ) {
            scopeNetLife {
                val roomGift = roomGift()
                if (null != roomGift) {
                    val dialog = GiftDialog.newInstance(
                        this@showGiftPop,
                        roomId,
                        isPrivate,
                        roomGift,
                        Member.getSeats(),
                        listener
                    )
                    dialog.show(supportFragmentManager, "dialog")
                } else {
                    toast("获取礼物失败")
                }
            }
        }

        //展示队列弹框
        fun AppCompatActivity.showRoomList(
            room: RoomDetailBean,
            callback: (roomId: String, owner: Boolean) ->
            Unit = { s: String, b: Boolean -> }
        ) {
            roomListDialog(room.fleet) { s: String, b: Boolean ->
                if (s == room.Chatroom_id) {
                    toast("您已在当前房间")
                    return@roomListDialog
                }
                imLeave {
                    finish()
                    callback(s, b)
                }
            }
        }

        //加载svg动画
        fun loadSvg(view: SVGAImageView, path: String, callback: () -> Unit) {
            val showGift = SPUtil.getBoolean(Tool.showGift, true)
            if (!showGift || TextUtils.isEmpty(path) || !path.contains("svg")) return
            Config.parser.decodeFromURL(
                URL(Config.FILE_PATH + path),
                object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val drawable = SVGADrawable(videoItem)
                        view.setImageDrawable(drawable)
                        view.startAnimation()
                        callback()
                    }

                    override fun onError() {
                    }
                })
        }

        /**
         * 踢出房间
         * @param userId 要踢出的人的 id
         */
        private fun kickUser(userId: String) {
            RCVoiceRoomEngine.getInstance().kickUserFromRoom(userId, object : RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * 抱下麦
         */
        private fun kickSeat(userId: String) {
            RCVoiceRoomEngine.getInstance().kickUserFromSeat(userId, object : RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * 静麦，注意：
         * 1、可以静麦自己也可以静麦其他人
         * 2、muteSeat 和 disableAudioRecording 都是操作麦克风状态，后者只能操作自己的麦克风状态，而且修改麦克风状态，该状态不会同步给房间内的其他人
         *
         * @param seatIndex 麦位序号
         * @param isMute    是否静音
         * @param callback  结果回调
         */
        private fun muteSeat(seatIndex: Int, isMute: Boolean) {
            RCVoiceRoomEngine.getInstance().muteSeat(seatIndex, isMute, object :
                RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * 锁麦
         */
        fun AppCompatActivity.setSeatLock(imVoice: ImageView) {
            val seatInfo = QuickEventListener.getSeatInfo(Member.currentId)
            if (null == seatInfo) {
                toast("未在麦位上，不可进行锁麦")
                return
            }
            val member = Member.getMember(seatInfo.userId)
            if (!seatInfo.isMute) {
                seatInfo.userId
                RCVoiceRoomEngine.getInstance().muteSeat(member!!.seatIndex, true) {
                    imVoice.background = ContextCompat.getDrawable(
                        this, R.drawable.ic_mic_switch_off
                    )
                    KToast.show("麦位已静音")
                }
            } else {
                RCVoiceRoomEngine.getInstance().muteSeat(member!!.seatIndex, false) {
                    imVoice.background = ContextCompat.getDrawable(
                        this, R.drawable.ic_mic_switch_on
                    )
                    KToast.show("麦位已解锁")
                }
            }
        }

        /**
         * 静音 取消静音
         */
        fun AppCompatActivity.muteRemote(isMute: Boolean, sy: ImageView) {
            RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute)
            if (isMute) {
                sy.background = ContextCompat.getDrawable(
                    this, R.drawable.ic_room_setting_mute
                )
                KToast.show("扬声器已静音")
            } else {
                sy.background = ContextCompat.getDrawable(
                    this, R.drawable.ic_room_setting_unmute
                )
                KToast.show("已取消静音")
            }
            //此时要将当前的状态同步到服务器，下次进入的时候可以同步
        }


        //申请上麦
        private fun AppCompatActivity.requestSeat(callback: () -> Unit) {
            RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    callback()
                    toast("已申请连线，等待接受")
                }

                override fun onError(code: Int, message: String) {
                    toast("请求连麦失败")
                }
            })
        }

        // 取消排麦请求
        private fun AppCompatActivity.cancelRequestSeat(callback: () -> Unit) {
            RCVoiceRoomEngine.getInstance().cancelRequestSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    callback()
                    toast("已取消排麦")
                }

                override fun onError(code: Int, message: String) {
                    toast("取消排麦失败")
                }
            })
        }

        private fun AppCompatActivity.requestOrCancel(
            currentStatus: Int,
            position: Int,
            callback: (currentStatus: Int) -> Unit
        ) {
            //申请上麦or取消申请
            requestSeatDialog(currentStatus) {
                if (currentStatus == Tool.STATUS_NOT_ON_SEAT) {
                    if (position != 1) {
                        Tool.currentSeatIndex = position + 2
                    } else {
                        Tool.currentSeatIndex = position
                    }
                    requestSeat {
                        callback(Tool.STATUS_WAIT_FOR_SEAT)
                    }
                } else if (currentStatus == Tool.STATUS_WAIT_FOR_SEAT) {
                    Tool.currentSeatIndex = -1
                    cancelRequestSeat {
                        callback(Tool.STATUS_NOT_ON_SEAT)
                    }
                }
            }
        }

        //麦位点击
        fun AppCompatActivity.seat(
            position: Int,
            isBoss: Boolean,
            models: Seat,
            room: RoomDetailBean,
            currentStatus: Int,
            listener: GiftDialog.OnSendGiftListener,
            callback: (currentStatus: Int) -> Unit
        ) {
            if (models.status != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                && models.userId == Member.currentId
            ) {
                //展示本人
                roomMyDialog(UserManager.get()!!) {
                    VoiceRoomApi.getApi().leaveSeat(false, null)
                    callback(Tool.STATUS_NOT_ON_SEAT)
                    Tool.currentSeatIndex = -1
                }
                return
            }
            if (models.status != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
                scopeNetLife {
                    val userInfo = userInfo(models.userId, room.Chatroom_id)
                    if (null != userInfo) {
                        roomUserDialog(userInfo, models.isMute) {
                            when (it) {
                                1 -> {
                                    //@ta
                                    clickSend(room.Chatroom_id, "@${userInfo.userName}")
                                }
                                2 -> {
                                    //发消息
                                    callTalk(userInfo.userId)
                                }
                                3 -> {
                                    //送礼物
                                    showGiftPop(
                                        room.Chatroom_id,
                                        room.password_type == 1,
                                        listener
                                    )
                                }
                                4 -> {
                                    //抱下麦
                                    kickSeat(userInfo.userId!!)
                                }
                                5 -> {
                                    //闭麦
                                    if (isBoss) {
                                        muteSeat(position, !models.isMute)
                                    } else {
                                        muteSeat(2 + position, !models.isMute)
                                    }
                                }
                                6 -> {
                                    //禁言

                                }
                                7 -> {
                                    //踢出房间
                                    kickUser(userInfo.userId)
                                }
                            }
                        }
                    } else {
                        toast("获取用户信息失败")
                    }
                }
            } else {
                if (position == 0 && currentStatus == Tool.STATUS_NOT_ON_SEAT) {
                    if (room.wheat_type == 1) {
                        requestSeatDialog(currentStatus) {
                            //零号直接上麦
                            callback(Tool.STATUS_HAVE_SEAT)
                            Tool.currentSeatIndex = 0
                            VoiceRoomApi.getApi().enterSeat(0, false, null)
                        }
                    } else {
                        toast("暂无权限")
                    }
                } else {
                    if (currentStatus == Tool.STATUS_HAVE_SEAT) {
                        toast("当前已排麦，不可重复申请")
                        return
                    }
                    //申请上麦or取消申请
                    requestOrCancel(currentStatus, position) {
                        callback(it)
                    }
                }
            }
        }

        //融云会话列表清空
        fun imClear(callback: () -> Unit) {
            RongIMClient.getInstance().syncConversationReadStatus(
                Conversation.ConversationType.PRIVATE, "", 0,
                object : RongIMClient.OperationCallback() {
                    override fun onSuccess() {
                        callback()
                    }

                    override fun onError(errorCode: RongIMClient.ErrorCode?) {

                    }
                }
            )
        }

        //融云回话列表消息全部已读
        fun imRead(callback: () -> Unit) {
            RongIMClient.getInstance().clearConversations(
                object : RongIMClient.ResultCallback<Boolean>() {
                    override fun onSuccess(t: Boolean?) {
                        callback()
                    }

                    override fun onError(e: RongIMClient.ErrorCode?) {

                    }
                }, Conversation.ConversationType.PRIVATE
            )
        }

        //离开房间
        fun imLeave(callback: () -> Unit) {
            VoiceRoomApi.getApi().leaveRoom { result ->
                NotificationService.unbindNotifyService()
                if (result) {
                    callback()
                    KToast.show("离开房间成功")
                } else {
                    KToast.show("离开房间失败")
                }
            }
        }

        fun closeRoom() {
            //关闭
//            VoiceRoomApi.getApi().leaveRoom { result ->
//                if (result) {
//                    closeRoomByService(this@RoomActivity, roomId!!)
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                } else {
//                    KToast.show("离开房间失败")
//                }
//            }
        }

        //加入房间
        fun join(roomId: String, callback: (result: Boolean) -> Unit) {
            if (TextUtils.isEmpty(roomId)) {
                KToast.show("房间ID为空")
                callback(false)
            }
            VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
                if (result) {
                    callback(true)
                } else {
                    KToast.show("加入房间失败")
                    callback(false)
                }
            }
        }

        /**
         * 被踢出房间回调
         * @param targetId 被踢用户的标识
         */
        fun AppCompatActivity.meOut(targetId: String?) {
            if (targetId == Member.currentId) {
                VoiceRoomApi.getApi().leaveRoom { result ->
                    NotificationService.unbindNotifyService()
                    if (result) {
                        KToast.show("您被踢出了房间")
                       finish()
                    }
                }
            }
        }

        //语音房座位变动
        fun AppCompatActivity.onSeat(
            roomId: String, seats: List<Seat>,
            seatBoss: BindingAdapter,
            seat: BindingAdapter,
            callback: (currentStatus: Int) -> Unit
        ) {
            Member.getSeats().clear()
            scopeNetLife {
                val apply = getMembers(roomId)
                seats.forEachIndexed { index, seat ->
                    if (!TextUtils.isEmpty(seat.userId)) {
                        val find = apply!!.find { action -> seat.userId == action.userId }
                        if (null != find) {
                            if (find.userId == Member.currentId) {
                                callback(Tool.STATUS_HAVE_SEAT)
                                Tool.currentSeatIndex = index
                            }
                            val member = find.toMember()
                            member.seatIndex = index
                            member.status = seat.status
                            member.isMute = seat.isMute
                            Member.setMember(member)
                            Member.setSeat(member)
                        }
                    }
                }.apply {
                    val count = seats.size ?: 0
                    //老板麦位
                    val bossSeats = seats.subList(0, 2)
                    seatBoss.models = bossSeats
                    //观众麦位
                    seat.models = seats.subList(2, count)
                }
            }
        }

        fun AppCompatActivity.roomMore(room:RoomDetailBean){
            roomTopDialog(room.collection_type == 1) {
                when (it) {
                    1 -> {
                        //举报
                        showInputDialog("请输入举报内容", l = 500) {
                            scopeNetLife {
                                val reportRoom = reportRoom(room.id.toString(), it)
                                if (null == reportRoom) {
                                    toast("举报异常")
                                } else {
                                    toast("举报成功")
                                }
                            }
                        }
                    }
                    2 -> {
                        //收藏
                        scopeNetLife {
                            val collection = collection(room.id.toString())
                            if (null != collection) {
                                if (room.collection_type == 1) {
                                    toast("取消收藏成功")
                                    room.collection_type = 0
                                } else {
                                    toast("收藏成功")
                                    room.collection_type = 1
                                }
                            } else {
                                if (room.collection_type == 1) {
                                    toast("取消收藏失败")
                                } else {
                                    toast("收藏失败")
                                }
                            }
                        }

                    }
                    3 -> {
                        //分享
                    }
                    4 -> {
                        //离开
                        imLeave { finish() }
                    }
                }
            }
        }
        private fun AppCompatActivity.getRank(roomId: String, list: RecyclerView) {
            //获取消费排行
            scopeNetLife {
                var roomRanking = roomRanking("1", roomId)
                if (null != roomRanking && roomRanking.isNotEmpty()) {
                    if (roomRanking.size > 3) {
                        roomRanking = roomRanking.subList(0, 3)
                    }
                    list.apply {
                        offset(roomRanking)
                        roomRankAdapter().apply {
                            R.id.header.onClick {
                                start(RankActivity::class.java) {
                                    putExtra("roomId", roomId)
                                }
                            }
                        }.models = roomRanking
                    }
                }
            }

        }

        //接收房间消息回调
        fun AppCompatActivity.receiveMsg(
            roomId: String, v: SVGAImageView, list: RecyclerView,
            callback: (messageContent: MessageContent) -> Unit
        ) {
            RCChatRoomMessageManager.obMessageReceiveByRoomId(roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { messageContent ->
                    callback(messageContent)
                    if (messageContent is RCChatroomGiftAll) {
                        //展示礼物动画
                        loadSvg(v, messageContent.giftPath) {}
                        getRank(roomId, list)
                    }
                }
        }

        //进入房间发送默认消息
        fun sendSystemMessage(room: RoomDetailBean) {
            // 默认消息
            val welcome = RCChatroomLocationMessage()
            welcome.content = java.lang.String.format("欢迎来到 %s", room.title)
            RCChatRoomMessageManager.sendLocationMessage(room.Chatroom_id, welcome)
//        val tips = RCChatroomLocationMessage()
//        tips.content = "感谢使用随心语音 语音房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。"
//        RCChatRoomMessageManager.sendLocationMessage(mRoomDetail!!.Chatroom_id, tips)
            // 广播消息
            val enter = RCChatroomEnter()
            enter.userId = UserManager.get()!!.userId
            enter.userName = UserManager.get()!!.userName
            sendMessage(room.Chatroom_id, enter)

        }

        //语音房发送文字消息
        private var inputBarDialog: InputBarDialog? = null
        fun Context.clickSend(info: String, roomId: String) {
            inputBarDialog = InputBarDialog(this, info,
                object : InputBar.InputBarListener {
                    override fun onClickSend(message: String?) {
                        if (TextUtils.isEmpty(message)) {
                            com.lalifa.utils.KToast.show("消息不能为空")
                            return
                        }
                        //发送文字消息
                        val barrage = RCChatroomBarrage()
                        barrage.content = message!!
                        barrage.userId = UserManager.get()!!.userId
                        barrage.userName = UserManager.get()!!.userName
                        sendMessage(roomId, barrage)
                    }

                    override fun onClickEmoji(): Boolean {
                        return false
                    }
                })
            inputBarDialog!!.show()
        }

        //语音房消息发送
        fun sendMessage(roomId: String, messages: MessageContent?) {
            RCChatRoomMessageManager.sendChatMessage(
                roomId, messages, true, { null }, null
            )
        }
        //发送广播
        fun AppCompatActivity.sendBroadcast(room:RoomDetailBean){
            showInputDialog(l = 200) {
                val message = RCAllBroadcastMessage()
                message.userId = UserManager.get()!!.userId
                message.userName = UserManager.get()!!.userName
                message.roomId = room.Chatroom_id
                message.roomType = "0"
                message.info = it
                message.isPrivate = room.password_type.toString()
                AllBroadcastManager.getInstance().addMessage(message)
            }
        }

        //发起会话
        private fun Context.callTalk(userId: String) {
            //发消息
            RouteUtils.routeToConversationActivity(
                this,
                Conversation.ConversationType.PRIVATE,
                userId,
                false
            )
        }

        //设置banner
        fun AppCompatActivity.setBanner(ad: List<Advertisement>, banner: Banner<*, *>) {
            val adList = arrayListOf<String>()
            ad.forEach {
                adList.add(Config.FILE_PATH + it.image)
            }
            banner.addBannerLifecycleObserver(this)
                .setAdapter(object : BannerImageAdapter<String>(adList.toList()) {
                    override fun onBindView(
                        holder: BannerImageHolder?,
                        data: String?,
                        position: Int,
                        size: Int
                    ) {
                        holder?.imageView?.apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            data?.let { it1 -> load(it1) }
                            onClick {
                                start(AdActivity::class.java) {
                                    putExtra("ad", ad[position].details)
                                }
                            }
                        }
                    }
                })
                .indicator = CircleIndicator(this)
        }
    }
}