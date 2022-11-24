package cn.rongcloud.config.dialog

import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.config.api.GiftBean
import cn.rongcloud.config.databinding.ItemChatGiftBinding
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.lalifa.ext.Config
import com.lalifa.extension.dp
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.backgroundDimDefault
import per.goweii.layer.dialog.ktx.contentView
import per.goweii.layer.dialog.ktx.gravity

fun showGiftDialog(bean: List<GiftBean>, callback: (bean: GiftBean) -> Unit = {}) {
    DialogLayer()
        .contentView(cn.rongcloud.config.R.layout.dialog_chat_gift)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .onInitialize {
            val giftList = findViewById<RecyclerView>(cn.rongcloud.config.R.id.giftList)
            //giftList.height = 50
            giftList?.apply {
                grid(4).divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.VERTICAL
                }.divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.HORIZONTAL
                }.setup {
                    addType<GiftBean>(cn.rongcloud.config.R.layout.item_chat_gift)
                    onBind {
                        val bind = getBinding<ItemChatGiftBinding>()
                        val model = getModel<GiftBean>()
                        bind.im.load(Config.FILE_PATH + model.image)
                        bind.name.text = model.name
                        bind.price.text = "¥${model.price}"
                        bind.itemGift.onClick {
                            callback.invoke(getModel())
                            dismiss()
                        }
                    }
                }.models = bean
            }
        }.show()
}