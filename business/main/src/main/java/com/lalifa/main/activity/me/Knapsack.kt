package com.lalifa.main.activity.me

import android.annotation.SuppressLint
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getData(){
        scopeNetLife {
            val shop = knapsack()
            binding.apply {
                typeList.apply {
                    knapsackList().apply {
                        R.id.im.onClick {
                            goodList.bindingAdapter.models = getModel<Classify>().knapsack
                        }
                    }
                    models = shop!!.classify
                }
                goodList.apply {
                    knapsacksList().apply {
                        models = shop!!.classify[0].knapsack
                        R.id.use.onClick {
                            useMyDress(getModel<KnapsackInfo>().id.toString())
                        }
                    }
                }
            }
        }
    }

    private fun useMyDress(id:String){
        scopeNetLife {
            val useDress = useDress(id)
            if(null!=useDress){
                getData()
            }else{
                toast("使用异常，请重新尝试")
            }
        }
    }
}