package com.lalifa.main.activity.me

import android.annotation.SuppressLint
import com.drake.brv.utils.models
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.goodsList
import com.lalifa.main.adapter.knapsackList
import com.lalifa.main.adapter.knapsacksList
import com.lalifa.main.adapter.shopList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityKnapsackBinding

class Knapsack : BaseTitleActivity<ActivityKnapsackBinding>() {
    override fun getViewBinding() = ActivityKnapsackBinding.inflate(layoutInflater)
    override fun title() = "背包"
    override fun initView() {
        initData()
    }

    private val list = arrayListOf<KnapsackInfo>()

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        scopeNetLife {
            val shop = knapsack()
            binding.apply {
                typeList.apply {
                    knapsackList().apply {
                        R.id.im.onClick {
                            val bean = getModel<Classify>()
                            list.clear()
                            list.addAll(bean.knapsack)
                            //goodList.adapter!!.notifyDataSetChanged()
                            goodList.models = list
                        }
                    }
                    models = shop!![0].classify
                }
                list.clear()
                for (item in shop!![0].classify) {
                    list.addAll(item.knapsack)
                }
                goodList.apply {
                    knapsacksList().apply {
                        models = list
                    }
                }
            }
        }
    }
}