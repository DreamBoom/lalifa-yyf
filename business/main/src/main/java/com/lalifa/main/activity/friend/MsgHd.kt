package com.lalifa.main.activity.friend

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.main.fragment.adapter.friendList
import com.lalifa.main.api.friendsList

class MsgHd : BaseListActivity() {
    override fun title()="互动消息"
    override fun iniView() {
        recyclerView.friendList().apply {

        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }


    override fun PageRefreshLayout.getData() {
        scopeNetLife {
          //  addData(friendsList())
        }
    }
}