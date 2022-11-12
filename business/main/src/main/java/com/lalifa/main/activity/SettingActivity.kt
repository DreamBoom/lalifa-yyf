package com.lalifa.main.activity

import cn.rongcloud.config.UserManager
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.toast
import com.lalifa.main.databinding.ActivitySettingBinding
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
            zx.onClick {                 //注销账号
                showTipDialog("注销账号将删除所有账户信息，确定要注销吗？") {
                    scopeNetLife {
                        UserManager.logout()
                        toast("注销成功！")
                    }
                }
            }
            logOut.onClick {                 //注销账号
                showTipDialog("确定退出当前账户？") {
                    scopeNetLife {
                        UserManager.logout()
                        toast("退出成功！")
                    }
                }
            }
        }
    }
}