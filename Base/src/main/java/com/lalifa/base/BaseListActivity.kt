package com.lalifa.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.page
import com.drake.statusbar.immersive
import com.drake.statusbar.statusPadding
import com.lalifa.base.databinding.CommonListLayoutBinding
import com.lalifa.extension.applyVisible
import com.lalifa.extension.onClick

abstract class BaseListActivity : AppCompatActivity() {
    private lateinit var mainBinding: CommonListLayoutBinding
    lateinit var recyclerView: RecyclerView
    lateinit var refreshLayout: PageRefreshLayout
    open fun hasRefresh(): Boolean = true

    //标题文本
    open fun title(): String = ""

    open fun isCanExit(): Boolean = false

    //返回文本
    open fun blackText(): String = ""

    //返回图标
    open fun blackImage(): Int = R.drawable.ic_base_back

    //标题颜色
    open fun titleColor(): Int = 0

    //返回文本颜色
    open fun blackColor(): Int = 0

    //文本菜单颜色
    open fun textColor(): Int = 0

    //标题右边图标
    open fun titleRightIcon(): Int = 0

    //右边文本按钮
    open fun textMenu(): String = ""
    open fun textBg(): Boolean = false

    //右边图片按钮
    open fun imageMenu(): Int = 0

    //是否隐藏标题栏  false 显示  true 隐藏
    open fun topBarHide(): Boolean = false


    //是否需要状态的宽度
    open fun startPadding(): Boolean = true

    //深色状态栏 true 深色 false 浅色
    open fun darkMode(): Boolean = true
    open fun darkMode(darkMode: Boolean) {
        immersive(darkMode = darkMode)
    }

    //设置背景颜色
    open fun setBackgroundColor(color: Int) {
        mainBinding.content.setBackgroundColor(color)
    }

    open fun getTopBar() = mainBinding.topBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this::mainBinding.isInitialized) mainBinding.content.removeAllViews()
        mainBinding = CommonListLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(mainBinding.root)
        recyclerView = RecyclerView(this)
        mainBinding.content.addView(recyclerView, -1, -1)
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        recyclerView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        if (hasRefresh()) {
            refreshLayout = recyclerView.page()
            refreshLayout.setPrimaryColors(ContextCompat.getColor(this, R.color.black))
            refreshLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            refreshLayout.onRefresh {
                getData()
            }
        }
        iniTitleBar()
        darkMode(darkMode())
        iniView()
        onData()
        onClick()
    }


    //数据填充
    open fun onData() {}
    open fun PageRefreshLayout.getData() {}

    //点击事件
    open fun onClick() {}
    fun getTextMenu(): TextView = mainBinding.textMenu
    fun setTitle(title: String) {
        mainBinding.title.text = title
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun iniTitleBar() {
        mainBinding.start.statusPadding()
        mainBinding.start.applyVisible(startPadding())
        mainBinding.topBar.applyVisible(!topBarHide())
        mainBinding.blackImage.applyVisible(blackText().isEmpty())
        mainBinding.title.text = title()
        mainBinding.blackText.text = blackText()
        mainBinding.textMenu.text = textMenu()
        if (textBg()) {
            mainBinding.textMenu.setBackgroundResource(R.drawable.blue_btn)
        }
        if (titleColor() != 0) mainBinding.title.setTextColor(getColor(titleColor()))
        if (blackColor() != 0) mainBinding.blackText.setTextColor(
            getColor(
                blackColor()
            )
        )
        if (textColor() != 0) mainBinding.textMenu.setTextColor(getColor(textColor()))
        if (blackImage() != 0) mainBinding.blackImage.setImageResource(blackImage())
        if (imageMenu() != 0) mainBinding.imageMenu.setImageResource(imageMenu())

        if (titleRightIcon() != 0) {
            val rightIcon = this.getDrawable(titleRightIcon());
            rightIcon?.setBounds(0, 0, rightIcon.minimumWidth, rightIcon.minimumHeight);
            mainBinding.title.setCompoundDrawables(null, null, rightIcon, null)
        }

        mainBinding.blackText.onClick {
            onBlackListener()
        }
        mainBinding.blackImage.onClick {
            Log.e("TAG", "iniTitleBar: ")
            onBlackListener()
        }
        mainBinding.imageMenu.onClick {
            onImageMenuListener()
        }
        mainBinding.textMenu.onClick {
            onTextMenuListener()
        }
    }

    //返回按钮监听
    open fun onBlackListener() {
        finish()
    }

    //右边文本按钮监听
    open fun onTextMenuListener() {

    }

    //右边图片按钮监听
    open fun onImageMenuListener() {}
    abstract fun iniView()
    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isCanExit()) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(
                        applicationContext,
                        "再按一次退出",
                        Toast.LENGTH_SHORT
                    ).show()
                    exitTime = System.currentTimeMillis()
                } else {
                    BaseApplication.get().exit()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}