package com.lalifa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 *
 * @ClassName AutoImage
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/22 10:51
 *
 */
@SuppressLint("AppCompatCustomView")
class AspectRatioImageView:ImageView {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}