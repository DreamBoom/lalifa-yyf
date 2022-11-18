package com.lalifa.main.activity

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.applyList
import com.lalifa.main.api.ApplyBean
import com.lalifa.main.api.applyFriend
import com.lalifa.main.api.applyList
import com.lalifa.main.databinding.ActivityFriendApplyBinding

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
                            applyFriend(getModel<ApplyBean>().id.toString(), "1")
                            getList()
                        }
                    }
                    R.id.noAgree.onClick {
                        scopeNetLife {
                            applyFriend(getModel<ApplyBean>().id.toString(), "2")
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