package com.lalifa.main.activity.me

import cn.rongcloud.config.UserManager
import com.drake.net.utils.scopeNet
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.adapter.guardAdapter
import com.lalifa.main.api.guard
import com.lalifa.main.databinding.ActivityWardBinding

class WardActivity : BaseTitleActivity<ActivityWardBinding>() {
    override fun getViewBinding() = ActivityWardBinding.inflate(layoutInflater)

    override fun initView() {
        getData()
    }

    private fun getData() {
        scopeNet {
            val guard = guard()
            if (null != guard && guard.isNotEmpty()) {
                binding.getData.gone()
                binding.apply {
                    guardHeader.load(Config.FILE_PATH + guard[0].avatar)
                    guardName.text = guard[0].userName
                    header.load(Config.FILE_PATH + UserManager.get()!!.avatar)
                    name.text = UserManager.get()!!.userName
                    if (guard.size > 1) {
                        val subList = guard.subList(1, guard.size - 1)
                        guardList.guardAdapter().models = subList
                    }
                }
            } else {
                toast("暂无数据")
                binding.getData.visible()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.getData.onClick {
            getData()
        }
    }
}