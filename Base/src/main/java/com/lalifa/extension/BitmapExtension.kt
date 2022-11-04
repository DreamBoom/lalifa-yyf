@file:Suppress("unused")

package com.lalifa.extension

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import com.drake.logcat.LogCat
import com.lalifa.utils.BitmapUtil
import java.io.*

/**
 * Bitmap extensions
 *
 * @author LiuBin
 * @version v1.0, 2019-05-20 15:48
 */

fun File.toBitmap(): Bitmap? {
    return BitmapFactory.decodeFile(this.absolutePath)
}

fun Drawable.toBitmap(): Bitmap {
    val config =
        if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(canvas)
    return bitmap
}

fun Bitmap.save(
    file: File,
    bitmapFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    bitmapQuality: Int = 90
): Boolean {
    var stream: OutputStream? = null
    return try {
        stream = FileOutputStream(file)
        this.compress(bitmapFormat, bitmapQuality, stream)
        stream.flush()
        stream.close()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        stream?.close()
    }
}

fun View.save(
    file: File,
    bitmapFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    bitmapQuality: Int = 100
): Boolean {
    return try {
        val bitmap = BitmapUtil.createBitmapFromView(this, measuredWidth, measuredHeight)
        bitmap.save(file, bitmapFormat, bitmapQuality)
    } catch (e: Exception) {
        false
    }
}
// fun View.saveImageToGallery(file: File): Boolean {
//     return try {
//         val values = ContentValues()
//         val resolver = context.contentResolver
//         values.put(MediaStore.Images.ImageColumns.DATA, file.absolutePath)
//         values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.name)
//         values.put(MediaStore.Images.ImageColumns.MIME_TYPE, MIME_IMAGE_JPEG)
//         // 将图片的拍摄时间设置为当前的时间
//         values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis())
//         val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//         var out: OutputStream? = null
//         if (uri != null) {
//             out = resolver.openOutputStream(uri)
//             val bitmap = BitmapUtil.createBitmapFromViewWithPx(this, measuredWidth, measuredHeight)
//             bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//             true
//         } else false
//     } catch (e: Exception) {
//         false
//     }
// }
/**
 * 保存Bitmap到图库
 * @receiver Bitmap?
 * @param context Context
 * @param file File
 * @return Boolean
 */
fun Bitmap?.saveImageToGallery(context: Context, file: File): Boolean {
    if (null == this) return false
    return try {
        val values = ContentValues()
        val resolver = context.contentResolver
        values.put(MediaStore.Images.ImageColumns.DATA, file.absolutePath)
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.name)
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, MIME_IMAGE_JPEG)
        // 将图片的拍摄时间设置为当前的时间
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis())
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val out = resolver.openOutputStream(uri)
            compress(Bitmap.CompressFormat.PNG, 100, out)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            true
        } else false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 缩放位图
 * @receiver Bitmap
 * @param ratio Float
 * @return Bitmap
 */
fun Bitmap.scaleBitmap(ratio: Float): Bitmap {
    val matrix = Matrix().also { it.preScale(ratio, ratio) }
    val newBM = Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
    if (newBM == this) return newBM
    recycle()
    return newBM
}

fun Bitmap.save(context: Context): String? {
    val path = "${context.cacheDir.absolutePath}/${System.currentTimeMillis()}.png"
    return if (!save(File(path))) null else path
}

fun Bitmap.toBase64(): String? {
    var result: String? = null
    val bos = ByteArrayOutputStream()
    try {
        compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        val bitmapBytes: ByteArray = bos.toByteArray()
        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    } catch (e: IOException) {
        LogCat.e(e)
        bos.flush()
        bos.close()
    }
    return result
}