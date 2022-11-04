package com.lalifa.che.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.logcat.LogCat
import com.lalifa.che.R
import com.lalifa.che.api.ChildInfo
import com.lalifa.che.api.Comment
import com.lalifa.che.api.Dynamic
import com.lalifa.che.databinding.ItemCheBinding
import com.lalifa.che.databinding.ItemPlBinding
import com.lalifa.che.databinding.ItemPlChildBinding
import com.lalifa.ext.Config
import com.lalifa.extension.imagesAdapter
import com.lalifa.extension.load

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
                childList.cheChildInfoList().apply {  }.models=bean.child
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