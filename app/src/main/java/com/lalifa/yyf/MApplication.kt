package com.lalifa.yyf

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.multidex.MultiDex
import com.drake.logcat.LogCat
import com.drake.tooltip.ToastConfig
import com.drake.tooltip.interfaces.ToastFactory
import com.lalifa.api.InitNet
import com.lalifa.base.BaseApplication
import com.lalifa.ext.*
import com.lalifa.ext.Config.Companion.parser
import com.lalifa.utils.ImPushUtil
import com.lalifa.utils.SystemUtil
import com.lalifa.yyf.BuildConfig.YYF
import com.lalifa.yyf.ry.ModuleManager.manager
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiProvider
import com.vanniktech.emoji.emoji.EmojiCategory
import com.vanniktech.emoji.ios.category.*
import io.rong.imkit.RongIM
import io.rong.imkit.conversation.extension.RongExtensionManager
import io.rong.imlib.RongCoreClient
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.MessageContent

class MApplication : BaseApplication() {

    companion object {
        private lateinit var INSTANCE: BaseApplication
        fun get() = INSTANCE
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        val process = SystemUtil.getProcessName()
        // 过滤非主进程
        if (!TextUtils.equals(process, packageName)) {
            return
        }
        INSTANCE = this
        if (null == modes) modes = ArrayList<String?>()
        modes!!.clear()
        modes!!.add(YYF!!.toString())
        manager().onInit()
        initConfig()
    }
    private var modes: MutableList<String?>? = null
    /**
     * 初始化通用吐司View
     */
    private fun initToast() {
        ToastConfig.initialize(get(), object : ToastFactory {
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

    /**
     * 初始化日志打印工具 列表空界面
     */
    private fun initConfig() {
        //初始化表情库
        EmojiManager.install(MyEmojiProvider.emojiProviderInstance)
        //初始化SVGA动画
        parser.init(this)
        //初始化IM
        ImPushUtil.getInstance(this).init()
        RongIM.init(this, Config.RONG_APP_KEY, true)
        RongCoreClient.init(this,Config.RONG_APP_KEY)
        LogCat.setDebug(BuildConfig.DEBUG, MApplication.get().getString(R.string.app_name))
        //注册自定义消息
        val myMessages = ArrayList<Class<out MessageContent?>>()
        myMessages.add(MyMediaMessageContent::class.java)
        RongIMClient.registerMessageType(myMessages)
        //注册自定义键盘
        RongExtensionManager.getInstance().extensionConfig = MyGiftConfig()
        initToast()
        InitNet.initNetHttp(this)
    }
    //表情库
    class MyEmojiProvider : EmojiProvider {
        override fun getCategories(): Array<EmojiCategory> {
            return arrayOf(
                SmileysAndPeopleCategory(),
                AnimalsAndNatureCategory(),
                FoodAndDrinkCategory(),
                ActivitiesCategory(),
                TravelAndPlacesCategory(),
                ObjectsCategory(),
                SymbolsCategory()
            )
        }

        companion object {
            val emojiProviderInstance = MyEmojiProvider()
        }
    }
}