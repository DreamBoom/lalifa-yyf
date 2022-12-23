package com.lalifa.main.activity.room.widght

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
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.flyco.tablayout.SlidingTabLayout
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Account
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.lalifa.extension.toast
import com.lalifa.main.R
import com.lalifa.main.api.RoomGift
import com.lalifa.main.api.RoomGiftBean
import com.lalifa.main.api.sendRoomGift
import com.lalifa.main.databinding.FragmentListBinding
import com.lalifa.main.fragment.adapter.roomGiftAdapter
import com.lalifa.main.fragment.adapter.seatGiftAdapter
import okhttp3.internal.notifyAll


/**
 * 直播间礼物弹框
 */
class GiftDialog(
    val act: AppCompatActivity,val roomId: String, val roomGiftBean: RoomGiftBean, var list: ArrayList<Account>
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val bundle = arguments
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
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
        attributes.height = 500
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
    private fun initView(view: View) {
        view.findViewById<ImageView>(R.id.popClose).onClick {
            dismiss()
        }
        val etNum = view.findViewById<EditText>(R.id.etNum)
        view.findViewById<TextView>(R.id.btnBuy).onClick {
            if(userId == ""){
                requireContext().toast("请选择赠送用户")
                return@onClick
            }
            if(giftId == ""){
                requireContext().toast("请选择赠送礼物")
                return@onClick
            }
            if(etNum.text()==""){
                requireContext().toast("请选择赠送数量")
                return@onClick
            }
            scopeNetLife {
                val sendRoomGift = sendRoomGift(
                    type, userId, roomId,
                    etNum.text(), giftId, userType
                )
                if(null!=sendRoomGift){
                    dismiss()
                }
            }

        }

        val peopleList = view.findViewById<RecyclerView>(R.id.peopleList)
        val tabLayout = view.findViewById<SlidingTabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.map_viewPager)
        list.add(0, Account())
        peopleList.seatGiftAdapter().apply {
            R.id.header.onClick {
                if (layoutPosition == 0) {
                    LogCat.e(list.toString())
                    val model = peopleList.bindingAdapter._data as ArrayList<Account>
                    if (getModel<Account>().select) {
                        userType = "0"
                        model.forEach { it.select = false }
                        notifyDataSetChanged()
                        userId = ""
                    } else {
                        //用户组添加 userId
                        userType = "1"
                        for(i in 0 until model.size){
                            model[i].select = true
                            if(i!=0){
                                userId += if( i !=model.size-1){
                                    model[i].userId+","
                                }else{
                                    model[i].userId
                                }
                            }
                        }
                        notifyDataSetChanged()
                    }
                } else {
                    userType = "0"
                    val model = peopleList.bindingAdapter._data as ArrayList<Account>
                    getModel<Account>(0).select = false
                    model.forEach { it.select = false }
                    getModel<Account>().select = true
                    userId = getModel<Account>().userId
                    notifyDataSetChanged()
                }

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

    companion object {
        fun newInstance(
            activity: AppCompatActivity,
            roomId: String,
            roomGiftBean: RoomGiftBean,
            list: ArrayList<Account>
        ): GiftDialog {
            val customDialogFragment = GiftDialog(activity, roomId,roomGiftBean, list)
//            val bundle = Bundle()
//            bundle.putString("content", content)
//            customDialogFragment.arguments = bundle
            return customDialogFragment
        }

        var giftId = ""
        var type = "1"
        var userType = "0"
        var userId = ""
    }

    class GiftFragment(val inType: Int, val list: List<RoomGift>) :
        BaseFragment<FragmentListBinding>() {
        override fun initView() {
            binding.list.roomGiftAdapter().apply {
                R.id.sendGift.onClick {
                    type = if (inType == 4) "2" else "1"
                    list.forEach { it.choose = false }
                    list[layoutPosition].choose = true
                    giftId = list[layoutPosition].id.toString()
                    notifyDataSetChanged()
                }
            }
            binding.list.models = list
        }

        override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
        ) = FragmentListBinding.inflate(layoutInflater)
    }
}