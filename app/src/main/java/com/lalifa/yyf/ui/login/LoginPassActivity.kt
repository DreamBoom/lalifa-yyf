package com.lalifa.yyf.ui.login

import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.feedback.SensorsUtil
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.user.UserProvider
import cn.rongcloud.config.router.RouterPath
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.extension.*
import com.lalifa.main.activity.MainActivity
import com.lalifa.yyf.MApplication
import com.lalifa.yyf.api.login
import com.lalifa.yyf.databinding.ActivityLoginPassBinding
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient

class LoginPassActivity : BaseActivity<ActivityLoginPassBinding>() {
    override fun getViewBinding() = ActivityLoginPassBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }
            register.onClick { start(RegisterActivity::class.java) }
            forget.onClick { start(ForgetPasswordActivity::class.java) }
            agree.onClick { agree.isSelected = !agree.isSelected }
            agreeInfo.onClick { }
            login.onClick {
                if (etPhone.isEmp() || etPhone.text().length < 11) {
                    toast("请输入正确的手机号")
                }
                if (etPass.isEmp()) {
                    toast("请输入密码")
                }
                scopeNetLife {
                    val user = login(etPhone.text(), etPass.text())
                    if (null != user) {
                        //在jpush上设置别名
                        JPushInterface.setAlias(
                            MApplication.get(), "13462439645"
                        ) { i, s, set ->
                            if (i == 0) {
                                LogCat.e("设置别名成功")
                            } else {
                                LogCat.e("设置别名失败")
                            }
                        }
                        UserManager.save(user.userinfo)
                        UserProvider.provider().update(user.userinfo.toUserInfo())
                        initRongIM(user.userinfo)
                    }
                }
            }
        }

    }

    private fun initRongIM(user: User) {
        if (!TextUtils.isEmpty(user.token)) {
            RongIM.connect(user.token, object : RongIMClient.ConnectCallback() {
                override fun onSuccess(t: String) {
                    SensorsUtil.instance().setUserProperties(UserManager.get()!!.mobile)
                    SensorsUtil.instance().registerSuperProperties(true)
                    start(MainActivity::class.java)
                    finish()
                }

                override fun onError(e: RongIMClient.ConnectionErrorCode?) {
                    if (e.toString() == "RC_CONNECTION_EXIST") {
                        UserManager.logout()
                    }
                    LogCat.e(e)
                }

                override fun onDatabaseOpened(code: RongIMClient.DatabaseOpenStatus?) {
                    LogCat.e(code.toString() + ">>>111")
                }
            })
        }
    }
}