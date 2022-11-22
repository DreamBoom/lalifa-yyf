package com.lalifa.main.activity.me

import cn.rongcloud.config.UserManager
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.toast
import com.lalifa.main.activity.login.ForgetPasswordActivity
import com.lalifa.main.activity.login.LoginActivity
import com.lalifa.main.databinding.ActivitySettingBinding
import com.lalifa.utils.ImPushUtil
import com.lalifa.utils.SPUtil
import com.lalifa.yyf.ext.showTipDialog

class SettingActivity : BaseTitleActivity<ActivitySettingBinding>() {
    override fun getViewBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            set.onClick { start(SetActivity::class.java) }
            logOut.onClick { }
            qsn.onClick { start(ModelActivity::class.java) }
            realName.onClick { start(RealName::class.java) }
            feelBack.onClick { start(FeedBack::class.java) }
            forget.onClick { start(ForgetPasswordActivity::class.java) }
            zx.onClick {                 //注销账号
                showTipDialog("注销账号将删除所有账户信息，确定要注销吗？") {
                    scopeNetLife {
                        UserManager.logout()
                        ImPushUtil.getInstance(this@SettingActivity).logout()
                        SPUtil.set(Config.IS_LOGIN, false)
                        toast("注销成功！")
                        finish()
                        start(LoginActivity::class.java)
                    }
                }
            }
            logOut.onClick {                 //注销账号
                showTipDialog("确定退出当前账户？") {
                    scopeNetLife {
                        UserManager.logout()
                        ImPushUtil.getInstance(this@SettingActivity).logout()
                        SPUtil.set(Config.IS_LOGIN, false)
                        toast("退出成功！")
                        finish()
                        start(LoginActivity::class.java)
                    }
                }
            }
        }
    }
}