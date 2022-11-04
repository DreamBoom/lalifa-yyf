package com.lalifa.main.activity

import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityCallUsBinding

class CallUs : BaseTitleActivity<ActivityCallUsBinding>() {
    override fun title()="专属客服"
    override fun getViewBinding()= ActivityCallUsBinding.inflate(layoutInflater)
    override fun initView() {

    }
}