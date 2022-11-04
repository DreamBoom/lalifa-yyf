package com.lalifa.yyf.weight

import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import com.lalifa.extension.*
import com.lalifa.yyf.weight.code.VerifyCodeView

class InputVerifyCodeView : RelativeLayout {

    private val editText by lazy { EditText(context) }
    private val itemLayout by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(itemLayout)
        itemLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(editText)
        editText.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            textSize = 0f
            isCursorVisible = false
            setBackgroundResource(0)
            setOnLongClickListener(null)
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            addTextChangedListener(onTextChanged = { _, _, _, _ ->
                setFocus()
                if (text().length == 6) onEndListener?.onEnd(text())
            })
        }
        for (i in 0..5) {
            val item = VerifyCodeView(context)
            itemLayout.addView(item)
            item.layoutParams =
                LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                    setMargins(2.dp, 0, 2.dp, 0)
                }
        }
        rootView.onClick {
            (context as Activity).showKeyboard(editText)
            setFocus()
        }
    }

    private var onEndListener: OnEndListener? = null
    fun setOnEndListener(onEndListener: OnEndListener) {
        this.onEndListener = onEndListener
    }


    private fun setFocus() {
        if (itemLayout.childCount == 0) return
        val numbers = editText.text()
        for (i in 0 until itemLayout.childCount) {
            (itemLayout.getChildAt(i) as VerifyCodeView).apply {
                setFocus(false)
                setText(if (i < numbers.length) String(CharArray(1) { numbers[i] }) else "")
            }
        }
        if (editText.text().isEmpty()) {
            (itemLayout.getChildAt(0) as VerifyCodeView).setFocus(true)
        } else
            (itemLayout.getChildAt(editText.text().length - 1) as VerifyCodeView).setFocus(true)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({
            (context as Activity).showKeyboard(editText)
            setFocus()
        }, 200)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (context as Activity).hideKeyboard(editText)
    }

    interface OnEndListener {
        fun onEnd(text: String)
    }
}