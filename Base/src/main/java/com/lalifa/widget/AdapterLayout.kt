package com.lalifa.widget

import android.database.DataSetObserver

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View

import android.view.ViewGroup
import java.lang.NullPointerException
import android.database.DataSetObservable
import com.lalifa.callback.LifecycleCallbacks


/**
 *
 * @ClassName AdapterLayout
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/5 18:54
 *
 */
abstract class AdapterLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ViewGroup(context, attrs, defStyleAttr) {
    protected var mAdapter: XBaseAdapter? = null
    protected var mObserver: DataSetObserver? = null
    private var isRegister = false

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    /**
     * 设置Adapter
     */
    fun setAdapter(adapter: XBaseAdapter?) {
        // 移除监听
        unRegisterAdapter()
        if (adapter == null) {
            throw NullPointerException("FlowBaseAdapter is null")
        }
        mAdapter = adapter
        mObserver = object : DataSetObserver() {
            override fun onChanged() {
                resetLayout()
            }
        }

        // 注册监听
        registerAdapter()
        resetLayout()
    }

    override fun onDetachedFromWindow() {
        unRegisterAdapter()
        super.onDetachedFromWindow()
    }

    private fun unRegisterAdapter() {
        // 移除监听
        if (mAdapter != null && mObserver != null && isRegister) {
            isRegister = false
            mAdapter!!.unregisterDataSetObserver(mObserver!!)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerAdapter()
    }

    private fun registerAdapter() {
        // 添加监听
        if (mAdapter != null && mObserver != null && !isRegister) {
            mAdapter!!.registerDataSetObserver(mObserver!!)
            isRegister = true
        }
    }

    /**
     * 重新添加布局
     */
    protected open fun resetLayout() {
        if (mAdapter == null) {
            return
        }
        removeAllViews()
        val count: Int = mAdapter!!.count
        for (i in 0 until count) {
            val view: View = mAdapter!!.getView(i, this)!!
            view.isFocusable = true
            addView(view)
        }
    }

    init {
        if (context is Activity) {
            context.application.registerActivityLifecycleCallbacks(object :
                LifecycleCallbacks() {
                override fun onActivityDestroyed(p0: Activity) {
                    if (p0 === context) {
                        // 移除监听
                        if (mAdapter != null && mObserver != null) {
                            mAdapter!!.unregisterDataSetObserver(mObserver!!)
                            mAdapter = null
                            mObserver = null
                            //mViews = null;
                        }
                        context.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
            })
        }
    }
}
abstract class XBaseAdapter {
    private val mObservable = DataSetObservable()

    /**
     * 数量
     */
    abstract val count: Int

    /**
     * 条目的布局
     */
    abstract fun getView(position: Int, parent: ViewGroup?): View?

    /**
     * 注册数据监听
     */
    fun registerDataSetObserver(observer: DataSetObserver) {
        try {
            mObservable.registerObserver(observer)
        } catch (e: Throwable) {
        }
    }

    /**
     * 移除数据监听
     */
    fun unregisterDataSetObserver(observer: DataSetObserver) {
        try {
            mObservable.unregisterObserver(observer)
        } catch (e: Throwable) {
        }
    }

    /**
     * 内容改变
     */
    fun notifyDataSetChanged() {
        mObservable.notifyChanged()
    }
}