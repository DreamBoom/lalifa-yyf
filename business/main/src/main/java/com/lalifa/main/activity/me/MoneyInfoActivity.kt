package com.lalifa.main.activity.me

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityMoneyInfoBinding

class MoneyInfoActivity : BaseTitleActivity<ActivityMoneyInfoBinding>() {
    override fun title()= "钱包明细"
    override fun getViewBinding() = ActivityMoneyInfoBinding.inflate(layoutInflater)

    override fun initView() {

    }

}