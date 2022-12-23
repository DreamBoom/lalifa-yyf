package com.lalifa.main.activity.room

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.adapter.RoomMessageAdapter
import com.lalifa.main.activity.room.ext.*
import com.lalifa.main.activity.room.ext.QuickEventListener.*
import com.lalifa.main.activity.room.message.RCChatRoomMessageManager
import com.lalifa.main.activity.room.message.RCChatroomBarrage
import com.lalifa.main.activity.room.message.RCChatroomEnter
import com.lalifa.main.activity.room.message.RCChatroomLocationMessage
import com.lalifa.main.activity.room.widght.*
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityRoomBinding
import com.lalifa.main.ext.*
import com.lalifa.main.fragment.adapter.roomRankAdapter
import com.lalifa.main.fragment.adapter.seatAdapter
import com.lalifa.main.fragment.adapter.seatBossAdapter
import com.lalifa.utils.SPUtil
import com.lalifa.utils.UiUtils
import com.lalifa.yyf.ext.showInputDialog
import com.lalifa.yyf.ext.showTipDialog
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent


/**
 * 演示房间api和麦位api的activity
 * 1.设置房间事件监听
 * 2.创建或者加入房间
 * 3.房主上麦 seatIndex = 0
 */
class RoomActivity : BaseActivity<ActivityRoomBinding>(), SeatListObserver,
    RoomInfoObserver, CreateRoomDialog.CreateRoomCallBack,
    RoomMessageAdapter.OnClickMessageUserListener {
    companion object {
        const val ACTION_ROOM = 1000
        private const val KET_ROOM_ID = "room_id"
        private const val KET_ROOM_OWNER = "room_owner"
        fun joinVoiceRoom(
            activity: Activity,
            roomId: String?,
            isRoomOwner: Boolean
        ) {
            val i = Intent(activity, RoomActivity::class.java)
            i.putExtra(KET_ROOM_ID, roomId)
            i.putExtra(KET_ROOM_OWNER, isRoomOwner)
            activity.startActivityForResult(i, ACTION_ROOM)
        }
    }

    override fun getViewBinding() = ActivityRoomBinding.inflate(layoutInflater)

    private var mRoomDetail: RoomDetailBean? = null
    private var roomId: String? = null
    private var owner = false
    private var msgList: ArrayList<MessageContent> = arrayListOf()
    var mRoomMessageAdapter: RoomMessageAdapter? = null
    override fun initView() {
        loadTag!!.show()
        roomId = intent.getStringExtra(KET_ROOM_ID)
        owner = intent.getBooleanExtra(KET_ROOM_OWNER, false)
        //设置监听
        get().observeSeatList(this)
        get().observeRoomInfo(this)
        get().register(this, owner)
        val linearLayoutManager = LinearLayoutManager(this)
        mRoomMessageAdapter = RoomMessageAdapter(
            this,
            binding.rlMessage, this
        )
        // 设置消息列表数据
        binding.rlMessage.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(
                DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.dp2px(5f))
            )
            adapter = mRoomMessageAdapter
        }
        joinRoom()
        //接收消息
