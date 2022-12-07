package com.lalifa.yyf.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.*
import com.drake.channel.receiveEvent
import com.lalifa.api.GiftBean
import com.lalifa.base.R
import com.lalifa.base.databinding.ItemChatGiftBinding
import com.lalifa.ext.Config
import com.lalifa.extension.*
import per.goweii.layer.core.anim.AnimStyle
import per.goweii.layer.core.anim.NullAnimatorCreator
import per.goweii.layer.core.ktx.*
import per.goweii.layer.core.widget.SwipeLayout
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.*
//

/**
 * 通用提示框
 * @receiver Activity
 * @param content String
 * @param title String
 * @param isShowCancelBtn Boolean
 * @param callback Function0<Unit>
 */
fun Activity.showTipDialog(
    content: String, title: String = "提示",
    isShowCancelBtn: Boolean = true,
    cancelText: String = "取消",
    sureText: String = "完成",
    callback: () -> Unit = {}
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_tips)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onClickToDismiss(R.id.sure_btn) { callback.invoke() }
        .onClickToDismiss(R.id.cancel_btn)
        .onInitialize {
            findViewById<TextView>(R.id.tip)?.text = title
            findViewById<TextView>(R.id.sure_btn)?.text = sureText
            findViewById<TextView>(R.id.cancel_btn)?.text = cancelText
            findViewById<TextView>(R.id.content)?.text = content
            findViewById<TextView>(R.id.cancel_btn)?.applyVisible(isShowCancelBtn)
            findViewById<ImageView>(R.id.line)?.applyVisible(isShowCancelBtn)
        }.show()
}

