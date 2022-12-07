package com.lalifa.main.activity.room

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.utils.VMLog
import com.bcq.adapter.recycle.RcyHolder
import com.bcq.adapter.recycle.RcySAdapter
import com.drake.net.utils.scopeNetLife
import com.kit.UIKit
import com.kit.utils.KToast
import com.kit.utils.Logger
import com.kit.wapper.IResultBack
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.extension.noEN
import com.lalifa.extension.onClick
import com.lalifa.extension.toast
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.*
import com.lalifa.main.activity.room.ext.QuickEventListener.*
import com.lalifa.main.activity.room.widght.InputBar
import com.lalifa.main.activity.room.widght.InputBarDialog
import com.lalifa.main.api.getMembers
import com.lalifa.main.databinding.ActivityRoomBinding
import com.lalifa.main.activity.room.ext.ApiFunDialogHelper.Companion.helper
import com.lalifa.main.activity.room.ext.ApiFunDialogHelper.OnApiClickListener
import com.lalifa.main.api.RoomDetailBean
import com.lalifa.main.api.collection
import com.lalifa.main.api.roomDetail
import com.lalifa.main.ext.roomTopDialog
import io.rong.imlib.IRongCoreCallback
import io.rong.imlib.IRongCoreEnum.CoreErrorCode
import io.rong.imlib.chatroom.base.RongChatRoomClient
import io.rong.imlib.model.ChatRoomInfo

/**
 * 演示房间api和麦位api的activity
 * 1.设置房间事件监听
 * 2.创建或者加入房间
 * 3.房主上麦 seatIndex = 0
 */
