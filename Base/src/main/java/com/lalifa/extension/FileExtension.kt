@file:Suppress("unused")

package com.lalifa.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * File extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */

/** 保存 InputStream 到文件 */
fun InputStream.toFile(file: File) {
    file.outputStream().use {
        this.copyTo(it)
    }
}

/**
 * 异步删除文件
 * @receiver File
 */
fun File.deleteAsync() {
    if (!exists()) return
    globalIOTask {
        // 如果是目录，递归删除目录。如果是文件，删除文件
        if (this.isDirectory) deleteRecursively() else delete()
    }
}

/**
 * 保存到图库
 * @receiver File?
 * @param context Context
 */
fun File?.saveToGallery(context: Context) {
    if (null == this) return
    try {
        MediaStore.Images.Media.insertImage(
            context.contentResolver, this.absolutePath, this.name, null
        )
        scanFile(context)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File?.scanFile(context: Context) {
    if (null == this || !this.exists()) return
    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(this)))
}

fun Context.scanFile(file: File?) {
    if (null == file || !file.exists()) return
    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
}

/**
 * 获取文件的大小
 * @receiver String
 * @return String
 */
fun String.size(): String {
    val file = File(this)
    if (!file.exists()) return "0K"
    if (file.isDirectory) return "0K"
    try {
        val size = FileInputStream(file).available()
        return size.toLong().formatByte()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return "0K"
}
fun String.sizeByte():Long{
    val file = File(this)
    if (!file.exists()) return 0
    if (file.isDirectory) return 0
    try {
        val size = FileInputStream(file).available()
        return size.toLong()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return 0
}

/**
 * 获取文件真实路径
 * @receiver Uri
 * @param activity Activity
 * @return String?
 */
fun Uri.realPath(activity: Activity): String? {
    if (scheme.isNullOrEmpty()) return null
    if ("content" == scheme!!.lowercase()) {
        val projection = arrayOf("_data")
        try {
            val cursor = activity.contentResolver!!.query(this, projection, null, null, null)
            val index = cursor!!.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
                return cursor.getString(index);
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else if ("file" == scheme!!.lowercase()) {
        return path
    }
    return null
}

/**
 * 获取文件最后一次修改的时间
 * @receiver String
 * @return String
 */
fun String.lastModified(): String {
    val file = File(this)
    if (!file.exists()) return "未知"
    if (file.isDirectory) return "未知"
    try {
        val time = file.lastModified()
        return time.format()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return "未知"
}
