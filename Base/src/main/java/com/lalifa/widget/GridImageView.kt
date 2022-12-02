package com.lalifa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.lalifa.base.R


/**
 *
 * @ClassName GridImageView
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/5 18:20
 * 加载库设置占位图会出现单张图片自适应大小错误
 */

/**
 * QQ空间类似的网格布局
 */
@SuppressLint("CustomViewStyleable")
class GridImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AdapterLayout(context, attrs, defStyleAttr) {
    /**
     * 列数
     */
    private var mSpan = 3

    /**
     * 自适应
     */
    private var isAuto = true

    /**
     * Item 水平之间的间距
     */
    private var mHorizontalSpace = 0

    /**
     * Item 垂直之间的间距
     */
    private var mVerticalSpace = 0

    /**
     * 最大的Item数量
     */
    private var mMaxItem = 9

    /**
     * 条目的宽高是否一样
     */
    private val isSquare: Boolean
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 获取控件的宽度
        val width = MeasureSpec.getSize(widthMeasureSpec)
        if (childCount <= 1 && isAuto) {
            if (childCount == 1 && isAuto) {
                val child: ImageView = getChildAt(0) as ImageView
                val drawable = child.drawable
                if (drawable != null) {
                    if (drawable.minimumWidth > drawable.minimumHeight) {
                        val itemWidth: Int = MeasureSpec.makeMeasureSpec(
                            (width - paddingLeft - paddingRight),
                            MeasureSpec.EXACTLY
                        )
                        val itemHeight = MeasureSpec.makeMeasureSpec(
                            drawable.minimumHeight * (width - paddingLeft - paddingRight) / drawable.minimumWidth,
                            MeasureSpec.EXACTLY
                        )
                        measureChild(child, itemWidth, itemHeight)
                        setMeasuredDimension(itemWidth, itemHeight)
                    } else {
                        val itemWidth: Int = MeasureSpec.makeMeasureSpec(
                            (width - paddingLeft - paddingRight) / 2,
                            MeasureSpec.EXACTLY
                        )
                        val itemHeight = MeasureSpec.makeMeasureSpec(
                            drawable.minimumHeight * ((width - paddingLeft - paddingRight) / 2) / drawable.minimumWidth,
                            MeasureSpec.EXACTLY
                        )
                        measureChild(child, itemWidth, itemHeight)
                        setMeasuredDimension(itemWidth, itemHeight)
                    }
                }
                return
            }
            setMeasuredDimension(0, 0)
            return
        }
        // 计算单个子View的宽度
        val itemWidth: Int =
            (width - paddingLeft - paddingRight - mHorizontalSpace * (mSpan - 1)) / mSpan
        // 测量子View的宽高
        var childCount: Int = childCount
        // 计算一下最大的条目数量

        childCount = childCount.coerceAtMost(mMaxItem)

        var height = 0
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            val itemSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY)
            // 判断条目宽高是否一样
            if (isSquare) {
                measureChild(child, itemSpec, itemSpec)
            } else {
                measureChild(child, itemSpec, heightMeasureSpec)
                if (i != 0) {
                    // 换行就加多一个高度
                    if (i % mSpan == 0) {
                        height += child.measuredHeight + mVerticalSpace
                    }
                } else {
                    height += child.measuredHeight
                }
            }
        }
        // 判断条目宽高是否一样

        if (isSquare) {
            height =
                (itemWidth * (if (childCount % mSpan == 0) childCount / mSpan else childCount / mSpan + 1)
                        + mVerticalSpace * ((childCount - 1) / mSpan))
        }
        height += paddingTop + paddingBottom
        // 指定自己的宽高
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childCount: Int = childCount

        // 计算一下最大的条目数量
        childCount = childCount.coerceAtMost(mMaxItem)
        if (childCount <= 0) {
            return
        }
        var cl: Int = paddingLeft
        var ct: Int = paddingTop
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val width = child.measuredWidth
            val height = child.measuredHeight
            child.layout(cl, ct, cl + width, ct + height)
            // 累加宽度
            cl += width + mHorizontalSpace
            // 如果是换行
            if ((i + 1) % mSpan == 0) {
                // 重置左边的位置
                cl = paddingLeft
                // 叠加高度
                ct += height + mVerticalSpace
            }
        }
    }

    /**
     * 重新添加布局
     */
    override fun resetLayout() {
        if (mAdapter == null) {
            return
        }
        removeAllViews()
        var count: Int = mAdapter!!.count
        count = count.coerceAtMost(mMaxItem)
        for (i in 0 until count) {
            val view: View = mAdapter!!.getView(i, this)!!
            addView(view)
        }
    }

    /**
     * 设置最大显示个数
     *
     * @param maxItem
     */
    fun setMaxItem(maxItem: Int) {
        mMaxItem = maxItem
        resetLayout()
    }

    /**
     * @param mSpanx
     */
    fun setGridSpan(mSpanx: Int) {
        mSpan = mSpanx
        invalidate()
    }

    fun setIsAuto(isAuto: Boolean) {
        this.isAuto = isAuto
        invalidate()
    }

    fun setHorizontalSpace(space: Int) {
        this.mHorizontalSpace = space
        invalidate()
    }

    fun setVerticalSpace(space: Int) {
        this.mVerticalSpace = space
        invalidate()
    }

    fun setSpace(hSpace: Int, vSpace: Int) {
        this.mHorizontalSpace = hSpace
        this.mVerticalSpace = vSpace
        invalidate()
    }

    init {
        // 获取自定义属性
        val array = context.obtainStyledAttributes(attrs, R.styleable.XGridLayout)
        mSpan = array.getInteger(R.styleable.XGridLayout_gridSpan, mSpan)
        mHorizontalSpace =
            array.getDimension(
                R.styleable.XGridLayout_gridHorizontalSpace,
                mHorizontalSpace.toFloat()
            )
                .toInt()
        mVerticalSpace =
            array.getDimension(R.styleable.XGridLayout_gridVerticalSpace, mVerticalSpace.toFloat())
                .toInt()
        mMaxItem = array.getInteger(R.styleable.XGridLayout_gridMaxItem, mMaxItem)
        isSquare = array.getBoolean(R.styleable.XGridLayout_gridIsSquare, true)
        array.recycle()
    }
}