package com.lalifa.main.ext

import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.roomkit.api.Rule
import cn.rongcloud.roomkit.api.recharge
import cn.rongcloud.roomkit.databinding.ItemCzBinding
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.logcat.LogCat
import com.drake.tooltip.toast
import com.lalifa.ext.Config
import com.lalifa.extension.*
import per.goweii.layer.core.Layer
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.*
import com.lalifa.main.R
import com.lalifa.main.api.Exchange
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

fun showDh(list:ArrayList<Exchange>,callback: (id: Int) -> Unit = {}) {
    var payId = -1
    DialogLayer()
        .contentView(R.layout.dialog_dh)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .addOnClickToDismissListener(R.id.dhClose)
        .onInitialize {
            findViewById<RecyclerView>(R.id.infoList)!!
                .grid(2).setup {
                addType<Exchange>(cn.rongcloud.roomkit.R.layout.item_cz)
                onBind {
                    val bean = getModel<Exchange>()
                    getBinding<ItemCzBinding>().apply {
                        num.text = bean.drill.toString()
                        money.text = bean.price
                    }
                }
                cn.rongcloud.roomkit.R.id.itemZs.onClick {
                    payId = getModel<Exchange>().id
                }
            }.models = list
            findViewById<TextView>(R.id.dhBuy)!!.onClick {
                callback.invoke(payId)
            }
        }
        .show()
}

fun showTx(money:Double,callback: (money: Double) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.dialog_tx)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .addOnClickToDismissListener(R.id.txClose)
        .onInitialize {
            val etMoney = findViewById<EditText>(R.id.etMoney)
            findViewById<TextView>(R.id.txAll)!!.onClick {
                etMoney!!.setText(money.toString())
            }
            findViewById<TextView>(R.id.txBuy)!!.onClick {
                callback.invoke(etMoney.double())
            }
        }
        .show()
}