//        RongCoreClient.addOnReceiveMessageListener(object : OnReceiveMessageWrapperListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onReceivedMessage(message: Message?, profile: ReceivedProfile?) {
//
//            }
//        })
    }

    private var seatBoss: BindingAdapter? = null
    private var seat: BindingAdapter? = null
    override fun onReady() {
        seatBoss = binding.seatBossList.seatBossAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(true, layoutPosition)
            }
        }
        seat = binding.rlSeat.seatAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(false, layoutPosition)
            }
        }
        binding.rankList.onClick {
            start(RankActivity::class.java) {
                putExtra("roomId", roomId!!)
            }
        }
    }

    override fun onMessage(message: Message?) {
        RCChatRoomMessageManager.onReceiveMessage(roomId, message!!.content)
    }

    //加入房间API
    private fun joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("房间ID不能为空")
            loadTag!!.dismiss()
            finish()
            return
        }
        VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
            if (result) {
                getRoomInfo()
            } else {
                loadTag!!.dismiss()
            }
        }
    }

    //获取房间详情
    private fun getRoomInfo() {
        binding.apply {
            scopeNetLife {
                val roomDetail = roomDetail(roomId!!.noEN())
                if (null != roomDetail) {
                    mRoomDetail = roomDetail
                    mRoomMessageAdapter!!.setRoomCreateId(mRoomDetail!!.userInfo!!.userId)
                    RoomName.text = roomDetail.title
                    tvRoomId.text = roomDetail.id.toString()
                    roomHeader.load(Config.FILE_PATH + roomDetail.image)
                    ivBackground.load(Config.FILE_PATH + roomDetail.background)
                    loadTag!!.dismiss()
                    val ad = roomDetail.advertisement
                    val adList = arrayListOf<String>()
                    ad.forEach {
                        adList.add(Config.FILE_PATH + it.image)
                    }
                    banner.addBannerLifecycleObserver(this@RoomActivity)
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
                        .indicator = CircleIndicator(this@RoomActivity)
                    sendSystemMessage()
                    getRoomRanking()
                } else {
                    toast("房间详情加载异常")
                    loadTag!!.dismiss()
                    finish()
                }
            }
        }
    }

    //获取消费排行
    private fun getRoomRanking() {
        scopeNetLife {
            var roomRanking = roomRanking("1", roomId!!)
            if (null != roomRanking && roomRanking.isNotEmpty()) {
                if (roomRanking.size > 3) {
                    roomRanking = roomRanking.subList(0, 3)
                }
                binding.rankList.apply {
                    this.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            if (parent.getChildPosition(view) !== roomRanking.size - 1) {
                                outRect.bottom = -10
                            }
                        }
                    })
                    roomRankAdapter().models = roomRanking
                }
            }
        }
    }

    var inUserIdList = arrayListOf<String>()

    //用户进入房间
    override fun onUserIn(userId: String?) {
        inUserIdList.add(userId!!)
        if (!isShowIn) {
            showUserIn()
        }
    }

    private var isShowIn = false
    private fun showUserIn() {
        if (inUserIdList.size > 0) {
            isShowIn = true
            scopeNetLife {
                val userInfo = userInfo(inUserIdList[0], roomId!!)
                //展示进场动画
                val showIn = SPUtil.getBoolean(roomId!!, true)
                if (showIn && !TextUtils.isEmpty(userInfo!!.car)) {
                    MUtils.loadSvg(binding.svgIn, userInfo.car) {
                        inUserIdList.removeAt(0)
                        showUserIn()
                    }
                } else {
                    inUserIdList.removeAt(0)
                    showUserIn()
                }
            }
        } else {
            isShowIn = false
        }
    }

    //点击麦位逻辑
    private fun clickSeat(isBoss: Boolean, position: Int) {
        val models = if (isBoss) {
            seatBoss!!.getModel<Seat>(position)
        } else {
            seat!!.getModel<Seat>(position)
        }
        if (models.status != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
            && models.userId == AccountManager.currentId
        ) {
            //展示本人
            roomMyDialog(UserManager.get()!!) {
                VoiceRoomApi.getApi().leaveSeat(false, null)
                currentStatus = Tool.STATUS_NOT_ON_SEAT
                Tool.currentSeatIndex = -1
            }
            return
        }
        if (models.status != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
            && mRoomDetail!!.manage_type == 1
        ) {
            scopeNetLife {
                val userInfo = userInfo(models.userId.noEN(), roomId!!.noEN())
                if (null != userInfo) {
                    roomUserDialog(userInfo) {
                        when (it) {
                            1 -> {
                                //@ta
                            }
                            2 -> {
                                //发消息
                            }
                            3 -> {
                                //送礼物
                            }
                            4 -> {
                                //抱下麦
                            }
                            5 -> {
                                //闭麦
                            }
                            6 -> {
                                //禁言
                            }
                            7 -> {
                                //踢出房间
                            }
                        }
                    }
                } else {
                    toast("获取用户信息失败")
                }
            }
        } else {
            if (position == 0 && currentStatus == Tool.STATUS_NOT_ON_SEAT) {
                if (mRoomDetail!!.wheat_type == 1) {
                    requestSeatDialog(currentStatus) {
                        //零号直接上麦
                        currentStatus = Tool.STATUS_HAVE_SEAT
                        Tool.currentSeatIndex = 0
                        VoiceRoomApi.getApi().enterSeat(0, true, null)
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
                requestSeatDialog(currentStatus) {
                    if (currentStatus == Tool.STATUS_NOT_ON_SEAT) {
                        Tool.currentSeatIndex = position
                        requestSeat()
                    } else if (currentStatus == Tool.STATUS_WAIT_FOR_SEAT) {
                        Tool.currentSeatIndex = -1
                        cancelRequestSeat()
                    }
                }
            }
        }
    }

    //申请上麦
    private var currentStatus: Int = Tool.STATUS_NOT_ON_SEAT
    private fun requestSeat() {
        RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
            override fun onSuccess() {
                currentStatus = Tool.STATUS_WAIT_FOR_SEAT
                toast("已申请连线，等待接受")
            }

            override fun onError(code: Int, message: String) {
                toast("请求连麦失败")
            }
        })
    }

    // 取消排麦请求
    private fun cancelRequestSeat() {
        RCVoiceRoomEngine.getInstance().cancelRequestSeat(object : RCVoiceRoomCallback {
            override fun onSuccess() {
                currentStatus = Tool.STATUS_NOT_ON_SEAT
                toast("已取消排麦")
            }

            override fun onError(code: Int, message: String) {
                toast("取消排麦失败")
            }
        })
    }


    override fun onClick() {
        super.onClick()
        binding.apply {
            sendMessage.onClick { clickSend() }
            more.onClick { more() }
            setting.onClick { setPop() }
            create.onClick { showCreateRoomDialog(roomId!!) }
            train.onClick {
                roomListDialog(mRoomDetail!!.fleet) { s: String, b: Boolean ->
                    if (s == roomId) {
                        toast("您已在当前房间")
                        return@roomListDialog
                    }
                    VoiceRoomApi.getApi().leaveRoom { result ->
                        NotificationService.unbindNotifyService()
                        if (result) {
                            KToast.show("离开房间成功")
                            finish()
                            RoomActivity.joinVoiceRoom(this@RoomActivity, s, b)
                        } else {
                            KToast.show("离开房间失败")
                        }
                    }

                }
            }
            gift.onClick {
                loadTag!!.show()
                scopeNetLife {
                    val roomGift = roomGift()
                    if (null != roomGift) {
                        loadTag!!.dismiss()
                        val dialog = GiftDialog.newInstance(
                            this@RoomActivity, roomId!!, roomGift,
                            AccountManager.getSeats()
                        )
                        dialog.show(supportFragmentManager, "dialog")
                    } else {
                        loadTag!!.dismiss()
                        toast("获取礼物失败")
                    }
                }
            }
        }
    }

    //创建房间弹框
    private var mCreateRoomDialog: CreateRoomDialog? = null
    private fun showCreateRoomDialog(roomId: String) {
        mCreateRoomDialog = CreateRoomDialog(
            roomId,
            this,
            this
        )
        mCreateRoomDialog!!.show()
    }


    override fun onOnLineCount(userNum: Int) {

    }

    //麦位变化，刷新房间观众
    var isLoad = false
    override fun onSeatList(seatInfos: List<Seat>) {
        if (!isLoad) {
            isLoad = true
            scopeNetLife {
                getMembers(roomId!!.noEN())?.apply {
                    forEach { member ->
                        AccountManager.setAccount(member.toAccount(), false)
                    }.apply {
                        val count = seatInfos.size ?: 0
                        if (count > 0) {
                            //老板麦位
                            val bossSeats = seatInfos.subList(0, 2)
                            seatBoss!!.models = bossSeats
                            //观众麦位
                            val seats = seatInfos.subList(2, count)
                            seat!!.models = seats
                        }
                        //设置本人麦位状态
                        AccountManager.removeSeats()
                        for (i in seatInfos.indices) {
                            if (seatInfos[i].userId == AccountManager.currentId) {
                                currentStatus = Tool.STATUS_HAVE_SEAT
                                Tool.currentSeatIndex = i
                            }
                            //更新麦位用户信息
                            val account = AccountManager.getAccount(seatInfos[i].userId)
                            if (null != account) {
                                AccountManager.setSeat(account)
                            }
                        }
                        isLoad = false
                    }
                }
            }
        }
    }

    /**
     * 申请麦位回调
     * @param seatIndex -1 被拒绝 else 已同意
     * */
    override fun requestSeatAccepted(seatIndex: Int) {
        if (seatIndex == -1) {
            currentStatus = Tool.STATUS_NOT_ON_SEAT
            Tool.currentSeatIndex = -1
        } else {
            currentStatus = Tool.STATUS_HAVE_SEAT
        }
    }

    override fun seatSpeak(index: Int, audioLevel: Int) {
        //  LogCat.e("=====$index 号麦说话状态：$audioLevel")
    }

    //关闭
