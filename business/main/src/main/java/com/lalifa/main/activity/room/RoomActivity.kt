package com.lalifa.main.activity.room

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.brv.BindingAdapter
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.*
import com.lalifa.main.activity.room.ext.QuickEventListener.*
import com.lalifa.main.activity.room.widght.CreateRoomDialog
import com.lalifa.main.activity.room.widght.InputBar
import com.lalifa.main.activity.room.widght.InputBarDialog
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityRoomBinding
import com.lalifa.main.ext.*
import com.lalifa.main.fragment.adapter.seatAdapter
import com.lalifa.main.fragment.adapter.seatBossAdapter
import com.lalifa.yyf.ext.showTipDialog
import io.rong.imkit.IMCenter
import io.rong.imkit.MessageInterceptor
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import io.rong.message.TextMessage


/**
 * 演示房间api和麦位api的activity
 * 1.设置房间事件监听
 * 2.创建或者加入房间
 * 3.房主上麦 seatIndex = 0
 */
class RoomActivity : BaseActivity<ActivityRoomBinding>(), SeatListObserver,
    RoomInforObserver, CreateRoomDialog.CreateRoomCallBack, MessageInterceptor {
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
    override fun initView() {
        roomId = intent.getStringExtra(KET_ROOM_ID)
        owner = intent.getBooleanExtra(KET_ROOM_OWNER, false)
        init()
        //设置监听
        get().observeSeatList(this)
        get().observeRoomInfo(this)
        get().register(this, owner)
        IMCenter.getInstance().setMessageInterceptor(this)
        joinRoom()
    }

    private var seatBoss: BindingAdapter? = null
    private var seat: BindingAdapter? = null
    private fun init() {
        seatBoss = binding.seatBossList.seatBossAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(true, layoutPosition)
            }
        }
        seat = binding.rlSeat.seatAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(false,layoutPosition)
            }
        }
    }

    //加入房间API
    private fun joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("房间ID不能为空")
            finish()
            return
        }
        VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
            if (result) {
                getRoomInfo()
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
                    RoomName.text = roomDetail.title
                    tvRoomId.text = roomDetail.id.toString()
                    roomHeader.load(Config.FILE_PATH + roomDetail.image)
                    ivBackground.load(Config.FILE_PATH + roomDetail.background)
                    //进入直接上零号麦
//                    if (roomDetail.wheat_type== 1) {
//                        val models = seatBoss!!.getModel<Seat>(0)
//                        if(models.status== RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty){
//                            VoiceRoomApi.getApi().enterSeat(0, false, null)
//                        }
//                    }
                }
            }
        }
    }

    override fun onRoomInfo(roomInfo: RCVoiceRoomInfo) {

    }

    //点击麦位逻辑
    private fun clickSeat(isBoss: Boolean, position: Int) {
        //主播老板位
        if (isBoss) {
            val models = seatBoss!!.getModel<Seat>(position)
            if(models.status!=RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                &&models.userId == AccountManager.getCurrentId()){
                //展示本人
                roomMyDialog(UserManager.get()!!){
                    VoiceRoomApi.getApi().leaveSeat(false, null)
                }
                return
            }
            if (models.status!=RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                &&mRoomDetail!!.manage_type==1) {
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
                if(position==0){
                    if(mRoomDetail!!.wheat_type==1){
                        requestSeatDialog(Tool.STATUS_NOT_ON_SEAT){
                            //零号直接上麦
                            VoiceRoomApi.getApi().enterSeat(0, true, null)
                        }
                    }else{
                        toast("暂无权限")
                    }
                }else{
                    //申请上麦
                    requestSeatDialog(Tool.STATUS_NOT_ON_SEAT){
                        requestSeat()
                    }
                }
            }
        } else {
            //观众位
            val models = seat!!.getModel<Seat>(position)
            if(models.status!=RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                &&models.userId == AccountManager.getCurrentId()){
                //展示本人
                roomMyDialog(UserManager.get()!!){
                    requestSeatDialog(Tool.STATUS_HAVE_SEAT){
                        VoiceRoomApi.getApi().leaveSeat(false,null)
                    }
                }
                return
            }
            if (models.status!=RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                &&mRoomDetail!!.manage_type==1) {
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
                //申请上麦
                requestSeatDialog(Tool.STATUS_NOT_ON_SEAT){
                    requestSeat()
                }
            }
        }
    }

    private var currentStatus: Int = Tool.STATUS_NOT_ON_SEAT
    private fun requestSeat(){
        RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
            override fun onSuccess() {
                currentStatus = Tool.STATUS_WAIT_FOR_SEAT
                toast("已申请连线，等待房主接受")
            }

            override fun onError(code: Int, message: String) {
                toast("请求连麦失败")
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
        }
    }

    private var mCreateRoomDialog: CreateRoomDialog? = null

    private fun showCreateRoomDialog(roomId: String) {
        mCreateRoomDialog = CreateRoomDialog(
            roomId,
            this,
            this
        )
        mCreateRoomDialog!!.show()
    }

    //刷新房间观众
    var isLoad = false
    private fun refreshMembers() {
        if (!isLoad) {
            isLoad = true
            scopeNetLife {
                getMembers(roomId!!.noEN())?.apply {
                    forEach {
                        AccountManager.setAccount(it.toAccount(), false)
                    }
                    isLoad = false
                }
            }
        }
    }

    override fun onOnLineCount(userNum: Int) {
        refreshMembers()
    }

    var oldSeatInfos: List<Seat>? = null
    override fun onSeatList(seatInfos: List<Seat>) {
        oldSeatInfos = seatInfos
        val count = seatInfos.size ?: 0
        if (count > 0) {
            //老板麦位
            val bossSeats = seatInfos.subList(0, 2)
            seatBoss!!.models = bossSeats
            //观众麦位
            val seats = seatInfos.subList(2, count)
            seat!!.models = seats
        }
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
//                            val barrage = RCChatroomBarrage()
//                            barrage.content = message!!
//                            barrage.userId = UserManager.get()!!.userId
//                            barrage.userName = UserManager.get()!!.userName
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
    private fun setPop() {
        roomBottomDialog() {
            when (it) {
                1 -> {
                    //礼物动画开启
                }
                2 -> {
                    //清空消息

                }
                3 -> {
                    //房间管理
                    if (null != mRoomDetail) {
                        startCode(RoomSettingActivity::class.java) {
                            putExtra("room", mRoomDetail!!)
                        }
                    } else {
                        toast("获取房间信息，请稍后重试")
                    }
                }
                4 -> {
                    //显示随心值

                }
                5 -> {
                    //管理员
                    start(ManageListActivity::class.java) {
                        putExtra("roomId", roomId)
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
        oldSeatInfos = null
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

    //用户进入房间
    override fun onUserIn(userId: String?) {
//        scopeNetLife {
//            val userInfo = userInfo(userId!!.noEN(), roomId!!)
//            // 默认消息
//            val welcome = RCChatroomLocationMessage()
//            welcome.content = java.lang.String.format(
//                "欢迎${userInfo!!.userName}来到 %s",
//                mRoomDetail!!.title
//            )
//            sendMessage(welcome)
//        }
    }

    //消息发送
    private fun sendMessage(content: String) {
        val targetId = roomId
        val conversationType = Conversation.ConversationType.CHATROOM
        val messageContent = TextMessage.obtain(content)
        val message = Message.obtain(targetId, conversationType, messageContent)
        RongIMClient.getInstance()
            .sendMessage(message, null, null, object :
                IRongCallback.ISendMessageCallback {
                override fun onAttached(message: Message) {
                    LogCat.e("===onAttached=$message")
                }

                override fun onSuccess(message: Message) {
                    LogCat.e("===onSuccess=$message")
                }

                override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                    LogCat.e("===onError=$message===$errorCode")
                }
            })
    }

    //创建房间成功返回
    override fun onCreateSuccess(roomId: String?) {

    }

    //创建房间退出
    override fun onCreateExist(roomId: String?) {

    }

    override fun interceptReceivedMessage(
        message: Message?,
        left: Int,
        hasPackage: Boolean,
        offline: Boolean
    ): Boolean {
        LogCat.e("====000===$message")
        return true
    }

    override fun interceptOnSendMessage(message: Message?): Boolean {
        LogCat.e("====111SendMessage===$message")
        return true
    }

    override fun interceptOnSentMessage(message: Message?): Boolean {
        LogCat.e("====222SentMessage===$message")
        return true
    }

    override fun interceptOnInsertOutgoingMessage(
        type: Conversation.ConversationType?,
        targetId: String?,
        sentStatus: Message.SentStatus?,
        content: MessageContent?,
        sentTime: Long
    ): Boolean {
        LogCat.e("====333===$content")
        return true
    }

    override fun interceptOnInsertIncomingMessage(
        type: Conversation.ConversationType?,
        targetId: String?,
        senderId: String?,
        receivedStatus: Message.ReceivedStatus?,
        content: MessageContent?,
        sentTime: Long
    ): Boolean {
        LogCat.e("====444=$senderId=$targetId=$content")
        return true
    }
}