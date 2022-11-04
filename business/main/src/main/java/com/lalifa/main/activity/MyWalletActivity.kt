package com.lalifa.main.activity

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.getIntentString
import com.lalifa.extension.pageChangedListener
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.databinding.ActivityMyMoneyBinding
import com.lalifa.main.fragment.*

class MyWalletActivity : BaseTitleActivity<ActivityMyMoneyBinding>() {
    override fun getViewBinding() = ActivityMyMoneyBinding.inflate(layoutInflater)
    override fun title() = "我的钱包"
    override fun rightText() = "钱包明细"
    override fun initView() {
        val drill = getIntentString("drill")
        val money = getIntentString("money")

        binding.apply {
            SXMoney.text = drill
            goldMoney.text = money
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("收入", "支出")
            ) {
                add(MoneyOfGetFragment())
                add(MoneyOfOutFragment())
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    this@MyWalletActivity,
                    R.color.textColor2
                )
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }

    override fun rightClick() {
        super.rightClick()
        start(MoneyInfoActivity::class.java)
    }
}