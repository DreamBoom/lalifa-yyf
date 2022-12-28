package com.lalifa.main.activity.me

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseFragment
import com.lalifa.base.BaseListFragment
import com.lalifa.ext.Config
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.che.CheInfoActivity
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityGiftBinding
import com.lalifa.main.databinding.ActivityMeInfoBinding
import com.lalifa.main.databinding.ActivityWardBinding
import com.lalifa.main.databinding.MeTab1FragmentBinding
import com.lalifa.main.ext.MUtils
import com.lalifa.main.fragment.adapter.*

class MeInfoActivity : BaseActivity<ActivityMeInfoBinding>() {
    override fun getViewBinding() = ActivityMeInfoBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            header.load(Config.FILE_PATH + UserManager.get()!!.avatar)
            MUtils.loadSvg(binding.svg, UserManager.get()!!.frame!!){

            }
            name.text = UserManager.get()!!.userName
            if (UserManager.get()!!.gender == 0) {
                sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
            } else {
                sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
            }
            mId.text = "ID:${UserManager.get()!!.id}"
            scopeNetLife {
                val bean = homepage(UserManager.get()!!.id.toString())
                if (null != bean) {
                    bgTop.load(Config.FILE_PATH + bean.background)
                    viewPager.fragmentAdapter(
                        supportFragmentManager, arrayListOf("关于TA", "动态", "守护神", "礼物")
                    ) {
                        add(
                            MeTab1Fragment(
                                bean.room_record,
                                bean.bio,
                                bean.medal
                            )
                        )
                        add(CheFrag(bean.dynamic))
                        add(GuardFrag(bean.patron_saint))
                        add(GiftFrag(bean.theme, bean.gift_count))
                    }.pageChangedListener {
                        tabLayout.indicatorColor = Color.parseColor("#FF9D48")
                        tabLayout.textSelectColor =
                            ContextCompat.getColor(this@MeInfoActivity, R.color.textColor2)
                        tabLayout.textUnselectColor = Color.WHITE
                    }
                    tabLayout.setViewPager(viewPager)
                    tabLayout.currentTab = 0
                }
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }
            bgEdit.onClick {
                start(EditMyInfoActivity::class.java)
            }
            bgTop.onClick {
                imagePick(maxCount = 1) {
                    bgTop.load(it[0].path)
                }
            }
        }
    }
}

class MeTab1Fragment(val room: List<MeRoom>, val string: String, val xz: List<MeXz>) :
    BaseFragment<MeTab1FragmentBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = MeTab1FragmentBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            list1.meRoomAdapter().models = room
            bio.text = string.pk("本宝宝还没想好签名")
            list2.meXzAdapter().models = xz
        }
    }

}

class CheFrag(val dynamic: List<Dynamic>) : BaseListFragment() {
    override fun initView() {
        super.initView()
        binding.refreshLayout.setEnableRefresh(false)
        binding.refreshLayout.setEnableLoadMore(false)
        binding.recyclerView.cheMyList().apply {
            R.id.item_info.onClick {
                start(CheInfoActivity::class.java) {
                    putExtra("id", getModel<Dynamic>().id)
                }
            }
            R.id.more.onClick {
                val bean = getModel<Dynamic>()
            }
            R.id.like.onClick {
                val bean = getModel<Dynamic>()
                scopeNetLife {
                    dzChe(bean.id)
                    binding.refreshLayout.refresh()
                }
            }
            R.id.share.onClick {
                val bean = getModel<Dynamic>()

            }
            R.id.pl.onClick {
                start(CheInfoActivity::class.java) {
                    putExtra("id", getModel<Dynamic>().id)
                }
            }
        }.models = dynamic
    }
}

class GuardFrag(val guard: List<GuardBean>) : BaseFragment<ActivityWardBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityWardBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            binding.apply {
                guardHeader.load(Config.FILE_PATH + guard[0].avatar)
                guardName.text = guard[0].userName
                header.load(Config.FILE_PATH + UserManager.get()!!.avatar)
                name.text = UserManager.get()!!.userName
                if (guard.size > 1) {
                    val subList = guard.subList(1, guard.size - 1)
                    guardList.guardAdapter().models = subList
                }
            }
        }
    }
}

class GiftFrag(val list: List<Theme>, val giftNum: Int) : BaseFragment<ActivityGiftBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityGiftBinding.inflate(layoutInflater)

    var giftAdapter: BindingAdapter? = null
    override fun initView() {
        binding.apply {
            UserManager.get().apply {
                header.load(Config.FILE_PATH + this?.avatar)
                name.text = this?.userName
            }
            num.text = "已收集$giftNum 件礼物"
            giftGroup.giftGroupAdapter().apply {
                R.id.groupName.onClick {
                    giftAdapter!!.models = getModel<Theme>().gift
                }
            }.models = list
            giftAdapter = giftList.giftAdapter()
            giftAdapter!!.models = list[0].gift
        }
    }

}