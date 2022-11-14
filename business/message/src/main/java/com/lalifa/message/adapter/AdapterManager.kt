package com.lalifa.message.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.message.api.FriendBean
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.message.R
import com.lalifa.message.api.ApplyBean
import com.lalifa.message.api.NewFriendBean
import com.lalifa.message.databinding.ItemApplyBinding
import com.lalifa.message.databinding.ItemFriendBinding
import com.lalifa.message.databinding.ItemNewFriendBinding

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
            val bean = getModel<FriendBean>()
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