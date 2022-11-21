package com.lalifa.yyf

import android.annotation.SuppressLint
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.extension.start
import com.lalifa.extension.toast
import com.lalifa.extension.uiTask
import com.lalifa.main.activity.MainActivity
import com.lalifa.utils.SPUtil
import com.lalifa.yyf.databinding.ActivitySplashBinding
import com.lalifa.main.activity.login.LoginActivity

/**
 * 起始页
 */
@SuppressLint("CustomSplashScreen")
@Route(path = "/app/splash")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.CAMERA)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (!all) {
                        toast("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                    uiTask(1000) {
                        if (SPUtil.getBoolean(Config.IS_LOGIN)) {
                            start(MainActivity::class.java)
                            finish()
                        } else {
                            start(LoginActivity::class.java)
                            finish()
                        }
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        toast("被永久拒绝授权，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@SplashActivity, permissions)
                    } else {
                        toast("获取权限失败")
                    }
                }
            })
    }
}