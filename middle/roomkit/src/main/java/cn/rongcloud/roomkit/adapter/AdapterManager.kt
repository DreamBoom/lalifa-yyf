package cn.rongcloud.roomkit.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.api.Office
import cn.rongcloud.roomkit.api.RankBean
import cn.rongcloud.roomkit.databinding.ItemPhBinding
import cn.rongcloud.roomkit.databinding.ItemRoonListBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.ext.Config
import com.lalifa.extension.load

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
                if(bean.users.isNotEmpty()){
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
                num.text = "${4+layoutPosition}"
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