package com.lalifa.main.activity

import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.databinding.ActivityActiveBinding

class ActiveActivity : BaseTitleActivity<ActivityActiveBinding>() {
    override fun getViewBinding()= ActivityActiveBinding.inflate(layoutInflater)

    override fun initView() {
        binding.list.linear().setup {
            addType<String>(R.layout.item_active)
        }.models = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "")
    }
}