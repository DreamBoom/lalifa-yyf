package com.example.message.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseFragment
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.pageChangedListener
import com.lalifa.message.databinding.TabMessageBinding
import io.rong.imkit.conversationlist.ConversationListFragment

class TabMessageFragment : BaseFragment<TabMessageBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = TabMessageBinding.inflate(layoutInflater)
    //ConversationListFragment
    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                childFragmentManager,
                arrayListOf("系统消息", "小报", "申请通知", "与我相关")
            ) {
                add(ConversationListFragment())
                add(ConversationListFragment())
                add(ConversationListFragment())
                add(ConversationListFragment())
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    requireContext(),
                    com.lalifa.base.R.color.textColor2
                )
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }
}