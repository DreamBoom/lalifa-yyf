package com.lalifa.yyf

import android.text.TextUtils
import cn.rongcloud.config.AppConfig
import cn.rongcloud.config.BuildConfig
import cn.rongcloud.config.feedback.SensorsUtil
import cn.rongcloud.config.init.ModuleManager
import cn.rongcloud.config.router.ARouterWrapper
import cn.rongcloud.music.MusicInit
import cn.rongcloud.pk.PKInit
import cn.rongcloud.roomkit.RoomKitInit
import cn.rongcloud.thirdcdn.ThirdCDNConstant
import cn.rongcloud.voiceroom.utils.VMLog
import com.drake.statelayout.StateConfig
import com.lalifa.base.BaseApplication
import com.lalifa.utils.SystemUtil
import com.lalifa.yyf.BuildConfig.*
import com.lalifa.yyf.app.App
//import com.tencent.bugly.crashreport.CrashReport
import io.rong.imlib.RongCoreClient

class MApplication : BaseApplication() {

    companion object {
        private lateinit var INSTANCE: BaseApplication
        fun get() = INSTANCE
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        App.init()
        val process: String = SystemUtil.getProcessName()
        // 过滤非主进程
        if (!TextUtils.equals(process, packageName)) {
            return
        }
        initConfig()
    }

    /**
     * 初始化日志打印工具 列表空界面
     */
    private fun initConfig() {
        //LogCat.setDebug(BuildConfig.DEBUG, MApplication.get().getString(R.string.app_name))
        StateConfig.apply {
            emptyLayout = com.lalifa.base.R.layout.layout_common_empty
            loadingLayout = com.lalifa.base.R.layout.layout_common_loding
        }
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
            .register(RoomKitInit(), MusicInit(), PKInit())
        //初始化 bugly
//        CrashReport.initCrashReport(this, BUGLY_ID, DEBUG)

        initSensorsData()
        // 设置三方CDN推拉流地址
        ThirdCDNConstant.setPushAndPullUrl(
            THIRD_CDN_PUSH_URL,
            THIRD_CDN_PULL_URL
        )
    }

    /**
     * 神策埋点
     */
    private fun initSensorsData() {
        SensorsUtil.instance().init(this, VERSION_NAME, SENSORS_URL)
        //初始化公共属性
        val currentUserId = RongCoreClient.getInstance().currentUserId
        SensorsUtil.instance().registerSuperProperties(!TextUtils.isEmpty(currentUserId))
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouterWrapper.destory()
        ModuleManager.manager().unregister()
    }
}