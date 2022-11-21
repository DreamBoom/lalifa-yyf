package com.lalifa.main.activity.login

import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.main.databinding.ActivitySexBinding

import com.lalifa.yyf.ext.showTipDialog

/**
 * 选择性别界面
 */
class SexActivity : BaseActivity<ActivitySexBinding>() {
    override fun getViewBinding() = ActivitySexBinding.inflate(layoutInflater)
    private var mobile = ""
    private var code = ""
    private var password = ""
    private var confirmPassword = ""
    override fun initView() {
        mobile = getIntentString("mobile")
        code = getIntentString("code")
        password = getIntentString("password")
        confirmPassword = getIntentString("confirmPassword")
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }
            girl.onClick {
                showTipDialog(content = "性别一经确认无法修改哦~", cancelText = "我在想想") {
                    jump(1)
                }
            }
            boy.onClick {
                showTipDialog(content = "性别一经确认无法修改哦~", cancelText = "我在想想") {
                    jump(0)
                }
            }
        }
    }

    private fun jump(gender:Int){
        start(UserInfoActivity::class.java){
            putExtra("mobile",mobile)
            putExtra("code",code)
            putExtra("password",password)
            putExtra("confirmPassword",confirmPassword)
            putExtra("gender",gender)
        }
    }
}