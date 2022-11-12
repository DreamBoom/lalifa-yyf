package com.example.message.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.example.message.adapter.friendList
import com.example.message.api.friendsList

import com.lalifa.base.BaseFragment
import com.lalifa.message.R
import com.lalifa.message.databinding.TabFriendBinding

class TabFriendFragment : BaseFragment<TabFriendBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = TabFriendBinding.inflate(layoutInflater)

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