package com.lalifa.main.activity

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.adapter.newFriendList
import com.lalifa.main.api.newFriendsList
import com.lalifa.main.databinding.ActivityNewFriendBinding

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