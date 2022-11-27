package com.lalifa.main.activity.me

import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.callPhone
import com.lalifa.extension.onClick
import com.lalifa.main.databinding.ActivityCallUsBinding

class CallUs : BaseTitleActivity<ActivityCallUsBinding>() {
    override fun title()="专属客服"
    override fun getViewBinding()= ActivityCallUsBinding.inflate(layoutInflater)
    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.callUs.onClick {
            callPhone("11111111111")
        }
    }
}