package com.lalifa.style

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ypx.imagepicker.R
import com.lalifa.extension.onClick
import com.lalifa.extension.visible
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.adapter.MultiPreviewAdapter
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.PickerItemDisableCode
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.bean.selectconfig.MultiSelectConfig
import com.ypx.imagepicker.helper.recyclerviewitemhelper.SimpleItemTouchHelperCallback
import com.ypx.imagepicker.presenter.IPickerPresenter
import com.ypx.imagepicker.utils.PCornerUtils
import com.ypx.imagepicker.utils.PStatusBarUtil
import com.ypx.imagepicker.views.PickerUiConfig
import com.ypx.imagepicker.views.base.PickerControllerView
import com.ypx.imagepicker.views.base.PreviewControllerView
import java.lang.ref.WeakReference

/**
 *
 * @ClassName CustomPreviewControllerView
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:35
 *
 */
class CustomPreviewControllerView : PreviewControllerView {
    private lateinit var mPreviewRecyclerView: RecyclerView
    private lateinit var mBottomBar: RelativeLayout
    private lateinit var mSelectCheckBox: CheckBox
    private lateinit var mOriginalCheckBox: CheckBox
    private lateinit var previewAdapter: MultiPreviewAdapter
    private lateinit var presenter: IPickerPresenter
    private lateinit var selectConfig: BaseSelectConfig
    private lateinit var uiConfig: PickerUiConfig
    private lateinit var selectedList: ArrayList<ImageItem>
    private lateinit var mTitleContainer: FrameLayout
    private var isShowOriginal = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getLayoutId(): Int {
        return com.lalifa.base.R.layout.custom_preview
    }

    private lateinit var mTvNext: TextView

    override fun initView(view: View) {
        mPreviewRecyclerView = view.findViewById(R.id.mPreviewRecyclerView)
        mBottomBar = view.findViewById(R.id.bottom_bar)
        mSelectCheckBox = view.findViewById(R.id.mSelectCheckBox)
        mOriginalCheckBox = view.findViewById(R.id.mOriginalCheckBox)
        mTitleContainer = view.findViewById(R.id.mTitleContainer)
        mBottomBar.isClickable = true
        setOriginalCheckBoxDrawable(com.lalifa.base.R.mipmap.select_no, com.lalifa.base.R.mipmap.select_yes)
        setSelectCheckBoxDrawable(com.lalifa.base.R.mipmap.select_no, com.lalifa.base.R.mipmap.select_yes)
        mOriginalCheckBox.text = context.getString(com.ypx.imagepicker.R.string.picker_str_bottom_original)
        mSelectCheckBox.text = context.getString(R.string.picker_str_bottom_choose)

        mTvNext = view.findViewById(R.id.mTvNext)
        mTvNext.paint.isFakeBoldText = true
        mTvNext.background = PCornerUtils.cornerDrawable(
            Color.parseColor("#50B0B0B0"),
            dp(30f).toFloat()
        )
        mTvNext.onClick {
            completeView?.performClick()
        }

    }

    override fun setStatusBar() {
        setTitleBarColor(Color.WHITE)
        setBottomBarColor(Color.parseColor("#f0303030"))
    }

    override fun initData(
        selectConfig: BaseSelectConfig?,
        presenter: IPickerPresenter?,
        uiConfig: PickerUiConfig?,
        selectedList: ArrayList<ImageItem>?
    ) {
        this.selectConfig = selectConfig!!
        this.presenter = presenter!!
        this.selectedList = selectedList!!
        this.uiConfig = uiConfig!!
        isShowOriginal = selectConfig is MultiSelectConfig && selectConfig.isShowOriginalCheckBox
        initUI()
        initPreviewList()
    }

