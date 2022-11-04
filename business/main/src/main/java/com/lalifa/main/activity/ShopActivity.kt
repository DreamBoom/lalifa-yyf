package com.lalifa.main.activity

import android.annotation.SuppressLint
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.R
import com.lalifa.main.adapter.goodsList
import com.lalifa.main.adapter.shopList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityShopBinding
import com.lalifa.main.ext.showPriceDialog

class ShopActivity : BaseTitleActivity<ActivityShopBinding>() {
    override fun getViewBinding() = ActivityShopBinding.inflate(layoutInflater)
    override fun title() = "商城"
    override fun initView() {
        initData()
    }

    private val list = arrayListOf<Ware>()

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        scopeNetLife {
            val shop = shop()
            binding.apply {
                typeList.apply {
                    shopList().apply {
                        R.id.im.onClick {
                            val bean = getModel<shopBean>()
                            list.clear()
                            list.addAll(bean.wares)
                            // goodList.adapter!!.notifyDataSetChanged()
                            goodList.models = list
                        }
                    }
                    models = shop
                }
                list.clear()
                for (item in shop!!) {
                    list.addAll(item.wares)
                }
                goodList.apply {
                    goodsList().apply {
                        R.id.rlGood.onClick {
                            scopeNetLife {
                                val goodInfo = goodInfo(getModel<Ware>().id)
                                if (goodInfo != null) {
                                    showPriceDialog(goodInfo) {
                                        scopeNetLife {
                                            buyGood(it)
                                        }
                                    }
                                }
                            }
                        }
                        models = list
                    }
                }
            }
        }
    }
}