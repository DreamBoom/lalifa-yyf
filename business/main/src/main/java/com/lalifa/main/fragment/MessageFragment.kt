package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.PageRefreshLayout
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.base.BaseListFragment
import com.lalifa.ext.showClearDialog
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pk
import com.lalifa.extension.start
import com.lalifa.main.activity.friend.*
import com.lalifa.main.databinding.ViewMainMessageBinding
import com.lalifa.main.ext.MUtils
import com.lalifa.main.fragment.adapter.fanList
import com.lalifa.main.fragment.adapter.messageList
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongIMClient
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
            search.onClick { start(SearchActivity::class.java) }
            newFriend.onClick { start(NewFriend::class.java) }
            topMore.onClick {
                requireActivity().showClearDialog{
                    if(it==1){
                        //全部已读
                        MUtils.imClear {

                        }
                    }else{
                        //清空消息
                        MUtils.imRead {

                        }
                    }
                }
            }

            msgXt.onClick { start(MsgXt::class.java) }
            msgHd.onClick { start(MsgHd::class.java) }
            msgHy.onClick { start(FriendList::class.java) }
            msgSq.onClick { start(FriendApply::class.java) }

        }
    }

    class FriendFrag() : BaseListFragment() {
        var timeStamp = 0L
        var count = 10
        override fun initView() {
            super.initView()
            binding.recyclerView.messageList()
        }

        override fun PageRefreshLayout.getData() {
            scopeNetLife {
                RongIMClient.getInstance().getConversationListByPage(object :
                    RongIMClient.ResultCallback<List<Conversation>>() {
                    override fun onSuccess(t: List<Conversation>?) {
                        if (t != null) {
                            addData(t)
                            if (t.isNotEmpty()) {
                                timeStamp = t[t.size-1].sentTime
                            }
                        }
                    }

                    override fun onError(e: RongIMClient.ErrorCode?) {
                        LogCat.e("====" + e.toString())
                    }

                }, timeStamp, count, Conversation.ConversationType.PRIVATE)
            }
        }
    }
}