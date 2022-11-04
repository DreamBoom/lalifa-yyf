package com.lalifa.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.drake.statusbar.immersive
import com.drake.statusbar.statusPadding
import com.lalifa.base.databinding.CommonLayoutBinding
import com.lalifa.extension.applyVisible
import com.lalifa.extension.onClick

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