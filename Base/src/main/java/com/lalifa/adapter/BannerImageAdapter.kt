package com.lalifa.adapter

import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.holder.BannerImageHolder
import android.view.ViewGroup
import android.widget.ImageView

abstract class BannerImageAdapter<T>(mData: List<T>?) : BannerAdapter<T, BannerImageHolder>(mData) {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerImageHolder {
        val imageView = ImageView(parent.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        return BannerImageHolder(imageView)
    }
}