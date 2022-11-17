package com.lalifa.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.drake.brv.PageRefreshLayout
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * ApplicationBase
 */
abstract class BaseApplication : MultiDexApplication() {

    companion object {
        private lateinit var INSTANCE:BaseApplication
        fun get() = INSTANCE
    }
    fun exit(){
        activityList.forEach {
            it.finish()
        }
    }
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { _, _ -> ClassicsHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { _, _ -> ClassicsFooter(this) }
        PageRefreshLayout.startIndex = 1
        StateConfig.apply {
            emptyLayout = R.layout.layout_common_empty
            loadingLayout = R.layout.layout_common_loding
        }
        registerLife()
    }

    private val activityList = ArrayList<Activity>()
    private fun registerLife() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (!activityList.contains(activity)) {
                    activityList.add(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (activityList.contains(activity)) {
                    activityList.remove(activity)
                }
            }
        })
    }
}