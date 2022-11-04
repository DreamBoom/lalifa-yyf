package com.xxx.myapplication

import android.content.Context
import android.graphics.*
import android.util.Log
import android.util.TypedValue
import java.lang.RuntimeException
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 *
 * @ClassName MosaicBitmap
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/20 18:13
 *
 */
object MosaicBitmap {
    private const val min_size = 4
    fun Bitmap.mosaic(rect: Rect? = null, size: Int = 20,bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap?{
        return makeMosaic(this,rect,size,bitmapConfig)
    }
    private fun makeMosaic(
        bitmap: Bitmap,
        rect: Rect? = null,
        size: Int = 30,
        bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
    ): Bitmap? {
        var targetRect: Rect? = rect
        var blockSize = size
        if (bitmap.width == 0 || bitmap.height == 0 || bitmap.isRecycled
        ) {
            throw RuntimeException("bad bitmap to add mosaic")
        }
        if (blockSize < min_size) {
            blockSize = min_size
        }
        if (targetRect == null) {
            targetRect = Rect()
        }
        val bw = bitmap.width
        val bh = bitmap.height
        if (targetRect.isEmpty) {
            targetRect.set(0, 0, bw, bh)
        }
        //
        val rectW: Int = targetRect.width()
        val rectH: Int = targetRect.height()
        val bitmapPxs = IntArray(bw * bh)
        // fetch bitmap pxs
        bitmap.getPixels(bitmapPxs, 0, bw, 0, 0, bw, bh)
        //
        val rowCount = ceil((rectH.toFloat() / blockSize).toDouble()).toInt()
        val columnCount = ceil((rectW.toFloat() / blockSize).toDouble()).toInt()
        for (r in 0 until rowCount) { // row loop
            for (c in 0 until columnCount) { // column loop
                val startX: Int = targetRect.left + c * blockSize + 1
                val startY: Int = targetRect.top + r * blockSize + 1
                dimBlock(bitmapPxs, startX, startY, blockSize, bw, bh)
            }
        }
        return Bitmap.createBitmap(bitmapPxs, bw, bh, bitmapConfig)
    }

    /**
     * 从块内取样，并放大，从而达到马赛克的模糊效果
     *
     * @param pxs
     * @param startX
     * @param startY
     * @param blockSize
     * @param maxX
     * @param maxY
     */
    private fun dimBlock(
        pxs: IntArray, startX: Int, startY: Int,
        blockSize: Int, maxX: Int, maxY: Int
    ) {
        var stopX = startX + blockSize - 1
        var stopY = startY + blockSize - 1
        if (stopX > maxX) {
            stopX = maxX
        }
        if (stopY > maxY) {
            stopY = maxY
        }
        //
        var sampleColorX = startX + blockSize / 2
        var sampleColorY = startY + blockSize / 2
        //
        if (sampleColorX > maxX) {
            sampleColorX = maxX
        }
        if (sampleColorY > maxY) {
            sampleColorY = maxY
        }
        val colorLinePosition = (sampleColorY - 1) * maxX
        val sampleColor = pxs[colorLinePosition + sampleColorX - 1] // 像素从1开始，但是数组层0开始
        for (y in startY..stopY) {
            val p = (y - 1) * maxX
            for (x in startX..stopX) {
                // 像素从1开始，但是数组层0开始
                pxs[p + x - 1] = sampleColor
            }
        }
    }
}