package com.lalifa.main.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.che.databinding.ItemCheBinding
import com.lalifa.ext.Config
import com.lalifa.extension.dp
import com.lalifa.extension.imagesAdapter
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.main.R
import com.lalifa.main.api.*
import com.lalifa.main.databinding.*
import com.lalifa.utils.ImageLoader

/**
 * 商城
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.shopList(): BindingAdapter {
    return grid(5).setup {
        addType<shopBean>(R.layout.item_good_type)

        onBind {
            val bean = getModel<shopBean>()
            getBinding<ItemGoodTypeBinding>().apply {
                im.load(Config.FILE_PATH + bean.image)
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

/**
 * 爵位列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.titlesList(): BindingAdapter {
    return linear(orientation = LinearLayoutManager.HORIZONTAL).setup {
        addType<TitleBean>(R.layout.item_jw)
        onBind {
            val bean = getModel<TitleBean>()
            getBinding<ItemJwBinding>().apply {
                itemJw.load(Config.FILE_PATH + bean.background_image)
                name.text = bean.name
                header.load(Config.FILE_PATH + bean.image)
            }
        }
    }
}

/**
 * 爵位权益列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.tiList(type: Int): BindingAdapter {
    return linear().setup {
        when (type) {
            1 ->  addType<SpecialEffect>(R.layout.item_im)
            2 ->  addType<Headdres>(R.layout.item_im)
            else ->  addType<Mount>(R.layout.item_im)
        }
        onBind {
            getBinding<ItemImBinding>().apply {
                when (type) {
                    1 -> {
                        header.load(Config.FILE_PATH + getModel<SpecialEffect>().image)
                        name.text = getModel<SpecialEffect>().name
                    }
                    2 -> {
                        header.load(Config.FILE_PATH + getModel<Headdres>().image)
                        name.text = getModel<Headdres>().name
                    }
                    else -> {
                        header.load(Config.FILE_PATH + getModel<Mount>().image)
                        name.text = getModel<Mount>().name
                    }
                }
            }
        }
    }
}

/**
 * 发现页列表适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.cheList(): BindingAdapter {
    return linear().setup {
        addType<Dynamic>(R.layout.item_my_che)
        onBind {
            val bean = getModel<Dynamic>()
            getBinding<ItemMyCheBinding>().apply {
                header.load(bean.avatar)
                name.text = bean.userName
                time.text = bean.create_time
                if(bean.gender==0){
                    sex.setImageResource( com.lalifa.base.R.drawable.ic_icon_boy)
                }else{
                    sex.setImageResource( com.lalifa.base.R.drawable.ic_icon_gril)
                }
                info.text = bean.content
                bean.image.forEach {
                    Config.FILE_PATH+it
                }
                gridImg.imagesAdapter(bean.image,true)
                share.text = "${bean.share}"
                like.text = "${bean.fabulous}"
                pl.text = "${bean.comment_count}"
            }
        }
    }
}

/**
 * 活动列表适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.activeList(): BindingAdapter {
    return linear().setup {
        addType<ActivityBean>(R.layout.item_active)
        onBind {
            val bean = getModel<ActivityBean>()
            getBinding<ItemActiveBinding>().apply {
                name.text = bean.name
                time.text = bean.start_time
            }
        }
    }
}

/**
 * 活动详情适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.activeInfoList(): BindingAdapter {
    return linear().setup {
        addType<Activity>(R.layout.item_activity)
        onBind {
            val bean = getModel<Activity>()
            getBinding<ItemActivityBinding>().apply {
                money.text = "累计充值${bean.price}元"
                itemList.activeInfo2List().models = bean.activity_details
            }
        }
    }
}
/**
 * 活动详情子适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.activeInfo2List(): BindingAdapter {
    return grid(3).setup {
        addType<ActivityDetail>(R.layout.item_activity2)
        onBind {
            val bean = getModel<ActivityDetail>()
            getBinding<ItemActivity2Binding>().apply {
               im.load(Config.FILE_PATH+bean.goods.image)
            }
        }
    }
}