//    VoiceRoomApi.getApi().leaveRoom { result ->
//        if (result) {
//            closeRoomByService(this@RoomActivity, roomId!!)
//            setResult(Activity.RESULT_OK)
//            finish()
//        } else {
//            KToast.show("离开房间失败")
//        }
//    }


    //发送消息
    private var inputBarDialog: InputBarDialog? = null
    private fun clickSend() {
        inputBarDialog = InputBarDialog(this@RoomActivity,
            object : InputBar.InputBarListener {
                override fun onClickSend(message: String?) {
                    if (TextUtils.isEmpty(message)) {
                        com.lalifa.utils.KToast.show("消息不能为空")
                        return
                    }
                    //发送文字消息
                    sendMessage(message!!)
                }

                override fun onClickEmoji(): Boolean {
                    return false
                }
            })
        inputBarDialog!!.show()
    }

    //点击更多
    private fun more() {
        roomTopDialog(mRoomDetail!!.collection_type == 1) {
            when (it) {
                1 -> {
                    //举报
                    showInputDialog("请输入举报内容", l = 500) {
                        scopeNetLife {
                            val reportRoom = reportRoom(roomId!!.noEN(), it)
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
                        val collection = collection(mRoomDetail!!.id.toString())
                        if (null != collection) {
                            if (mRoomDetail!!.collection_type == 1) {
                                toast("取消收藏成功")
                                mRoomDetail!!.collection_type = 0
                            } else {
                                toast("收藏成功")
                                mRoomDetail!!.collection_type = 1
                            }
                        } else {
                            if (mRoomDetail!!.collection_type == 1) {
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
                    VoiceRoomApi.getApi().leaveRoom { result ->
                        NotificationService.unbindNotifyService()
                        if (result) {
                            KToast.show("离开房间成功")
                            finish()
                        } else {
                            KToast.show("离开房间失败")
                        }
                    }
                }
            }
        }
    }

    //点击设置
    @SuppressLint("NotifyDataSetChanged")
    private fun setPop() {
        roomBottomDialog() {
            when (it) {
                2 -> {
                    //清空消息

                }
                3 -> {
                    //房间管理
                    startCode(RoomSettingActivity::class.java) {
                        putExtra("room", mRoomDetail!!)
                    }
                }
                4 -> {
                    //显示随心值
                    seatBoss!!.notifyDataSetChanged()
                    seat!!.notifyDataSetChanged()
                }
                5 -> {
                    //管理员
                    if (mRoomDetail!!.office_type != 1) {
                        toast("暂无权限查看")
                    } else {
                        start(ManageListActivity::class.java) {
                            putExtra("roomId", roomId)
                        }
                    }
                }
                6 -> {
                    //礼物列表
                }
                7 -> {
                    //发布广播
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == 200 && data!!.getBooleanExtra("isChange", false))
            getRoomInfo()
    }

    //房间销毁
    override fun onDestroy() {
        //在销毁之前提前出栈顶
        try {
            if (null != mRoomMessageAdapter) mRoomMessageAdapter!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (inputBarDialog != null && inputBarDialog!!.isShowing) {
            inputBarDialog!!.dismiss()
        }
        VoiceRoomApi.getApi().leaveRoom(null)
        super.onDestroy()
    }

    //点击返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            showTipDialog("确定离开当前房间？") {
                VoiceRoomApi.getApi().leaveRoom { result ->
                    NotificationService.unbindNotifyService()
                    if (result) {
                        KToast.show("离开房间成功")
                        finish()
                    } else {
                        KToast.show("离开房间失败")
                    }
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //消息发送
    private fun sendMessage(content: String) {
        //发送文字消息
        val barrage = RCChatroomBarrage()
        barrage.content = content
        barrage.userId = UserManager.get()!!.userId
        barrage.userName = UserManager.get()!!.userName
        RCChatRoomMessageManager.sendChatMessage(roomId, barrage, true,
            {
               // addMessage(messageContent)
                null
            },null)
//        val targetId = roomId
//        val conversationType = Conversation.ConversationType.CHATROOM
//        val message = Message.obtain(targetId, conversationType, content)
//        RongIMClient.getInstance()
//            .sendMessage(message, null, null, object :
//                IRongCallback.ISendMessageCallback {
//                override fun onAttached(message: Message) {
//                    LogCat.e("===onAttached=$message")
//                }
//
//                @SuppressLint("NotifyDataSetChanged")
//                override fun onSuccess(message: Message) {
//                    //发送文字消息
//
//                }
//
//                override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
//                    LogCat.e("===onError=$message===$errorCode")
//                }
//            })
    }

    /**
     * 进入房间后发送默认的消息
     */
    private fun sendSystemMessage() {
        //消息接收
        RCChatRoomMessageManager.obMessageReceiveByRoomId(mRoomDetail!!.Chatroom_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { messageContent ->
                Log.v(TAG, messageContent.toString())
                //将消息显示到列表上
                mRoomMessageAdapter!!.interMessage(messageContent)

//                if (messageContent is RCChatroomGift || messageContent is RCChatroomGiftAll) {
//                    if (null != mView) mView.showVideoGift()
//                    getGiftCount()
//                } else if (messageContent is RCAllBroadcastMessage) {
//                    AllBroadcastManager.getInstance()
//                        .addMessage(messageContent as RCAllBroadcastMessage)
//                }else if (messageContent is RCChatroomLocationMessage) {
//                    VoiceEventHelper.helper().addMessage(messageContent)
//                }
            })
        // 默认消息
        val list: MutableList<MessageContent> = java.util.ArrayList(1)
        mRoomMessageAdapter!!.setMessages(list, true)
        val welcome = RCChatroomLocationMessage()
        welcome.content = java.lang.String.format("欢迎来到 %s", mRoomDetail!!.title)
        RCChatRoomMessageManager.sendLocationMessage(mRoomDetail!!.Chatroom_id, welcome)
        val tips = RCChatroomLocationMessage()
        tips.content = "感谢使用随心语音 语音房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。"
        RCChatRoomMessageManager.sendLocationMessage(mRoomDetail!!.Chatroom_id, tips)
        // 广播消息
        val enter = RCChatroomEnter()
        enter.userId = UserManager.get()!!.userId
        enter.userName = UserManager.get()!!.userName
        RCChatRoomMessageManager.sendChatMessage(mRoomDetail!!.Chatroom_id, enter, false,
            { null }) { _, _ -> null }
    }

    //创建房间成功返回
    override fun onCreateSuccess(roomId: String?) {

    }

    //创建房间退出
    override fun onCreateExist(roomId: String?) {

    }

    //点击消息
    override fun clickMessageUser(userId: String?) {

    }
}