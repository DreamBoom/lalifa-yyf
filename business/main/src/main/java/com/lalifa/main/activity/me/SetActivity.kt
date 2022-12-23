package com.lalifa.main.activity.me

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.activity.room.ext.Tool
import com.lalifa.main.databinding.ActivitySetBinding
import com.lalifa.utils.SPUtil

class SetActivity : BaseTitleActivity<ActivitySetBinding>() {
    override fun getViewBinding() = ActivitySetBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            val boolean = SPUtil.getBoolean(Tool.showGift, true)
            giftIn.isChecked = boolean
            giftIn.setOnCheckedChangeListener { p0, p1 -> SPUtil.set(Tool.showGift, p1) }
        }
    }

}