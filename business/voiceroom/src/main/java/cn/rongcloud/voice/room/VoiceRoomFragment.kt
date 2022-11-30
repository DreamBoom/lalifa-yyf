package cn.rongcloud.voice.room

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.RoomDetailBean
import cn.rongcloud.config.api.roomCheck
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.user.UserProvider
import cn.rongcloud.music.MusicControlManager
import cn.rongcloud.music.MusicMiniView
import cn.rongcloud.roomkit.api.Office
import cn.rongcloud.roomkit.intent.IntentWrap
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage
import cn.rongcloud.roomkit.message.RCChatroomBarrage
import cn.rongcloud.roomkit.message.RCChatroomVoice
import cn.rongcloud.roomkit.provider.VoiceRoomProvider.Companion.provider
import cn.rongcloud.roomkit.ui.RoomOwnerType
import cn.rongcloud.roomkit.ui.RoomType
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager
import cn.rongcloud.roomkit.ui.room.AbsRoomActivity
import cn.rongcloud.roomkit.ui.room.AbsRoomFragment
import cn.rongcloud.roomkit.ui.room.RoomMessageAdapter
import cn.rongcloud.roomkit.ui.room.RoomMessageAdapter.OnClickMessageUserListener
import cn.rongcloud.roomkit.ui.room.dialog.ExitRoomPopupWindow
import cn.rongcloud.roomkit.ui.room.dialog.ExitRoomPopupWindow.OnOptionClick
import cn.rongcloud.roomkit.ui.room.dialog.RoomNoticeDialog
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield
import cn.rongcloud.roomkit.ui.room.dialog.shield.ShieldDialog
import cn.rongcloud.roomkit.ui.room.dialog.shield.ShieldDialog.OnShieldDialogListener
import cn.rongcloud.roomkit.ui.room.fragment.BackgroundSettingFragment
import cn.rongcloud.roomkit.ui.room.fragment.MemberListFragment
import cn.rongcloud.roomkit.ui.room.fragment.MemberListFragment.OnClickUserListener
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment
import cn.rongcloud.roomkit.ui.room.fragment.gift.VideoGiftFragment
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun.BaseFun
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSettingFragment
import cn.rongcloud.roomkit.ui.room.model.Member
import cn.rongcloud.roomkit.ui.room.widget.*
import cn.rongcloud.roomkit.ui.room.widget.AllBroadcastView.OnClickBroadcast
import cn.rongcloud.roomkit.ui.room.widget.RoomBottomView.OnBottomOptionClickListener
import cn.rongcloud.roomkit.ui.roomlist.CreateRoomDialog
import cn.rongcloud.roomkit.widget.EditDialog
import cn.rongcloud.roomkit.widget.EditDialog.OnClickEditDialog
import cn.rongcloud.roomkit.widget.InputPasswordDialog
import cn.rongcloud.roomkit.widget.decoration.DefaultItemDecoration
import cn.rongcloud.voice.Constant
import cn.rongcloud.voice.R
import cn.rongcloud.voice.model.UiSeatModel
import cn.rongcloud.voice.room.adapter.VoiceRoomSeatsAdapter
import cn.rongcloud.voice.room.helper.VoiceEventHelper
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.logcat.LogCat.e
import com.drake.net.utils.scopeNetLife
import com.lalifa.ext.Config
import com.lalifa.ui.UIStack
import com.lalifa.utils.ImageLoader
import com.lalifa.utils.UiUtils
import com.lalifa.wapper.IResultBack
import com.lalifa.widget.dialog.dialog.VRCenterDialog
import io.rong.imkit.utils.RouteUtils
import io.rong.imkit.utils.StatusBarUtil
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo

/**
 * @author 李浩
 * @date 2021/9/24
 */
