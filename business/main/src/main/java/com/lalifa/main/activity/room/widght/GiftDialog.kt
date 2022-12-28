package com.lalifa.main.activity.room.widght

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.net.utils.scopeNetLife
import com.flyco.tablayout.SlidingTabLayout
import com.lalifa.base.BaseFragment
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.lalifa.extension.toast
import com.lalifa.main.R
import com.lalifa.main.activity.room.message.AllBroadcastManager
import com.lalifa.main.activity.room.message.RCAllBroadcastMessage
import com.lalifa.main.activity.room.message.RCChatroomGiftAll
import com.lalifa.main.activity.room.ext.Member
import com.lalifa.main.api.RoomGift
import com.lalifa.main.api.RoomGiftBean
import com.lalifa.main.api.sendRoomGift
import com.lalifa.main.databinding.FragmentListBinding
import com.lalifa.main.fragment.adapter.roomGiftAdapter
import com.lalifa.main.fragment.adapter.seatGiftAdapter
import com.lalifa.utils.UIKit
import io.rong.imlib.model.MessageContent


/**
 * 直播间礼物弹框
 */
class GiftDialog(
    val act: AppCompatActivity,
    val roomId: String,
    val isPrivate: Boolean,
    val roomGiftBean: RoomGiftBean,
    val list: ArrayList<Member>,
    var onSendGiftListener: OnSendGiftListener
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val bundle = arguments
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
        this.mOnSendGiftListener = onSendGiftListener
    }

    override fun onStart() {
        super.onStart()

        initWindow()
    }

    /**
     * 获取手机屏幕宽度
     */
    fun getW(): Int {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            act.windowManager.currentWindowMetrics.bounds.width()
        } else {
            val dm = DisplayMetrics()
            act.windowManager.defaultDisplay.getRealMetrics(dm)
            dm.widthPixels
        }

    }

    private fun initWindow() {
        //初始化window相关表现
        val window = dialog?.window
        //设备背景为透明（默认白色）
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // window?.attributes?.width = matchp
        //window?.attributes?.height = 350
        val attributes = window!!.attributes
        attributes.gravity = Gravity.BOTTOM //下方
        attributes.width = getW() //满屏
        attributes.height = 1200
        window.attributes = attributes
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //加载布局
        val view = inflater.inflate(R.layout.pop_send_gift, container)
        initView(view)
        return view
    }

    //初始化View
    @SuppressLint("NotifyDataSetChanged")
    private fun initView(view: View) {
        view.findViewById<ImageView>(R.id.popClose).onClick {
            dismiss()
        }
        val etNum = view.findViewById<EditText>(R.id.etNum)
        view.findViewById<TextView>(R.id.btnBuy).onClick {
            if (userId == "") {
                requireContext().toast("请选择赠送用户")
                return@onClick
            }
            if (giftId == "") {
                requireContext().toast("请选择赠送礼物")
                return@onClick
            }
            if (etNum.text() == "") {
                requireContext().toast("请选择赠送数量")
                return@onClick
            }
            giftNum = etNum.text()
            scopeNetLife {
                val sendRoomGift = sendRoomGift(
                    type, userId, roomId,
                    etNum.text(), giftId, userType
                )
                var finalIsAll = false
                if (null != sendRoomGift) {
                    if (userId.contains(",")) {
                        // 全选只发一条全麦打赏的广播
                        finalIsAll = true
                       // sendGiftBroadcast(true)
                    } else {
                        finalIsAll = false
                      //  sendGiftBroadcast(false)
                    }
                    Thread {
                        try {
                            UIKit.runOnUiThread {
                                sendGiftMessage(finalIsAll)
                            }
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            }

        }

        val peopleList = view.findViewById<RecyclerView>(R.id.peopleList)
        val tabLayout = view.findViewById<SlidingTabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.map_viewPager)
        val qm = view.findViewById<ConstraintLayout>(R.id.qm)
        qm.setOnClickListener {
            val model = peopleList.bindingAdapter._data as ArrayList<Member>
            if (userType == "0") {
                userType = "1"
                model.forEach { it.select = false }
                peopleList.bindingAdapter.notifyDataSetChanged()
            } else {
                userType = "0"
                for (i in 0 until model.size) {
                    model[i].select = true
                    userId += if (i != model.size - 1) {
                        model[i].userId + ","
                    } else {
                        model[i].userId
                    }
                }
                peopleList.bindingAdapter.notifyDataSetChanged()
            }
        }
        peopleList.seatGiftAdapter().apply {
            R.id.header.onClick {
                userType = "0"
                val model = peopleList.bindingAdapter._data as ArrayList<Member>
                getModel<Member>(0).select = false
                model.forEach { it.select = false }
                getModel<Member>().select = true
                userId = getModel<Member>().userId
                userName = getModel<Member>().userName
                notifyDataSetChanged()
            }
        }.models = list
        viewPager!!.fragmentAdapter(
            childFragmentManager,
            arrayListOf("普通", "盲盒", "特权", "背包")
        ) {
            add(GiftFragment(1, roomGiftBean.gift))
            add(GiftFragment(2, roomGiftBean.blind_box))
            add(GiftFragment(3, roomGiftBean.gift_bag))
            add(GiftFragment(4, roomGiftBean.knapsack))
        }
        tabLayout!!.setViewPager(viewPager)

    }

//    private fun sendGiftBroadcast(isAll: Boolean) {
//        val message = RCAllBroadcastMessage()
//        message.userId = UserManager.get()!!.userId
//        message.userName = UserManager.get()!!.userName
//        if (!isAll) {
//            message.targetId = userId
//            message.targetName = userName
//        } else {
//            message.targetId = ""
//            message.targetName = ""
//        }
//        message.giftCount = giftNum
//        message.giftId = giftId
//        message.giftPath = giftPath
//        message.giftValue = giftValue
//        message.giftName = giftName
//        message.roomId = roomId
//        message.roomType = ""
//        message.isPrivate = "$isPrivate"
//        AllBroadcastManager.getInstance().addMessage(message)
//    }

    /**
     * 礼物发送成功后发送消息
     *
     * @param members
     * @param isAll
     */
    private fun sendGiftMessage(isAll: Boolean) {
        var messages: MessageContent? = null
        val all = RCChatroomGiftAll()
        all.userId = UserManager.get()!!.userId
        all.userName = UserManager.get()!!.userName
        all.giftId = giftId
        all.giftName = giftName
        all.giftPath = giftPath
        if (isAll) {
            all.targetId = ""
            all.targetName = ""
        } else {
            all.targetId = userId
            all.targetName = userName
        }
        all.number = giftNum.toInt()
        all.price = giftValue.toDouble()
        messages = all
        // 回调回去结果
        mOnSendGiftListener?.onSendGiftSuccess(messages)
        dismiss()
    }

    private var mOnSendGiftListener: OnSendGiftListener? = null

    interface OnSendGiftListener {
        fun onSendGiftSuccess(messages: MessageContent?)
    }

    companion object {
        fun newInstance(
            activity: AppCompatActivity,
            roomId: String,
            isPrivate: Boolean,
            roomGiftBean: RoomGiftBean,
            list: ArrayList<Member>,
            onSendGiftListener: OnSendGiftListener
        ): GiftDialog {
            val customDialogFragment = GiftDialog(
                activity, roomId, isPrivate, roomGiftBean, list,
                onSendGiftListener
            )
//            val bundle = Bundle()
//            bundle.putString("content", content)
//            customDialogFragment.arguments = bundle
            return customDialogFragment
        }

        var giftId = ""
        var giftName = ""
        var giftValue = ""
        var giftPath = ""
        var type = "1"
        var userType = "0"
        var userId = ""
        var userName = ""
        var giftNum = ""
    }

    class GiftFragment(val inType: Int, val list: List<RoomGift>) :
        BaseFragment<FragmentListBinding>() {
        override fun initView() {
            binding.diaList.roomGiftAdapter().apply {
                R.id.sendGift.onClick {
                    type = if (inType == 4) "2" else "1"
                    list.forEach { it.choose = false }
                    list[layoutPosition].choose = true
                    giftId = list[layoutPosition].id.toString()
                    giftName = list[layoutPosition].name
                    giftValue = list[layoutPosition].price
                    giftPath = list[layoutPosition].effect_image
                    notifyDataSetChanged()
                }
            }
            binding.diaList.models = list
        }

        override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
        ) = FragmentListBinding.inflate(layoutInflater)
    }
}