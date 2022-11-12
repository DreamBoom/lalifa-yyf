package com.lalifa.main.activity

import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.adapter.activeList
import com.lalifa.main.api.ActivityBean
import com.lalifa.main.api.getActivity
import com.lalifa.main.databinding.ActivityActiveBinding

class ActiveActivity : BaseTitleActivity<ActivityActiveBinding>() {
    override fun getViewBinding()= ActivityActiveBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val activity = getActivity()
            binding.list.activeList().apply {
                R.id.look.onClick {
                    start(ActiveInfo::class.java){
                        putExtra("id",getModel<ActivityBean>().id)
                    }
                }
            }.models = activity
        }
    }
}