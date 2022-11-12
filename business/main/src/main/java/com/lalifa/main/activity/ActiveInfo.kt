package com.lalifa.main.activity

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.activeInfoList
import com.lalifa.main.api.getActivityInfo
import com.lalifa.main.databinding.ActivityActiveInfoBinding

class ActiveInfo : BaseTitleActivity<ActivityActiveInfoBinding>() {
    override fun getViewBinding() = ActivityActiveInfoBinding.inflate(layoutInflater)
    override fun title()="活动详情"
    override fun initView() {
        val id = intent.getIntExtra("id", 1)
        scopeNetLife {
            val info = getActivityInfo(id.toString())
            binding.apply {
                tvActive.text = "活动时间:${info!!.start_time}-${info!!.end_time}"
                tvActive.degrees = 60
                tvRule.text = info.rule
                list.activeInfoList().apply {
                    R.id.get.onClick {

                    }
                }.models = info.activity_list
            }
        }
    }

}