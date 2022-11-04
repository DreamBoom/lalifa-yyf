package com.lalifa.yyf.ext

import android.view.View
import androidx.viewbinding.ViewBinding
import com.drake.logcat.LogCat
import com.google.android.material.bottomsheet.BottomSheetBehavior

val listener = object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_DRAGGING -> {
                //拖拽中
                LogCat.e("拖拽中")
            }
            BottomSheetBehavior.STATE_SETTLING -> {
                //拖拽停止
                LogCat.e("拖拽停止")
            }
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                //半展开
                LogCat.e("半展开")
            }
            BottomSheetBehavior.STATE_EXPANDED -> {
                //完全展开
                LogCat.e("完全展开")
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                //折叠
                LogCat.e("折叠")
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        LogCat.e("偏移量 $slideOffset")
    }
}

fun ViewBinding.bottomBehavior(callback: BottomSheetBehavior.BottomSheetCallback = listener): BottomSheetBehavior<View> {
    return BottomSheetBehavior.from(root).apply {
        addBottomSheetCallback(callback)
    }
}

fun BottomSheetBehavior<View>.show(){
    state = BottomSheetBehavior.STATE_COLLAPSED
}
fun BottomSheetBehavior<View>.hide(){
    state = BottomSheetBehavior.STATE_HIDDEN
}