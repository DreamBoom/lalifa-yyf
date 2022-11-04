package com.lalifa.main.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drake.brv.utils.models
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.goodsList
import com.lalifa.main.adapter.shopList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityKnapsackBinding
import com.lalifa.main.databinding.ActivityShopBinding
import com.lalifa.main.ext.showPriceDialog

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
                    shopList().apply {
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
                    goodsList().apply {
                        models = list
                    }
                }
            }
        }
    }
}