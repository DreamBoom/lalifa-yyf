package com.lalifa.widget

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @ClassName ViewPagerLayoutMannager
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/8/22 13:32
 * @Des
 */
class ViewPagerLayoutManager : LinearLayoutManager, RecyclerView.OnChildAttachStateChangeListener {
    private val pagerSnapHelper: PagerSnapHelper by lazy { PagerSnapHelper() }

    constructor(context: Context) : this(context, VERTICAL)
    constructor(context: Context, orientation: Int) : super(context, orientation, false)


    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        view ?: return
        pagerSnapHelper.attachToRecyclerView(view)
        view.addOnChildAttachStateChangeListener(this)
    }


    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        view?.removeOnChildAttachStateChangeListener(this)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        if (childCount == 1) pagerChangedListener?.onInit()
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        if (childCount == 1) pagerChangedListener?.onPageRelease(getPosition(view))
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            val viewIdle = pagerSnapHelper.findSnapView(this@ViewPagerLayoutManager) ?: return
            val positionIdle = getPosition(viewIdle)
            if (childCount == 1) {
                pagerChangedListener?.onPageSelected(positionIdle)
            }
        }
    }

    private var pagerChangedListener: OnPagerChangedListener? = null


    fun setOnPagerChangedListener(pagerChangedListener: OnPagerChangedListener) {
        this.pagerChangedListener = pagerChangedListener
    }


    interface OnPagerChangedListener {

        /**
         * 初始化
         */
        fun onInit()

        fun onPageSelected(position: Int)

        fun onPageRelease(position: Int)
    }
}