package com.lalifa.main.activity.login

import android.annotation.SuppressLint
import com.drake.channel.sendTag
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityInputCodeBinding
import com.lalifa.widget.InputVerifyCodeView

class InputCodeActivity : BaseTitleActivity<ActivityInputCodeBinding>() {
    override fun getViewBinding() = ActivityInputCodeBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.codeView.setOnEndListener(object : InputVerifyCodeView.OnEndListener {
            override fun onEnd(text: String) {
                sendTag("RegisterSuccess")
                finish()
            }
        })
        val bundle = this.intent.extras
        binding.yzm.text = "输入我们发送至"+bundle?.get("phone").toString()+"的验证码"
    }
}