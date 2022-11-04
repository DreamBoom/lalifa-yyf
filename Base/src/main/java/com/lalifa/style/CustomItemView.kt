package com.lalifa.style

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ypx.imagepicker.R
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.PickerItemDisableCode
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.presenter.IPickerPresenter
import com.ypx.imagepicker.utils.PCornerUtils
import com.ypx.imagepicker.views.base.PickerItemView
import com.ypx.imagepicker.views.redbook.RedBookItemView

/**
 *
 * @ClassName CustomItemView
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:29
 *
 */
class CustomItemView: PickerItemView {
    private lateinit var imageView: ImageView
    private lateinit var mVMask: View
    private lateinit var mVSelect:View
    private lateinit var mTvIndex: TextView
    private lateinit var mTvDuration: TextView
    private lateinit var selectConfig: BaseSelectConfig

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * @return item布局id
     */
    override fun getLayoutId(): Int {
        return com.ypx.imagepicker.R.layout.picker_item
    }

    /**
     * @param view 初始化view
     */
    override fun initView(view: View) {
        mTvIndex = view.findViewById(R.id.mTvIndex)
        mVMask = view.findViewById(R.id.v_mask)
        mVSelect = view.findViewById<View>(R.id.v_select)
        imageView = view.findViewById(R.id.iv_image)
        mTvDuration = view.findViewById(R.id.mTvDuration)
    }

    override fun getCameraView(
        selectConfig: BaseSelectConfig,
        presenter: IPickerPresenter?
    ): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.picker_item_camera, null)
        val mTvvCamera = view.findViewById<TextView>(R.id.tv_camera)
        mTvvCamera.text =
            if (selectConfig.isOnlyShowVideo) context.getString(R.string.picker_str_item_take_video) else context.getString(
                R.string.picker_str_item_take_photo
            )
        return view
    }

    override fun getCheckBoxView(): View {
        return mVSelect
    }

    override fun initItem(
        imageItem: ImageItem?,
        presenter: IPickerPresenter,
        selectConfig: BaseSelectConfig?
    ) {
        this.selectConfig = selectConfig!!
        presenter.displayImage(imageView, imageItem, imageView.width, true)
    }

    override fun disableItem(imageItem: ImageItem?, disableCode: Int) {
        //默认开启校验是否超过最大数时item状态为不可选中,这里关闭它
        if (disableCode == PickerItemDisableCode.DISABLE_OVER_MAX_COUNT) {
            return
        }
        mVMask.visibility = VISIBLE
        mVMask.setBackgroundColor(Color.parseColor("#80FFFFFF"))
        mTvIndex.visibility = GONE
        mVSelect.visibility = GONE
    }

    @SuppressLint("DefaultLocale")
    override fun enableItem(imageItem: ImageItem, isChecked: Boolean, indexOfSelectedList: Int) {
        mTvIndex.visibility = VISIBLE
        mVSelect.visibility = VISIBLE
        if (imageItem.isVideo) {
            mTvDuration.visibility = VISIBLE
            mTvDuration.text = imageItem.getDurationFormat()
            if (selectConfig.isVideoSinglePick && selectConfig.isSinglePickAutoComplete) {
                mTvIndex.visibility = GONE
                mVSelect.visibility = GONE
            }
        } else {
            mTvDuration.visibility = GONE
        }
        if (indexOfSelectedList >= 0) {
            mTvIndex.text = String.format("%d", indexOfSelectedList + 1)
            mTvIndex.background =
                PCornerUtils.cornerDrawableAndStroke(
                    Color.parseColor("#2079f3"),
                    dp(12f).toFloat(),
                    dp(1f),
                    Color.WHITE
                )
        } else {
            mTvIndex.setBackgroundResource(com.lalifa.base.R.drawable.selsct)
            mTvIndex.text = ""
        }
        if (imageItem.isPress) {
            mVMask.visibility = VISIBLE
            val halfColor = Color.parseColor("#2079f3")
            val maskDrawable = PCornerUtils.cornerDrawableAndStroke(
                halfColor, 0f, dp(2f),
                themeColor
            )
            mVMask.background = maskDrawable
        } else {
            mVMask.visibility = GONE
        }
    }
}