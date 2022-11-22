package com.lalifa.main.activity.friend

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.friendList
import com.lalifa.main.api.FriendBean
import com.lalifa.main.api.friendsList
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class FriendList : BaseListActivity() {
    override fun title() = "好友"
    override fun iniView() {
        recyclerView.friendList().apply {
            R.id.item.onClick {
                RouteUtils.routeToConversationActivity(
                    context,
                    Conversation.ConversationType.PRIVATE,
                    getModel<FriendBean>().id.toString(),
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
            addData(friendsList())
        }
    }
}