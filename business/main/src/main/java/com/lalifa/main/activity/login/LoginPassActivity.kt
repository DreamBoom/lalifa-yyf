package com.lalifa.main.activity.login

import android.net.Uri
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.activity.MainActivity
import com.lalifa.main.activity.room.ext.Tool
import com.lalifa.main.activity.room.ext.User
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.main.api.login
import com.lalifa.main.databinding.ActivityLoginPassBinding
import com.lalifa.utils.SPUtil
import com.mob.MobSDK
import com.mob.OperationCallback
import io.rong.imkit.RongIM
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imkit.userinfo.UserDataProvider
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.UserInfo

class LoginPassActivity : BaseActivity<ActivityLoginPassBinding>() {
    override fun getViewBinding() = ActivityLoginPassBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            val boolean = SPUtil.getBoolean(Tool.agree, false)
            agree.isSelected = boolean
            back.onClick { finish() }
            register.onClick { start(RegisterActivity::class.java) }
            forget.onClick { start(ForgetPasswordActivity::class.java) }
            agree.onClick {
                agree.isSelected = !agree.isSelected
            }
            agreeInfo.onClick { }
            login.onClick {
                if(!agree.isSelected){
                    toast("请勾选同意《用户服务协议》")
                    return@onClick
                }
                if (etPhone.isEmp() || etPhone.text().length < 11) {
                    toast("请输入正确的手机号")
                }
                if (etPass.isEmp()) {
                    toast("请输入密码")
                }
                login.disable()
                scopeNetLife {
                    val user = login(etPhone.text(), etPass.text())
                    if (null != user) {
                        RongUserInfoManager.getInstance().setUserInfoProvider({ userId ->
                                UserInfo(userId, user.userinfo.userName,
                                    Uri.parse(Config.FILE_PATH+user.userinfo.avatar))
                            }, true)
                        SPUtil.set(Tool.agree, true)
                        submitPrivacyGrantResult(true)
                        login.enable()
                        InitNet.initNetHttp(this@LoginPassActivity,user.userinfo.token)
                        initRongIM(user.userinfo)
                    } else {
                        toast("登录失败")
                        login.enable()
                    }
                }
            }
        }

    }
    private fun submitPrivacyGrantResult(granted: Boolean) {
        MobSDK.submitPolicyGrantResult(granted, object : OperationCallback<Void?>() {
            override fun onComplete(data: Void?) {
                LogCat.d(TAG, "隐私协议授权结果提交：成功")
            }

            override fun onFailure(t: Throwable) {
                LogCat.d(TAG, "隐私协议授权结果提交：失败")
            }
        })
    }
    private fun initRongIM(user: User) {
        if (!TextUtils.isEmpty(user.imToken)) {
            RongIM.connect(user.imToken, object : RongIMClient.ConnectCallback() {
                override fun onSuccess(t: String) {
                    //在jpush上设置别名
                    JPushInterface.setAlias(
                        this@LoginPassActivity, user.userId
                    ) { i, s, set -> }
                    UserManager.save(user)
                    SPUtil.set(Config.IS_LOGIN, true)
                    start(MainActivity::class.java){
                        putExtra("initIm",true)
                    }
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