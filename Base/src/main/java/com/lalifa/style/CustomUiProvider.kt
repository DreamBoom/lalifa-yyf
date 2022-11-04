package com.lalifa.style

import android.content.Context
import android.graphics.Color
import com.ypx.imagepicker.views.PickerUiProvider
import com.ypx.imagepicker.views.base.*
import com.ypx.imagepicker.views.wx.WXFolderItemView

/**
 *
 * @ClassName CustomUiProvider
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:07
 *
 */
class CustomUiProvider: PickerUiProvider() {
    override fun getTitleBar(context: Context): PickerControllerView {
        return CustomTitleBar(context)
    }

    //定制选择器底部栏，返回null即代表没有底部栏，默认实现为 WXBottomBar
    override fun getBottomBar(context: Context): PickerControllerView? {
        return CustomBottomBar(context)
    }

    override fun getItemView(context: Context?): PickerItemView {
        return CustomItemView(context)
    }

    override fun getFolderItemView(context: Context?): PickerFolderItemView{
        val itemView = super.getFolderItemView(context) as WXFolderItemView
        itemView.setIndicatorColor(Color.RED)
        itemView.setBackgroundColor(Color.WHITE)
        itemView.setNameTextColor(Color.BLACK)
        itemView.setCountTextColor(Color.parseColor("#50F5f5f5"))
        itemView.setDividerColor(Color.parseColor("#50F5f5f5"))
        return itemView
    }

    //定制选择器预览页面,默认实现为 WXPreviewControllerView
    override fun getPreviewControllerView(context: Context): PreviewControllerView {
        return CustomPreviewControllerView(context)
    }

    //定制选择器单图剪裁页面,默认实现为 WXSingleCropControllerView
    override fun getSingleCropControllerView(context: Context): SingleCropControllerView {
        return super.getSingleCropControllerView(context)
    }
}