package com.lalifa.main.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseFragment
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.pageChangedListener
import com.lalifa.main.R
import com.lalifa.main.databinding.ViewMainMessageBinding

class MessageFragment : BaseFragment<ViewMainMessageBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainMessageBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                childFragmentManager,
                arrayListOf("消息", "好友", "群组")
            ) {
                add(TabMessageFragment())
                add(TabFriendFragment())
                add(TabGroupFragment())
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(requireContext(),R.color.textColor2)
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }
}