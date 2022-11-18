package com.lalifa.yyf

//import com.tencent.bugly.crashreport.CrashReport
import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.multidex.MultiDex
import cn.jpush.android.api.JPushInterface
import cn.rongcloud.config.AppConfig
import cn.rongcloud.config.init.ModuleManager
import cn.rongcloud.config.router.ARouterWrapper
import cn.rongcloud.roomkit.RoomKitInit
import cn.rongcloud.thirdcdn.ThirdCDNConstant
import com.lalifa.base.BaseApplication
import com.lalifa.ext.Config
import com.lalifa.utils.SystemUtil
import com.lalifa.utils.UIKit
import com.lalifa.yyf.BuildConfig.*
import com.lalifa.yyf.app.App
import io.rong.imkit.RongIM

class MApplication : BaseApplication() {

    companion object {
        const val DELAY = 500
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
        // 过滤非主进程
        if (!TextUtils.equals(process, packageName)) {
            return
        }
        INSTANCE = this
        App.init()
        initConfig()
    }

    /**
     * 初始化日志打印工具 列表空界面
     */
    private fun initConfig() {
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        //LogCat.setDebug(BuildConfig.DEBUG, MApplication.get().getString(R.string.app_name))
        ARouterWrapper.init(this)
        AppConfig.get().init(
            APP_KEY,
            UM_APP_KEY,
            BASE_SERVER_ADDRES,
            BUSINESS_TOKEN,
            INTERIAL,
            YYF!!
        )
        // init rong
        ModuleManager.manager()
            .register(RoomKitInit())
        //初始化 bugly
//        CrashReport.initCrashReport(this, BUGLY_ID, DEBUG)
        // 设置三方CDN推拉流地址
        ThirdCDNConstant.setPushAndPullUrl(
            THIRD_CDN_PUSH_URL,
            THIRD_CDN_PULL_URL
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouterWrapper.destory()
        ModuleManager.manager().unregister()
    }
}