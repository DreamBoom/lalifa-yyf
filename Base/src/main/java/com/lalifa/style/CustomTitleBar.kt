package com.lalifa.style

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ypx.imagepicker.R
import com.lalifa.extension.applyVisible
import com.lalifa.extension.gone
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.ImageSet
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.utils.PCornerUtils
import com.ypx.imagepicker.views.base.PickerControllerView

/**
 *
 * @ClassName CustomTitleBar
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:08
 *
 */
class CustomTitleBar : PickerControllerView {
    private var mTvTitle: TextView? = null
    private var mArrowImg: ImageView? = null
    private var mTvNext: TextView? = null
    private var mTvSelectNum: TextView? = null
    private var mSelected: ImageView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun getViewHeight(): Int {
        return dp(55f)
    }

    private fun setImageSetArrowIconID(imageSetArrowIconID: Int) {
        mArrowImg?.setImageResource(imageSetArrowIconID)
    }

    override fun getLayoutId(): Int {
        return com.lalifa.base.R.layout.title_bar
    }

    override fun initView(view: View) {
        val mBackImg = view.findViewById<ImageView>(R.id.mBackImg)
        mTvTitle = view.findViewById(R.id.mTvSetName)
        mTvTitle?.typeface = Typeface.DEFAULT_BOLD
        mSelected = view.findViewById(com.lalifa.base.R.id.selected)
        mSelected?.gone()
        mArrowImg = view.findViewById(R.id.mArrowImg)
        mTvNext = view.findViewById(R.id.mTvNext)
        mTvNext?.paint?.isFakeBoldText = true
        mTvNext?.gone()
        mTvSelectNum = view.findViewById(R.id.mTvSelectNum)
        mTvSelectNum?.visibility = View.GONE
        mArrowImg?.scaleType = ImageView.ScaleType.CENTER
        setImageSetArrowIconID(com.lalifa.base.R.drawable.down)
        mBackImg?.setImageResource(com.lalifa.base.R.mipmap.view_close)
        mBackImg?.setOnClickListener { onBackPressed() }
        setBackgroundColor(Color.WHITE)
        mTvNext?.background = PCornerUtils.cornerDrawable(
            Color.parseColor("#50B0B0B0"),
            dp(30f).toFloat()
        )
        mTvNext?.text = context.getString(R.string.picker_str_title_right)
    }

    override fun isAddInParent(): Boolean {
        return true
    }

    override fun getCanClickToCompleteView(): View? {
        return null
    }

    override fun getCanClickToIntentPreviewView(): View? {
        return null
    }

    override fun getCanClickToToggleFolderListView(): View {
        return mTvTitle!!
    }


    override fun setTitle(title: String?) {
        mTvTitle?.text = title
        mTvTitle.applyVisible(title?.contains("/") != true)
        mArrowImg.applyVisible(title?.contains("/") != true)
    }

    override fun onTransitImageSet(isOpen: Boolean) {
        if (isOpen) {
            mArrowImg?.rotation = 180f
        } else {
            mArrowImg?.rotation = 0f
        }
    }

    override fun onImageSetSelected(imageSet: ImageSet) {
        mTvTitle?.text = imageSet.name
    }


    @SuppressLint("DefaultLocale")
    override fun refreshCompleteViewState(
        selectedList: ArrayList<ImageItem?>?,
        selectConfig: BaseSelectConfig?
    ) {
        if (selectedList != null && selectedList.size == 0) {
            mTvNext?.isEnabled = false
            mTvNext?.setTextColor(Color.BLACK)
            mTvNext?.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), dp(30f).toFloat())
            mTvSelectNum?.visibility = View.GONE
        } else {
            mTvNext?.isEnabled = true
            mTvNext?.setTextColor(Color.WHITE)
            mTvNext?.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#2079f3"), dp(30f).toFloat())
        }
    }
}