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
        //?????????????????????????????????
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
                    //??????????????????
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

        //???????????????????????????????????????
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

        //???????????????????????????
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
                    toast("??????????????????")
                }
            }
        }

        //??????????????????
        fun AppCompatActivity.showRoomList(
            room: RoomDetailBean,
            callback: (roomId: String, owner: Boolean) ->
            Unit = { s: String, b: Boolean -> }
        ) {
            roomListDialog(room.fleet) { s: String, b: Boolean ->
                if (s == room.Chatroom_id) {
                    toast("?????????????????????")
                    return@roomListDialog
                }
                imLeave {
                    finish()
                    callback(s, b)
                }
            }
        }

        //??????svg??????
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
         * ????????????
         * @param userId ?????????????????? id
         */
        private fun kickUser(userId: String) {
            RCVoiceRoomEngine.getInstance().kickUserFromRoom(userId, object : RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * ?????????
         */
        private fun kickSeat(userId: String) {
            RCVoiceRoomEngine.getInstance().kickUserFromSeat(userId, object : RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * ??????????????????
         * 1?????????????????????????????????????????????
         * 2???muteSeat ??? disableAudioRecording ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         *
         * @param seatIndex ????????????
         * @param isMute    ????????????
         * @param callback  ????????????
         */
        private fun muteSeat(seatIndex: Int, isMute: Boolean) {
            RCVoiceRoomEngine.getInstance().muteSeat(seatIndex, isMute, object :
                RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {}
            })
        }

        /**
         * ??????
         */
        fun AppCompatActivity.setSeatLock(imVoice: ImageView) {
            val seatInfo = QuickEventListener.getSeatInfo(Member.currentId)
            if (null == seatInfo) {
                toast("????????????????????????????????????")
                return
            }
            val member = Member.getMember(seatInfo.userId)
            if (!seatInfo.isMute) {
                seatInfo.userId
                RCVoiceRoomEngine.getInstance().muteSeat(member!!.seatIndex, true) {
                    imVoice.background = ContextCompat.getDrawable(
                        this, R.drawable.ic_mic_switch_off
                    )
                    KToast.show("???????????????")
                }
            } else {
                RCVoiceRoomEngine.getInstance().muteSeat(member!!.seatIndex, false) {
                    imVoice.background = ContextCompat.getDrawable(
                        this, R.drawable.ic_mic_switch_on
                    )
                    KToast.show("???????????????")
                }
            }
        }

        /**
         * ?????? ????????????
         */
        fun AppCompatActivity.muteRemote(isMute: Boolean, sy: ImageView) {
            RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute)
            if (isMute) {
                sy.background = ContextCompat.getDrawable(
                    this, R.drawable.ic_room_setting_mute
                )
                KToast.show("??????????????????")
            } else {
                sy.background = ContextCompat.getDrawable(
                    this, R.drawable.ic_room_setting_unmute
                )
                KToast.show("???????????????")
            }
            //?????????????????????????????????????????????????????????????????????????????????
        }


        //????????????
        private fun AppCompatActivity.requestSeat(callback: () -> Unit) {
            RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    callback()
                    toast("??????????????????????????????")
                }

                override fun onError(code: Int, message: String) {
                    toast("??????????????????")
                }
            })
        }

        // ??????????????????
        private fun AppCompatActivity.cancelRequestSeat(callback: () -> Unit) {
            RCVoiceRoomEngine.getInstance().cancelRequestSeat(object : RCVoiceRoomCallback {
                override fun onSuccess() {
                    callback()
                    toast("???????????????")
                }

                override fun onError(code: Int, message: String) {
                    toast("??????????????????")
                }
            })
        }

        private fun AppCompatActivity.requestOrCancel(
            currentStatus: Int,
            position: Int,
            callback: (currentStatus: Int) -> Unit
        ) {
            //????????????or????????????
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

        //????????????
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
                //????????????
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
                                    //?????????
                                    callTalk(userInfo.userId)
                                }
                                3 -> {
                                    //?????????
                                    showGiftPop(
                                        room.Chatroom_id,
                                        room.password_type == 1,
                                        listener
                                    )
                                }
                                4 -> {
                                    //?????????
                                    kickSeat(userInfo.userId!!)
                                }
                                5 -> {
                                    //??????
                                    if (isBoss) {
                                        muteSeat(position, !models.isMute)
                                    } else {
                                        muteSeat(2 + position, !models.isMute)
                                    }
                                }
                                6 -> {
                                    //??????

                                }
                                7 -> {
                                    //????????????
                                    kickUser(userInfo.userId)
                                }
                            }
                        }
                    } else {
                        toast("????????????????????????")
                    }
                }
            } else {
                if (position == 0 && currentStatus == Tool.STATUS_NOT_ON_SEAT) {
                    if (room.wheat_type == 1) {
                        requestSeatDialog(currentStatus) {
                            //??????????????????
                            callback(Tool.STATUS_HAVE_SEAT)
                            Tool.currentSeatIndex = 0
                            VoiceRoomApi.getApi().enterSeat(0, false, null)
                        }
                    } else {
                        toast("????????????")
                    }
                } else {
                    if (currentStatus == Tool.STATUS_HAVE_SEAT) {
                        toast("????????????????????????????????????")
                        return
                    }
                    //????????????or????????????
                    requestOrCancel(currentStatus, position) {
                        callback(it)
                    }
                }
            }
        }

        //????????????????????????
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

        //????????????????????????????????????
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

        //????????????
        fun imLeave(callback: () -> Unit) {
            VoiceRoomApi.getApi().leaveRoom { result ->
                NotificationService.unbindNotifyService()
                if (result) {
                    callback()
                    KToast.show("??????????????????")
                } else {
                    KToast.show("??????????????????")
                }
            }
        }

        fun closeRoom() {
            //??????
//            VoiceRoomApi.getApi().leaveRoom { result ->
//                if (result) {
//                    closeRoomByService(this@RoomActivity, roomId!!)
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                } else {
//                    KToast.show("??????????????????")
//                }
//            }
        }

        //????????????
        fun join(roomId: String, callback: (result: Boolean) -> Unit) {
            if (TextUtils.isEmpty(roomId)) {
                KToast.show("??????ID??????")
                callback(false)
            }
            VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
                if (result) {
                    callback(true)
                } else {
                    KToast.show("??????????????????")
                    callback(false)
                }
            }
        }

        /**
         * ?????????????????????
         * @param targetId ?????????????????????
         */
        fun AppCompatActivity.meOut(targetId: String?) {
            if (targetId == Member.currentId) {
                VoiceRoomApi.getApi().leaveRoom { result ->
                    NotificationService.unbindNotifyService()
                    if (result) {
                        KToast.show("?????????????????????")
                       finish()
                    }
                }
            }
        }

        //?????????????????????
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
                    //????????????
                    val bossSeats = seats.subList(0, 2)
                    seatBoss.models = bossSeats
                    //????????????
                    seat.models = seats.subList(2, count)
                }
            }
        }

        fun AppCompatActivity.roomMore(room:RoomDetailBean){
            roomTopDialog(room.collection_type == 1) {
                when (it) {
                    1 -> {
                        //??????
                        showInputDialog("?????????????????????", l = 500) {
                            scopeNetLife {
                                val reportRoom = reportRoom(room.id.toString(), it)
                                if (null == reportRoom) {
                                    toast("????????????")
                                } else {
                                    toast("????????????")
                                }
                            }
                        }
                    }
                    2 -> {
                        //??????
                        scopeNetLife {
                            val collection = collection(room.id.toString())
                            if (null != collection) {
                                if (room.collection_type == 1) {
                                    toast("??????????????????")
                                    room.collection_type = 0
                                } else {
                                    toast("????????????")
                                    room.collection_type = 1
                                }
                            } else {
                                if (room.collection_type == 1) {
                                    toast("??????????????????")
                                } else {
                                    toast("????????????")
                                }
                            }
                        }

                    }
                    3 -> {
                        //??????
                    }
                    4 -> {
                        //??????
                        imLeave { finish() }
                    }
                }
            }
        }
        private fun AppCompatActivity.getRank(roomId: String, list: RecyclerView) {
            //??????????????????
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

        //????????????????????????
        fun AppCompatActivity.receiveMsg(
            roomId: String, v: SVGAImageView, list: RecyclerView,
            callback: (messageContent: MessageContent) -> Unit
        ) {
            RCChatRoomMessageManager.obMessageReceiveByRoomId(roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { messageContent ->
                    callback(messageContent)
                    if (messageContent is RCChatroomGiftAll) {
                        //??????????????????
                        loadSvg(v, messageContent.giftPath) {}
                        getRank(roomId, list)
                    }
                }
        }

        //??????????????????????????????
        fun sendSystemMessage(room: RoomDetailBean) {
            // ????????????
            val welcome = RCChatroomLocationMessage()
            welcome.content = java.lang.String.format("???????????? %s", room.title)
            RCChatRoomMessageManager.sendLocationMessage(room.Chatroom_id, welcome)
//        val tips = RCChatroomLocationMessage()
//        tips.content = "???????????????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????"
//        RCChatRoomMessageManager.sendLocationMessage(mRoomDetail!!.Chatroom_id, tips)
            // ????????????
            val enter = RCChatroomEnter()
            enter.userId = UserManager.get()!!.userId
            enter.userName = UserManager.get()!!.userName
            sendMessage(room.Chatroom_id, enter)

        }

        //???????????????????????????
        private var inputBarDialog: InputBarDialog? = null
        fun Context.clickSend(info: String, roomId: String) {
            inputBarDialog = InputBarDialog(this, info,
                object : InputBar.InputBarListener {
                    override fun onClickSend(message: String?) {
                        if (TextUtils.isEmpty(message)) {
                            com.lalifa.utils.KToast.show("??????????????????")
                            return
                        }
                        //??????????????????
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

        //?????????????????????
        fun sendMessage(roomId: String, messages: MessageContent?) {
            RCChatRoomMessageManager.sendChatMessage(
                roomId, messages, true, { null }, null
            )
        }
        //????????????
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

        //????????????
        private fun Context.callTalk(userId: String) {
            //?????????
            RouteUtils.routeToConversationActivity(
                this,
                Conversation.ConversationType.PRIVATE,
                userId,
                false
            )
        }

        //??????banner
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