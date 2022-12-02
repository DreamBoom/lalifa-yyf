package cn.rongcloud.roomkit.ui.room.fragment

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.api.uploadIm
import cn.rongcloud.roomkit.ui.room.fragment.BackgroundSettingFragment.OnSelectBackgroundListener
import com.drake.net.utils.scopeNet
import com.google.android.material.imageview.ShapeableImageView
import com.lalifa.extension.imagePick
import com.lalifa.extension.load
import com.lalifa.widget.dialog.dialog.BaseBottomSheetDialog

/**
 * @author gyn
 * @date 2021/10/8
 */
class BackgroundSettingFragment(
    private val currentBg: String,
    private val listener: OnSelectBackgroundListener?
) : BaseBottomSheetDialog(
    R.layout.fragment_background_setting
) {
    private var mTvConfirm: AppCompatTextView? = null
    private var ivBackground: ShapeableImageView? = null
    var imPath = ""
    override fun initView() {
        imPath = currentBg
        mTvConfirm = requireView().findViewById<View>(R.id.tv_confirm) as AppCompatTextView
        ivBackground = requireView().findViewById<View>(R.id.iv_background) as ShapeableImageView
        ivBackground!!.setOnClickListener {
            imagePick(maxCount = 1) {
                ivBackground!!.load(it[0].path)
                scopeNet {
                    val uploadIm = uploadIm(it[0].path)
                    imPath = uploadIm.fullurl
                }
            }
        }
        mTvConfirm!!.setOnClickListener { v: View? ->
            if (listener != null) {
                listener.selectBackground(imPath)
                dismiss()
            }
        }
    }

    interface OnSelectBackgroundListener {
        fun selectBackground(url: String?)
    }
}