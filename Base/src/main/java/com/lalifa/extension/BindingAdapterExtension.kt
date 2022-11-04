package com.lalifa.extension

import com.drake.brv.BindingAdapter

/**
 * 适配器扩展
 * @ClassName BindAdapterExtension
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/5/9 16:32
 * @Des
 */
/**
 * 带动画的移除position
 * @receiver BindingAdapter
 * @param modelPosition Int  对应model的position  不包含头布局和脚布局
 */
fun BindingAdapter.removeAt(modelPosition: Int) {
    if (modelPosition<0) return
    if (models.isNullOrEmpty() || modelPosition>=models!!.size) return
    (models as ArrayList).removeAt(modelPosition)
    notifyItemRangeRemoved(modelPosition, 1)
}