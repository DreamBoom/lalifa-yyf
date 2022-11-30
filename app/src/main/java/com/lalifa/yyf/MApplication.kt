package com.lalifa.yyf

import android.content.Context
import android.text.TextUtils
import androidx.multidex.MultiDex
import cn.rongcloud.config.AppConfig
import cn.rongcloud.config.init.ModuleManager
import cn.rongcloud.config.router.ARouterWrapper
import cn.rongcloud.roomkit.RoomKitInit
import cn.rongcloud.thirdcdn.ThirdCDNConstant
import com.alibaba.android.arouter.launcher.ARouter
import com.lalifa.base.BaseApplication
import com.lalifa.utils.ImPushUtil
import com.lalifa.utils.SystemUtil
import com.lalifa.yyf.BuildConfig.*
import com.lalifa.yyf.app.App

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
        if (!TextUtils.equals(process, packageName)) {
            return
        }
        INSTANCE = this
        App.init()
        initConfig()
        AppConfig.initNetHttp(this)
    }

    /**
     * 初始化日志打印工具 列表空界面
     */
    private fun initConfig() {
        ImPushUtil.getInstance(this).init()
        //LogCat.setDebug(BuildConfig.DEBUG, MApplication.get().getString(R.string.app_name))
        if (BuildConfig.DEBUG) {
            // 两行开启日志代码必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()  // 打印日志
            ARouter.openDebug()// 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouterWrapper.init(this)
        ARouter.init(this);
        AppConfig.get().init(
            APP_KEY,
            UM_APP_KEY,
            BASE_SERVER_ADDRES,
            INTERIAL,
            YYF!!
        )
        // init rong
        ModuleManager.manager()
            .register(RoomKitInit())
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