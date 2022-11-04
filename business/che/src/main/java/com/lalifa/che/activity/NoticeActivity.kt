package com.lalifa.che.activity

import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseTitleActivity
import com.lalifa.che.R
import com.lalifa.che.databinding.ActivityNoticeBinding

class NoticeActivity : BaseTitleActivity<ActivityNoticeBinding>() {
    override fun getViewBinding()= ActivityNoticeBinding.inflate(layoutInflater)
    override fun title()= "互动通知"
    override fun initView() {
        binding.list.linear().setup {
            addType<String>(R.layout.item_notice)
        }.models = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "")
    }
}