fun Fragment.showTipDialog(
    content: String, title: String = "温馨提示",
    isShowCancelBtn: Boolean = false,
    cancelText: String = "取消", sureText: String = "确定",
    callback: () -> Unit = {}
) {
    requireActivity().showTipDialog(content, title, isShowCancelBtn, cancelText, sureText, callback)
}
//
///**
// * 显示一个带取消按钮的list弹框
// * @receiver Activity
// * @param contents ArrayList<String>
// * @param sureText String
// * @param callback Function1<Int, Unit>
// */
//fun Activity.showListDialog(
//    contents: ArrayList<String>,
//    sureText: String = "完成",
//    callback: DialogLayer.(Int) -> Unit = {}
//) {
//    DialogLayer(this)
//        .cancelableOnTouchOutside(true)
//        .contentView(R.layout.dialog_content)
//        .backgroundDimDefault()
//        .gravity(Gravity.BOTTOM)
//        .animStyle(AnimStyle.BOTTOM)
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .onClickToDismiss(R.id.sure_btn)
//        .onInitialize {
//            requireViewById<RecyclerView>(R.id.list).linear().setup {
//                addType<String>(R.layout.item_dialog_text)
//                onBind {
//                    getBinding<ItemDialogTextBinding>().content.text = getModel()
//                }
//                R.id.content.onClick {
//                    callback.invoke(this@onInitialize, modelPosition)
//                }
//            }.models = contents
//            requireViewById<TextView>(R.id.sure_btn).text = sureText
//        }.show()
//}
//
//fun Fragment.showListDialog(
//    contents: ArrayList<String>,
//    sureText: String = "完成",
//    callback: DialogLayer.(Int) -> Unit = {}
//) {
//    requireActivity().showListDialog(contents, sureText, callback)
//}
//
///**
// * 显示一个通用List弹框
// * @receiver Activity
// * @param title String
// * @param block [@kotlin.ExtensionFunctionType] Function1<RecyclerView, Unit>
// */
//fun Activity.showCommonListDialog(title: String = "", block: RecyclerView.(DialogLayer) -> Unit) {
//    DialogLayer(this)
//        .cancelableOnTouchOutside(true)
//        .contentView(R.layout.dialog_parameter)
//        .backgroundDimDefault()
//        .gravity(Gravity.BOTTOM)
//        .animStyle(AnimStyle.BOTTOM)
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .onClickToDismiss(R.id.close_btn)
//        .onInitialize {
//            requireViewById<RecyclerView>(R.id.list).apply { block.invoke(this, this@onInitialize) }
//            requireViewById<TextView>(R.id.title).text = title
//        }.show()
//}
fun showGiftDialog(bean: List<GiftBean>, callback: (bean: GiftBean) -> Unit = {}) {
    DialogLayer()
        .contentView(R.layout.dialog_chat_gift)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .onInitialize {
            val giftList = findViewById<RecyclerView>(R.id.giftList)
            //giftList.height = 50
            giftList?.apply {
                grid(4).divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.VERTICAL
                }.divider {
                    setDivider(8.dp)
                    orientation = DividerOrientation.HORIZONTAL
                }.setup {
                    addType<GiftBean>(R.layout.item_chat_gift)
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
/**
 * 显示一个在键盘上方的弹框
 * @receiver Activity
 * @param hint String
 * @param l int 可输入长度
 * @param callback Function1<[@kotlin.ParameterName] String, Unit>
 */
fun Activity.showInputDialog(
    hint: String = "请输入",
    content: String = "",
    l:Int,
    callback: (content: String) -> Unit
) {
    DialogLayer(this)
        .contentView(R.layout.dialog_input)
        .cancelableOnTouchOutside(true)
        .contentAnimator(NullAnimatorCreator())
        .backgroundDimDefault()
        .addInputMethodCompat(true)
        .onPreShow {
            val et = requireViewById<EditText>(R.id.et_input)
            et.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et, InputMethodManager.SHOW_FORCED)
        }.onPreDismiss {
            val et = requireViewById<EditText>(R.id.et_input)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et.windowToken, 0)
        }.onInitialize {
            val inputEt = requireViewById<EditText>(R.id.et_input).apply {
                setHint(hint)
                maxEms = l
                setText(content)
            }
            requireViewById<TextView>(R.id.sure).onClick {
                if (inputEt.text().isEmpty()) {
                    toast("请输入内容")
                    return@onClick
                }
                callback.invoke(inputEt.text())
                dismiss()
            }
        }.show()
}

///**
// * 显示添加好友支付弹框
// * @receiver Context
// */
//fun Context.showPayDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_pay_tip)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.CENTER)
//        .backgroundDimDefault()
//        .animStyle(AnimStyle.ZOOM)
//        .onClickToDismiss(R.id.cancel_btn)
//        .onClickToDismiss(R.id.sure_btn) {
//            //去支付
//            DialogLayer(this@showPayDialogs)
//                .contentView(R.layout.dialog_pay)
//                .cancelableOnTouchOutside(true)
//                .gravity(Gravity.BOTTOM)
//                .backgroundDimDefault()
//                .animStyle(AnimStyle.BOTTOM)
//                .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//                .onClickToDismiss(R.id.close_btn)
//                .onClickToDismiss(R.id.sure_btn) {
//                    //去支付
//
//                }.onInitialize {
//                    val wechat = requireViewById<TextView>(R.id.pay_wechat)
//                    val alipay = requireViewById<TextView>(R.id.pay_alipay)
//                    wechat.onClick {
//                        alipay.isSelected = false
//                        it.isSelected = true
//                    }
//                    alipay.onClick {
//                        wechat.isSelected = false
//                        it.isSelected = true
//                    }
//                }
//                .show()
//        }.show()
//}
//
///**
// * 显示直播结束弹框
// * @receiver Context
// */
//fun Context.showLiveDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_close_live)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.CENTER)
//        .backgroundDimDefault()
//        .animStyle(AnimStyle.ZOOM)
//        .onDismiss { finish() }
//        .onClickToDismiss(R.id.cancel_btn) {
//
//        }.show()
//
//}
//
//
///**
// * 显示好友收费弹框
// * @receiver Context
// */
//fun Context.showMoneyDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_money)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.CENTER)
//        .backgroundDimDefault()
//        .animStyle(AnimStyle.ZOOM)
//        .onClickToDismiss(R.id.cancel_btn) {
//
//        }
//        .onClickToDismiss(R.id.sure) {
//
//        }.show()
//
//}
//
///**
// * 显示新故事弹框
// * @receiver Context
// */
//fun Activity.showNewStoryDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_new_story)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onClickToDismiss(R.id.sure_btn) {
//
//        }.onClickToDismiss(R.id.ll1) {
//            val intent = Intent()
//            intent.setClass(
//                this@showNewStoryDialogs,
//                SelectFriendsActivity::class.java
//            )
//            intent.putExtra("isList", 2)
//            startActivity(intent)
//        }.onClickToDismiss(R.id.ll2) {
//            startActivity<RecordActivity>()
//        }.onInitialize {
//        }.show()
//}
//
///**
// * 显示公众号弹框
// * @receiver Context
// */
//fun Activity.showGzhDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_gzh)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .onClickToDismiss(R.id.sure_btn) {
//
//        }.onClickToDismiss(R.id.t1) {
//
//        }.onClickToDismiss(R.id.t2) {
//        }
//        .onInitialize {
//        }.show()
//}
//
///**
// * 显示代币弹框
// * @receiver Context
// */
//fun Activity.showDbDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_db)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .onClickToDismiss(R.id.sure_btn) {
//
//        }.onClickToDismiss(R.id.t1) {
//            startActivity<DbInfoActivity>()
//        }.onClickToDismiss(R.id.t2) {
//        }
//        .onInitialize {
//        }.show()
//}
//
///**
// * 显示管理好友弹框
// * @receiver Context
// */
//fun Activity.showFriendDialogs() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_my_friend)
//        .cancelableOnTouchOutside(true)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .onClickToDismiss(R.id.down) {
//
//        }.onInitialize {
//            val list = requireViewById<RecyclerView>(R.id.list)
//            val barList = requireViewById<RecyclerView>(R.id.bar_list)
//            barList.linear().setup {
//                addType<String>(R.layout.item_select)
//                onBind {
//                    getBinding<ItemSelectBinding>().content.text = getModel()
//                }
//            }.models = arrayListOf(
//                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
//                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
//            )
//            list.linear().setup {
//                singleMode = false
//                addType<String>(R.layout.item_friends)
//            }.models = arrayListOf("", "", "", "", "", "", "", "")
//        }.show()
//}
//
///**
// * 显示充值钻石弹框
// */
// fun showCzDialog(activity: AppCompatActivity) {
//    DialogLayer(activity)
//        .cancelableOnTouchOutside(true)
//        .contentView(R.layout.dialog_cz)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .onDismiss {
//        }
//        .onClickToDismiss(R.id.close) {
//
//        }.onInitialize {
//            val list = findViewById<RecyclerView>(R.id.list)
//            val setup1 = list!!.grid(3).dividerSpace(12.dp)
//                .dividerSpace(8.dp, DividerOrientation.VERTICAL).setup {
//                    addType<Int>(R.layout.item_cz)
//                    addType<String>(R.layout.item_cz1)
//                    R.id.input.onClick {
//                        dismiss()
//                        showCzZsDialog(activity)
//                    }
//                }
//            setup1.addModels(arrayListOf(1, 1, 1, 1, 1))
//            setup1.addModels(arrayListOf(""))
//        }.show()
//}
//
///**
// * 显示软键盘（输入法）（只适用于Activity，不适用于Fragment）
// */
//fun showSoftKeyboard2(activity: Activity) {
//    val view: View? = activity.currentFocus
//    if (view != null) {
//        val inputMethodManager =
//            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//    }
//}
///**
// * 显示充值钻石弹框·1
// */
//
//@SuppressLint("SetTextI18n")
//private fun showCzZsDialog(activity: AppCompatActivity) {
//    DialogLayer(activity)
//        .contentView(R.layout.dialog_czzs)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            val pay = findViewById<TextView>(R.id.pay)
//            val mo = findViewById<EditText>(R.id.mo)
//            val p1 = findViewById<TextView>(R.id.p1)
//            val p2 = findViewById<TextView>(R.id.p2)
//            val p3 = findViewById<TextView>(R.id.p3)
//            val p4 = findViewById<TextView>(R.id.p4)
//            pay!!.onClick {
//                val trim = mo!!.text.toString().trim()
//                if (TextUtils.isEmpty(trim)) {
//                    activity.toast("请填写金额")
//                    return@onClick
//                }
//                dismiss()
//            }
//            p1!!.onClick { mo!!.setText("50") }
//            p2!!.onClick { mo!!.setText("200") }
//            p3!!.onClick { mo!!.setText("1000") }
//            p4!!.onClick { mo!!.setText("10000") }
//        }.show()
//    showSoftKeyboard2(activity)
//}
//
///**
// * 显示购物弹框
// */
//fun Activity.showCartDialog(isZb: Boolean) {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_live_cart)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            findViewById<RecyclerView>(R.id.list)!!.linear()
//                .setup {
//                    if (isZb) {
//                        addType<String>(R.layout.item_live_goods)
//                    } else {
//                        addType<String>(R.layout.item_live_goods_pay)
//                    }
//                }.models = arrayListOf("", "", "", "", "", "", "", "")
//            findViewById<ImageView>(R.id.close)!!.onClick { dismiss() }
//            val addGoods = findViewById<TextView>(R.id.addGoods)
//            if (isZb) {
//                addGoods!!.visible()
//            } else {
//                addGoods!!.gone()
//            }
//            addGoods!!.onClick {
//                startActivity<AddLiveGoodsActivity>()
//                dismiss()
//            }
//        }.show()
//}
//
///**
// * 显示美食弹框
// */
//fun Activity.showPlaceMoreDialog(
//    isCollection: Boolean
//) {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_map_food)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            findViewById<RecyclerView>(R.id.place_list)!!.linear()
//                .setup { addType<String>(R.layout.item_map_location) }
//                .models = arrayListOf("", "", "", "", "", "", "", "")
//            findViewById<ImageView>(R.id.cancel_btn)!!.onClick {
//                dismiss()
//            }
//            findViewById<TextView>(R.id.title)!!.text = if (isCollection) "收藏" else "美食"
//            findViewById<ImageView>(R.id.img)!!.setImageResource(if (isCollection) R.mipmap.icon_map_collect else R.mipmap.icon_map_food)
//        }.show()
//}
//
///**
// * 显示好友弹框
// */
//fun Activity.showAppointment(owner: LifecycleOwner) {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_map_appointment)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            val recyclerView = findViewById<RecyclerView>(R.id.list)
//            recyclerView!!.grid(5).setup {
//                addType<String>(R.layout.item_map_appointment)
//                onBind {
//                    getBinding<ItemMapAppointmentBinding>().avatar.setImageResource(
//                        if (getModel<String>().isEmpty()) R.drawable.icon_map_firend_add
//                        else R.drawable.default_avatar
//                    )
//                }
//                R.id.avatar.onClick {
//                    //选择好友
//                    val intent = Intent()
//                    intent.setClass(
//                        context,
//                        AddFriendsActivity::class.java
//                    )
//                    intent.putExtra("type", 1)
//                    startActivity(intent)
//                }
//            }.models = arrayListOf("")
//            owner.receiveEvent<Int>("selectFriends") {
//                val list = arrayListOf<String>().apply {
//                    for (i in 0 until it) add("1")
//                }
//                recyclerView.addModels(list, index = recyclerView.models!!.indexOf(""))
//            }
//        }.show()
//}
//
//
///**
// * 显示地点弹框
// */
//fun Activity.showPlaceDialog() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_map_location)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            findViewById<RecyclerView>(R.id.place_list)!!.linear()
//                .setup { addType<String>(R.layout.item_map_location) }
//                .models = arrayListOf("", "", "", "", "", "", "", "")
//            findViewById<ImageView>(R.id.collect_btn)!!.onClick {
//                activity.showPlaceMoreDialog(true)
//            }
//            findViewById<TextView>(R.id.food_btn)!!.onClick {
//                activity.showPlaceMoreDialog(false)
//
//            }
//            findViewById<ImageView>(R.id.cancel_btn)!!.onClick { dismiss() }
//        }.show()
//}
//
///**
// * 显示我的表情弹框
// */
//fun Activity.showEmojiDialog() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_map_emoji)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            val recyclerView = findViewById<RecyclerView>(R.id.list)
//            recyclerView!!.grid(4).dividerSpace(12.dp)
//                .dividerSpace(8.dp, DividerOrientation.VERTICAL).setup {
//                    addType<String>(R.layout.item_map_emoji)
//                }.models = arrayListOf("", "", "", "", "", "", "")
//        }.show()
//}
///**
// * 显示好友弹框
// */
//fun Activity.showFriendsDialog() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_map_friends)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//            findViewById<TextView>(R.id.add_friend_btn)!!.onClick {
//                start(AddFriendsActivity::class.java)
//            }
//        }.show()
//}
//
///**
// * 显示分享代码弹框
// */
//fun Activity.showShareCodeDialog() {
//    DialogLayer(this)
//        .contentView(R.layout.dialog_code)
//        .gravity(Gravity.BOTTOM)
//        .backgroundDimDefault()
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .animStyle(AnimStyle.BOTTOM)
//        .cancelableOnTouchOutside(true)
//        .onDismiss {}
//        .onInitialize {
//
//        }.show()
//}
//
//
//
//
//
