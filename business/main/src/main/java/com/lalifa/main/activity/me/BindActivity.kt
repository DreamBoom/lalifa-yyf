package com.lalifa.main.activity.me

import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.getIntentString
import com.lalifa.main.databinding.ActivityBindBinding

class BindActivity : BaseTitleActivity<ActivityBindBinding>() {
    override fun getViewBinding() = ActivityBindBinding.inflate(layoutInflater)
    override fun initView() {
        val type = getIntentInt("type", 0)
        setTitle(if (type == 0) "绑定支付宝" else "绑定微信")

    }

}