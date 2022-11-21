package com.lalifa.yyf.app

import android.app.Application
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.jpush.android.api.JPushInterface
import cn.rongcloud.config.UserManager
import com.drake.channel.sendTag
import com.drake.net.utils.TipUtils.toast
import com.drake.tooltip.ToastConfig
import com.drake.tooltip.interfaces.ToastFactory
import com.lalifa.api.JsonHttpConverter
import com.lalifa.api.NetHttp
import com.lalifa.ext.Config
import com.lalifa.ext.Config.Companion.HOST
import com.lalifa.extension.pk
import com.lalifa.yyf.MApplication
import com.lalifa.yyf.R
import io.rong.imkit.RongIM
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongCoreClient


object App {
    fun init() {
        initToast()
    }

    /**
     * 初始化通用吐司View
     */
    private fun initToast() {
        ToastConfig.initialize(MApplication.get(), object : ToastFactory {
            override fun onCreate(
                context: Application,
                message: CharSequence,
                duration: Int,
                tag: Any?
            ): Toast? {
                val toast = Toast.makeText(context, message, duration)
                val view = View.inflate(context, R.layout.view_toast, null)
                view.findViewById<TextView>(android.R.id.message).text = message
                toast.view = view
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                return toast
            }

        })
    }
}