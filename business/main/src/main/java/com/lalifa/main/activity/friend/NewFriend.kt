package com.lalifa.main.activity.friend

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.newFriendList
import com.lalifa.main.api.FriendBean
import com.lalifa.main.api.NewFriendBean
import com.lalifa.main.api.newFriendsList
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class NewFriend : BaseListActivity() {
    override fun title() = "新人"
    override fun iniView() {
        recyclerView.newFriendList().apply {
            R.id.hi.onClick {
                RouteUtils.routeToConversationActivity(
                    context,
                    Conversation.ConversationType.PRIVATE,
                    getModel<NewFriendBean>().id.toString(),
                    false
                )
            }
        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }


    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            addData(newFriendsList())
        }
    }
}