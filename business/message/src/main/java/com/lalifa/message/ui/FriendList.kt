package com.lalifa.message.ui

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.message.adapter.friendList
import com.lalifa.message.api.addFriend
import com.lalifa.message.api.friendsList
import com.lalifa.message.databinding.ActivityFriendListBinding

class FriendList : BaseTitleActivity<ActivityFriendListBinding>() {
    override fun title()="好友"
    override fun getViewBinding()= ActivityFriendListBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val friendsList = friendsList()
            binding.apply {
                list.friendList().apply {

                }.models = friendsList
            }
        }
    }
}