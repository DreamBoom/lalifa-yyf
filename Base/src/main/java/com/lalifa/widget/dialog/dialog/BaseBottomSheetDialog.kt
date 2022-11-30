package com.lalifa.widget.dialog.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import com.drake.logcat.LogCat.e
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lalifa.base.R
import com.lalifa.utils.ScreenUtil
import com.lalifa.widget.dialog.dialog.BaseBottomSheetDialog
import java.lang.Exception

/**
 * Created by gyn on 2021/11/23
 */
abstract class BaseBottomSheetDialog(@field:LayoutRes @param:LayoutRes private val layoutId: Int) :
    BottomSheetDialogFragment() {
    private var mBehavior: BottomSheetBehavior<View>? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, layoutId, null)
        dialog.setContentView(view)
        val parent = view.parent as View
        mBehavior = BottomSheetBehavior.from(parent)
        mBehavior!!.isHideable = isHideable
        mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        if (isFullScreen) {
            mBehavior!!.peekHeight = ScreenUtil.getScreenHeight()
            parent.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    abstract fun initView()
    override fun onDetach() {
        super.onDetach()
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            e(TAG, e.localizedMessage)
        }
    }

    /**
     * 是否可以拖动关闭
     *
     * @return
     */
    protected open val isHideable: Boolean
        protected get() = false

    /**
     * 是否全屏
     *
     * @return
     */
    protected open val isFullScreen: Boolean
        protected get() = false

    /**
     * 初始化监听
     */
    protected open fun initListener() {}

    companion object {
        private val TAG = BaseBottomSheetDialog::class.java.simpleName
    }
}