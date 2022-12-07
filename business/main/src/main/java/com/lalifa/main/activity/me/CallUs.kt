package com.lalifa.main.activity.me

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.callPhone
import com.lalifa.extension.onClick
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.callAdapter
import com.lalifa.main.api.CallBean
import com.lalifa.main.api.FriendBean
import com.lalifa.main.api.getCallList
import com.lalifa.main.databinding.ActivityCallUsBinding
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class CallUs : BaseTitleActivity<ActivityCallUsBinding>() {
    override fun title() = "专属客服"
    override fun getViewBinding() = ActivityCallUsBinding.inflate(layoutInflater)
    override fun initView() {
        scopeNetLife {
            val callList = getCallList()
            if(null!=callList){
                binding.callList.callAdapter().apply {
                    R.id.itemCall.onClick {
                        RouteUtils.routeToConversationActivity(
                            context,
                            Conversation.ConversationType.PRIVATE,
                            getModel<CallBean>().id.toString(),
                            false
                        )
                    }
                }.models = callList
            }
        }
    }
}