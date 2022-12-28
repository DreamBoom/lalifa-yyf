package com.lalifa.main.activity.me

import android.annotation.SuppressLint
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.toast
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.goodsList
import com.lalifa.main.fragment.adapter.knapsackList
import com.lalifa.main.fragment.adapter.knapsacksList
import com.lalifa.main.fragment.adapter.shopList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityKnapsackBinding

class Knapsack : BaseTitleActivity<ActivityKnapsackBinding>() {
    override fun getViewBinding() = ActivityKnapsackBinding.inflate(layoutInflater)
    override fun title() = "背包"
    override fun initView() {
        initData()
    }


    private fun initData() {
        getData()
    }
    var shop:KnapsackBean?=null
    var apply:BindingAdapter?=null
    var choose = 0
    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        scopeNetLife {
            shop = knapsack()
            binding.apply {
                 apply = goodList.knapsacksList().apply {
                    R.id.use.onClick {
                        useMyDress(getModel<KnapsackInfo>().id.toString())
                    }
                }
                apply!!.models = shop!!.classify[choose].knapsack
                typeList.knapsackList().apply {
                    R.id.im.onClick {
                        choose = layoutPosition
                        apply!!.models = getModel<Classify>().knapsack
                    }
                }.models = shop!!.classify
            }
        }
    }

    private fun useMyDress(id: String) {
        scopeNetLife {
            val useDress = useDress(id)
            if (null != useDress) {
                shop!!.classify[choose].knapsack.forEach {
                    if(it.id.toString() == id){
                        it.type = 1
                    }else{
                        it.type = 0
                    }
                }
                apply!!.notifyDataSetChanged()
            } else {
                toast("使用异常，请重新尝试")
            }
        }
    }
}