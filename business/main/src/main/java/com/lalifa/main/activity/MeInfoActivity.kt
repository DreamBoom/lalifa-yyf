package com.lalifa.main.activity

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseActivity
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.databinding.ActivityMeInfoBinding
import com.lalifa.main.fragment.MeTab1Fragment

class MeInfoActivity : BaseActivity<ActivityMeInfoBinding>() {
    override fun getViewBinding() = ActivityMeInfoBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("关于TA", "动态", "守护神", "礼物")
            ) {
                add(MeTab1Fragment())
                add(MeTab1Fragment())
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
                imagePick(maxCount = 1) {
                    bgTop.load(it[0].path)
                }
            }
        }
    }

}