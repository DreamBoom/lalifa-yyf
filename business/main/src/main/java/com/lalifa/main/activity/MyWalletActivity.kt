package com.lalifa.main.activity

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListFragment
import com.lalifa.base.BaseTitleActivity
import com.lalifa.che.api.cheList
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.adapter.moneyList
import com.lalifa.main.api.wallet
import com.lalifa.main.databinding.ActivityMyMoneyBinding

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
                add(MoneyFragment(0))
                add(MoneyFragment(1))
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

    override fun onClick() {
        super.onClick()
        binding.apply {
            dh.onClick {

            }
        }
    }
}


class MoneyFragment(val type: Int) : BaseListFragment() {
    override fun initView() {
        super.initView()
        binding.recyclerView.moneyList().apply {

        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            val dynamic = wallet(type.toString(), index.toString())
            addData(dynamic!!.record)
        }
    }
}