package com.lalifa.main.fragment.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.ext.Config
import com.lalifa.ext.User
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.*
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
 * 背包
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.knapsackList(): BindingAdapter {
    return grid(5).setup {
        addType<Classify>(R.layout.item_good_type)

        onBind {
            val bean = getModel<Classify>()
            getBinding<ItemGoodTypeBinding>().apply {
                //    im.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
            }
        }
    }
}

/**
 * 背包-物品
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.knapsacksList(): BindingAdapter {
    return grid(5).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<KnapsackInfo>(R.layout.item_good)
        onBind {
            val bean = getModel<KnapsackInfo>()
            getBinding<ItemGoodBinding>().apply {
                // im.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
                //  price.text = bean.price
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
            1 -> addType<SpecialEffect>(R.layout.item_im)
            2 -> addType<Headdres>(R.layout.item_im)
            else -> addType<Mount>(R.layout.item_im)
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
fun RecyclerView.cheMyList(): BindingAdapter {
    return linear().setup {
        addType<Dynamic>(R.layout.item_my_che)
        onBind {
            val bean = getModel<Dynamic>()
            getBinding<ItemMyCheBinding>().apply {
                header.load(bean.avatar.pk(""))
                name.text = bean.userName
                time.text = bean.create_time
                if (null != bean.gender && bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                }
                info.text = bean.content
                bean.image.forEach {
                    Config.FILE_PATH + it
                }
                gridImg.imagesAdapter(bean.image, true)
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
        addType<ActivityDetail>(R.layout.item_activity_two)
        onBind {
            val bean = getModel<ActivityDetail>()
            getBinding<ItemActivityTwoBinding>().apply {
                im.load(Config.FILE_PATH + bean.goods.image)
            }
        }
    }
}

/**
 * 好友列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.friendList(): BindingAdapter {
    return linear().setup {
        addType<FriendBean>(R.layout.item_friend)
        onBind {
            val bean = getModel<FriendBean>()
            getBinding<ItemFriendBinding>().apply {
                itemHeader.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
                mId.text = "ID:${bean.id}"
                if (bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                }
            }
        }
    }
}

/**
 * 新人列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.newFriendList(): BindingAdapter {
    return linear().setup {
        addType<NewFriendBean>(R.layout.item_new_friend)
        onBind {
            val bean = getModel<NewFriendBean>()
            getBinding<ItemNewFriendBinding>().apply {
                itemHeader.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
                mId.text = "ID:${bean.id}"
                if (bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                }
            }
        }
    }
}

/**
 * 申请列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.applyList(): BindingAdapter {
    return linear().setup {
        addType<ApplyBean>(R.layout.item_apply)
        onBind {
            val bean = getModel<ApplyBean>()
            getBinding<ItemApplyBinding>().apply {
                itemHeader.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
                mId.text = "ID:${bean.id}"
                info.text = bean.postscript
                time.text = bean.create_time
                if (bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
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
        addType<Dynamic>(R.layout.item_che)
        onBind {
            val bean = getModel<Dynamic>()
            getBinding<ItemCheBinding>().apply {
                header.load(bean.avatar.pk(""))
                name.text = bean.userName
                time.text = bean.create_time
                if (null != bean.gender && bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                }
                info.text = bean.content
                bean.image.forEach {
                    Config.FILE_PATH + it
                }
                gridImg.imagesAdapter(bean.image, true)
                share.text = "${bean.share}"
                like.text = "${bean.fabulous}"
                pl.text = "${bean.comment_count}"
            }
        }
    }
}

/**
 * 评论列表适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.cheInfoList(): BindingAdapter {
    return linear().setup {
        addType<Comment>(R.layout.item_pl)
        onBind {
            val bean = getModel<Comment>()
            getBinding<ItemPlBinding>().apply {
                header.load(bean.avatar)
                name.text = bean.userName
                time.text = bean.create_time
                info.text = bean.content
                like.text = "${bean.fabulous}"
                childList.cheChildInfoList().apply { }.models = bean.child
            }
        }
    }
}

/**
 * 评论子列表适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.cheChildInfoList(): BindingAdapter {
    return linear().setup {
        addType<ChildInfo>(R.layout.item_pl_child)
        onBind {
            val bean = getModel<ChildInfo>()
            getBinding<ItemPlChildBinding>().apply {
                header.load(bean.avatar)
                name.text = bean.userName
                time.text = bean.create_time
                info.text = bean.content
                like.text = "${bean.fabulous}"
            }
        }
    }
}

/**
 * 我的粉丝、我的关注适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.fanList(): BindingAdapter {
    return linear().setup {
        addType<FriendBean>(R.layout.item_fan)
        onBind {
            val bean = getModel<FriendBean>()
            getBinding<ItemFanBinding>().apply {
                name.text = bean.userName
                itemHeader.load(Config.FILE_PATH + bean.avatar)
                if (bean.gender == 1) {
                    imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                } else {
                    imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                }
            }
        }
    }
}

/**
 * 黑名单列表适配器
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.blackList(): BindingAdapter {
    return linear().setup {
        addType<FriendBean>(R.layout.item_friend)
        onBind {
            val bean = getModel<FriendBean>()
            getBinding<ItemFriendBinding>().apply {

            }
        }
    }
}

/**
 * 首页热门主持
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.mainList1(): BindingAdapter {
    return grid(3).setup {
        addType<Captain>(R.layout.item_home1)
    }.apply {
        onBind {
            val bean = getModel<Captain>()
            getBinding<ItemHome1Binding>().apply {
                header.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
            }
        }
    }
}

/**
 * 首页热门队长
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.mainList2(): BindingAdapter {
    return grid(3).setup {
        addType<Host>(R.layout.item_home1)
        onBind {
            val bean = getModel<Host>()
            onBind {
                val bean = getModel<Host>()
                getBinding<ItemHome1Binding>().apply {
                    header.load(Config.FILE_PATH + bean.avatar)
                    name.text = bean.userName
                }
            }
        }
    }
}


/**
 * 首页热门房间
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.mainList3(): BindingAdapter {
    return grid(2).setup {
        addType<Room>(R.layout.item_home2)
        onBind {
            val bean = getModel<Room>()
            getBinding<ItemHome2Binding>().apply {
                bg.load(Config.FILE_PATH + bean.background)
                fire.text = bean.hot.toString()
                name.text = bean.title
                when (bean.type_id) {
                    1 -> t2.text = "同城"
                    else -> t2.text = "其他"
                }
            }
        }
    }
}

/**
 * 礼物墙 组别
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.giftGroupAdapter(): BindingAdapter {
    return grid(3).setup {
        addType<Theme>(R.layout.item_gift_group)
        onBind {
            val bean = getModel<Theme>()
            getBinding<ItemGiftGroupBinding>().apply {
                groupName.text = bean.name
            }
        }
    }
}

/**
 * 礼物墙
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.giftAdapter(): BindingAdapter {
    return grid(4).setup {
        addType<Gift>(R.layout.item_gift_wall)
        onBind {
            val bean = getModel<Gift>()
            getBinding<ItemGiftWallBinding>().apply {
                im.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
            }
        }
    }
}

/**
 * 礼物记录
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.giftHistoryAdapter(): BindingAdapter {
    return linear().setup {
        addType<GiftHistoryBean>(R.layout.item_gift_history)
        onBind {
            val bean = getModel<GiftHistoryBean>()
            getBinding<ItemGiftHistoryBinding>().apply {
                giftHeader.load(Config.FILE_PATH + bean.avatar)
                giftIm.load(Config.FILE_PATH + bean.image)
                name.text = bean.userName
                giftTime.text = bean.create_time.substring(0, 10)
            }
        }
    }
}

/**
 * 守护列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
@SuppressLint("SetTextI18n")
fun RecyclerView.guardAdapter(): BindingAdapter {
    return linear().setup {
        addType<GuardBean>(R.layout.item_guard)
        onBind {
            val bean = getModel<GuardBean>()
            getBinding<ItemGuardBinding>().apply {
                itemNum.text = (layoutPosition + 1).toString()
                itemGuardHeader.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
                mId.text = bean.user_id.toString()
                gxNum.text = "${bean.yield}贡献值"
                if (bean.gender == 1) {
                    imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                } else {
                    imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                }
            }
        }
    }
}

/**
 * 客服
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.callAdapter(): BindingAdapter {
    return linear().setup {
        addType<CallBean>(R.layout.item_call)
        onBind {
            val bean = getModel<CallBean>()
            getBinding<ItemCallBinding>().apply {
                callHeader.load(Config.FILE_PATH + bean.avatar)
                callName.text = bean.userName
            }
        }
    }
}

/**
 * 经常去的房间
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.meRoomAdapter(): BindingAdapter {
    return grid(4).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<MeRoom>(R.layout.item_im1)
        onBind {
            val bean = getModel<MeRoom>()
            getBinding<ItemIm1Binding>().apply {
                header.load(Config.FILE_PATH + bean.image)
                name.text = bean.title
            }
        }
    }
}

/**
 * 勋章
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.meXzAdapter(): BindingAdapter {
    return grid(4).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<MeXz>(R.layout.item_im1)
        onBind {
            val bean = getModel<MeXz>()
            getBinding<ItemIm1Binding>().apply {
                header.load(Config.FILE_PATH + bean.image)
                name.text = bean.name
            }
        }
    }
}

/**
 * 商城
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.roomListAdapter(): BindingAdapter {
    return linear().setup {
        addType<Office>(R.layout.item_roon_list)
        onBind {
            val bean = getModel<Office>()
            getBinding<ItemRoonListBinding>().apply {
                header.load(Config.FILE_PATH + bean.image)
                name.text = bean.title
                type.text = bean.type_name
                if (bean.users.isNotEmpty()) {
                    userHeader.load(Config.FILE_PATH + bean.image)
                }
            }
        }
    }
}


/**
 * 排行
 * @receiver RecyclerView
 * @return BindingAdapter
 */
