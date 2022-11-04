package com.lalifa.style

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lalifa.base.R
import com.lalifa.extension.applyVisible
import com.lalifa.extension.gone
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.ImageSet
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.utils.PCornerUtils
import com.ypx.imagepicker.views.base.PickerControllerView
import java.util.ArrayList

/**
 *
 * @ClassName CustomTitleBar
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:08
 *
 */
class CustomBottomBar : PickerControllerView {

    private lateinit var mTvNext: TextView

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getLayoutId(): Int {
        return R.layout.bottom_bar
    }

    override fun initView(view: View) {
        mTvNext = view.findViewById(R.id.mTvNext)
        mTvNext.paint.isFakeBoldText = true
        mTvNext.background = PCornerUtils.cornerDrawable(
            Color.parseColor("#50B0B0B0"),
            dp(30f).toFloat()
        )
    }


    override fun getViewHeight(): Int {
        return dp(55f)
    }

    override fun getCanClickToCompleteView(): View {
        return mTvNext
    }

    override fun getCanClickToIntentPreviewView(): View? {
        return null
    }

    override fun getCanClickToToggleFolderListView(): View? {
        return null
    }

    override fun setTitle(title: String?) {

    }

    override fun onTransitImageSet(isOpen: Boolean) {

    }

    override fun onImageSetSelected(imageSet: ImageSet?) {

    }

    override fun refreshCompleteViewState(
        selectedList: ArrayList<ImageItem>?,
        selectConfig: BaseSelectConfig?
    ) {
        if (selectedList != null && selectedList.size == 0) {
            mTvNext.isEnabled = false
            mTvNext.setTextColor(Color.BLACK)
            mTvNext.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), dp(30f).toFloat())
        } else {
            mTvNext.isEnabled = true
            mTvNext.setTextColor(Color.WHITE)
            mTvNext.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#2079f3"), dp(30f).toFloat())
            mTvNext.text="确定 ${selectedList?.size}"
        }
    }


}