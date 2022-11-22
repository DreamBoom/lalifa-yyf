package com.lalifa.main.activity.me

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.content.ContextCompat
import cn.rongcloud.config.UserManager
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseListFragment
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.che.CheInfoActivity
import com.lalifa.main.activity.me.EditMyInfoActivity
import com.lalifa.main.adapter.cheMyList
import com.lalifa.main.api.Dynamic
import com.lalifa.main.api.dzChe
import com.lalifa.main.api.release
import com.lalifa.main.databinding.ActivityMeInfoBinding
import com.lalifa.main.fragment.MeTab1Fragment

class MeInfoActivity : BaseActivity<ActivityMeInfoBinding>() {
    override fun getViewBinding() = ActivityMeInfoBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            header.load(Config.FILE_PATH+ UserManager.get()!!.avatar)
            name.text = UserManager.get()!!.userName

            if(UserManager.get()!!.gender==0){
                sex.setImageResource( com.lalifa.base.R.drawable.ic_icon_boy)
            }else{
                sex.setImageResource( com.lalifa.base.R.drawable.ic_icon_gril)
            }
            mId.text = "ID:${UserManager.get()!!.id}"
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("关于TA", "动态", "守护神", "礼物")
            ) {
                add(MeTab1Fragment())
                add(CheFrag())
                add(MeTab1Fragment())
                add(MeTab1Fragment())
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

class CheFrag() : BaseListFragment() {
    override fun initView() {
        super.initView()
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
        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            val dynamic = release(index)!!.dynamic
            addData(dynamic)
        }
    }
}