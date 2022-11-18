package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lalifa.base.BaseFragment
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.activity.*
import com.lalifa.main.databinding.ViewMainMessageBinding
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class MessageFragment : BaseFragment<ViewMainMessageBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainMessageBinding.inflate(layoutInflater)

    override fun initView() {
        binding.viewPager.fragmentAdapter(
            childFragmentManager,
            arrayListOf("")
        ) {
            add(ConversationList())
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            search.onClick { }
            newFriend.onClick { start(NewFriend::class.java) }
            topMore.onClick {
                RouteUtils.routeToConversationActivity(context,
                    Conversation.ConversationType.PRIVATE, "2", false)
            }

            msgXt.onClick { start(MsgXt::class.java) }
            msgHd.onClick { start(MsgHd::class.java) }
            msgHy.onClick { start(FriendList::class.java) }
            msgSq.onClick { start(FriendApply::class.java) }

        }
    }

}