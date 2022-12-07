package com.lalifa.main.activity.friend

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.applyList
import com.lalifa.main.api.ApplyBean
import com.lalifa.main.api.applyFriend
import com.lalifa.main.api.applyList

class FriendApply : BaseListActivity() {
    override fun title() = "好友申请"
    override fun iniView() {
         apply = recyclerView.applyList().apply {
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
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
        getList()
    }


    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            addData(applyList())
        }
    }

    var apply: com.drake.brv.BindingAdapter? = null
    private fun getList() {
        scopeNetLife {
            val applyList = applyList()
            apply!!.models = applyList
        }
    }
}