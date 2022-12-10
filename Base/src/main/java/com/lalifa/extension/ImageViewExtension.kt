package com.lalifa.extension

import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cc.shinichi.library.ImagePreview
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.lalifa.base.R
import com.lalifa.ext.Config

import com.lalifa.transformation.MosaicBitmapTransformation
import com.lalifa.groupavatars.GroupAvatarsLib
import com.lalifa.groupavatars.layout.DingLayoutManager
import com.lalifa.groupavatars.layout.ILayoutManager
import com.lalifa.utils.BlurTransformation
import com.lalifa.widget.GridImageView
import com.lalifa.widget.XBaseAdapter


/**
 *
 * @ClassName ImageViewExtension
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2021/12/24 17:10
 *
 */

/**
 * 加载图片
 * @receiver ImageView
 * @param url String
 * @param resourceId Int
 */
fun ImageView.loadLocal(resourceId: Int = R.drawable.mx_common_divider) {
    Glide.with(this.context).load(resourceId).into(this)
}

/**
 * 加载图片
 * @receiver ImageView
 * @param url String
 * @param resourceId Int
 */
fun ImageView.load(url: String, resourceId: Int = R.drawable.mx_common_divider) {
    Glide.with(this.context).load(url).error(resourceId).into(this)
}

/**
 * 加载圆形图片
 * @receiver ImageView
 * @param url String
 * @param resourceId Int
 */
fun ImageView.loadCircle(url: String, resourceId: Int = R.drawable.mx_common_divider) {
    Glide.with(this.context).load(url).error(resourceId).circleCrop()
        .into(this)
}

/**+
 * 加载圆角图
 * @receiver ImageView
 * @param url String
 * @param radius Int
 * @param resourceId Int
 */
fun ImageView.loadRound(
    url: String,
    radius: Int = 10,
    centerCrop: Boolean = false,
    resourceId: Int = R.drawable.mx_common_divider
) {
    Glide.with(this.context).load(url).apply(
        RequestOptions().error(resourceId).transform(
            MultiTransformation(
                if (centerCrop) CenterCrop() else FitCenter(),
                RoundedCorners(radius.dp)
            )
        )
    ).into(this)
}

/**
 * 加载马赛克
 * @receiver ImageView
 * @param url String
 * @param rect Rect?
 * @param size Int
 * @param circleCrop Boolean
 * @param resourceId Int
 */
fun ImageView.loadMosaic(
    url: String,
    rect: Rect? = null,
    size: Int = 20,
    circleCrop: Boolean = false,
    resourceId: Int = R.drawable.mx_common_divider
) {
    Glide.with(this.context).load(url).apply(
        RequestOptions().error(resourceId).transform(
            MultiTransformation(
                MosaicBitmapTransformation(rect, size),
                if (circleCrop) CircleCrop() else CenterCrop()
            )
        )
    ).into(this)
}

/**
 * 高斯模糊
 * @receiver ImageView
 * @param url String
 * @param blurRadius Int
 * @param centerCrop Boolean
 * @param resourceId Int
 */
fun ImageView.loadBlur(
    url: String,
    blurRadius: Int = 10,
    centerCrop: Boolean = false,
    resourceId: Int = R.drawable.mx_common_divider
) {
    Glide.with(this.context).load(url).apply(
        RequestOptions().error(resourceId).transform(
            MultiTransformation(
                if (centerCrop) CenterCrop() else FitCenter(),
                BlurTransformation(blurRadius.dp)
            )
        )
    ).into(this)
}

/**
 * 高斯模糊
 * @receiver ImageView
 * @param url String
 * @param blurRadius Int
 * @param centerCrop Boolean
 * @param resourceId Int
 */
fun ImageView.loadBlur(
    res: Int,
    blurRadius: Int = 10,
    blurSampling: Int = 1,
    centerCrop: Boolean = true,
    resourceId: Int = R.drawable.mx_common_divider
) {
    Glide.with(this.context).load(res).apply(
        RequestOptions().error(resourceId).transform(
            MultiTransformation(
                if (centerCrop) CenterCrop() else FitCenter(),
                BlurTransformation(blurRadius,blurSampling)
            )
        )
    ).into(this)
}


