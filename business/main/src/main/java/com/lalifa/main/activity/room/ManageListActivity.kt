package com.lalifa.main.activity.room

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.api.getManage
import com.lalifa.main.databinding.ActivityManageListBinding
import com.lalifa.main.fragment.adapter.roomManagerAdapter

class ManageListActivity : BaseActivity<ActivityManageListBinding>() {
    override fun getViewBinding() = ActivityManageListBinding.inflate(layoutInflater)

    override fun initView() {
        val roomId = getIntentString("roomId")
        scopeNetLife {
            val manage = getManage(roomId.noEN())
            if (null != manage && manage.isNotEmpty()) {
                binding.empty.gone()
                binding.manageList.roomManagerAdapter().apply {
                    R.id.remove.onClick {

                    }
                }.models = manage
            } else {
                binding.empty.visible()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.close.onClick { finish() }
    }

}