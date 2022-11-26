package com.lalifa.main.activity.me

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityBindPhoneBinding

class BindPhoneActivity : BaseTitleActivity<ActivityBindPhoneBinding>() {
    override fun title()="绑定手机号"
    override fun getViewBinding()= ActivityBindPhoneBinding.inflate(layoutInflater)

    override fun initView() {

    }

}