package com.lalifa.message.ui

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.message.adapter.friendList
import com.lalifa.message.adapter.newFriendList
import com.lalifa.message.api.friendsList
import com.lalifa.message.api.newFriendsList
import com.lalifa.message.databinding.ActivityNewFriendBinding

class NewFriend : BaseTitleActivity<ActivityNewFriendBinding>() {
    override fun title()="新人"
    override fun getViewBinding() = ActivityNewFriendBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val newFriendsList = newFriendsList()
            binding.apply {
                list.newFriendList().apply {

                }.models = newFriendsList
            }
        }
    }
}