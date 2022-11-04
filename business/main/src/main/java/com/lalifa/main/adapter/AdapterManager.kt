package com.lalifa.main.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.che.api.ChildInfo
import com.lalifa.ext.Config
import com.lalifa.extension.dp
import com.lalifa.extension.load
import com.lalifa.main.R
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ItemGoodBinding
import com.lalifa.main.databinding.ItemGoodTypeBinding
import com.lalifa.main.databinding.ItemMoneyBinding

/**
 * 商城
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.shopList(): BindingAdapter {
    return grid(5).setup {
        addType<Classify>(R.layout.item_good_type)

        onBind {
            val bean = getModel<Classify>()
            getBinding<ItemGoodTypeBinding>().apply {
               // im.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
            }
        }
    }
}

/**
 * 商城-商品
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.goodsList(): BindingAdapter {
    return grid(5).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<Ware>(R.layout.item_good)
        onBind {
            val bean = getModel<Ware>()
            getBinding<ItemGoodBinding>().apply {
                im.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
                price.text = bean.price
            }
        }
    }
}


/**
 * 钱包明细列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.moneyList(): BindingAdapter {
    return linear().setup {
        addType<MoneyRecord>(R.layout.item_money)
        onBind {
            val bean = getModel<MoneyRecord>()
            getBinding<ItemMoneyBinding>().apply {
                type.text = bean.note
                time.text = bean.create_time
                money.text = bean.price
            }
        }
    }
}