class RoomActivity : BaseActivity<ActivityRoomBinding>(),
    OnApiClickListener, SeatListObserver,
    RoomInforObserver, DialogInterface.OnDismissListener {
    private var rl_seat: RecyclerView? = null
    protected var createrBinder: SeatHandleBinder? = null
    protected var bossBinder: SeatHandleBinder? = null
    protected var adapter: RcySAdapter<Seat, RcyHolder>? = null
    override fun initView() {
        roomId = intent.getStringExtra(KET_ROOM_ID)
        owner = intent.getBooleanExtra(KET_ROOM_OWNER, false)
        enter = intent.getBooleanExtra(KET_ENTER, false)
        // 麦位列表
        initSeats()
        //设置麦位列表监听
        get().observeSeatList(this)
        get().observeRoomInfo(this)
        get().register(this, owner)
        joinRoom()
        refreshMembers()
        // 加入房间后
        // RCKTVManager.getInstance().startListener(roomId, null);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        if (owner) {
            // 模拟房主 操作房价相关的api
            menu.add(ACTION_API).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == ACTION_API) {
            helper().showRoomApiDialog(this, this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private var roomId: String? = null
    private var owner = false
    private var enter = false
    fun joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("房间ID不能为空")
            finish()
            return
        }
        VoiceRoomApi.getApi().joinRoom(roomId) { result: Boolean ->
            Log.e(TAG, "result = $result")
            if (result) { // 加入房间成功 后跟新在线麦位数
                onOnLineCount()
                if (enter) {
                    VoiceRoomApi.getApi().enterSeat(0, false, null)
                }
                NotificationService.bindNotifyService(this@RoomActivity, ACTION_NOTIFY)
            }
        }
    }

    var mRoomDetail: RoomDetailBean? = null
    override fun onRoomInfo(roomInfo: RCVoiceRoomInfo) {
        binding.apply {
            scopeNetLife {
                val roomDetail = roomDetail(roomId!!.noEN())
                if (null != roomDetail) {
                    mRoomDetail = roomDetail
                    RoomName.text = roomDetail.title
                    tvRoomId.text = roomDetail.id.toString()
                    roomHeader.load(Config.FILE_PATH + roomDetail.image)
                }
            }
        }
    }

    private fun refreshMembers() {
        scopeNetLife {
            val members = getMembers(roomId!!)
            if (null != members) {
                // 跟新的信息已经保存到AccountManager中
                //将自己排除
                members.forEach {
                    //将自己排除
                    if (!it.userId.equals(AccountManager.getCurrentId())) {
                        AccountManager.setAccount(it.toAccount(), false)
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onOnLineCount() {
        refreshMembers()
        RongChatRoomClient.getInstance().getChatRoomInfo(roomId,
            0,
            ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC,
            object : IRongCoreCallback.ResultCallback<ChatRoomInfo?>() {
                override fun onSuccess(chatRoomInfo: ChatRoomInfo?) {
                    //f
                    val onlineCount = chatRoomInfo?.totalMemberCount ?: 0
                }

                override fun onError(errorCode: CoreErrorCode) {
                    VMLog.e(TAG, "getOnLineUserCount#onError$errorCode")
                }
            })
    }

    var onSeatUserId: String? = null
    override fun onSeatList(seatInfos: List<Seat>) {
        val count = seatInfos?.size ?: 0
        if (count > 0) {
            // 单独处理房主 老板位
            if (null != createrBinder) createrBinder!!.bind(seatInfos[0])
            if (null != bossBinder) bossBinder!!.bind(seatInfos[1])
            // 麦位信息
            val seats = seatInfos.subList(2, count)
            if (null != adapter) adapter!!.setData(seats, true)
            for (i in 0 until count) {
                val seat = seatInfos[i]
                val userId = seat.userId
                if (!TextUtils.isEmpty(userId) && !TextUtils.equals(
                        userId,
                        AccountManager.getCurrentId()
                    )
                ) {
                    onSeatUserId = userId
                }
            }
        }
    }

    private fun initSeats() {
        rl_seat = findViewById(R.id.rl_seat)
        val creater = findViewById<View>(R.id.creater)
        val boss = findViewById<View>(R.id.boss)
        val holder = RcyHolder(creater)
        val bossHolder = RcyHolder(boss)
        createrBinder = SeatHandleBinder(this, holder, 0)
        bossBinder = SeatHandleBinder(this, bossHolder, 1)
        adapter = object : RcySAdapter<Seat, RcyHolder>(
            this,
            R.layout.layout_seat_item
        ) {
            override fun convert(holder: RcyHolder?, seatInfo: Seat?, position: Int) {
                val binder = SeatHandleBinder(
                    context as Activity, holder, position + 1
                )
                binder.bind(seatInfo)
            }
        }
        rl_seat!!.layoutManager = GridLayoutManager(this, 4)
        rl_seat!!.adapter = adapter
    }

    override fun onApiClick(v: View?, action: ApiFun?) {
        if (action == ApiFun.invite_seat) {
            //和业务相关
            // 基于QuickDemo 目前在展示的观众列表是：房主进房间之后进入房间的观众
            // 房主进房间的观众列表需要依赖服务端
            helper().showSelectDialog(this,
                roomId, "选择上麦观众", IResultBack { result ->
                    val userId = result!!.userId
                    VoiceRoomApi.getApi().handleRoomApi(action, userId, null)
                })
        } else {
            VoiceRoomApi.getApi().handleRoomApi(action, null, null)
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
    override fun onDismiss(dialogInterface: DialogInterface) {}

    companion object {
        const val ACTION_NOTIFY = "RoomActivity"
        const val ACTION_ROOM = 1000
        private const val ACTION_API = "房间Api"
        private const val KET_ROOM_ID = "room_id"
        private const val KET_ROOM_OWNER = "room_owner"
        private const val KET_ENTER = "enter"
        fun joinVoiceRoom(
            activity: Activity,
            roomId: String?,
            isRoomOwner: Boolean,
            enter: Boolean
        ) {
            val i = Intent(activity, RoomActivity::class.java)
            i.putExtra(KET_ROOM_ID, roomId)
            i.putExtra(KET_ROOM_OWNER, isRoomOwner)
            i.putExtra(KET_ENTER, enter)
            activity.startActivityForResult(i, ACTION_ROOM)
        }

        var permissionIndex =
            0//外部存储不可用，内部存储路径：data/data/com.learn.test/files//外部存储可用:/storage/emulated/0/Android/data/包名/files

        /**
         * 获取文件存储根路径：
         * 外部存储可用，返回外部存储路径:/storage/emulated/0/Android/data/包名/files
         * 外部存储不可用，则返回内部存储路径：data/data/包名/files
         */
        val filesPath: String
            get() {
                val filePath: String
                filePath =
                    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                        //外部存储可用:/storage/emulated/0/Android/data/包名/files
                        UIKit.getContext().getExternalFilesDir(null)!!.path
                    } else {
                        //外部存储不可用，内部存储路径：data/data/com.learn.test/files
                        UIKit.getContext().filesDir.path
                    }
                return filePath
            }
    }

    override fun getViewBinding() = ActivityRoomBinding.inflate(layoutInflater)

    private var inputBarDialog: InputBarDialog? = null
    override fun onClick() {
        super.onClick()
        binding.apply {
            sendMessage.onClick {
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
            more.onClick {
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
        }
    }

    private fun closeInput() {
        if (inputBarDialog != null && inputBarDialog!!.isShowing) {
            inputBarDialog!!.dismiss()
        }
    }

    private fun sendMessage(content: String) {
        RCVoiceRoomEngine.getInstance()
            .notifyVoiceRoom("sendMessge", content, object : RCVoiceRoomCallback {
                override fun onSuccess() {}
                override fun onError(code: Int, message: String) {
                    Logger.e(TAG, "notifyVoiceRoom#onError: [$code] msg = $message")
                }
            })
    }

    override fun onDestroy() {
        closeInput()
        VoiceRoomApi.getApi().leaveRoom(null)
        super.onDestroy()
    }

}