    private lateinit var topBox: ImageView
    private fun initUI() {
        titleBar = uiConfig.pickerUiProvider.getTitleBar(context)
        bottomBar = uiConfig.pickerUiProvider.getBottomBar(context)
        mTitleContainer.addView(
            titleBar, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        topBox = titleBar.findViewById<ImageView>(com.lalifa.base.R.id.selected)
        topBox.visible()
        topBox.onClick {
            topBox.isSelected = !topBox.isSelected
            val selected = topBox.isSelected
            if (!selected) {
                mSelectCheckBox.isChecked = false
                selectedList.remove(currentImageItem)
            } else {
                val disableCode = PickerItemDisableCode.getItemDisableCode(
                    currentImageItem, selectConfig, selectedList,
                    selectedList.contains(currentImageItem)
                )
                if (disableCode != PickerItemDisableCode.NORMAL) {
                    val message = PickerItemDisableCode.getMessageFormCode(
                        context,
                        disableCode,
                        presenter,
                        selectConfig
                    )
                    if (message.isNotEmpty()) {
                        presenter.tip(WeakReference(context).get(), message)
                    }
                    mSelectCheckBox.isChecked = false
                    return@onClick
                }
                if (!selectedList.contains(currentImageItem)) {
                    selectedList.add(currentImageItem)
                }
                mSelectCheckBox.isChecked = true
            }
            titleBar.refreshCompleteViewState(selectedList, selectConfig)
            notifyPreviewList(currentImageItem)
        }
        mSelectCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                mSelectCheckBox.isChecked = false
                selectedList.remove(currentImageItem)
            } else {
                val disableCode = PickerItemDisableCode.getItemDisableCode(
                    currentImageItem, selectConfig, selectedList,
                    selectedList.contains(currentImageItem)
                )
                if (disableCode != PickerItemDisableCode.NORMAL) {
                    val message = PickerItemDisableCode.getMessageFormCode(
                        context,
                        disableCode,
                        presenter,
                        selectConfig
                    )
                    if (message.isNotEmpty()) {
                        presenter.tip(WeakReference(context).get(), message)
                    }
                    mSelectCheckBox.isChecked = false
                    return@OnCheckedChangeListener
                }
                if (!selectedList.contains(currentImageItem)) {
                    selectedList.add(currentImageItem)
                }
                mSelectCheckBox.isChecked = true
            }
            titleBar.refreshCompleteViewState(selectedList, selectConfig)
            notifyPreviewList(currentImageItem)
        })
        mOriginalCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mSelectCheckBox.isChecked = true
            }
            ImagePicker.isOriginalImage = isChecked
        }
    }
    @SuppressLint("SetTextI18n")
    private fun initPreviewList() {
        mPreviewRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        previewAdapter = MultiPreviewAdapter(selectedList, presenter)
        mPreviewRecyclerView.adapter = previewAdapter
        val callback = SimpleItemTouchHelperCallback(previewAdapter)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(mPreviewRecyclerView)
        nextUI()
    }
    @SuppressLint("SetTextI18n")
    private fun nextUI(){
        if (selectedList.size == 0) {
            mTvNext.isEnabled = false
            mTvNext.setTextColor(Color.BLACK)
            mTvNext.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), dp(30f).toFloat())
        } else {
            mTvNext.isEnabled = true
            mTvNext.setTextColor(Color.WHITE)
            mTvNext.background =
                PCornerUtils.cornerDrawable(Color.parseColor("#2079f3"), dp(30f).toFloat())
        }
        mTvNext.text = "确定 ${if (selectedList.size == 0) "" else selectedList.size}"
    }
    /**
     * 刷新预览编辑列表
     *
     * @param imageItem 当前预览的图片
     */

    private fun notifyPreviewList(imageItem: ImageItem?) {
        previewAdapter.setPreviewImageItem(imageItem)
        nextUI()
        if (selectedList.contains(imageItem)) {
            mPreviewRecyclerView.smoothScrollToPosition(selectedList.indexOf(imageItem))
        }
    }

    private lateinit var titleBar: PickerControllerView
    private lateinit var bottomBar: PickerControllerView
    override fun getCompleteView(): View? {

        return bottomBar.canClickToCompleteView
    }

    override fun singleTap() {
        if (mTitleContainer.visibility == VISIBLE) {
            mTitleContainer.animation =
                AnimationUtils.loadAnimation(context, R.anim.picker_top_out)
            mBottomBar.animation = AnimationUtils.loadAnimation(context, R.anim.picker_fade_out)
            mPreviewRecyclerView.animation = AnimationUtils.loadAnimation(
                context, R.anim.picker_fade_out
            )
            mTitleContainer.visibility = GONE
            mBottomBar.visibility = GONE
            mPreviewRecyclerView.visibility = GONE
        } else {
            mTitleContainer.animation =
                AnimationUtils.loadAnimation(context, R.anim.picker_top_in)
            mBottomBar.animation = AnimationUtils.loadAnimation(context, R.anim.picker_fade_in)
            mPreviewRecyclerView.animation = AnimationUtils.loadAnimation(
                context, R.anim.picker_fade_in
            )
            mTitleContainer.visibility = VISIBLE
            mBottomBar.visibility = VISIBLE
            mPreviewRecyclerView.visibility = VISIBLE
        }
    }

    private lateinit var currentImageItem: ImageItem

    @SuppressLint("DefaultLocale")
    override fun onPageSelected(position: Int, imageItem: ImageItem, totalPreviewCount: Int) {
        currentImageItem = imageItem
        titleBar.setTitle(String.format("%d/%d", position + 1, totalPreviewCount))
        mSelectCheckBox.isChecked = selectedList.contains(imageItem)
        topBox.isSelected = selectedList.contains(imageItem)
        notifyPreviewList(imageItem)
        titleBar.refreshCompleteViewState(selectedList, selectConfig)
        nextUI()
        if (!imageItem.isVideo && isShowOriginal) {
            mOriginalCheckBox.visibility = VISIBLE
            mOriginalCheckBox.isChecked = ImagePicker.isOriginalImage
        } else {
            mOriginalCheckBox.visibility = GONE
        }
    }

    private fun setOriginalCheckBoxDrawable(unCheckDrawableID: Int, checkedDrawableID: Int) {
        PCornerUtils.setCheckBoxDrawable(mOriginalCheckBox, checkedDrawableID, unCheckDrawableID)
    }

    private fun setSelectCheckBoxDrawable(unCheckDrawableID: Int, checkedDrawableID: Int) {
        PCornerUtils.setCheckBoxDrawable(mSelectCheckBox, checkedDrawableID, unCheckDrawableID)
    }

    private fun setTitleBarColor(titleBarColor: Int) {
        mTitleContainer.setBackgroundColor(titleBarColor)
        mTitleContainer.setPadding(0, PStatusBarUtil.getStatusBarHeight(context), 0, 0)
        PStatusBarUtil.setStatusBar(
            context as Activity, Color.TRANSPARENT, true,
            PStatusBarUtil.isDarkColor(titleBarColor)
        )
    }

    private fun setBottomBarColor(bottomBarColor: Int) {
        mBottomBar.setBackgroundColor(bottomBarColor)
        mPreviewRecyclerView.setBackgroundColor(bottomBarColor)
    }
}