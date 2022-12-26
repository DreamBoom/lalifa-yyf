package com.lalifa.main.activity.room.ext

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcq.adapter.interfaces.IAdapte
import com.bcq.adapter.recycle.RcyHolder
import com.bcq.adapter.recycle.RcySAdapter
import com.drake.net.utils.scopeNet
import com.kit.UIKit
import com.kit.wapper.IResultBack
import com.lalifa.ext.Account
import com.lalifa.extension.noEN
import com.lalifa.extension.pk
import com.lalifa.main.R
import com.lalifa.main.api.getMembers
import com.lalifa.main.activity.room.widght.QDialog
import com.lalifa.main.api.Member
import java.util.*

class ApiFunDialogHelper {
    private var dialog: QDialog? = null

    interface OnApiClickListener {
        fun onApiClick(v: View?, `fun`: ApiFun?)
    }

    fun showSeatApiDialog(activity: Activity, listener: OnApiClickListener) {
        showApiDialog(activity, "麦位Api功能演示", SEAT_API, listener)
    }

    fun showRoomApiDialog(activity: Activity, listener: OnApiClickListener) {
        showApiDialog(activity, "房间Api功能演示", ROOM_API, listener)
    }

    /**
     * 显示api演示弹框
     *
     * @param activity
     * @param title    标题
     * @param apis     api名称
     * @param listener 监听
     */
    private fun showApiDialog(
        activity: Activity,
        title: String,
        apis: Array<ApiFun>,
        listener: OnApiClickListener
    ) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog = null }
        }
        dialog!!.replaceContent(
            title,
            "取消",
            { dismissDialog() },
            "",
            null,
            initApiFunView(apis, listener)
        )
        dialog!!.show()
    }

    private fun initApiFunView(apiNames: Array<ApiFun>, listener: OnApiClickListener): View {
        val refresh = RecyclerView(
            dialog!!.context
        )
        refresh.layoutManager = GridLayoutManager(dialog!!.context, 2)
        val adapter: IAdapte<ApiFun, RcyHolder> = ApiAdapter(dialog!!.context, listener)
        adapter.setRefreshView(refresh)
        adapter.setData(listOf(*apiNames), true)
        return refresh
    }

    fun dismissDialog() {
        if (null != dialog) {
            dialog!!.dismiss()
        }
        dialog = null
    }

    /**
     * 显示编辑框
     *
     * @param activity
     * @param title    标题
     */
    fun showEditorDialog(activity: Activity?, title: String?, resultBack: IResultBack<String?>?) {
        showEditorDialog(activity, title, "", resultBack)
    }

    fun showEditorDialog(
        activity: Activity?,
        title: String?,
        cofirm: String?,
        resultBack: IResultBack<String?>?
    ) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog = null }
        }
        val editText = EditText(dialog!!.context)
        editText.hint = "请输出房间id"
        dialog!!.replaceContent(
            title,
            "",
            null,
            if (TextUtils.isEmpty(cofirm)) "新建/加入" else cofirm,
            {
                dismissDialog()
                val roomId = editText.text.toString().trim { it <= ' ' }
                resultBack?.onResult(roomId)
            },
            editText
        )
        dialog!!.show()
    }

    /**
     * 显示选择邀请观众的弹框
     *
     * @param activity
     * @param title
     * @param resultBack
     */
    fun showSelectDialog(
        activity: Activity?, roomId: String?, title: String?,
        resultBack: IResultBack<Member?>?
    ) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog = null }
        }
        val refresh = RecyclerView(
            dialog!!.context
        )
        val adapter = object : RcySAdapter<Member, RcyHolder>(
            dialog!!.context, R.layout.layout_item_selector
        ) {
            override fun convert(holder: RcyHolder, accout: Member, position: Int) {
                holder.setText(R.id.selector_name, accout.userName)
                holder.itemView.setOnClickListener { resultBack?.onResult(accout) }
            }
        }
        refresh.layoutManager = LinearLayoutManager(dialog!!.context)
        adapter.setRefreshView(refresh)
