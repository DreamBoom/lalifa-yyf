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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.logcat.LogCat
import com.flyco.tablayout.SlidingTabLayout
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Account
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.main.R
import com.lalifa.main.api.RoomGift
import com.lalifa.main.api.RoomGiftBean
import com.lalifa.main.databinding.FragmentListBinding
import com.lalifa.main.fragment.adapter.roomGiftAdapter
import com.lalifa.main.fragment.adapter.seatGiftAdapter
import okhttp3.internal.notifyAll


/**
 * 直播间礼物弹框
 */
class GiftDialog(val act: AppCompatActivity,val roomGiftBean: RoomGiftBean
    ,var list:ArrayList<Account>) : DialogFragment() {
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
        }else{
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
        attributes.height =  1100
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
    var apply:BindingAdapter?=null
    private fun initView(view: View) {
        view.findViewById<ImageView>(R.id.popClose).onClick {
            dismiss()
        }

        val peopleList = view.findViewById<RecyclerView>(R.id.peopleList)
        val tabLayout = view.findViewById<SlidingTabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.map_viewPager)
        list.add(0, Account())
         apply = peopleList.seatGiftAdapter().apply {
            R.id.header.onClick {
                if (layoutPosition == 0) {
                    LogCat.e(list.toString())
                    if (list[0].select) {
                        list.forEach { it.select = false }
                    } else {
                        list.forEach { it.select = true }
                    }
                } else {
                    if (list[layoutPosition].select) {
                        list[0].select = false
                    }
                    list[layoutPosition].select = !list[layoutPosition].select
                }
                apply!!.models = list
            }
        }
        apply!!.models = list
        viewPager!!.fragmentAdapter(
            childFragmentManager,
            arrayListOf("普通", "盲盒", "特权", "背包")
        ) {
            add(GiftFragment(1,roomGiftBean.gift))
            add(GiftFragment(2,roomGiftBean.blind_box))
            add(GiftFragment(3,roomGiftBean.gift_bag))
            add(GiftFragment(4,roomGiftBean.knapsack))
        }
        tabLayout!!.setViewPager(viewPager)

    }

    companion object {
        fun newInstance(activity: AppCompatActivity,roomGiftBean: RoomGiftBean,list:ArrayList<Account>): GiftDialog {
            val customDialogFragment = GiftDialog(activity,roomGiftBean,list)
//            val bundle = Bundle()
//            bundle.putString("content", content)
//            customDialogFragment.arguments = bundle
            return customDialogFragment
        }
    }

    class GiftFragment(val type:Int,val list: List<RoomGift>) : BaseFragment<FragmentListBinding>() {
        override fun initView() {
            binding.list.roomGiftAdapter().apply {
                R.id.sendGift.onClick {
                    //发送礼物
                }
            }
            binding.list.models = list
        }

        override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
        )=FragmentListBinding.inflate(layoutInflater)
    }
}