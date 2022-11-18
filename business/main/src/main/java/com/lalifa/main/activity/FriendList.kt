package com.lalifa.main.activity

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.adapter.friendList
import com.lalifa.main.api.friendsList
import com.lalifa.main.databinding.ActivityFriendListBinding

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