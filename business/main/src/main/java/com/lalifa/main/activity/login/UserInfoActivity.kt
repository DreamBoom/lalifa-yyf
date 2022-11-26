package com.lalifa.main.activity.login

import android.text.TextUtils
import android.util.Log
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.extension.*
import com.lalifa.main.api.ImgBean
import com.lalifa.main.api.register
import com.lalifa.main.api.upload
import com.lalifa.main.databinding.ActivityUserInfoBinding
import java.util.*

/**
 * 完善信息界面
 */
class UserInfoActivity : BaseActivity<ActivityUserInfoBinding>() {
    override fun getViewBinding() = ActivityUserInfoBinding.inflate(layoutInflater)
    private var mobile = ""
    private var code = ""
    private var password = ""
    private var confirmPassword = ""
    private var gender = 0
    private var photo = ""
    private var myAge = -1
    private var myBirthday = ""
    private var upload: ImgBean? = null
    override fun initView() {
        mobile = getIntentString("mobile")
        code = getIntentString("code")
        password = getIntentString("password")
        confirmPassword = getIntentString("confirmPassword")
        gender = getIntentInt("gender", 0)

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }

            image.onClick {
                imagePick(maxCount = 1) {
                    scopeNetLife {
                        upload = upload(it[0].path)
                        if (upload != null) {
                            runOnUiThread {
                                photo = upload!!.url
                                image.load( upload!!.fullurl)
                            }
                        }
                    }
                }
            }
            age.onClick {
                timePickView {
                    myBirthday = it
                    val calendar = Calendar.getInstance()//取得当前时间的年月日 时分秒
                    val year = calendar[Calendar.YEAR]
                    val substring = it.substring(0, 4).toInt()
                    age.text = it
                    myAge = year - substring
                }
            }
            next.onClick {
                if (TextUtils.isEmpty(photo)) {
                    toast("请上传头像")
                    return@onClick
                }
                if (etName.isEmp()) {
                    toast("请输入昵称")
                    return@onClick
                }
                if (myAge == -1) {
                    toast("请选择生日")
                    return@onClick
                }
                next.disable()
                register1(etName.text())
            }
        }
    }

    private fun register1(nickName: String) {
        scopeNetLife { // 创建作用域
            val register = register(
                mobile, code, password, confirmPassword, gender,
                nickName, photo, myAge, myBirthday
            )
            if(register!=null){
                start(LoginActivity::class.java)
                finish()
            }else{
                binding.next.enable()
                toast("请求异常")
            }

        }
    }
}