class VoiceRoomFragment : AbsRoomFragment<VoiceRoomPresenter?>(), IVoiceRoomFragmentView,
    OnClickMessageUserListener, OnBottomOptionClickListener, OnClickUserListener,
    View.OnClickListener, OnClickBroadcast , CreateRoomDialog.CreateRoomCallBack {
    private var mBackgroundImageView: ImageView? = null
    private var mGiftAnimationView: GiftAnimationView? = null
    private var mRoomTitleBar: RoomTitleBar? = null
    private var mNoticeView: TextView? = null
    private var mRoomSeatView: RoomSeatView? = null
    private var mRoomBottomView: RoomBottomView? = null
    private var mMessageView: RecyclerViewAtVP2? = null
    private var mRoomMessageAdapter: RoomMessageAdapter? = null
    private var mAllBroadcastView: AllBroadcastView? = null
    private var mMusicMiniView: MusicMiniView? = null
    private var rv_seat_list: RecyclerView? = null
    private var voiceRoomSeatsAdapter: VoiceRoomSeatsAdapter? = null
    private var mMemberSettingFragment: MemberSettingFragment? = null
    private var mExitRoomPopupWindow: ExitRoomPopupWindow? = null
    private var mNoticeDialog: RoomNoticeDialog? = null
    private var mMemberListFragment: MemberListFragment? = null
    private var mRoomSettingFragment: RoomSettingFragment? = null
    private var mInputPasswordDialog: InputPasswordDialog? = null
    private var mEditDialog: EditDialog? = null
    private var mShieldDialog: ShieldDialog? = null
    private var mBackgroundSettingFragment: BackgroundSettingFragment? = null
    private var mGiftFragment: GiftFragment? = null
    private var mRoomId: String? = null
    private var isCreate = false
    private var clVoiceRoomView: ConstraintLayout? = null
    private var rlRoomFinishedId: RelativeLayout? = null
    private var btnGoBackList: Button? = null
    override fun setLayoutId(): Int {
        return R.layout.fragment_new_voice_room
    }

    override fun init() {
        mRoomId = requireArguments().getString(ROOM_ID)
        isCreate = requireArguments().getBoolean(IntentWrap.KEY_IS_CREATE)
        clVoiceRoomView = requireView().findViewById<View>(R.id.cl_voice_room_view) as ConstraintLayout

        // 双击点赞的view
        mGiftAnimationView = requireView().findViewById(R.id.gift_view)
        mGiftAnimationView!!.setOnBottomOptionClickListener { rcChatroomLike ->
            present!!.sendMessage(
                rcChatroomLike
            )
        }
        val layoutParams = clVoiceRoomView!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = StatusBarUtil.getStatusBarHeight(requireContext())
        clVoiceRoomView!!.layoutParams = layoutParams
        rlRoomFinishedId = requireView().findViewById<View>(R.id.rl_room_finished_id) as RelativeLayout
        btnGoBackList = requireView().findViewById<View>(R.id.btn_go_back_list) as Button
        mRoomSettingFragment = RoomSettingFragment(present)
        getView<TextView>(R.id.create).setOnClickListener { createRoom() }
        // 全局广播View
        mAllBroadcastView = getView(R.id.view_all_broadcast)
        mAllBroadcastView!!.setOnClickBroadcast(OnClickBroadcast { message: RCAllBroadcastMessage ->
            clickBroadcast(
                message
            )
        })

        // 头部
        mRoomTitleBar = getView(R.id.room_title_bar)
        mRoomTitleBar!!.setOnMemberClickListener().subscribe { //添加防抖动
            mMemberListFragment = MemberListFragment(
                present!!.roomId,
                this@VoiceRoomFragment
            )
            mMemberListFragment!!.show(childFragmentManager)
        }
        //麦位
        rv_seat_list = getView(R.id.rv_seat_list)
        val gridLayoutManager = GridLayoutManager(getActivity(), 4)
        rv_seat_list!!.layoutManager = gridLayoutManager
        voiceRoomSeatsAdapter =
            VoiceRoomSeatsAdapter(getActivity()) { seatModel: UiSeatModel, position: Int ->
                onClickVoiceRoomSeats(
                    seatModel,
                    position
                )
            }
        voiceRoomSeatsAdapter!!.setHasStableIds(true)
        rv_seat_list!!.adapter = voiceRoomSeatsAdapter
        mRoomTitleBar!!.setOnMenuClickListener().subscribe { o: Any? -> clickMenu() }
        mNoticeView = getView(R.id.tv_notice)
        mNoticeView!!.setOnClickListener { v: View? -> showNoticeDialog(false) }
        // 背景
        mBackgroundImageView = getView(R.id.iv_background)
        provider().getAsyn(mRoomId!!, object :IResultBack<RoomDetailBean?> {
            override fun onResult(t: RoomDetailBean?) {
                setRoomBackground(
                Config.FILE_PATH + t!!.background
            )
            }
        })
        // 房主座位
        mRoomSeatView = getView(R.id.room_seat_view)
        mRoomSeatView!!.setRoomOwnerHeadOnclickListener(View.OnClickListener { v: View? ->
            //弹出
            if (present!!.roomOwnerType == RoomOwnerType.VOICE_OWNER) {
                //如果是房间所有者 ,如果在麦位上,那么弹出下麦和关闭麦克风弹窗，如果不在麦位上，那么直接上麦
                present!!.onClickRoomOwnerView(childFragmentManager)
            }
        })
        // 底部操作按钮和双击送礼物
        mRoomBottomView = getView(R.id.room_bottom_view)
        // 弹幕消息列表
        mMessageView = getView(R.id.rv_message)
        val linearLayoutManager = LinearLayoutManager(context)
        mMessageView!!.setLayoutManager(linearLayoutManager)
        mMessageView!!.addItemDecoration(
            DefaultItemDecoration(
                Color.TRANSPARENT,
                0,
                UiUtils.dp2px(5f)
            )
        )
        mRoomMessageAdapter = RoomMessageAdapter(context, mMessageView, this, RoomType.VOICE_ROOM)
        mMessageView!!.setAdapter(mRoomMessageAdapter)
        voiceRoom = getView(R.id.voice_room)
        if (null == mNoticeDialog) {
            mNoticeDialog = RoomNoticeDialog(activity)
        }

        // 音乐小窗口
        mMusicMiniView = getView(R.id.mmv_view)
        mMusicMiniView!!.setOnMusicClickListener(View.OnClickListener { v: View? ->
            if (present!!.roomOwnerType == RoomOwnerType.VOICE_OWNER) {
                showMusicDialog()
            }
        })
    }

    /**
     * 创建队伍
     *
     * @param isEdit
     */
    private fun createRoom() {
        // 创建之前检查是否已有创建的房间
        scopeNetLife {
            val roomCheck = roomCheck()
            if (null != roomCheck) {
                if (TextUtils.isEmpty(roomCheck.RoomId)) {
                    showCreateRoomDialog()
                } else {
                    onCreateExist(roomCheck.RoomId)
                }
            }
        }
    }
    private var mCreateRoomDialog: CreateRoomDialog? = null
    private var confirmDialog: VRCenterDialog? = null
    /**
     * 展示创建房间弹窗
     */
    private var mLauncher: ActivityResultLauncher<*>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result != null && result.data != null && result.data!!.data != null && mCreateRoomDialog != null
        ) {
            mCreateRoomDialog!!.setCoverUri(result.data!!.data)
        }
    }
    private fun showCreateRoomDialog() {
        mCreateRoomDialog = CreateRoomDialog(
            requireActivity(),
            mLauncher,
            this@VoiceRoomFragment
        )
        mCreateRoomDialog!!.show()
    }

    override fun onCreateExist(roomId: String) {
        confirmDialog = VRCenterDialog(requireActivity(), null)
        confirmDialog!!.replaceContent(
            getString(cn.rongcloud.roomkit.R.string.text_you_have_created_room),
            getString(cn.rongcloud.roomkit.R.string.cancel),
            null,
            getString(cn.rongcloud.roomkit.R.string.confirm),
            { jumpRoom(roomId) },
            null
        )
        confirmDialog!!.show()
    }

    /**
     * 跳转到相应的房间
     *
     * @param voiceRoomBean
     */
    private fun jumpRoom(roomId: String) {
        IntentWrap.launchRoom(requireContext(), roomId)
    }

    override fun onCreateSuccess(roomid: String) {
//        mAdapter.getData().add(0, voiceRoomBean)
//        mAdapter.notifyItemInserted(0)

        val list: ArrayList<String> = ArrayList()
        list.add(roomid)
        launchRoomActivity(roomid, list, 0, isCreate)
    }

    fun launchRoomActivity(
        roomId: String, roomIds: ArrayList<String>, position: Int, isCreate: Boolean
    ) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(
            roomId
        ) {
            IntentWrap.launchRoom(
                requireContext(),
                roomIds, position, isCreate
            )
        }
    }
    /**
     * 显示公告弹窗
     *
     * @param isEdit
     */
    override fun showNoticeDialog(isEdit: Boolean) {
        mNoticeDialog!!.show(present!!.notice, isEdit) { notice: String? ->
            //修改公告信息
            present!!.modifyNotice(notice)
        }
    }

    /**
     * 点击右上角菜单按钮
     */
    private fun clickMenu() {
        if (present!!.roomOwnerType == null) {
            finish()
            return
        }
        // pk中房主才提示
        if (TextUtils.equals(
                UserManager.get()!!.userId,
                present!!.createUserId)) {
            return
        }
        mExitRoomPopupWindow =
            ExitRoomPopupWindow(context, present!!.roomOwnerType, object : OnOptionClick {
                override fun clickPackRoom() {
                    //最小化窗口,判断是否有权限
                    if (checkDrawOverlaysPermission(false)) {
                        requireActivity().finish()

                        //缩放动画,并且显示悬浮窗，在这里要做悬浮窗判断
                        requireActivity().overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        MiniRoomManager.getInstance().show(
                            requireContext(),
                            present!!.roomId,
                            present!!.themePictureUrl,
                            requireActivity().intent,
                            VoiceEventHelper.helper()
                        )
                    } else {
                        showOpenOverlaysPermissionDialog()
                    }
                }

                override fun clickLeaveRoom() {
                    // 观众离开房间
                    present!!.leaveRoom()
                }

                override fun clickCloseRoom() {
                    // 房主关闭房间
                    present!!.closeRoom()
                }
            })
        mExitRoomPopupWindow!!.show(mBackgroundImageView)
    }

    /**
     * 麦位被点击的情况
     *
     * @param seatModel
     * @param position
     */
    private fun onClickVoiceRoomSeats(seatModel: UiSeatModel, position: Int) {
        when (present!!.roomOwnerType) {
            RoomOwnerType.VOICE_OWNER -> onClickVoiceRoomSeatsByOwner(seatModel, position)
            RoomOwnerType.VOICE_VIEWER -> onClickVoiceRoomSeatsByViewer(seatModel, position)
            else -> {}
        }
    }

    /**
     * 观众点击麦位的时候
     *
     * @param seatModel
     * @param position
     */
    private fun onClickVoiceRoomSeatsByViewer(seatModel: UiSeatModel, position: Int) {
        if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            // 点击锁定座位
            showToast("该座位已锁定")
        } else if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
            //点击空座位 的时候
            present!!.enterSeatViewer(position)
        } else if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            if (!TextUtils.isEmpty(seatModel.userId) && seatModel.userId == UserManager.get()!!
                    .userId
            ) {
                // 点击自己头像
                present!!.showNewSelfSettingFragment(seatModel).show(childFragmentManager)
            } else {
                // 点击别人头像
                present!!.getUserInfo(seatModel.userId)
            }
        }
    }

    /**
     * 房主点击麦位的时候
     *
     * @param seatModel
     * @param position
     */
    private fun onClickVoiceRoomSeatsByOwner(seatModel: UiSeatModel, position: Int) {
        if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
            || seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
        ) {
            //点击空座位或者锁定座位的时候，弹出弹窗
            present!!.enterSeatOwner(seatModel)
        } else if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            //弹窗设置弹窗
            // 点击别人头像
            present!!.getUserInfo(seatModel.userId)
        }
    }

    override fun initListener() {
        super.initListener()
        btnGoBackList!!.setOnClickListener { v: View -> onClick(v) }
    }

    private var voiceRoom: View? = null
    override fun joinRoom() {
        present!!.init(mRoomId!!, isCreate)
        mAllBroadcastView!!.setBroadcastListener()
    }

    override fun onBackPressed() {
        clickMenu()
    }

    override fun addSwitchRoomListener() {
        (requireActivity() as AbsRoomActivity).addSwitchRoomListener(mRoomId, this)
    }

    override fun removeSwitchRoomListener() {
        (requireActivity() as AbsRoomActivity).removeSwitchRoomListener(mRoomId)
    }

    override fun createPresent(): VoiceRoomPresenter {
        return VoiceRoomPresenter(this, lifecycle)
    }

    /**
     * 显示单条消息
     *
     * @param messageContent
     * @param isRefresh
     */
    override fun showMessage(messageContent: MessageContent?, isRefresh: Boolean) {
        val list: MutableList<MessageContent> = ArrayList(1)
        //        if (messageContent != null) {
//            list.add(messageContent);
//        }
//        mRoomMessageAdapter.setMessages(list, isRefresh);
        if (isRefresh) {
            if (messageContent != null) {
                list.add(messageContent)
            }
            mRoomMessageAdapter!!.setMessages(list, true)
        } else {
            mRoomMessageAdapter!!.interMessage(messageContent)
        }
    }

    override fun showVideoGift() {
        val f = VideoGiftFragment(context)
        f.show()
    }



    override fun refreshMessageList() {
        mRoomMessageAdapter!!.notifyDataSetChanged()
    }

    override fun refreshMusicView(show: Boolean, name: String, url: String) {
        if (show) {
            mMusicMiniView!!.show(name, url)
        } else {
            mMusicMiniView!!.dismiss()
        }
    }

    /**
     * 显示消息集合
     *
     * @param messageContentList
     * @param isRefresh
     * fun showMessageList(messageContentList: List<MessageContent></MessageContent>?>?, isRefresh: Boolean)
     */
    override fun showMessageList(messageContentList: List<MessageContent>, isRefresh: Boolean) {
        mRoomMessageAdapter!!.setMessages(messageContentList, isRefresh)
    }

    override fun showSettingDialog(funList: List<MutableLiveData<BaseFun>>) {
        mRoomSettingFragment!!.show(childFragmentManager, funList)
    }

    /**
     * 设置房间数据
     *
     * @param voiceRoomBean
     */
    override fun setRoomData(voiceRoomBean: RoomDetailBean?) {
        clVoiceRoomView!!.visibility = View.VISIBLE
        rlRoomFinishedId!!.visibility = View.GONE
        // 加载背景
        //todo config.filepath
        ImageLoader.loadUrl(
            mBackgroundImageView!!,
            voiceRoomBean!!.background,
            com.lalifa.base.R.color.black
        )
        // 设置title数据
        mRoomTitleBar!!.setData(
            present!!.roomOwnerType,
            voiceRoomBean.title,
            voiceRoomBean.id,
            voiceRoomBean.uid.toString(),
            present
        )

        // 设置底部按钮
        mRoomBottomView!!.setData(present!!.roomOwnerType,
            this, voiceRoomBean.Chatroom_id)
        // 设置消息列表数据
        mRoomMessageAdapter!!.setRoomCreateId(voiceRoomBean.userInfo!!.userId)
    }

    override fun destroyRoom() {
        super.destroyRoom()
        //离开房间的时候
        clVoiceRoomView!!.visibility = View.INVISIBLE
        rlRoomFinishedId!!.visibility = View.GONE
        //取消对当前房间的监听
        VoiceEventHelper.helper().unregeister()
        //取消当前对于各种消息的回调监听
        present!!.unInitListener()
    }

    override fun onSpeakingStateChanged(isSpeaking: Boolean) {
        mRoomSeatView!!.setSpeaking(isSpeaking)
    }

    /**
     * 刷新当前房主信息Ui
     *
     * @param uiSeatModel
     */
    override fun refreshRoomOwner(uiSeatModel: UiSeatModel) {
        if (uiSeatModel == null) {
            return
        }
        if (TextUtils.isEmpty(uiSeatModel.userId)) {
            mRoomSeatView!!.setData("", null)
            mRoomSeatView!!.setSpeaking(false)
            // mute 状态不变
//            mRoomSeatView.setRoomOwnerMute(false);
            mRoomSeatView!!.setGiftCount(0L)
        } else {
//            Log.e(TAG, "refreshRoomOwner: " + uiSeatModel.toString());
            UserProvider.provider().getAsyn(uiSeatModel.userId, object : IResultBack<UserInfo?> {
                override fun onResult(userInfo: UserInfo?) {
                    if (userInfo != null) {
                        mRoomSeatView!!.setData(userInfo.name, userInfo.portraitUri.toString())
                    } else {
                        mRoomSeatView!!.setData("", "")
                    }
                }
            })
            mRoomSeatView!!.setSpeaking(uiSeatModel.isSpeaking)
            if (uiSeatModel.extra != null) {
                mRoomSeatView!!.setRoomOwnerMute(uiSeatModel.extra.isDisableRecording)
            } else {
                mRoomSeatView!!.setRoomOwnerMute(false)
            }
            mRoomSeatView!!.setGiftCount(uiSeatModel.giftCount.toLong())
        }
    }

    /**
     * 设置公告的内容
     *
     * @param notice
     */
    override fun setNotice(notice: String) {
        if (null != mNoticeDialog) {
            mNoticeDialog!!.setNotice(notice)
        }
    }

    /**
     * 点击消息列表中的用户名称
     *
     * @param userId
     */
    override fun clickMessageUser(userId: String) {
        UserProvider.provider().getAsyn(userId, object : IResultBack<UserInfo> {
            override fun onResult(userInfo: UserInfo?) {
                val user = User()
                user.userId = userId
                user.userName = userInfo!!.name
                user.avatar = userInfo.portraitUri.toString()
                clickUser(user)
            }

        })
    }

    override fun enterSeatSuccess() {
        showToast("上麦成功")
    }

    override fun onSeatInfoChange(index: Int, uiSeatModel: UiSeatModel) {}

    /**
     * 座位发生了改变
     *
     * @param uiSeatModelList
     */
    override fun onSeatListChange(uiSeatModelList: List<UiSeatModel>) {
        val uiSeatModels = uiSeatModelList.subList(1, uiSeatModelList.size)
        voiceRoomSeatsAdapter!!.refreshData(uiSeatModels)
        refreshRoomOwner(uiSeatModelList[0])
    }

    override fun changeStatus(status: Int) {
        VoiceEventHelper.helper().currentStatus = status
        when (status) {
            VoiceRoomPresenter.STATUS_NOT_ON_SEAT -> {
                //申请中
                mRoomBottomView!!.setRequestSeatImage(R.drawable.ic_request_enter_seat)
                (requireActivity() as AbsRoomActivity).setCanSwitch(true)
            }
            VoiceRoomPresenter.STATUS_WAIT_FOR_SEAT ->                 //等待中
                mRoomBottomView!!.setRequestSeatImage(R.drawable.ic_wait_enter_seat)
            VoiceRoomPresenter.STATUS_ON_SEAT -> {
                //已经在麦上
                mRoomBottomView!!.setRequestSeatImage(R.drawable.ic_on_seat)
                (requireActivity() as AbsRoomActivity).setCanSwitch(false)
            }
        }
    }

    override fun showUnReadRequestNumber(number: Int) {
        //如果不是房主，不设置
        if (present!!.roomOwnerType == RoomOwnerType.VOICE_OWNER) {
            mRoomBottomView!!.setmSeatOrderNumber(number)
        }
    }

    /**
     * 显示撤销麦位申请的弹窗
     */
    override fun showRevokeSeatRequest() {
        when (present!!.currentStatus) {
            VoiceRoomPresenter.STATUS_ON_SEAT -> {}
            VoiceRoomPresenter.STATUS_WAIT_FOR_SEAT -> present!!.showRevokeSeatRequestFragment()
            VoiceRoomPresenter.STATUS_NOT_ON_SEAT -> present!!.enterSeatViewer(-1)
        }
    }

    override fun onNetworkStatus(i: Int) {
        mRoomTitleBar!!.post { mRoomTitleBar!!.setDelay(i) }
    }

    override fun finish() {
        //在销毁之前提前出栈顶
        try {
            UIStack.getInstance().remove(requireActivity() as VoiceRoomActivity)
            requireActivity().finish()
            if (null != mRoomMessageAdapter) mRoomMessageAdapter!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clickSendMessage(message: String) {
        //发送文字消息
        val barrage = RCChatroomBarrage()
        barrage.content = message
        barrage.userId = UserManager.get()!!.userId
        barrage.userName = UserManager.get()!!.userName
        present!!.sendMessage(barrage)
    }

    /**
     * 发送私信
     */
    override fun clickPrivateMessage() {
        RouteUtils.routeToSubConversationListActivity(
            requireActivity(),
            Conversation.ConversationType.PRIVATE,
            "消息"
        )
    }

    override fun clickSeatOrder() {

    }

    /**
     * 设置按钮
     */
    override fun clickSettings() {
        present!!.showSettingDialog()
    }

    /**
     * 请求上麦按钮
     */
    override fun clickRequestSeat() {
        present!!.requestSeat(-1)
    }

    /**
     * 发送礼物
     */
    override fun onSendGift() {
        present!!.sendGift()
    }

    override fun onSendVoiceMessage(rcChatroomVoice: RCChatroomVoice) {
        RCChatRoomMessageManager.sendChatMessage(
            present!!.roomId,
            rcChatroomVoice,
            true,
            { integer: Any? -> null }) { coreErrorCode: Any?, integer: Any? -> null }
    }

    /**
     * TODO 麦克风被占用的前提下
     * 1、如果不在麦位上，不可以发送
     * 2、如果在麦位上,但是没有被禁麦，也不可以发送
     *
     * @return
     */
    override fun canSend(): Boolean {
        //不在麦位上，或者在麦位上但是没有被禁麦
        val seatInfo = VoiceEventHelper.helper().getSeatInfo(
            UserManager.get()!!.userId
        )
        return if (seatInfo == null || seatInfo != null && !seatInfo.isMute) {
            false
        } else true
    }

    override fun clickUser(user: User) {
        //如果点击的是本人的名称，那么无效
        if (TextUtils.equals(user.userId, UserManager.get()!!.userId)) {
            return
        }
        present!!.getUserInfo(user.userId)
    }

    override fun showSetPasswordDialog(item: MutableLiveData<BaseFun?>) {
        mInputPasswordDialog =
            InputPasswordDialog(requireContext(), false, object :
                InputPasswordDialog.OnClickListener {
                override fun clickCancel() {
                    mInputPasswordDialog!!.dismiss()
                }

                override fun clickConfirm(password: String) {
                    if (TextUtils.isEmpty(password)) {
                        return
                    }
                    if (password.length < 4) {
                        showToast("请输入4位密码")
                        return
                    }
                    mInputPasswordDialog!!.dismiss()
                    present!!.setRoomPassword(true, password, item)
                }
            })
        mInputPasswordDialog!!.show()
    }

    override fun showSetRoomNameDialog(name: String) {
        mEditDialog = EditDialog(requireContext(), "修改房间标题",
            "请输入房间名",
            name,
            10,
            false,
            object : OnClickEditDialog {
                override fun clickCancel() {
                    mEditDialog!!.dismiss()
                }

                override fun clickConfirm(newName: String) {
                    if (TextUtils.isEmpty(newName)) {
                        showToast("房间名称不能为空")
                        return
                    }
                    present!!.setRoomName(newName)
                    mEditDialog!!.dismiss()
                }
            })
        mEditDialog!!.show()
    }

    /**
     * 屏蔽词弹窗
     *
     * @param roomId
     */
    override fun showShieldDialog(roomId: String) {
        mShieldDialog =
            ShieldDialog(requireActivity(), roomId, 10, object : OnShieldDialogListener {
                override fun onAddShield(shield: String, shields: List<Shield>) {
                    present!!.shield
                    RCVoiceRoomEngine.getInstance()
                        .notifyVoiceRoom(Constant.EVENT_ADD_SHIELD, shield, null)
                }

                override fun onDeleteShield(shield: Shield, shields: List<Shield>) {
                    present!!.shield
                    RCVoiceRoomEngine.getInstance()
                        .notifyVoiceRoom(Constant.EVENT_DELETE_SHIELD, shield.name, null)
                }
            })
        mShieldDialog!!.show()
    }

    /**
     * 房间背景弹窗
     *
     * @param url
     */
    override fun showSelectBackgroundDialog(url: String) {
        mBackgroundSettingFragment = BackgroundSettingFragment(url, present)
        mBackgroundSettingFragment!!.show(childFragmentManager)
    }

    override fun showSendGiftDialog(
        voiceRoomBean: RoomDetailBean,
        selectUserId: String,
        members: List<Member>
    ) {
        mGiftFragment = GiftFragment(voiceRoomBean, selectUserId, present)
        mGiftFragment!!.refreshMember(members)
        mGiftFragment!!.show(childFragmentManager)
    }

    override fun showUserSetting(member: Member, uiSeatModel: UiSeatModel) {
        if (mMemberSettingFragment == null) {
            mMemberSettingFragment = MemberSettingFragment(present!!.roomOwnerType!!, present)
        }
        if (uiSeatModel != null) {
            //说明当前用户在麦位上
            mMemberSettingFragment!!.setMemberIsOnSeat(uiSeatModel.index > -1)
            mMemberSettingFragment!!.setSeatPosition(uiSeatModel.index)
            mMemberSettingFragment!!.setMute(uiSeatModel.isMute)
        } else {
            mMemberSettingFragment!!.setMemberIsOnSeat(false)
        }
        mMemberSettingFragment!!.show(childFragmentManager, member, present!!.createUserId)
    }

    override fun showMusicDialog() {
        MusicControlManager.getInstance().showDialog(childFragmentManager, present!!.roomId)
    }

    /**
     * 当前房间直播已经结束
     */
    override fun showFinishView() {
        clVoiceRoomView!!.visibility = View.INVISIBLE
        rlRoomFinishedId!!.visibility = View.VISIBLE
    }

    override fun showLikeAnimation() {
        mGiftAnimationView!!.showFov(mRoomBottomView!!.giftViewPoint)
    }

    override fun setOnlineCount(OnlineCount: Int) {
        mRoomTitleBar!!.setOnlineNum(OnlineCount)
    }

    override fun switchOtherRoom(roomId: String) {
        (requireActivity() as AbsRoomActivity).switchOtherRoom(roomId)
    }

    override fun setTitleFollow(isFollow: Boolean) {
        mRoomTitleBar!!.setFollow(isFollow)
    }

    override fun setVoiceName(name: String) {
        mRoomTitleBar!!.setRoomName(name)
    }

    override fun setRoomBackground(url: String) {
        ImageLoader.loadUrl(mBackgroundImageView!!, url, com.lalifa.base.R.color.black)
    }

    override fun refreshSeat() {
        voiceRoomSeatsAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_go_back_list) {
            //直接退出当前房间
            finish()
        }
    }

    override fun clickBroadcast(message: RCAllBroadcastMessage) {
        mAllBroadcastView!!.showMessage(null)
        present!!.jumpRoom(message)
    }

    companion object {
        @JvmStatic
        fun getInstance(roomId: String?, isCreate: Boolean): Fragment {
            val bundle = Bundle()
            bundle.putString(ROOM_ID, roomId)
            bundle.putBoolean(IntentWrap.KEY_IS_CREATE, isCreate)
            val fragment: Fragment = VoiceRoomFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}