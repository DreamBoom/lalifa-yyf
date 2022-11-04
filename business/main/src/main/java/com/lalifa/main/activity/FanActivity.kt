package com.lalifa.main.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.main.R
import com.lalifa.main.databinding.ActivityFanBinding
import com.lalifa.main.fragment.MoneyOfGetFragment
import com.lalifa.main.fragment.MoneyOfOutFragment
import com.lalifa.main.fragment.MyFanFragment
import com.lalifa.main.fragment.MyGzFragment

class FanActivity : BaseActivity<ActivityFanBinding>() {
    override fun getViewBinding() = ActivityFanBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("我的关注", "我的粉丝")
            ) {
                add(MyFanFragment())
                add(MyGzFragment())
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