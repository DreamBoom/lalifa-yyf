package com.lalifa.main.activity.me

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseListFragment
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.fanList
import com.lalifa.main.api.friends
import com.lalifa.main.databinding.ActivityFanBinding

class FanActivity : BaseActivity<ActivityFanBinding>() {
    override fun getViewBinding() = ActivityFanBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("我的关注", "我的粉丝")
            ) {
                add(FanFrag(1))
                add(FanFrag(2))
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    this@FanActivity,
                    R.color.textColor2
                )
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }

    override fun onClick() {
        super.onClick()
        binding.back.onClick { finish() }
    }
}

class FanFrag(val type:Int) : BaseListFragment() {
    override fun initView() {
        super.initView()
        binding.recyclerView.fanList()
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            val dynamic = friends(type)
            addData(dynamic)
        }
    }
}