package com.lalifa.main.activity.me

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.main.adapter.blackList
import com.lalifa.main.api.friendsList

class BlackListActivity : BaseListActivity() {
    override fun title() = "黑名单"
    override fun iniView() {
        recyclerView.blackList().apply {

        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            addData(friendsList())
        }
    }
}