package com.lalifa.main.activity.room

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import com.drake.brv.BindingAdapter
import com.drake.logcat.LogCat
import com.drake.logcat.LogCat.e
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.AccountManager
import com.lalifa.main.activity.room.ext.NotificationService
import com.lalifa.main.activity.room.ext.QuickEventListener.*
import com.lalifa.main.activity.room.ext.Seat
import com.lalifa.main.activity.room.ext.VoiceRoomApi
import com.lalifa.main.activity.room.message.RCChatroomBarrage
import com.lalifa.main.activity.room.widght.CreateRoomDialog
import com.lalifa.main.activity.room.widght.InputBar
import com.lalifa.main.activity.room.widght.InputBarDialog
import com.lalifa.main.api.RoomDetailBean
import com.lalifa.main.api.collection
import com.lalifa.main.api.getMembers
import com.lalifa.main.api.roomDetail
import com.lalifa.main.databinding.ActivityRoomBinding
import com.lalifa.main.ext.roomBottomDialog
import com.lalifa.main.ext.roomTopDialog
import com.lalifa.main.fragment.adapter.seatAdapter
import com.lalifa.main.fragment.adapter.seatBossAdapter
import com.lalifa.widget.dialog.dialog.VRCenterDialog
import com.lalifa.yyf.ext.showTipDialog
import io.rong.imlib.*
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
    RoomInforObserver, MessageObserver, CreateRoomDialog.CreateRoomCallBack {
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
        get().observeMessage(this)
        get().register(this, owner)
        joinRoom()
    }

    private var seatBoss: BindingAdapter? = null
    private var seat: BindingAdapter? = null
    private fun init() {
        seatBoss = binding.seatBoss.seatBossAdapter().apply {
            R.id.ivPortrait.onClick {
                toast("选择上麦观众")
            }
        }
        seat = binding.rlSeat.seatAdapter().apply {
            R.id.ivPortrait.onClick {
                toast("选择上麦观众")
            }
        }
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

    //加入房间API
    private fun joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("房间ID不能为空")
            finish()
            return
        }
        VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
            if (result) {
                if (owner) {
                    //主播进来直接加入麦位，不需要主动更新麦位
                    VoiceRoomApi.getApi().enterSeat(0, true, null)
                }
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
                }
            }
        }
    }

    override fun onRoomInfo(roomInfo: RCVoiceRoomInfo) {

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
                    if(null!=mRoomDetail){
                        startCode(RoomSettingActivity::class.java) {
                            putExtra("room", mRoomDetail!!)
                        }
                    }else{
                        toast("获取房间信息，请稍后重试")
                    }
                }
                4 -> {
                    //显示随心值

                }
                5 -> {
                    //管理员
                    start(ManageListActivity::class.java){
                        putExtra("roomId",roomId)
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
            .sendMessage(message, null, null, object : IRongCallback.ISendMessageCallback {
                override fun onAttached(message: Message) {}
                override fun onSuccess(message: Message) {}
                override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {}
            })


//        RongCoreClient.getInstance().sendMessage(
//            Conversation.ConversationType.CHATROOM, roomId, content,
//            null,
//            null,
//            object : IRongCoreCallback.ISendMessageCallback {
//                override fun onAttached(message: Message) {
//                    LogCat.e("===onAttached=$message")
//                }
//                override fun onSuccess(message: Message) {
//                    LogCat.e("===onSuccess=$message")
//                }
//                override fun onError(message: Message, coreErrorCode:
//                IRongCoreEnum.CoreErrorCode) {
//                    LogCat.e("===onError=$message===$coreErrorCode")
//                }
//            })
    }

    override fun onMessage(message: Message?) {
        e("====888888" + message!!.toString())
        if (message!!.conversationType == Conversation.ConversationType.CHATROOM) {
            LogCat.e("=====222==$message")
        }
    }

    //创建房间成功返回
    override fun onCreateSuccess(roomId: String?) {

    }
    //创建房间退出
    override fun onCreateExist(roomId: String?) {

    }
}