@SuppressLint("SetTextI18n")
fun RecyclerView.phAdapter(): BindingAdapter {
    return linear().setup {
        addType<RankBean>(R.layout.item_ph)
        onBind {
            val bean = getModel<RankBean>()
            getBinding<ItemPhBinding>().apply {
                num.text = "${4 + layoutPosition}"
                header.load(Config.FILE_PATH + bean.avatar)
                name.text = bean.userName
                cf.text = "财富值:${bean.yield}"
                mId.text = bean.user_id.toString()
                if (bean.gender == 0) {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                } else {
                    sex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                }
            }
        }
    }
}

/**
 * 主播老板麦位
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.seatBossAdapter(): BindingAdapter {
    return grid(2).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(35.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        //{"audioLevel":0,"mute":false,"speaking":false,"status":0}
        addType<Seat>(R.layout.layout_seat_item)
        onBind {
            val seatInfo = getModel<Seat>()
            val useing = seatInfo.status == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing
            val lock = seatInfo.status == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
            getBinding<LayoutSeatItemBinding>().apply {
                if (useing) {
                    val account = AccountManager.getAccount(seatInfo.userId)
                    if (null != account) {
                        ivPortrait.load(account.avatar, R.mipmap.ic_room_seat)
                        //麦位上用户名称
                        memberName.text = account.userName
                    }
                } else {
                    //麦位上用户名称
                    ivPortrait.loadLocal(R.mipmap.ic_room_seat)
                    memberName.text = if (layoutPosition == 0) "主播" else "老板位"
                }
                //是否锁定
                if (lock) seatLocked.visible() else seatLocked.gone()
                //是否静音
                if (seatInfo.isMute) seatMute.visible() else seatMute.gone()
            }
        }
    }
}

/**
 * 麦位
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.seatAdapter(): BindingAdapter {
    //{"audioLevel":0,"mute":false,"speaking":false,"status":0}
    return grid(4).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(25.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<Seat>(R.layout.layout_seat_item)
        onBind {
            val seatInfo = getModel<Seat>()
            val useing = seatInfo.status == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing
            val lock = seatInfo.status == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
            getBinding<LayoutSeatItemBinding>().apply {
                if (useing) {
                    val account = AccountManager.getAccount(seatInfo.userId)
                    if (null != account) {
                        ivPortrait.load(account.avatar, R.mipmap.ic_room_seat)
                        //麦位上用户名称
                        memberName.text = account.userName
                    }
                } else {
                    //麦位上用户名称
                    ivPortrait.loadLocal(R.mipmap.ic_room_seat)
                    memberName.text = "观众"
                }
                //是否锁定
                if (lock) seatLocked.visible() else seatLocked.gone()
                //是否静音
                if (seatInfo.isMute) seatMute.visible() else seatMute.gone()
            }
        }
    }
}

/**
 * 房间管理员
 * @receiver RecyclerView
 * @return BindingAdapter
 */
@SuppressLint("SetTextI18n")
fun RecyclerView.roomManagerAdapter(): BindingAdapter {
    return linear().setup {
        addType<User>(R.layout.item_manger)
        onBind {
            val bean = getModel<User>()
            getBinding<ItemMangerBinding>().apply {
                mHeader.load(Config.FILE_PATH + bean.avatar)
                mName.text = bean.userName
                mId.text = bean.user_id.toString()
            }
        }
    }
}

/**
 * 房间背景
 * @receiver RecyclerView
 * @return BindingAdapter
 */
fun RecyclerView.roomBgAdapter(): BindingAdapter {
    return grid(4).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<RoomBgBean>(R.layout.item_room_bg)
        onBind {
            val bean = getModel<RoomBgBean>()
            getBinding<ItemRoomBgBinding>().apply {
                itemRoomBg.load(Config.FILE_PATH + bean.image)
                if (bean.check) {
                    check.setImageResource(R.drawable.ic_check1)
                } else {
                    check.setImageResource(R.drawable.ic_check)
                }
            }
        }
    }
}


