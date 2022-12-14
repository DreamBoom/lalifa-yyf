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
        //????????????
        get().observeSeatList(this)
        get().observeRoomInfo(this)
        get().register(this, owner)
        binding.allBroadcast.setOnClickBroadcast(this)
        val linearLayoutManager = LinearLayoutManager(this)
        mRoomMessageAdapter = RoomMessageAdapter(
            this,
            binding.rlMessage, this
        )
        // ????????????????????????
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
        //??????????????????
        receiveMsg(roomId!!, binding.svgGift, binding.rankList) {
            //???????????????????????????
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

    //?????????????????????????????????
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
            //??????????????????
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
     * ????????????????????????????????????
     */
    private fun sendSystemMessage() {
        // ????????????
        val list: MutableList<MessageContent> = java.util.ArrayList(1)
        mRoomMessageAdapter!!.setMessages(list, true)
        MUtils.sendSystemMessage(mRoomDetail!!)
    }

    //??????????????????
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
                    toast("????????????????????????")
                    loadTag!!.dismiss()
                    finish()
                }
            }
        }
    }

    //??????????????????
    override fun onUserIn(userId: String?) {
        showUserCar(roomId!!, userId!!, binding.svgIn)
    }

    //??????????????????
    private fun clickSeat(isBoss: Boolean, position: Int, models: Seat) {
        seat(position, isBoss, models, mRoomDetail!!, currentStatus, this) {
            currentStatus = it
        }
    }

    /**
     * ?????????????????????
     * @param targetId ?????????????????????
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
     * ??????????????????
     * @param seatIndex -1 ????????? else ?????????
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

    //????????????
    @SuppressLint("NotifyDataSetChanged")
    private fun setPop() {
        roomBottomDialog {
            when (it) {
                2 -> {
                    //????????????
                    // ????????????
                    val list: MutableList<MessageContent> = java.util.ArrayList(1)
                    mRoomMessageAdapter!!.setMessages(list, true)
                }
                3 -> {
                    //????????????
                    startCode(RoomSettingActivity::class.java) {
                        putExtra("room", mRoomDetail!!)
                    }
                }
                4 -> {
                    //???????????????
                    seatBoss!!.notifyDataSetChanged()
                    seat!!.notifyDataSetChanged()
                }
                5 -> {
                    //?????????
                    if (mRoomDetail!!.office_type != 1) {
                        toast("??????????????????")
                    } else {
                        start(ManageListActivity::class.java) {
                            putExtra("roomId", roomId!!)
                        }
                    }
                }
                6 -> {
                    //????????????
                    showCommonListDialog("????????????") {
                        scopeNetLife {
                            roomGiftHistoryAdapter().models = roomGiftHistory(roomId!!)
                        }
                    }
                }
                7 -> {
                    //????????????
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

    //???????????????
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            showTipDialog("???????????????????????????") {
                MUtils.imLeave { finish() }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //????????????????????????
    override fun onCreateSuccess(roomId: String?) {

    }

    //??????????????????
    override fun onCreateExist(roomId: String?) {

    }

    //????????????
    override fun clickMessageUser(userId: String?) {

    }

    //??????????????????
    override fun onSendGiftSuccess(messages: MessageContent?) {
        sendMessage(roomId!!, messages)
    }

    //????????????
    override fun clickBroadcast(message: RCAllBroadcastMessage?) {

    }
}