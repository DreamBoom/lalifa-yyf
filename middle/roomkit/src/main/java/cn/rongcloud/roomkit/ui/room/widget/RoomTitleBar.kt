package cn.rongcloud.roomkit.ui.room.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.collection
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.message.RCFollowMsg
import cn.rongcloud.roomkit.ui.RoomOwnerType
import cn.rongcloud.roomkit.ui.room.model.Member
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.jakewharton.rxbinding4.view.clicks
import com.lalifa.extension.toast
import com.lalifa.utils.ImageLoader
import com.lalifa.utils.UiUtils
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * @author gyn
 * @date 2021/9/17
 */
class RoomTitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {
    private val mRootView: View
    private var mNameTextView: TextView? = null
    private var mIDTextView: TextView? = null
    private var mOnlineTextView: TextView? = null
    private var mDelayTextView: TextView? = null
    private var mMenuButton: ImageButton? = null
    private var mLeftView: View? = null
    private var mFollowTextView: TextView? = null
    private var member: Member? = null
    private var onFollowClickListener: OnFollowClickListener? = null
    private var isShowFollow = false
    private var mDelay = 0
    private var mCreaterImageview: CircleImageView? = null
    private var roomOwnerType: RoomOwnerType? = null
    private var tvRoomOnlineCount: TextView? = null
    private var mTvSwitchGame: TextView? = null
    private var mBtnNotice: ImageButton? = null
    private var roomId: String = ""
    private var isFollow: Boolean = false
    private var user:User?=null
    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_room_title_bar, this)
        initView()
    }

    private fun initView() {
        mNameTextView = mRootView.findViewById(R.id.tv_room_name)
        mLeftView = mRootView.findViewById(R.id.cl_left)
        mIDTextView = mRootView.findViewById(R.id.tv_room_id)
        mOnlineTextView = mRootView.findViewById(R.id.tv_room_online)
        mDelayTextView = mRootView.findViewById(R.id.tv_room_delay)
        mMenuButton = mRootView.findViewById(R.id.btn_menu)
        mFollowTextView = mRootView.findViewById(R.id.tv_follow)
        mCreaterImageview = mRootView.findViewById(R.id.iv_creater_id)
        tvRoomOnlineCount = mRootView.findViewById(R.id.tv_room_online_count)
        mFollowTextView!!.setOnClickListener { v: View? -> follow() }
        mTvSwitchGame = mRootView.findViewById(R.id.tv_switch_game)
        mBtnNotice = mRootView.findViewById(R.id.btn_notice)
    }

    fun setOnLineMemberClickListener(): Observable<*> {
        return tvRoomOnlineCount!!.clicks().throttleFirst(1, TimeUnit.SECONDS)
    }

    fun setOnMemberClickListener(): Observable<*> {
        return mLeftView!!.clicks().throttleFirst(1, TimeUnit.SECONDS)
    }

    fun setOnMenuClickListener(): Observable<*> {
        return mMenuButton!!.clicks().throttleFirst(1, TimeUnit.SECONDS)
    }

    fun setOnNoticeClickListener(): Observable<*> {
        return mBtnNotice!!.clicks().throttleFirst(1, TimeUnit.SECONDS)
    }

    fun setOnSwitchClickListener(): Observable<*> {
        return mTvSwitchGame!!.clicks().throttleFirst(1, TimeUnit.SECONDS)
    }

    fun setData(
        user: User,
        isFollow: Boolean,
        roomOwnerType: RoomOwnerType?,
        name: String?,
        id: Int,
        roomUserId: String?,
        onFollowClickListener: OnFollowClickListener?
    ) {
        this.user = user
        this.isFollow = isFollow
        this.onFollowClickListener = onFollowClickListener
        this.roomOwnerType = roomOwnerType
        this.roomId = id.toString()
        setViewState()
        setRoomName(name)
        setRoomId(id)
        getFollowStatus(roomUserId!!)
        setFollow(isFollow)
    }


    fun setCreatorName(creatorName: String?) {
        mNameTextView!!.text = creatorName
    }

    fun setCreatorPortrait(portrait: String?) {
        ImageLoader.loadUrl(mCreaterImageview!!, portrait, R.drawable.default_portrait)
    }

    fun setRoomId(id: Int) {
        mIDTextView!!.text = String.format("ID %s", id)
    }

    fun setRoomName(name: String?) {
        mNameTextView!!.text = name
    }

    fun setOnlineNum(num: Int) {
        mOnlineTextView!!.text = String.format("在线 %s", num)
        tvRoomOnlineCount!!.text = num.toString() + ""
    }

