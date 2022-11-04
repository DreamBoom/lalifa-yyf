@file:Suppress("unused")

package com.lalifa.extension

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale
import java.util.regex.Pattern

/**
 * String extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */

/** 复制文本到剪贴板 */
fun String.copyToClip(activity: Activity): Boolean {
    if (this.isEmpty()) return false

    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return try {
        val clipData = ClipData.newPlainText("text", this)
        clipboardManager.setPrimaryClip(clipData)
        activity.toast("复制成功")
        true
    } catch (e: Exception) {
        false
    }
}

fun String.eq(target: String?): Boolean {
    return target.equals(this, true)
}

// 获取Http链接后面拼接的参数值
fun String?.getUrlParams(key: String, defaultValue: String = ""): String {
    if (this.isNullOrEmpty()) return defaultValue
    val uri = Uri.parse(this)
    return uri.getQueryParameter(key) ?: defaultValue
}

// Http链接后面拼接参数值
fun String?.appendUrlParams(key: String, value: String): String {
    if (this.isNullOrEmpty()) return ""
    return if (this.contains("?")) "${this}&${key}=${value}" else "${this}?${key}=${value}"
}

fun String?.withMaxLength(maxLength: Int, suffix: String = ""): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        if (this.length > maxLength) (this.substring(0, maxLength) + suffix) else this
    } catch (e: Exception) {
        this
    }
}

/** 格式化手机号，隐藏中间4位 */
fun String?.formatPhone(): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        this.substring(0, 3) + "****" + this.substring(7)
    } catch (e: Exception) {
        this
    }
}


fun String?.filterPhone(): String {
    try {
        if (this.isNullOrEmpty()) return ""
        // 找到所有符合正则表达式的字符串
        var list = this.findPhone()
        var result: String
        if (list.isEmpty()) {
            // 如果没找到，替换所有空格后，再查找一遍
            result = this.replace(" ", "")
            list = result.findPhone()
        } else {
            result = this
        }
        list.forEach {
            result = result.replace(it, it.formatPhone())
        }
        return result
    } catch (e: Exception) {
        return this ?: ""
    }
}

fun String?.findPhone(pattern: Pattern = Pattern.compile("1\\d{10}")): MutableList<String> {
    val result = mutableListOf<String>()
    if (this.isNullOrEmpty()) return result
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        val text = matcher.group()
        result.add(text)
    }
    return result
}

/**
 * 拆分以字符串分隔的数据
 *
 * @receiver String?
 * @param content String
 * @return List<String>
 */
fun String?.splitComma(content: String = ","): List<String> {
    if (null == this) return mutableListOf()
    return if (this.indexOf(content) != -1) this.split(content) else mutableListOf(this)
}


fun String.toMultipartBody(): MultipartBody.Part {
    val name = this.toLowerCase(Locale.CHINA)
    val mime = when {
        name.endsWith(".zip", true) -> "application/zip"
        name.endsWith(".png", true) -> "image/png"
        name.endsWith(".gif", true) -> "image/gif"
        name.endsWith(".jpg", true) || name.endsWith(".jpeg") -> "image/jpeg"
        else -> "image/*"
    }
    val file = File(this)
    return MultipartBody.Part.createFormData(
        "file",
        file.name,
        file.asRequestBody(mime.toMediaType())
    )
}

/**
 * 安全数据转换
 */
fun String?.safeConvertInt(defaultValue: Int = 0): Int {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toInt()
    } catch (e: Exception) {
        defaultValue
    }
}

fun String?.safeConvertLong(defaultValue: Long = 0): Long {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toLong()
    } catch (e: Exception) {
        defaultValue
    }
}

fun String?.safeConvertDouble(defaultValue: Double = 0.0): Double {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toDouble()
    } catch (e: Exception) {
        defaultValue
    }
}

fun String?.safeUrl(): String {
    if (this.isNullOrEmpty()) return ""
    if (this.startsWith("//", true)) return "http:$this"
    return this
}

fun String?.isSafeUrl(): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.startsWith("http://", true) ||
            this.startsWith("https://", true)
}

fun String?.toColor(defaultValue: Int = -1): Int {
    if (this.isNullOrEmpty() || !startsWith("#")) return defaultValue
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        defaultValue
    }
}

/**
 * 集合以字符串分隔转字符串
 * @receiver List<String>
 * @param string String?
 * @return String
 */
fun List<String>.string(string: String? = ","): String {
    if (isEmpty()) return ""
    if (size == 1) return this[0]
    val stringBuffer = StringBuffer()
    forEach {
        stringBuffer.append(it).append(",")
    }
    return stringBuffer.substring(0, stringBuffer.length - 1).toString()
}

fun String.bitmap(context: Context, callback: (bitmap: Bitmap) -> Unit) {
    Glide.with(context).asBitmap().load(this).into(object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            callback.invoke(resource)
        }
    })
}

/**
 * 根据图片路径获取base64
 * @receiver String
 * @param context Context
 * @param callback Function1<[@kotlin.ParameterName] String?, Unit>
 */
fun String.toBase64(context: Context, callback: (base64: String?) -> Unit) {
    bitmap(context) {
        callback.invoke(it.toBase64())
    }
}

/**
 * Unicode编码
 * @receiver String 内容
 * @return String 编码后
 */
fun String.decodeUnicode(): String {
    val builder = StringBuilder()
    for (element in this) {
        // 如果你的Kotlin版本低于1.5，这里 element.code 会报错 找不到方法,请替换成:
        // Kotlin < 1.5
        // var s = Integer.toHexString(element.toInt())
        // Kotlin >= 1.5
        var s = Integer.toHexString(element.code)

        if (s.length == 2) {// 英文转16进制后只有两位，补全4位
            s = "00$s"
        }
        builder.append("\\u$s")
    }
    return builder.toString()
}

/**
 * 解码Unicode字符串，得到原始字符串
 *
 * @return 解码后的原始字符串
 */
fun String.encodeUnicode(): String {
    val builder = StringBuilder()
    val hex = split("\\\\u".toRegex()).toTypedArray()
    for (i in 1 until hex.size) {
        val data = hex[i].toInt(16)
        builder.append(data.toChar())
    }
    return builder.toString()
}