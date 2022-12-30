package com.lalifa.main.activity.room

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseActivity
import com.lalifa.ext.*
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.adapter.RoomMessageAdapter
import com.lalifa.main.activity.room.ext.*
import com.lalifa.main.activity.room.ext.QuickEventListener.*
import com.lalifa.main.activity.room.message.*
import com.lalifa.main.activity.room.widght.*
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityRoomBinding
import com.lalifa.main.ext.*
import com.lalifa.main.ext.MUtils.Companion.clickSend
import com.lalifa.main.ext.MUtils.Companion.meOut
import com.lalifa.main.ext.MUtils.Companion.muteRemote
import com.lalifa.main.ext.MUtils.Companion.onSeat
import com.lalifa.main.ext.MUtils.Companion.receiveMsg
import com.lalifa.main.ext.MUtils.Companion.roomMore
import com.lalifa.main.ext.MUtils.Companion.seat
import com.lalifa.main.ext.MUtils.Companion.sendBroadcast
import com.lalifa.main.ext.MUtils.Companion.sendMessage
import com.lalifa.main.ext.MUtils.Companion.setBanner
import com.lalifa.main.ext.MUtils.Companion.setSeatLock
import com.lalifa.main.ext.MUtils.Companion.showGiftPop
import com.lalifa.main.ext.MUtils.Companion.showRoomList
import com.lalifa.main.ext.MUtils.Companion.showUserCar
import com.lalifa.main.fragment.adapter.roomGiftHistoryAdapter
import com.lalifa.main.fragment.adapter.seatAdapter
import com.lalifa.main.fragment.adapter.seatBossAdapter
import com.lalifa.utils.UiUtils
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent

class RoomActivity : BaseActivity<ActivityRoomBinding>(), SeatListObserver,
    RoomInfoObserver, CreateRoomDialog.CreateRoomCallBack,
    RoomMessageAdapter.OnClickMessageUserListener,
    GiftDialog.OnSendGiftListener, AllBroadView.OnClickBroadcast {
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
    var mRoomMessageAdapter: RoomMessageAdapter? = null
    override fun initView() {
        loadTag!!.show()
        roomId = intent.getStringExtra(KET_ROOM_ID)
        owner = intent.getBooleanExtra(KET_ROOM_OWNER, false)
        //设置监听
        get().observeSeatList(this)
        get().observeRoomInfo(this)
        get().register(this, owner)
        binding.allBroadcast.setOnClickBroadcast(this)
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
        seatBoss = binding.seatBossList.seatBossAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(true, layoutPosition, getModel())
            }
        }
        seat = binding.rlSeat.seatAdapter().apply {
            R.id.ivPortrait.onClick {
                clickSeat(false, layoutPosition, getModel())
            }
        }
        //消息接收监听
        receiveMsg(roomId!!, binding.svgGift, binding.rankList) {
            //将消息显示到列表上
            if (RCChatRoomMessageManager.isShowingMessage(it)) {
                mRoomMessageAdapter!!.interMessage(it)
            }
        }
        MUtils.join(roomId!!) {
            if (it) {
                getRoomInfo()
            } else {
                loadTag!!.dismiss()
                finish()
            }
        }
    }

    //麦位变化，刷新房间观众
    override fun onSeatList(seatInfos: List<Seat>) {
        onSeat(roomId!!, seatInfos, seatBoss!!, seat!!) {
            currentStatus = it
        }
    }

    private var seatBoss: BindingAdapter? = null
    private var seat: BindingAdapter? = null
    override fun onReady() {
        scopeNetLife {
            val userInfo = userInfo(UserManager.get()!!.userId, roomId!!)
            Member.setMember(userInfo)
            if (userInfo!!.manage_type != 1) {
                binding.voice.gone()
                binding.sy.gone()
            }
            //展示进场动画
            showUserCar(
                roomId!!,
                userInfo.userId, binding.svgIn
            )
        }
    }

    override fun onMessage(message: Message?) {
        RCChatRoomMessageManager.onReceiveMessage(roomId, message!!.content)
    }

    /**
     * 进入房间后发送默认的消息
     */
    private fun sendSystemMessage() {
        // 默认消息
        val list: MutableList<MessageContent> = java.util.ArrayList(1)
        mRoomMessageAdapter!!.setMessages(list, true)
        MUtils.sendSystemMessage(mRoomDetail!!)
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
                    setBanner(roomDetail.advertisement, binding.banner)
                    sendSystemMessage()
                } else {
                    toast("房间详情加载异常")
                    loadTag!!.dismiss()
                    finish()
                }
            }
        }
    }

    //用户进入房间
    override fun onUserIn(userId: String?) {
        showUserCar(roomId!!, userId!!, binding.svgIn)
    }

    //点击麦位逻辑
    private fun clickSeat(isBoss: Boolean, position: Int, models: Seat) {
        seat(position, isBoss, models, mRoomDetail!!, currentStatus, this) {
            currentStatus = it
        }
    }

    /**
     * 被踢出房间回调
     * @param targetId 被踢用户的标识
     */
    override fun onOut(targetId: String?) {
        meOut(targetId)
    }

    private var currentStatus: Int = Tool.STATUS_NOT_ON_SEAT

    override fun onClick() {
        super.onClick()
        binding.apply {
            sendMessage.onClick { clickSend("", roomId!!) }
            more.onClick { roomMore(mRoomDetail!!) }
            setting.onClick { setPop() }
            create.onClick {
                CreateRoomDialog(
                    roomId,
                    this@RoomActivity,
                    this@RoomActivity
                ).show()
            }
            train.onClick {
                showRoomList(mRoomDetail!!) { s, b ->
                    joinVoiceRoom(this@RoomActivity, s, b)
                }
            }
            gift.onClick {
                showGiftPop(
                    roomId!!,
                    mRoomDetail!!.password_type == 1,
                    this@RoomActivity
                )
            }
            voice.onClick {
                setSeatLock(binding.voice)
            }
            sy.onClick {
                roomIsMute = !roomIsMute
                muteRemote(!roomIsMute, binding.sy)
            }
        }
    }

    var roomIsMute = false

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

    }

    //点击设置
    @SuppressLint("NotifyDataSetChanged")
    private fun setPop() {
        roomBottomDialog {
            when (it) {
                2 -> {
                    //清空消息
                    // 默认消息
                    val list: MutableList<MessageContent> = java.util.ArrayList(1)
                    mRoomMessageAdapter!!.setMessages(list, true)
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
                            putExtra("roomId", roomId!!)
                        }
                    }
                }
                6 -> {
                    //礼物列表
                    showCommonListDialog("礼物记录") {
                        scopeNetLife {
                            roomGiftHistoryAdapter().models = roomGiftHistory(roomId!!)
                        }
                    }
                }
                7 -> {
                    //发布广播
                    sendBroadcast(mRoomDetail!!)
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

    //点击返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            showTipDialog("确定离开当前房间？") {
                MUtils.imLeave { finish() }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
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

    //礼物发送成功
    override fun onSendGiftSuccess(messages: MessageContent?) {
        sendMessage(roomId!!, messages)
    }

    //点击广播
    override fun clickBroadcast(message: RCAllBroadcastMessage?) {

    }
}