//        scopeNet {
//            getMembers(roomId!!.noEN())?.forEach {
//                //将自己排除
//                if (!it.userId.equals(AccountManager.currentId)) {
//                    AccountManager.setAccount(it.toAccount(), false)
//                }
//            }
//        }
        adapter.setData(Member.getMembers(), true)
        dialog!!.replaceContent(
            title,
            "取消",
            { dismissDialog() },
            "",
            null,
            refresh
        )
        dialog!!.show()
    }

    fun showSelectDialog(
        activity: Activity?,
        title: String?,
        resultBack: IResultBack<Member?>?,
        all: Boolean
    ) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog: DialogInterface? -> this@ApiFunDialogHelper.dialog = null }
        }
        val refresh = RecyclerView(
            dialog!!.context
        )
        val adapter = object : RcySAdapter<Member, RcyHolder>(
            dialog!!.context, R.layout.layout_item_selector
        ) {
            override fun convert(holder: RcyHolder, accout: Member, position: Int) {
                holder.setText(R.id.selector_name, accout.userName)
                holder.itemView.setOnClickListener { view: View? -> resultBack?.onResult(accout) }
            }
        }
        refresh.layoutManager = LinearLayoutManager(dialog!!.context)
        adapter.setRefreshView(refresh)
        if (all) {
            adapter.setData(Member.getMembers(), true)
        } else {
            val accounts = Member.getMembers()
            val onlines: MutableList<Member> = ArrayList()
            for (a in accounts) {
                val id = a.userId
                // 排除自己
                if (id != Member.currentId && QuickEventListener.get().audienceIds.contains(
                        a.userId
                    )
                ) {
                    onlines.add(a)
                }
            }
            adapter.setData(onlines, true)
        }
        dialog!!.replaceContent(
            title,
            "取消",
            { v: View? -> dismissDialog() },
            "",
            null,
            refresh
        )
        dialog!!.show()
    }

    fun showTipDialog(activity: Activity?, message: String?, resultBack: IResultBack<Boolean?>?) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog: DialogInterface? -> this@ApiFunDialogHelper.dialog = null }
        }
        val textView = TextView(dialog!!.context)
        textView.text = message
        textView.textSize = 18f
        textView.setTextColor(Color.parseColor("#343434"))
        UIKit.runOnUiTherad {
            dialog!!.replaceContent(
                "提示",
                "取消",
                { v: View? ->
                    dismissDialog()
                    resultBack?.onResult(false)
                },
                "确定",
                { v: View? ->
                    dismissDialog()
                    resultBack?.onResult(true)
                },
                textView
            )
        }
        dialog!!.show()
    }

    fun showTipDialog(
        activity: Activity?,
        title: String?,
        message: String?,
        resultBack: IResultBack<Boolean?>?
    ) {
        if (null == dialog || !dialog!!.enable()) {
            dialog = QDialog(
                activity
            ) { dialog: DialogInterface? -> this@ApiFunDialogHelper.dialog = null }
        }
        val textView = TextView(dialog!!.context)
        textView.text = message
        textView.textSize = 18f
        textView.setTextColor(Color.parseColor("#343434"))
        UIKit.runOnUiTherad {
            dialog!!.replaceContent(
                title,
                "拒绝",
                { v: View? ->
                    dismissDialog()
                    resultBack?.onResult(false)
                },
                "同意",
                { v: View? ->
                    dismissDialog()
                    resultBack?.onResult(true)
                },
                textView
            )
        }
        dialog!!.show()
    }

    /**
     * api功能适配器
     */
    private class ApiAdapter(context: Context, listener: OnApiClickListener) :
        RcySAdapter<ApiFun, RcyHolder>(context, R.layout.layout_seat_item) {
        private val listener: OnApiClickListener?

        init {
            this.listener = listener
        }

        override fun convert(holder: RcyHolder, s: ApiFun, position: Int) {
            holder.setText(R.id.api_fun, s.value.pk(""))
            holder.itemView.setOnClickListener { view: View? ->
                listener?.onApiClick(view, s)
                seatApi.dismissDialog()
            }
        }
    }

    companion object {
        private val seatApi = ApiFunDialogHelper()
        val SEAT_API = arrayOf(
            ApiFun.seat_mute,
            ApiFun.seat_mute_un,
            ApiFun.seat_lock,
            ApiFun.seat_lock_un,
            ApiFun.seat_enter,
            ApiFun.seat_enter_plugin,
            ApiFun.seat_left,
            ApiFun.seat_left_plugin,
            ApiFun.seat_request,
            ApiFun.seat_request_cancel,
            ApiFun.seat_close_mic,
            ApiFun.seat_open_mic,
            ApiFun.seat_switch,
            ApiFun.seat_switch_plugin,
            ApiFun.seat_update,
            ApiFun.seat_update_plugin,
            ApiFun.seat_pick_out
        )
        val ROOM_API = arrayOf(
            ApiFun.room_all_mute,
            ApiFun.room_all_mute_un,
            ApiFun.room_all_lock,
            ApiFun.room_all_lock_nu,
            ApiFun.room_update_name,
            ApiFun.room_update_count,
            ApiFun.room_free,
            ApiFun.room_free_un,
            ApiFun.invite_seat,
            ApiFun.invite_pk,
            ApiFun.invite_pk_cancel,
            ApiFun.invite_pk_mute,
            ApiFun.invite_quit_pk
        )

        @JvmStatic
        fun helper(): ApiFunDialogHelper {
            return seatApi
        }
    }
}