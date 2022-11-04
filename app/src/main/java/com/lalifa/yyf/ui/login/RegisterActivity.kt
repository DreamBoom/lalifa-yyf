package com.lalifa.yyf.ui.login

import android.content.Intent
import android.text.TextUtils
import androidx.core.widget.addTextChangedListener
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.*
import com.lalifa.yyf.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    override fun title() = "注册"
    override fun getViewBinding() = ActivityRegisterBinding.inflate(layoutInflater)
    override fun initView() {
        binding.apply {
            etPhone.addTextChangedListener {s->
                val length = s.toString().length
                binding.etCode.setSendEnable(length == 11)
            }
            binding.etCode.setSendListener { code ->
                //        scopeNetLife { // 创建作用域
//            // 这个大括号内就属于作用域内部
//            val data =
//                Get<String>("qq.php/") {
//                    param("qq", "80548262")
//                }.await() // 发起GET请求并返回`String`类型数据
//        }
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick{finish()}
            next.onClick {
                val code = etCode.code
                if(etPhone.isEmp()){
                    toast("请输入手机号")
                    return@onClick
                }
                if(TextUtils.isEmpty(code)){
                    toast("请输入验证码")
                    return@onClick
                }
                if(etPass.isEmp()){
                    toast("请输入密码")
                    return@onClick
                }
                if(etPass1.isEmp()){
                    toast("请确认密码")
                    return@onClick
                }
                start(SexActivity::class.java){
                    putExtra("mobile",etPhone.text())
                    putExtra("code",code)
                    putExtra("password",etPass.text())
                    putExtra("confirmPassword",etPass1.text())
                }
            }
        }
    }
}