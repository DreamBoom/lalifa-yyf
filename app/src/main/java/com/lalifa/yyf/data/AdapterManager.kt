package com.lalifa.yyf.data

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.*
import com.google.android.flexbox.FlexboxLayoutManager
import com.lalifa.extension.*
import com.lalifa.yyf.R
import com.lalifa.yyf.databinding.*



///**
// * 聚光灯-用户主页视频列表
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//fun RecyclerView.lightUserVideoList(): BindingAdapter {
//    return grid(3).divider {
//        includeVisible = true
//        orientation = DividerOrientation.VERTICAL
//        setDivider(11.dp)
//    }.divider {
//        includeVisible = true
//        orientation = DividerOrientation.HORIZONTAL
//        setDivider(11.dp)
//    }.setup {
//        addType<Int>(R.layout.item_user_video)
//        onBind {
//            getBinding<ItemUserVideoBinding>().cover.setImageResource(getModel())
//        }
//    }
//}
//
///**
// * 商品-评论列表
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//fun RecyclerView.goodEvaluationList(): BindingAdapter {
//    return linear().setup {
//        addType<String>(R.layout.item_good_evaluate)
//    }
//}
//
////购物车规格  测试实体类
//data class CartParamsBean(var title: String, var list: ArrayList<String>)
//
///**
// * 购物车规格
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//fun RecyclerView.cartParamsList(): BindingAdapter {
//    return linear().divider {
//        orientation = DividerOrientation.HORIZONTAL
//        setDivider(12.dp)
//        includeVisible = true
//    }.setup {
//        addType<CartParamsBean>(R.layout.item_add_cart)
//        onCreate {
//            getBinding<ItemAddCartBinding>().labelList.cartParamsInList()
//        }
//        onBind {
//            getBinding<ItemAddCartBinding>().apply {
//                title.text = getModel<CartParamsBean>().title
//                labelList.models = getModel<CartParamsBean>().list
//            }
//        }
//    }
//}
//
//fun RecyclerView.cartParamsInList(): BindingAdapter {
//    layoutManager = FlexboxLayoutManager(context)
//    return divider {
//        orientation = DividerOrientation.GRID
//        setDivider(12.dp)
//        includeVisible = false
//    }.setup {
//        singleMode = true
//        addType<String>(R.layout.item_add_cart_in)
//        onBind {
//            getBinding<ItemAddCartInBinding>().content.apply {
//                text = getModel()
//                isSelected = checkedPosition.contains(modelPosition)
//            }
//        }
//        onChecked { position, checked, allChecked ->
//            notifyDataSetChanged()
//        }
//        R.id.content.onClick {
//            setChecked(modelPosition, true)
//        }
//    }
//}
//
//data class UserOrderBean(val type: Int, val list: ArrayList<Int>)
//
///**
// * 用户订单列表
// * @receiver RecyclerView
// */
//fun RecyclerView.userOrderList(): BindingAdapter {
//    return linear().setup {
//        addType<UserOrderBean>(R.layout.item_user_orders)
//        onCreate {
//            getBinding<ItemUserOrdersBinding>().list.userOrderInList()
//        }
//        onBind {
//
//            val bean = getModel<UserOrderBean>()
//            getBinding<ItemUserOrdersBinding>().apply {
//                list.models = bean.list
//                when (bean.type) {
//                    0 -> {
//                        //待收货
//                        goShopBtn.text = "待收货"
//                    }
//                    1 -> {
//                        //待收货
//                        goShopBtn.text = "待收货"
//                    }
//                    2 -> {
//                        //已完成
//                        goShopBtn.text = "已完成"
//                    }
//                    3 -> {
//                        //退款/售后
//                        goShopBtn.text = "退款/售后"
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun RecyclerView.userOrderInList(): BindingAdapter {
//    return linear().setup {
//        addType<String>(R.layout.item_user_order_in)
//    }
//}
//
///**
// * 商品订单列表
// * @receiver RecyclerView
// * @param type 1:用户订单 2：商家订单
// */
//fun RecyclerView.storeOrderList(type: Int): BindingAdapter {
//    return linear().setup {
//        addType<UserOrderBean>(R.layout.item_store_oder)
//        onCreate {
//
//        }
//        onBind {
//            val bean = getModel<UserOrderBean>()
//            getBinding<ItemStoreOderBinding>().list.storeOrderInList(bean.type, type)
//            getBinding<ItemStoreOderBinding>().apply {
//                list.models = bean.list
//                when (bean.type) {
//                    0 -> {
//                        //待发货
//                        goShopBtn.text = "待发货"
//                    }
//                    1 -> {
//                        //待收货
//                        goShopBtn.text = "待收货"
//                    }
//                    2 -> {
//                        //已完成
//                        if (type == 1) {
//                            goShopBtn.text = "已完成"
//                        } else {
//                            goShopBtn.text = "交易成功"
//                        }
//                    }
//                    3 -> {
//                        //退款/售后
//                        goShopBtn.text = "退款/售后"
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun RecyclerView.storeOrderInList(type: Int, t: Int): BindingAdapter {
//    return linear().setup {
//        if (t == 2) {
//            addType<Int>(R.layout.item_store_oder_in)
//            onBind {
//                if (type == 3) {
//                    getBinding<ItemStoreOderInBinding>().state.apply {
//                        text = when (getModel<Int>()) {
//                            1 -> "退款成功"
//                            2 -> "退款中"
//                            else -> "退款中"
//                        }
//                    }
//                }
//            }
//        } else {
//            when (type) {
//                1 -> addType<Int>(R.layout.item_store_oder_in1)
//                else -> addType<Int>(R.layout.item_store_oder_in2)
//            }
//            onBind {
//                if (type == 3) {
//                    val state = getBinding<ItemStoreOderIn2Binding>().state
//                    val call = getBinding<ItemStoreOderIn2Binding>().call
//                    when (getModel<Int>()) {
//                        1 -> {
//                            state.text = "退款成功"
//                            call.gone()
//                        }
//                        2 -> {
//                            state.text = "退款中"
//                            call.visible()
//                        }
//                        else -> {
//                            state.text = "退款中"
//                            call.visible()
//                        }
//                    }
//
//                }
//            }
//        }
//
//        R.id.orderItem.onClick {
//            when (type) {
//                0 -> {
//                    //待发货
//                    //context.startOrder(Order1Activity::class.java, t)
//                }
//                1 -> {
//                    //待收货
//                  //  context.startOrder(Order2Activity::class.java, t)
//                }
//                2 -> {
//                    //已完成
//                  //  context.startOrder(Order3Activity::class.java, t)
//                }
//                3 -> {
//                    //退款/售后
//                    if (t == 1) {
//                     //   context.startOrder(Order4Activity::class.java, getModel())
//                    } else {
////                        when (getModel<Int>()) {
////                            1 -> context.startOrder(Order41Activity::class.java, t)
////                            2 -> context.startOrder(Order42Activity::class.java, t)
////                            else -> context.startOrder(Order43Activity::class.java, t)
////                        }
//                    }
//
//                }
//            }
//        }
//    }
//}
//
///**
// * 优惠券列表
// * @receiver RecyclerView
// * @param type Int 0:使用优惠券  1:店铺修改优惠券 3：使用红包
// * @return BindingAdapter
// */
//fun RecyclerView.couponsList(type: Int = 0): BindingAdapter {
//    return linear().setup {
//        addType<Boolean>(R.layout.item_coupons)
//        onBind {
//            getBinding<ItemCouponsBinding>().apply {
//                when (type) {
//                    1 -> {
//                        item.isSelected = true
//                        btn.text = "删除"
//                    }
//                    0 -> {
//                        item.isSelected = getModel()
//                    }
//                    else -> {
//                        title.text = "红包"
//                        item.isSelected = true
//                        btn.text = "去使用"
//                    }
//                }
//            }
//        }
//    }
//}
//
///**
// * 添加聊天、
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//fun RecyclerView.userList(): BindingAdapter {
//    return linear().setup {
//        addType<String>(R.layout.item_share_persion)
//        onCreate {
//            getBinding<ItemSharePersionBinding>().list.linear().setup {
//                addType<String>(R.layout.item_share_persion_in)
//            }
//        }
//        onBind {
//            getBinding<ItemSharePersionBinding>().apply {
//                title.text = getModel()
//                list.models = arrayListOf("", "", "", "")
//            }
//        }
//    }
//}
//
///**
// * 添加好友
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//fun RecyclerView.addFriendsList(): BindingAdapter {
//    return linear().setup {
//        addType<String>(R.layout.item_chat_add_friend)
//
//        R.id.add_friend.onClick {
//            //添加好友
//            context.showPayDialogs()
//        }
//        R.id.delete_btn.onClick {
//            //删除
//            removeAt(modelPosition)
//        }
//    }
//}
//
///**
// * 直播分类列表
// * @receiver RecyclerView
// * @return BindingAdapter
// */
//data class liveSortBean(var name: String, var check: Boolean)
//
//fun RecyclerView.liveSortList(activity: Activity): BindingAdapter {
//    return linear().setup {
//        addType<liveSortBean>(R.layout.item_live_sort)
//        singleMode = true
//        onBind {
//            getBinding<ItemLiveSortBinding>().apply {
//                val bean = getModel<liveSortBean>()
//                itemName.text = bean.name
//                if (bean.check) {
//                    select.setTextColor(ContextCompat.getColor(context, R.color.grey))
//                    select.background = ContextCompat.getDrawable(context, R.drawable.select_gray)
//                    select.text = "已选择"
//                } else {
//                    select.setTextColor(ContextCompat.getColor(context, R.color.white))
//                    select.background = ContextCompat.getDrawable(context, R.drawable.select_blue)
//                    select.text = "选择"
//                }
//            }
//        }
//        R.id.select.onClick {
//            val intent = Intent()
//            intent.putExtra("type", modelPosition)
//            activity.setResult(200, intent)
//            context.finish()
//        }
//    }
//}