//        /**
//         * 是否在麦位上
//         */
//        public void setIsLinkSeat(boolean isLinkSeat){
//            if (roomOwnerType!=null&&roomOwnerType.equals(RoomOwnerType.LIVE_VIEWER)) {
//                if (isLinkSeat){
//                    mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_live_room));
//                }else {
//                    mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
//                }
//            }
//        }
    fun setDelay(delay: Int) {
        setDelay(delay, true)
    }

    private fun setDelay(delay: Int, isShow: Boolean) {
        if (isShow) {
            if (mDelay == delay) {
                return
            }
            mDelay = delay
            mDelayTextView!!.visibility = VISIBLE
            mDelayTextView!!.text = delay.toString() + "ms"
            val leftPicId: Int
            leftPicId = if (delay < 100) {
                R.drawable.ic_room_delay_1
            } else if (delay < 299) {
                R.drawable.ic_room_delay_2
            } else {
                R.drawable.ic_room_delay_3
            }
            mDelayTextView!!.setCompoundDrawablesRelativeWithIntrinsicBounds(leftPicId, 0, 0, 0)
        } else {
            mDelayTextView!!.visibility = GONE
        }
    }

    private fun getFollowStatus(roomUserId: String) {
        if (TextUtils.equals(roomUserId, UserManager.get()!!.userId)) {
            isShowFollow = false
            setFollow(false)
            return
        }
        isShowFollow = true
    }

    fun setFollow(isFollow: Boolean) {
        if (isShowFollow) {
            mFollowTextView!!.visibility = VISIBLE
            mLeftView!!.setPadding(0, 0, UiUtils.dp2px(6f), 0)
            if (isFollow) {
                mFollowTextView!!.text = "已收藏"
                mFollowTextView!!.setBackgroundResource(R.drawable.btn_titlebar_followed)
            } else {
                mFollowTextView!!.text = "收藏"
                mFollowTextView!!.setBackgroundResource(R.drawable.bg_voice_room_send_button)
            }
        } else {
            mFollowTextView!!.visibility = GONE
            mLeftView!!.setPadding(0, 0, UiUtils.dp2px(20f), 0)
        }
    }



    /**
     * 关注
     */
    private fun follow() {
        scopeNetLife {
            val collection = collection(roomId)
            if (null != collection) {
                setFollow(!isFollow)
                if (!isFollow) {
                    isFollow = true
                  //  context.toast("关注成功")
//                    if (onFollowClickListener != null) {
//                        val followMsg =  RCFollowMsg()
//                        followMsg.user = UserManager.get()
//                        followMsg.targetUser = user!!.toUser()
//                        LogCat.e("followMsg===="+followMsg.targetUser.toString())
//                        onFollowClickListener!!.clickFollow(true, followMsg)
//                    }
                } else {
                    isFollow = false
                  //  context.toast("取消关注成功")
//                    if (onFollowClickListener != null) {
//                        onFollowClickListener!!.clickFollow(false, null)
//                    }
                }
            } else {
                if (isFollow) {
                    context.toast("关注失败")
                } else {
                    context.toast("取消关注失败")
                }
            }
        }
    }

    /**
     * 设置不同房间的状态
     */
    fun setViewState() {
        val layoutParams = mNameTextView!!.layoutParams as LayoutParams
        //非直播房
        layoutParams.bottomToTop = mIDTextView!!.id
        mNameTextView!!.layoutParams = layoutParams
        mCreaterImageview!!.visibility = GONE
        mLeftView!!.setBackgroundResource(R.drawable.bg_room_title_left)
        mOnlineTextView!!.visibility = VISIBLE
        mIDTextView!!.visibility = VISIBLE
        tvRoomOnlineCount!!.visibility = GONE
        mMenuButton!!.setImageDrawable(resources.getDrawable(R.drawable.ic_menu))
        setmLeftViewMarginStart(0)
    }

    fun setSwitchGameVisible(visible: Boolean) {
        mTvSwitchGame!!.visibility = if (visible) VISIBLE else GONE
    }

    fun setSwitchGameName(name: String?) {
        mTvSwitchGame!!.text = name
    }

    /**
     * 设置距离右边的边距
     *
     * @param marginStart
     */
    private fun setmLeftViewMarginStart(marginStart: Int) {
        val layoutParams = mLeftView!!.layoutParams as LayoutParams
        layoutParams.marginStart = marginStart
        mLeftView!!.layoutParams = layoutParams
    }

    interface OnFollowClickListener {
        fun clickFollow(isFollow: Boolean, followMsg: RCFollowMsg?)
    }
}