@file:Suppress("unused")

package com.lalifa.extension

import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText


/**
 * 获取焦点
 * @receiver EditText
 * @param fillText String?
 */
fun EditText.applyFocus(fillText: String? = null) {
    fillText?.let { if (it.isNotEmpty()) this.setText(it) }
    post {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val content = text()
        if (content.isNotEmpty()) setSelection(content.length)
    }
}

/**
 * 获取输入的字符串
 * @receiver EditText?
 * @return String
 */
fun EditText?.text(): String {
    if (this == null) return ""
    val editable = text
    if (null != editable) {
        return editable.toString().trim()
    }
    return ""
}
/**
 * 获取输入的字符串是否为空
 * @receiver EditText?
 * @return String
 */
fun EditText?.isEmp(): Boolean {
    if (this == null) return true
    val editable = text
    var b = true
    if (null != editable) {
        val trim = editable.toString().trim()
        b = TextUtils.isEmpty(trim)
    }
    return b
}
/**
 * 是否为空
 * @receiver EditText
 * @return Boolean
 */
fun EditText.isEmpty(): Boolean = this.text().isEmpty()

fun EditText?.float(): Float {
    if (this == null) return 0F
    val content = text()
    return try {
        content.toFloatOrNull() ?: 0F
    } catch (e: Exception) {
        0F
    }
}

fun EditText?.double(): Double {
    if (this == null) return 0.0
    val content = text()
    return try {
        content.toDoubleOrNull() ?: 0.0
    } catch (e: Exception) {
        0.0
    }
}

fun EditText?.int(): Int {
    if (this == null) return 0
    val content = text()
    return try {
        content.toIntOrNull() ?: 0
    } catch (e: Exception) {
        0
    }
}

fun EditText?.long(): Long {
    if (this == null) return 0L
    val content = text()
    return try {
        content.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        0L
    }
}

/**
 * 文本输入框禁用回车键
 */
fun EditText?.disableEnter() {
    this?.setOnEditorActionListener { _, _, event ->
        return@setOnEditorActionListener KeyEvent.KEYCODE_ENTER == event.keyCode
    }
}

/**
 * 设置默认密码状态
 * @receiver EditText?
 * @param show Boolean 是否显示可见的密码
 */
fun EditText?.applyShowPassword(show: Boolean) {
    this?.inputType =
        if (show) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
}

/**
 * 设置文本框是否可以编辑
 * @receiver EditText
 * @param edit Boolean
 */
fun EditText.applyEdit(edit:Boolean){
    if (edit){
        isFocusableInTouchMode=true
        isFocusable=true
        requestFocus()
    }else{
        isFocusableInTouchMode=false
        isFocusable=false
    }
}

open class AbsTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}