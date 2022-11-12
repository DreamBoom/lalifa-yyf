package com.example.message.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.example.message.api.FriendBean
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.message.R
import com.lalifa.message.databinding.ItemFriendBinding

/**
 * 商城
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