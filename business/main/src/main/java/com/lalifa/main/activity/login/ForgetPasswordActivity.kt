package com.lalifa.main.activity.login

import androidx.core.widget.addTextChangedListener
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.applyShowPassword
import com.lalifa.extension.onClick
import com.lalifa.main.databinding.ActivityForgetPasswordBinding

/**
 * 忘记密码
 */
class ForgetPasswordActivity : BaseTitleActivity<ActivityForgetPasswordBinding>() {
    override fun title() = "忘记密码"
    override fun getViewBinding() = ActivityForgetPasswordBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
//            passwordEt.addTextChangedListener {
//                sureBtn.isEnabled = it.toString().isNotEmpty()
//            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
//            showPasswordBtn.onClick {
//                it.isSelected = !it.isSelected
//                passwordEt.applyShowPassword(it.isSelected)
//            }
//            showPasswordAgainBtn.onClick {
//                it.isSelected = !it.isSelected
//                passwordAgainEt.applyShowPassword(it.isSelected)
//            }
        }
    }
}