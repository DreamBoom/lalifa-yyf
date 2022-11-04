package com.lalifa.callback

import android.app.Activity
import android.app.Application

import android.os.Bundle




/**
 *
 * @ClassName LifecycleCallbacks
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/5 18:55
 *
 */
open class LifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}