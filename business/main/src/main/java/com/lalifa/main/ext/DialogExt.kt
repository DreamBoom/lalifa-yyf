package com.lalifa.main.ext

import android.text.TextUtils
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNet
import com.drake.tooltip.toast
import com.lalifa.ext.Config
import com.lalifa.ext.Config.Companion.parser
import com.lalifa.main.activity.room.ext.Member
import com.lalifa.main.activity.room.ext.User
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.Tool
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ItemCzBinding
import com.lalifa.main.databinding.ItemDayBinding
import com.lalifa.main.fragment.adapter.popRoomAdapter
import com.lalifa.utils.SPUtil
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import per.goweii.layer.core.anim.AnimStyle
import per.goweii.layer.core.ktx.onClickToDismiss
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.animStyle
import per.goweii.layer.dialog.ktx.backgroundDimDefault
import per.goweii.layer.dialog.ktx.contentView
import per.goweii.layer.dialog.ktx.gravity
import java.net.URL


fun showPriceDialog(bean: GoodInfoBean, callback: (id: Int) -> Unit = {}) {
    var id = -1
    DialogLayer()
        .contentView(R.layout.dialog_goos_info)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .addOnClickToDismissListener(R.id.close)
        .onInitialize {
            val imageView = findViewById<SVGAImageView>(R.id.svgaImage)
            parser.decodeFromURL(
                URL(Config.FILE_PATH + bean.effect_image),
                object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val drawable = SVGADrawable(videoItem)
                        imageView!!.setImageDrawable(drawable)
                        imageView.startAnimation()
                    }

                    override fun onError() {

                    }
                })


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

fun showDh(list: ArrayList<Exchange>, callback: (id: Int) -> Unit = {}) {
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
                    addType<Exchange>(R.layout.item_cz)
                    onBind {
                        val bean = getModel<Exchange>()
                        getBinding<ItemCzBinding>().apply {
                            num.text = bean.drill.toString()
                            money.text = bean.price
                        }
                    }
                    R.id.itemZs.onClick {
                        payId = getModel<Exchange>().id
                    }
                }.models = list
            findViewById<TextView>(R.id.dhBuy)!!.onClick {
                callback.invoke(payId)
            }
        }
        .show()
}

fun showTx(money: Double, callback: (money: Double) -> Unit = {}) {
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

fun inputPasswordDialog(callback: (pass: String) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.layout_input_password_dialog)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .addOnClickToDismissListener(R.id.txClose)
        .onInitialize {
            val etMoney = findViewById<VerifyEditText>(R.id.et_password)
            findViewById<TextView>(R.id.btn_confirm)!!.onClick {
                if (TextUtils.isEmpty(etMoney!!.content)) {
                    toast("请输入密码")
                } else {
                    if (etMoney.content.length < 4) {
                        toast("请输入4位密码")
                    } else {
                        callback.invoke(etMoney.content)
                    }
                }
            }
        }
        .show()
}

fun roomTopDialog(isLike: Boolean, callback: (type: Int) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.popup_exit_room)
        .gravity(Gravity.TOP)
        .backgroundDimDefault()
        .animStyle(animStyle = AnimStyle.TOP)
        .setOutsideTouchToDismiss(true)
        .addOnClickToDismissListener(R.id.iv_close)
        .onInitialize {
            findViewById<TextView>(R.id.tvSc)!!.text = if (isLike) "取消收藏" else "收藏房间"
            findViewById<LinearLayout>(R.id.ll_jb_room)!!.onClick {
                callback.invoke(1)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll_sc_room)!!.onClick {
                callback.invoke(2)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll_fx_room)!!.onClick {
                callback.invoke(3)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll_gb_room)!!.onClick {
                callback.invoke(4)
                dismiss()
            }
        }
        .show()
}

fun roomBottomDialog(callback: (type: Int) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.popup_set_room)
        .gravity(Gravity.BOTTOM)
        .animStyle(animStyle = AnimStyle.BOTTOM)
        .backgroundDimDefault()
        .onClickToDismiss(R.id.iv_close)
        .setOutsideTouchToDismiss(true)
        .onInitialize {
            val member = Member.getMember(Member.currentId)
            val llGl = findViewById<LinearLayout>(R.id.ll_gl)
            if(member!!.manage_type!=1){
                llGl!!.gone()
            }
            val showIn = SPUtil.getBoolean(Tool.showGift, true)
            val showSx = SPUtil.getBoolean(Tool.showSx, true)
            val tvShowIn = findViewById<TextView>(R.id.tvShowIn)
            val tvShowSx = findViewById<TextView>(R.id.tvShowSx)
            tvShowIn!!.text = if (showIn) "礼物动画关闭" else "礼物动画开启"
            tvShowSx!!.text = if (showSx) "隐藏随心值" else "显示随心值"
            findViewById<LinearLayout>(R.id.ll1)!!.onClick {
                SPUtil.set(Tool.showGift, !showIn)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll2)!!.onClick {
                callback.invoke(2)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll3)!!.onClick {
                callback.invoke(3)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll4)!!.onClick {
                SPUtil.set(Tool.showSx, !showSx)
                val boolean1 = SPUtil.getBoolean(Tool.showSx, true)
                callback.invoke(4)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll5)!!.onClick {
                callback.invoke(5)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll6)!!.onClick {
                callback.invoke(6)
                dismiss()
            }
            findViewById<LinearLayout>(R.id.ll7)!!.onClick {
                callback.invoke(7)
                dismiss()
            }
        }
        .show()
}

