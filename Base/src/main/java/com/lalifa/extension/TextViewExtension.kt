package com.lalifa.extension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.widget.TextView
import com.lalifa.widget.CenterImageSpan
import android.os.Build
import android.os.Handler
import android.os.Looper

import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.text.SpannableStringBuilder

import android.text.TextUtils

import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View

import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lalifa.base.R
import com.lalifa.widget.URLDrawable
import android.view.ViewTreeObserver.OnGlobalLayoutListener as OnGlobalLayoutListener1


/**
 * TextView extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */
// 添加：删除线、中横线
fun TextView.addDeleteLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

// 移除：删除线、中横线
fun TextView.removeDeleteLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG.inv())
}

// 添加：下划线
fun TextView.addUnderLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

// 移除：下划线
fun TextView.removeUnderLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags and (Paint.UNDERLINE_TEXT_FLAG.inv())
}

// 移除：下划线
fun TextView.applyDrawable(
    left: Drawable? = null, top: Drawable? = null,
    right: Drawable? = null, bottom: Drawable? = null
) {
    applyDrawableBounds(left)
    applyDrawableBounds(top)
    applyDrawableBounds(right)
    applyDrawableBounds(bottom)
    setCompoundDrawables(left, top, right, bottom)
}

private fun applyDrawableBounds(drawable: Drawable?) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
}

fun TextView.clearDrawable() {
    // setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    setCompoundDrawables(null, null, null, null)
}

fun TextView.applyHtml(content: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        this.text = Html.fromHtml(content)
    }
}

fun TextView.applyImageSpan(drawableResId: Int, text: String) {
    val imageSpan = CenterImageSpan(context, drawableResId)
    val content = SpannableString("* $text")
    // content.setSpan(imageSpan, 0, 1, 0)
    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    setText(content)
}

/**
 * 设置textView结尾...后面显示的文字和颜色
 * @param context 上下文
 * @param textView textview
 * @param minLines 最少的行数
 * @param originText 原文本
 * @param endText 结尾文字
 * @param endColorID 结尾文字颜色id
 * @param isExpand 当前是否是展开状态
 */
fun TextView.toggleEllipsize(
    minLines: Int = 1,
    originText: String,
    endText: String,
    endColorID: Int,
    isExpand: Boolean
) {
    if (TextUtils.isEmpty(originText)) {
        return
    }
    val function: ViewTreeObserver.OnGlobalLayoutListener =
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    this@toggleEllipsize.text = originText
                } else {
                    val paddingLeft = this@toggleEllipsize.paddingLeft
                    val paddingRight = this@toggleEllipsize.paddingRight
                    val paint = this@toggleEllipsize.paint
                    val moreText = this@toggleEllipsize.textSize * endText.length
                    val availableTextWidth =
                        (this@toggleEllipsize.width - paddingLeft - paddingRight) *
                                minLines - moreText
                    val ellipsizeStr = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.resources.getColor(endColorID)),
                            temp.length - endText.length,
                            temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        this@toggleEllipsize.text = ssb
                    } else {
                        this@toggleEllipsize.text = originText
                    }
                }
                this@toggleEllipsize.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        }
    this.viewTreeObserver.addOnGlobalLayoutListener(function)
}

fun TextView.privacyAndUser(callback: (Int) -> Unit) {
    val spannableString = SpannableString("已阅读并同意火半的用户协议和隐私政策")
    spannableString.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            callback(0)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.parseColor("#ff2079f3")
            ds.isUnderlineText = false
        }
    }, 9, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            callback(1)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.parseColor("#ff2079f3")
            ds.isUnderlineText = false
        }
    }, 14, 18, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

fun TextView.foregroundColor(start: Int, end: Int, color: Int) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(color),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = spannableString
}

/**
 * 追加字体大小和颜色和样式
 * @receiver TextView
 * @param content String
 * @param size Float
 * @param color Int
 */
fun TextView.appendSpan(
    content: String,
    size: Float = this.textSize,
    color: Int = currentTextColor,
    style: Int = typeface.style,
    clickCallback: () -> Unit = {}
) {
    append(SpannableString(content).also {
        it.setSpan(ForegroundColorSpan(color), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        it.setSpan(AbsoluteSizeSpan(size.toInt()), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        it.setSpan(StyleSpan(style), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        it.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickCallback.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, 0, it.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    })
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

/**
 * 追加样式Span
 * @receiver TextView
 * @param content String
 * @param style Int
 */
fun TextView.styleSpan(content: String, style: Int) {
    append(SpannableString(content).also {
        it.setSpan(StyleSpan(style), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    })
}

fun TextView.countdown(maxTime: Int = 60) {
    var time = maxTime
    val handler = Handler(Looper.myLooper()!!)
    handler.postDelayed(object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            time--
            if (time > 0) {
                text = "${time}s"
                isEnabled = false
                handler.postDelayed(this, 1000)
            } else {
                isEnabled = true
                text = "发送验证码"
                handler.removeCallbacks(this)
            }
        }
    }, 1000)
}

fun TextView.loadHtml(content: String) {
    text = Html.fromHtml(content, { source ->
        val urlDrawable = URLDrawable()
        Glide.with(context).asBitmap().load(source).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                urlDrawable.bitmap = resource
                urlDrawable.setBounds(0, 0, resource.width, resource.height)
                invalidate()
                text = text
            }
        })
        urlDrawable
    }, null)
}

