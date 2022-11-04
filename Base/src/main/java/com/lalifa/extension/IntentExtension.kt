@file:Suppress("unused")

package com.lalifa.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.lalifa.activity.WebActivity
import java.io.File

/**
 * Intent extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */

/** 打开应用详情页面 */
fun Activity.openAppDetail() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/** 进入拨号界面 */
fun Activity.openDial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/** 系统浏览器打开Url */
fun Activity.openUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        toast("url为空")
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        toast("打开链接失败：${e.message}")
    }
}

/**
 * 打开Url
 *
 * > 优先查找系统能解析此url的组件
 * > 其次App内打开 `failureUrl`
 * > `failureUrl` 为空时跳转 `downloadAppUrl` 下载App
 */
fun Activity.openUrl(url: String?, failureUrl: String?, downloadAppUrl: String?) {
    if (url.isNullOrEmpty()) {
        toast("url为空")
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        if (failureUrl.isNullOrEmpty()) {
            openUrl(downloadAppUrl)
        } else start(WebActivity::class.java) { putExtra("urls", failureUrl) }
    }
}


/**
 * 打开Url
 *
 * > 优先查找系统能解析此url的组件
 * > 其次系统浏览器内打开 `failureUrl`
 * > `failureUrl` 为空时跳转 `downloadAppUrl` 下载App
 */
fun Activity.openUrlWithSystem(url: String?, failureUrl: String?, downloadAppUrl: String?) {
    if (url.isNullOrEmpty()) {
        toast("url为空")
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        openUrl(failureUrl)
        if (failureUrl.isNullOrEmpty()) {
            openUrl(downloadAppUrl)
        } else openUrl(failureUrl)
    }
}


// const val WX_PACKAGE_NAME = "com.tencent.mm"
// const val WX_LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI"
// const val WX_SHARE_IMG_UI = "com.tencent.mm.ui.tools.ShareImgUI"
// const val WX_SHARE_TO_TIMELINE_UI = "com.tencent.mm.ui.tools.ShareToTimeLineUI"
/** 分享图片列表到微信好友 */
fun Activity.shareImageListToWechat(list: List<File>) {
    try {
        val intent = Intent()
        val imageUris = ArrayList<Uri>()
        list.forEach {
            // 使用FileProvider分享多图到微信会提示错误：多文件分享仅支持照片格式
            // val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //     FileProvider.getUriForFile(this, App.config().provider(), it)
            // } else Uri.fromFile(it)
            // imageUris.add(uri)
            if (it.exists()) imageUris.add(Uri.fromFile(it))
        }
        intent.action = Intent.ACTION_SEND_MULTIPLE //设置分享行为
        intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
        intent.type = "image/*" //设置分享内容的类型
        // intent.putExtra("Kdescription", "")
        // intent.putStringArrayListExtra(Intent.EXTRA_TEXT, arrayListOf())
        // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        // startActivity(Intent.createChooser(intent, "图片分享"))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("分享失败")
    }
}

fun Fragment.browse(url: String, newTask: Boolean = false) = activity?.browse(url, newTask)

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}


//分享
fun Fragment.share(text: String, subject: String = "", title: String? = null) =
    activity?.share(text, subject, title)

fun Context.share(text: String, subject: String = "", title: String? = null): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, title))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

//发送邮件
fun Fragment.email(email: String, subject: String = "", text: String = "") =
    activity?.email(email, subject, text)

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false

}

//直接拨打电话
fun Fragment.makeCall(number: String): Boolean = activity?.makeCall(number) ?: false

fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

//发送短信
fun Fragment.sendSMS(number: String, text: String = ""): Boolean =
    activity?.sendSMS(number, text) ?: false

fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}