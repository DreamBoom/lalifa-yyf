package com.lalifa.main.activity

import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.databinding.ActivitySettingBinding

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

        }
    }
}