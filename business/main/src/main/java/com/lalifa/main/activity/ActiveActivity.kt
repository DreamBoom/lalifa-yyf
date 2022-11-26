package com.lalifa.main.activity

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.adapter.activeList
import com.lalifa.main.api.ActivityBean
import com.lalifa.main.api.getActivity

class ActiveActivity : BaseListActivity() {
    override fun title()= "活动列表"
    override fun iniView() {
        recyclerView.activeList().apply {
            R.id.look.onClick {
                start(ActiveInfo::class.java){
                    putExtra("id",getModel<ActivityBean>().id)
                }
            }
        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            addData(getActivity())
        }
    }
}