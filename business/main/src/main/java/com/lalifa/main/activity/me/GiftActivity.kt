package com.lalifa.main.activity.me

import cn.rongcloud.config.UserManager
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.main.databinding.ActivityGiftBinding

class GiftActivity : BaseTitleActivity<ActivityGiftBinding>() {
    override fun getViewBinding() = ActivityGiftBinding.inflate(layoutInflater)
    override fun title() = "礼物墙"
    override fun initView() {
        binding.apply {
            UserManager.get().apply {
                header.load(Config.FILE_PATH + this?.avatar)
                name.text = this?.userName
            }
        }
    }
}