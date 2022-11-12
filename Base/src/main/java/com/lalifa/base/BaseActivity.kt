package com.lalifa.base

import androidx.viewbinding.ViewBinding

/**
 *
 * @ClassName BaseActivity
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/6/30 16:24
 * @Des
 */
abstract class BaseActivity<T : ViewBinding> : BaseTitleActivity<T>() {
    override fun topBarHide() = true
}