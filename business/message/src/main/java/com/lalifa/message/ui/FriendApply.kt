package com.lalifa.message.ui

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.message.R
import com.lalifa.message.adapter.applyList
import com.lalifa.message.api.ApplyBean
import com.lalifa.message.api.addFriend
import com.lalifa.message.api.applyList
import com.lalifa.message.databinding.ActivityFriendApplyBinding

class FriendApply : BaseTitleActivity<ActivityFriendApplyBinding>() {
    override fun title() = "好友申请"
    override fun getViewBinding() = ActivityFriendApplyBinding.inflate(layoutInflater)
    var apply: com.drake.brv.BindingAdapter? = null
    override fun initView() {
        scopeNetLife {
            binding.apply {
                apply = list.applyList().apply {
                    R.id.agree.onClick {
                        scopeNetLife {
                            addFriend(getModel<ApplyBean>().id.toString(), "1")
                            getList()
                        }
                    }
                    R.id.noAgree.onClick {
                        scopeNetLife {
                            addFriend(getModel<ApplyBean>().id.toString(), "2")
                            getList()
                        }
                    }
                }

            }
        }
        getList()
    }

    private fun getList() {
        scopeNetLife {
            val applyList = applyList()
            apply!!.models = applyList
        }
    }
}