package com.lalifa.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.xxx.myapplication.MosaicBitmap
import com.xxx.myapplication.MosaicBitmap.mosaic
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
 *
 * @ClassName MosaicBitmapTransformation
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/21 09:27
 *
 */
class MosaicBitmapTransformation(var rect: Rect? = null, var size: Int = 20,
                                 val bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888) :
    BitmapTransformation() {

    private val id = javaClass.toString()
    private val idBYTES = id.toByteArray(CHARSET)
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBYTES)
        val widthData = ByteBuffer.allocate(4).putInt(size).array()
        messageDigest.update(widthData)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val result = pool.get(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888)
        result.setHasAlpha(true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        canvas.setBitmap(null)
        return result.mosaic(rect, size,bitmapConfig)!!
    }
}