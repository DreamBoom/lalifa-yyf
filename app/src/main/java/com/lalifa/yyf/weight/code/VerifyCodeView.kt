package com.lalifa.yyf.weight.code

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.lalifa.extension.gone
import com.lalifa.extension.visible
import com.lalifa.yyf.databinding.ViewVerifyCodeBinding


class VerifyCodeView : RelativeLayout {
    private var binding: ViewVerifyCodeBinding? = null

    private val valuesHolder: PropertyValuesHolder by lazy {
        PropertyValuesHolder.ofKeyframe(
            "alpha",
            Keyframe.ofFloat(0f, 1f), Keyframe.ofFloat(0.4f, 1f),
            Keyframe.ofFloat(0.45f, 0f), Keyframe.ofFloat(1f, 0f),
        )
    }
    private var objectAnimator: ObjectAnimator? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        binding = ViewVerifyCodeBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setText(text: String) {
        binding?.content?.text = text
    }

    fun setFocus(focus: Boolean) {
        if (focus) {
            binding?.cursorView?.visible()
            if (objectAnimator != null) objectAnimator!!.cancel()
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder( binding?.cursorView, valuesHolder).apply {
                duration = 1500
                repeatCount = 1
            }
            objectAnimator?.start()
        } else {
            binding?.cursorView?.gone()
            if (objectAnimator != null) {
                objectAnimator!!.cancel()
                objectAnimator = null
            }
        }
    }
}