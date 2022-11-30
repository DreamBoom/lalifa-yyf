package cn.rongcloud.roomkit.ui.room.fragment

import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.FragmentManager
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.follow
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.ui.room.model.MemberCache.Companion.get
import cn.rongcloud.roomkit.ui.RoomOwnerType
import com.lalifa.widget.dialog.dialog.BaseBottomSheetDialog
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback
import com.lalifa.utils.KToast
import io.rong.imkit.utils.RouteUtils
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment
import cn.rongcloud.roomkit.ui.room.model.MemberCache
import cn.rongcloud.roomkit.ui.room.fragment.SeatActionClickListener
import cn.rongcloud.roomkit.message.RCFollowMsg
import cn.rongcloud.roomkit.ui.room.model.Member
import com.drake.net.utils.scopeNet
import com.lalifa.utils.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import io.rong.imlib.model.Conversation

/**
 * @author gyn
 * @date 2021/9/27
 */
class MemberSettingFragment(
    private val mRoomOwnerType: RoomOwnerType,
    private val mOnMemberSettingClickListener: OnMemberSettingClickListener?
) : BaseBottomSheetDialog(R.layout.fragment_member_setting) {
    private var mGlBg: Guideline? = null
    private var mIvMemberPortrait: CircleImageView? = null
    private var mTvMemberName: AppCompatTextView? = null
    private var mTvSeatPosition: AppCompatTextView? = null
    private var mClButtons: ConstraintLayout? = null
    private var mBtnSendGift: AppCompatButton? = null
    private var mBtnSendMessage: AppCompatButton? = null
    private var mClMemberSetting: ConstraintLayout? = null
    private var mLlInvitedSeat: LinearLayout? = null
    private var mLlKickSeat: LinearLayout? = null
    private var mLlCloseSeat: LinearLayout? = null
    private var mLlMuteSeat: LinearLayout? = null
    private var mIvMuteSeat: AppCompatImageView? = null
    private var mTvMuteSeat: AppCompatTextView? = null
    private var mLlKickRoom: LinearLayout? = null
    private var mLlKickGame: LinearLayout? = null
    private var mLlSelfEnterSeat: LinearLayout? = null
    private var mLlInvitedGame: LinearLayout? = null
    private var mRlSettingAdmin: RelativeLayout? = null
    private var mTvSettingAdmin: AppCompatTextView? = null
    private var mBtnFollow: AppCompatButton? = null

    //麦位的状态
    var isMutes = true
    private var roomUserId: String? = null

    // 操作的用户是否在麦位上
    var memberIsOnSeats = false

    // 自己是否在麦位上
    var selfIsOnSeat = false

    // 自己是否是队长
    var selfIsCaptain = false

    // 要操作的对方是否加入游戏了
    var memberIsInGame = false

    // 游戏是否进行中
    var isGamePlaying = false

    //麦位的位置
    var seatPositions = 1
    private var member: Member? = null
    fun setMute(mute: Boolean) {
        isMutes = mute
    }

    fun setSeatPosition(seatPosition: Int) {
        this.seatPositions = seatPosition
    }

    fun setMemberIsOnSeat(memberIsOnSeat: Boolean) {
        this.memberIsOnSeats = memberIsOnSeat
    }

    fun setSelfAndMemberInfo(
        selfIsOnSeat: Boolean,
        selfIsCaptain: Boolean,
        memberIsInGame: Boolean,
        isGamePlaying: Boolean
    ) {
        this.selfIsOnSeat = selfIsOnSeat
        this.selfIsCaptain = selfIsCaptain
        this.memberIsInGame = memberIsInGame
        this.isGamePlaying = isGamePlaying
    }

    override fun initView() {
        mGlBg = requireView().findViewById<View>(R.id.gl_bg) as Guideline
        mIvMemberPortrait = requireView().findViewById<View>(R.id.iv_member_portrait) as CircleImageView
        mTvMemberName = requireView().findViewById<View>(R.id.tv_member_name) as AppCompatTextView
        mTvSeatPosition = requireView().findViewById<View>(R.id.tv_seat_position) as AppCompatTextView
        mClButtons = requireView().findViewById<View>(R.id.cl_buttons) as ConstraintLayout
        mBtnSendGift = requireView().findViewById<View>(R.id.btn_send_gift) as AppCompatButton
        mBtnSendMessage = requireView().findViewById<View>(R.id.btn_send_message) as AppCompatButton
        mClMemberSetting = requireView().findViewById<View>(R.id.cl_member_setting) as ConstraintLayout
        mLlInvitedSeat = requireView().findViewById<View>(R.id.ll_invited_seat) as LinearLayout
        mLlKickSeat = requireView().findViewById<View>(R.id.ll_kick_seat) as LinearLayout
        mLlCloseSeat = requireView().findViewById<View>(R.id.ll_close_seat) as LinearLayout
        mLlMuteSeat = requireView().findViewById<View>(R.id.ll_mute_seat) as LinearLayout
        mIvMuteSeat = requireView().findViewById<View>(R.id.iv_mute_seat) as AppCompatImageView
        mTvMuteSeat = requireView().findViewById<View>(R.id.tv_mute_seat) as AppCompatTextView
        mLlKickRoom = requireView().findViewById<View>(R.id.ll_kick_room) as LinearLayout
        mLlKickGame = requireView().findViewById<View>(R.id.ll_kick_game) as LinearLayout
        mLlSelfEnterSeat = requireView().findViewById<View>(R.id.ll_self_enter_seat) as LinearLayout
        mLlInvitedGame = requireView().findViewById<View>(R.id.ll_invited_game) as LinearLayout
        mRlSettingAdmin = requireView().findViewById<View>(R.id.rl_setting_admin) as RelativeLayout
        mTvSettingAdmin = requireView().findViewById<View>(R.id.tv_setting_admin) as AppCompatTextView
        mBtnFollow = requireView().findViewById<View>(R.id.btn_follow) as AppCompatButton
        refreshView()
    }

    public override fun initListener() {
        super.initListener()
        if (mOnMemberSettingClickListener == null) {
            return
        }
        mRlSettingAdmin!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickSettingAdmin(
                member!!.toUser(), ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show("设置失败")
                    }
                })
        }
        mLlInvitedSeat!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickInviteSeat(
                -1,
                member!!.toUser(),
                ClickCallback { result: Boolean, msg: String? ->
                    KToast.show(msg)
                    if (result) {
                        dismiss()
                    }
                })
        }
        mLlKickRoom!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickKickRoom(
                member!!.toUser(), ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mLlKickSeat!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickKickSeat(
                member!!.toUser(), ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                        KToast.show("发送下麦通知成功")
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mLlMuteSeat!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickMuteSeat(
                seatPositions,
                !isMutes,
                ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mLlCloseSeat!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickCloseSeat(
                seatPositions,
                true,
                ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    }
                    KToast.show(msg)
                })
        }
        mBtnSendGift!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickSendGift(member!!.toUser())
            dismiss()
        }
        mLlSelfEnterSeat!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.switchSelfEnterSeat(
                member,
                seatPositions,
                ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mLlInvitedGame!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickInvitedGame(
                member!!.toUser(), ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mLlKickGame!!.setOnClickListener { v: View? ->
            mOnMemberSettingClickListener.clickKickGame(
                member!!.toUser(), ClickCallback { result: Boolean, msg: String? ->
                    if (result) {
                        dismiss()
                    } else {
                        KToast.show(msg)
                    }
                })
        }
        mBtnSendMessage!!.setOnClickListener { v: View? ->
            dismiss()
            RouteUtils.routeToConversationActivity(
                requireContext(),
                Conversation.ConversationType.PRIVATE,
                member!!.userId
            )
        }
        mBtnFollow!!.setOnClickListener { v: View? -> follow() }
    }

    fun show(fragmentManager: FragmentManager?, user: User?, roomUserId: String?) {
        member = Member.fromUser(user)
        this.roomUserId = roomUserId
        show(requireFragmentManager(), TAG)
    }

    fun show(fragmentManager: FragmentManager?, member: Member?, roomUserId: String?) {
        this.member = member
        this.roomUserId = roomUserId
        show(requireFragmentManager(), TAG)
    }

    private fun refreshView() {

        // 自己是否是管理员
        val selfIsAdmin = get().isAdmin(
            UserManager.get()!!.userId
        )
        // 操作的用户是否是管理员
        val memberIsAdmin = get().isAdmin(member!!.userId)
        // 操作的用户是否是房主
        val memberIsOwner = TextUtils.equals(roomUserId, member!!.userId)

        // 头像和昵称
        ImageLoader.loadUrl(mIvMemberPortrait!!, member!!.portraitUrl, R.drawable.default_portrait)
        mTvMemberName!!.text = member!!.userName
        // 麦位信息显示
        setSeatPosition(memberIsOnSeats)
        // 设置管理员按钮
        refreshSettingAdmin(memberIsAdmin)
        // 设置底部操作按钮view展示
        refreshBottomView(selfIsAdmin, memberIsAdmin, memberIsOnSeats, memberIsOwner)
        refreshFollow(member!!.isFollow)
    }

    private fun refreshBottomView(
        selfIsAdmin: Boolean,
        memberIsAdmin: Boolean,
        memberIsOnSeat: Boolean,
        memberIsOwner: Boolean
    ) {
        mLlInvitedSeat!!.visibility = View.GONE
        mLlKickSeat!!.visibility = View.GONE
        mLlSelfEnterSeat!!.visibility = View.GONE
        mLlCloseSeat!!.visibility = View.GONE
        mLlMuteSeat!!.visibility = View.GONE
        mLlKickRoom!!.visibility = View.GONE
        mLlInvitedGame!!.visibility = View.GONE
        mLlKickGame!!.visibility = View.GONE
        when (mRoomOwnerType) {
            RoomOwnerType.VOICE_OWNER -> {
                if (memberIsOwner) {
                    mClButtons!!.visibility = View.INVISIBLE
                    mClMemberSetting!!.visibility = View.GONE
                    mTvSeatPosition!!.visibility = View.INVISIBLE
                    mRlSettingAdmin!!.visibility = View.GONE
                    return
                }
                mClMemberSetting!!.visibility = View.VISIBLE
                // 上下麦
                if (memberIsOnSeat) {
                    mLlKickSeat!!.visibility = View.VISIBLE
                    mLlMuteSeat!!.visibility = View.VISIBLE
                    mLlCloseSeat!!.visibility = View.VISIBLE
                    //根据麦位状态
                    if (isMutes) {
                        mIvMuteSeat!!.setImageResource(R.drawable.ic_room_setting_unmute_all)
                        mTvMuteSeat!!.text = "取消禁麦"
                    } else {
                        mIvMuteSeat!!.setImageResource(R.drawable.ic_member_setting_mute_seat)
                        mTvMuteSeat!!.text = "座位禁麦"
                    }
                } else {
                    mLlInvitedSeat!!.visibility = View.VISIBLE
                }
                // 可以踢人
                mLlKickRoom!!.visibility = View.VISIBLE
            }
            RoomOwnerType.VOICE_VIEWER ->                 // 自己和对方都是管理,或对方是房主,不显示底部操作
                if (selfIsAdmin && memberIsAdmin || memberIsOwner) {
                    mClMemberSetting!!.visibility = View.GONE
                } else if (selfIsAdmin) { // 自己是管理，对方是普通用户
                    mClMemberSetting!!.visibility = View.VISIBLE
                    // 可以踢人
                    mLlKickRoom!!.visibility = View.VISIBLE
                    // 可以上下麦
                    if (memberIsOnSeat) {
                        mLlKickSeat!!.visibility = View.VISIBLE
                    } else {
                        mLlInvitedSeat!!.visibility = View.VISIBLE
                    }
                } else {
                    // 自己是普通用户不能操作
                    mClMemberSetting!!.visibility = View.GONE
                }
        }
    }

    private fun refreshFollow(isFollow: Boolean) {
        if (isFollow) {
            mBtnFollow!!.text = "已关注"
        } else {
            mBtnFollow!!.text = "关注"
        }
    }

    private fun refreshSettingAdmin(memberIsAdmin: Boolean) {
        when (mRoomOwnerType) {
            RoomOwnerType.VOICE_OWNER -> {
                mRlSettingAdmin!!.visibility = View.VISIBLE
                if (memberIsAdmin) {
                    mTvSettingAdmin!!.text = "撤回管理"
                    mTvSettingAdmin!!.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_member_setting_is_admin,
                        0,
                        0,
                        0
                    )
                } else {
                    mTvSettingAdmin!!.text = "设为管理"
                    mTvSettingAdmin!!.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_member_setting_not_admin,
                        0,
                        0,
                        0
                    )
                }
            }
            else -> mRlSettingAdmin!!.visibility = View.GONE
        }
    }

    private fun setSeatPosition(memberIsOnSeat: Boolean) {
        if (memberIsOnSeat) {
            mTvSeatPosition!!.visibility = View.VISIBLE
            // TODO：这里需要麦位位置
            mTvSeatPosition!!.text = String.format("%s 号麦位", seatPositions)
        } else {
            mTvSeatPosition!!.visibility = View.GONE
        }
    }

    /**
     * 关注
     */
    private fun follow() {
        val isFollow = !member!!.isFollow
        scopeNet{
            val follow = follow(member!!.userId)
        }
        //todo 222
//        OkApi.get(VRApi.followUrl(member.getUserId()), null, new WrapperCallBack() {
//            @Override
//            public void onResult(Wrapper result) {
//                if (result.ok()) {
//                    if (isFollow) {
//                        ToastUtils.s(getContext(), "关注成功");
//                        if (mOnMemberSettingClickListener != null) {
//                            RCFollowMsg followMsg = new RCFollowMsg();
//                            followMsg.setUser(UserManager.get());
//                            followMsg.setTargetUser(member.toUser());
//                            mOnMemberSettingClickListener.clickFollow(true, followMsg);
//                        }
//                    } else {
//                        ToastUtils.s(getContext(), "取消关注成功");
//                        if (mOnMemberSettingClickListener != null) {
//                            mOnMemberSettingClickListener.clickFollow(false, null);
//                        }
//                    }
//                    member.setStatus(isFollow ? 1 : 0);
//                    refreshFollow(isFollow);
//                    dismiss();
//                } else {
//                    if (isFollow) {
//                        ToastUtils.s(getContext(), "关注失败");
//                    } else {
//                        ToastUtils.s(getContext(), "取消关注失败");
//                    }
//                }
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//                if (isFollow) {
//                    ToastUtils.s(getContext(), "关注失败");
//                } else {
//                    ToastUtils.s(getContext(), "取消关注失败");
//                }
//            }
//        });
    }

    interface OnMemberSettingClickListener : SeatActionClickListener {
        /**
         * 设置管理员
         *
         * @param user
         * @param callback
         */
        fun clickSettingAdmin(user: User?, callback: ClickCallback<Boolean>?)

        /**
         * 踢出房间
         *
         * @param user
         * @param callback
         */
        fun clickKickRoom(user: User?, callback: ClickCallback<Boolean>?)

        /**
         * 发送礼物
         *
         * @param user
         */
        fun clickSendGift(user: User?)

        /**
         * 关注
         *
         * @param followMsg
         */
        fun clickFollow(isFollow: Boolean, followMsg: RCFollowMsg?)
    }

    companion object {
        private val TAG = MemberSettingFragment::class.java.simpleName
    }
}