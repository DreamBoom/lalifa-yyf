package cn.rongcloud.config

import android.content.Context
import android.text.TextUtils
import cn.rongcloud.config.AppConfig
import cn.rongcloud.config.init.ModuleManager
import io.rong.imkit.conversation.extension.RongExtensionManager
import cn.rongcloud.config.ryutiles.MyGiftConfig
import com.drake.channel.sendTag
import com.drake.net.utils.TipUtils
import com.lalifa.api.JsonHttpConverter
import com.lalifa.api.NetHttp
import com.lalifa.ext.Config
import com.lalifa.extension.pk
import java.util.ArrayList

class AppConfig {
    private object Holder {
        val INSTANCE = AppConfig()
    }

    fun init(
        appKey: String,
        umengAppKey: String,
        baseServerAddress: String,
        businessToken: String,
        international: Boolean,
        busiModes: Array<String?>?
    ) {
        this.appKey = appKey
        this.umengAppKey = umengAppKey
        this.baseServerAddress = baseServerAddress
        this.businessToken = businessToken
        isInternational = international
        if (null == modes) modes = ArrayList<String?>()
        modes!!.clear()
        if (null != busiModes) {
            for (mode in busiModes) {
                modes!!.add(mode)
            }
        }
        ModuleManager.manager().onInit()
        RongExtensionManager.getInstance().extensionConfig = MyGiftConfig()
    }

    var appKey = ""
        private set
    var umengAppKey = ""
        private set
    var baseServerAddress = ""
        private set
    var businessToken = ""
        private set
    var isInternational = false
        private set
    private var modes: MutableList<String?>? = null

    /**
     * 判断mode是否配置
     *
     * @param modeName 功能模块
     * @return 是否配置
     */
    fun hasMode(modeName: String?): Boolean {
        return modes!!.contains(modeName)
    }

    fun getModes(): List<String?> {
        return ArrayList(modes)
    }

    companion object {
        @JvmStatic
        fun get(): AppConfig {
            return Holder.INSTANCE
        }
        /**
         * 初始化网络请求，全局配置
         */
        fun initNetHttp(context: Context) {
            NetHttp.init(context, Config.HOST, JsonHttpConverter(),
                block = {
                    //全局请求头/参数
                    //addHeader("Content-Type","application/json; charset=utf-8")
                    if(UserManager.get()!=null&&!TextUtils.isEmpty(UserManager.get()!!.token)){
                        addHeader("token", UserManager.get()!!.token.pk(""))
                    }
                }, error = {
                    TipUtils.toast(it.message)
                    when (it.code) {
                        102 -> {
                            //token失效
                            sendTag("HttpLogout")
                        }
                    }
                })
        }
    }
}