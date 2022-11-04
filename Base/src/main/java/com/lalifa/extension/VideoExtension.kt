package com.lalifa.extension

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import java.io.File

/**
 *
 * @ClassName VideoExtension
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/13 10:12
 * @Des
 */

/**
 * 视频缩略图
 * @receiver String 视频地址 本地
 * @return Bitmap?
 */
fun String.getVideoThumbnail(): Bitmap? {
    return if (!File(this).exists())
        null
    else
        ThumbnailUtils.createVideoThumbnail(
        this,
        MediaStore.Video.Thumbnails.MINI_KIND
    )
}

/**
 * 获取视频封面地址
 * @receiver String
 * @param context Context
 * @return String?
 */
fun String.getVideoCover(context: Context): String? {
    return getVideoThumbnail()?.save(context)
}