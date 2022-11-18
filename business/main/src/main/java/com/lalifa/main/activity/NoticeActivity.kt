package com.lalifa.main.activity

import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.databinding.ActivityNoticeBinding

class NoticeActivity : BaseTitleActivity<ActivityNoticeBinding>() {
    override fun getViewBinding()= ActivityNoticeBinding.inflate(layoutInflater)
    override fun title()= "互动通知"
    override fun initView() {
        binding.list.linear().setup {
            addType<String>(R.layout.item_notice)
        }.models = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "")
    }
}