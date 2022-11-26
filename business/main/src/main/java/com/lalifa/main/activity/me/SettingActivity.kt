package com.lalifa.main.activity.me

import cn.rongcloud.config.UserManager
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.toast
import com.lalifa.groupavatars.cache.DiskLruCacheHelper
import com.lalifa.main.activity.login.ForgetPasswordActivity
import com.lalifa.main.activity.login.LoginActivity
import com.lalifa.main.databinding.ActivitySettingBinding
import com.lalifa.utils.CacheDataManager
import com.lalifa.utils.ImPushUtil
import com.lalifa.utils.SPUtil
import com.lalifa.yyf.ext.showTipDialog
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class SettingActivity : BaseTitleActivity<ActivitySettingBinding>() {
    override fun getViewBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun initView() {
        val cacheSize = CacheDataManager.getTotalCacheSize(this@SettingActivity)
        binding.cache.text = cacheSize
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            set.onClick { start(SetActivity::class.java) }
            blackList.onClick { start(BlackListActivity::class.java) }
            logOut.onClick { }
            bindPhone.onClick { start(BindPhoneActivity::class.java) }
            bindZfb.onClick {
                start(BindActivity::class.java) {
                    putExtra("type", 0)
                }
            }
            bindWx.onClick {
                start(BindActivity::class.java) {
                    putExtra("type", 1)
                }
            }
            qsn.onClick { start(ModelActivity::class.java) }
            realName.onClick { start(RealName::class.java) }
            feelBack.onClick { start(FeedBack::class.java) }
            forget.onClick { start(ForgetPasswordActivity::class.java) }
            clear.onClick {
                val cacheS = CacheDataManager.getTotalCacheSize(this@SettingActivity)
                if (cacheS == "0.0Byte") {
                    toast("缓存已清理")
                    return@onClick
                }
                CacheDataManager.clearAllCache(this@SettingActivity)
                val cacheSize = CacheDataManager.getTotalCacheSize(this@SettingActivity)
                cache.text = cacheSize
            }
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