/**
 * 群组头像
 * @receiver ImageView
 * @param groupId String
 * @param size Int
 * @param data List<String>
 * @param round Int
 * @param space Int
 * @param childAvatarRound Int
 * @param iLayoutManager ILayoutManager
 * @param textBgColor Int
 * @param bgColor Int
 * @param resourceId Int
 */
fun ImageView.groupAvatars(
    groupId: String,
    size: Int,
    data: List<String>,
    round: Int = 0,
    space: Int = 0,
    childAvatarRound: Int = 0,
    iLayoutManager: ILayoutManager = DingLayoutManager(),
    textBgColor: Int = com.ypx.imagepicker.R.color.white_F5,
    bgColor: Int = com.ypx.imagepicker.R.color.white_F5,
    resourceId: Int = R.drawable.mx_common_divider

) {
    GroupAvatarsLib.init(context)
        // 必选，设置最终生成的图片尺寸，单位dp（一般就是当前imageView的大小）
        .setSize(size)
        // 设置钉钉或者微信群头像类型 DingLayoutManager、 WechatLayoutManager
        // 目前钉钉最多组合4个，微信最多9个。超出会自动截取前4或9个
        .setLayoutManager(iLayoutManager)
        // 设置使用昵称生成头像时的背景颜色
        .setNickAvatarColor(textBgColor)
        // 设置昵称生成头像时的文字大小 ,单位dp （设置为0时 = 单个小头像的1/4大小）
        .setNickTextSize(0)
        // 设置群组ID，用于生成缓存key
        .setGroupId(groupId)
        // 设置加载最终图片的圆角大小，单位dp，默认0
        .setRound(round)
        // 设置内部单个图片的圆角，单位dp，默认0
        .setChildAvatarRound(childAvatarRound)
        // 单个图片之间的距离，单位dp，默认0dp
        .setGap(space)
        // 设置生成的图片背景色间距
        .setGapColor(bgColor)
        // 单个网络图片加载失败时，会展示默认图片
        .setPlaceholder(resourceId)
        // 设置数据（可设置网络图片地址或者昵称）
        .setDatas(data)
        // 设置要显示最终图片的ImageView
        .setImageView(this)
        .build()
}


fun GridImageView.imagesAdapter(imageList: List<String>, onClick: Boolean = true) {
    setAdapter(object : XBaseAdapter() {
        override val count: Int
            get() = imageList.size

        override fun getView(position: Int, parent: ViewGroup?): View? {

            val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_image_item, parent, false)
            val findViewById = view.findViewById<ImageView>(R.id.image)
            if (onClick) {
                findViewById.onClick {
                    if (imageList[position].isNotEmpty()) {
                        context.preview(position, imageList)
                    }
                }
            }
            if (imageList[position].isNotEmpty()) {
                findViewById.loadRound(
                    imageList[position],
                    4,
                    count > 1
                )
            } else {
                findViewById.setImageResource(R.drawable.zw_white)
            }
            return view
        }
    })
}

fun GridImageView.imagesCommonAdapter(imageList: ArrayList<String>, onClick: Boolean = true) {
    val oldImageList = ArrayList<String>()
    oldImageList.addAll(imageList)
    if (imageList.size == 4) {
        imageList.add(2, "")
        imageList.add(5, "")
    }
    setAdapter(object : XBaseAdapter() {
        override val count: Int
            get() = imageList.size

        override fun getView(position: Int, parent: ViewGroup?): View? {

            val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_image_item, parent, false)
            val findViewById = view.findViewById<ImageView>(R.id.image)
            if (onClick) {
                findViewById.onClick {
                    if (imageList[position].isNotEmpty()) {
                        context.preview(position, oldImageList)
                    }
                }
            }
            if (imageList[position].isNotEmpty()) {
                findViewById.loadRound(
                    imageList[position],
                    12,
                    count > 1
                )
            } else {
                findViewById.setImageResource(R.drawable.zw_white)
            }
            return view
        }
    })
}

/**
 * 加载网络视频封面
 * @receiver ImageView
 * @param url String
 */
fun ImageView.loadVideoImg(url: String) {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(url, hashMapOf())
    setImageBitmap(mmr.getFrameAtTime(-1))
    mmr.release()
}