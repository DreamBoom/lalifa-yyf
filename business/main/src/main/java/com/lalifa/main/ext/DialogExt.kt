package com.lalifa.main.ext

import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.ext.Config
import com.lalifa.extension.*
import per.goweii.layer.core.Layer
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.*
import com.lalifa.main.R
import com.lalifa.main.api.GoodInfoBean
import com.lalifa.main.api.Spec
import com.lalifa.main.databinding.ItemDayBinding

fun showPriceDialog(bean: GoodInfoBean, callback: (id: Int) -> Unit = {}) {
    var id = -1
    DialogLayer()
        .contentView(R.layout.dialog_goos_info)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .addOnClickToDismissListener(R.id.close)
        .onInitialize {
            findViewById<ImageView>(R.id.im)!!.load(Config.FILE_PATH + bean.image)
            findViewById<TextView>(R.id.name)!!.text = bean.name
            val price = findViewById<TextView>(R.id.price)
            findViewById<RecyclerView>(R.id.infoList)?.apply {
                grid(3).divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.VERTICAL
                }.divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.HORIZONTAL
                }.setup {
                    addType<Spec>(R.layout.item_day)
                    onBind {
                        getBinding<ItemDayBinding>().priceDay.text = "${getModel<Spec>().days}天"
                    }
                    R.id.priceDay.onClick {
                        price!!.text = getModel<Spec>().price
                        id = getModel<Spec>().id
                    }
                }.models = bean.specs
            }
            findViewById<TextView>(R.id.buy)!!.onClick {
                callback.invoke(id)
            }
        }
        .show()

}

fun showDh() {
    DialogLayer()
        .contentView(R.layout.dialog_dh)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .onInitialize {
            //findViewById<ImageView>(R.id.im)!!.load(Config.FILE_PATH + bean.image)
        }
        .show()

}