//管理查看麦位
fun roomUserDialog(user: Member, isMute:Boolean, callback: (type: Int) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.popup_info)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .onInitialize {
            findViewById<TextView>(R.id.name)!!.text = user.userName
            findViewById<TextView>(R.id.mId)!!.text = "ID:${user.userId}"
            findViewById<TextView>(R.id.level)!!.text = user.level
            findViewById<TextView>(R.id.ac2)!!.text = if(isMute)"开麦" else "闭麦"
            val ll = findViewById<LinearLayoutCompat>(R.id.ll2)
            if(Member.getMember(Member.currentId)!!.manage_type!=1){
                ll!!.gone()
            }
            val sex = findViewById<ImageView>(R.id.sex)
            val svg = findViewById<SVGAImageView>(R.id.svg)!!
            if (!TextUtils.isEmpty(user.frame)) {
                MUtils.loadSvg(svg, user.frame) {}
            }
            findViewById<ImageView>(R.id.header)!!.load(Config.FILE_PATH + user.avatar)
            if (UserManager.get()!!.gender == 0) {
                sex!!.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
            } else {
                sex!!.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
            }
            val gz = findViewById<TextView>(R.id.gz)
            findViewById<ImageView>(R.id.popClose)!!.onClick { dismiss() }
            gz!!.text = if (user.follow_type==1) "取消关注" else "关注"
            gz.onClick {
                scopeNet {
                    val follow = follow(user.userId!!)
                    if (follow != null) {
                        dismiss()
                    }
                }
            }
            findViewById<TextView>(R.id.at)!!.onClick {
                callback.invoke(1)
                dismiss()
            }
            findViewById<TextView>(R.id.message)!!.onClick {
                callback.invoke(2)
                dismiss()
            }
            findViewById<TextView>(R.id.send)!!.onClick {
                callback.invoke(3)
                dismiss()
            }
            findViewById<TextView>(R.id.ac1)!!.onClick {
                callback.invoke(4)
                dismiss()
            }
            findViewById<TextView>(R.id.ac2)!!.onClick {
                callback.invoke(5)
                dismiss()
            }
            findViewById<TextView>(R.id.ac3)!!.onClick {
                callback.invoke(6)
                dismiss()
            }
            findViewById<TextView>(R.id.ac4)!!.onClick {
                callback.invoke(7)
                dismiss()
            }
        }
        .show()
}

//本人查看麦位
fun roomMyDialog(user: User, callback: () -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.popup_info_me)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .onInitialize {
            findViewById<TextView>(R.id.name)!!.text = user.userName
            findViewById<TextView>(R.id.mId)!!.text = "ID:${user.userId}"
            findViewById<TextView>(R.id.level)!!.text = user.level
            val sex = findViewById<ImageView>(R.id.sex)
            findViewById<ImageView>(R.id.header)!!.load(Config.FILE_PATH + user.avatar)
            val svg = findViewById<SVGAImageView>(R.id.svg)!!
            if (!TextUtils.isEmpty(user.frame)) {
                MUtils.loadSvg(svg, user.frame!!) {

                }
            }
            if (user.gender == 0) {
                sex!!.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
            } else {
                sex!!.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
            }
            findViewById<ImageView>(R.id.popClose)!!.onClick {
                svg.clear()
                dismiss()
            }
            findViewById<TextView>(R.id.out)!!.onClick {
                callback()
                svg.clear()
                dismiss()
            }
        }
        .show()
}

//麦位申请
fun requestSeatDialog(state: Int, callback: () -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.pop_seat_request)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .onInitialize {
            val title = findViewById<TextView>(R.id.tvTitle)!!
            val request = findViewById<TextView>(R.id.request)!!
            when (state) {
                Tool.STATUS_NOT_ON_SEAT -> {
                    title.text = "申请上麦"
                    request.text = "申请上麦"
                }
                Tool.STATUS_WAIT_FOR_SEAT -> {
                    title.text = "已申请上麦"
                    request.text = "撤回连线申请"
                }
            }
            findViewById<TextView>(R.id.cancel)!!.onClick { dismiss() }
            request.onClick {
                callback()
                dismiss()
            }
        }
        .show()
}

//队列
fun roomListDialog(
    data: List<Fleet>,
    callback: (roomId: String, owner: Boolean) -> Unit = { s: String, b: Boolean -> }
) {
    DialogLayer()
        .contentView(R.layout.pop_room_list)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .setOutsideTouchToDismiss(true)
        .addOnClickToDismissListener(R.id.ivClose)
        .onInitialize {
            val empty = findViewById<ImageView>(R.id.empty)!!
            val list = findViewById<RecyclerView>(R.id.list)!!

            if (null != data && data.isNotEmpty()) {
                empty.gone()
                list.visible()
                list.popRoomAdapter().apply {
                    R.id.inRoom.onClick {
                        if (getModel<Fleet>().userId == Member.currentId) {
                            callback(getModel<Fleet>().roomid, true)
                        } else {
                            callback(getModel<Fleet>().roomid, false)
                        }
                        dismiss()
                    }
                }.models = data
            } else {
                empty.visible()
                list.gone()
            }
        }
        .show()
}