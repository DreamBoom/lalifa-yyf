package com.lalifa.style

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ypx.imagepicker.adapter.PickerItemAdapter
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.data.ICameraExecutor
import com.ypx.imagepicker.data.IReloadExecutor
import com.ypx.imagepicker.data.ProgressSceneEnum
import com.ypx.imagepicker.presenter.IPickerPresenter
import com.ypx.imagepicker.utils.PViewSizeUtils
import com.ypx.imagepicker.views.PickerUiConfig

/**
 *
 * @ClassName CustomPresenter
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/2/17 10:05
 *
 */
class CustomPresenter(private var isImage: Boolean = true) : IPickerPresenter {


    /**
     * 图片加载，在安卓10上，外部存储的图片路径只能用Uri加载，私有目录的图片可以用绝对路径加载
     * 所以这个方法务必需要区分有uri和无uri的情况
     * 一般媒体库直接扫描出来的图片是含有uri的，而剪裁生成的图片保存在私有目录中，因此没有uri，只有绝对路径
     * 所以这里需要做一个兼容处理
     *
     * @param view        imageView
     * @param item        图片信息
     * @param size        加载尺寸
     * @param isThumbnail 是否是缩略图
     */
    override fun displayImage(view: View, item: ImageItem, size: Int, isThumbnail: Boolean) {
        val `object`: Any = if (item.uri != null) item.uri else item.path
        Glide.with(view.context).load(`object`).apply(
            RequestOptions()
                .format(if (isThumbnail) DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888)
        )
            .override(if (isThumbnail) size else Target.SIZE_ORIGINAL)
            .into((view as ImageView))
    }

    /**
     * 设置自定义ui显示样式，不可返回null
     * 该方法返回一个PickerUiConfig对象
     *
     *
     *
     * 该对象可以配置如下信息：
     * 1.主题色
     * 2.相关页面背景色
     * 3.选择器标题栏，底部栏，item，文件夹列表item，预览页面，剪裁页面的定制
     *
     *
     *
     *
     * 详细使用方法参考 (@link https://github.com/yangpeixing/YImagePicker/blob/master/YPX_ImagePicker_androidx/app/src/main/java/com/ypx/imagepickerdemo/style/WeChatPresenter.java)
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    override fun getUiConfig(context: Context?): PickerUiConfig {
        val uiConfig = PickerUiConfig()
        //设置主题色
        uiConfig.themeColor = Color.parseColor("#2079f3")
        //设置是否显示状态栏
        uiConfig.isShowStatusBar = true
        //设置状态栏颜色
        uiConfig.statusBarColor = Color.WHITE
        //设置选择器背景
        uiConfig.pickerBackgroundColor = Color.WHITE
        //设置单图剪裁背景色
        uiConfig.singleCropBackgroundColor = Color.WHITE
        //设置预览页面背景色
        uiConfig.previewBackgroundColor = Color.WHITE
        //设置选择器文件夹打开方向
        uiConfig.folderListOpenDirection = PickerUiConfig.DIRECTION_TOP
        //设置文件夹列表距离顶部/底部边距
        uiConfig.folderListOpenMaxMargin = 0
        //设置小红书剪裁区域的背景色
        uiConfig.cropViewBackgroundColor = Color.WHITE
        //设置文件夹列表距离底部/顶部的最大间距。通俗点就是设置文件夹列表的高
        if (context != null) {
            uiConfig.folderListOpenMaxMargin = PViewSizeUtils.dp(context, 100f)
        }

        //自定义选择器标题栏，底部栏，item，文件夹列表item，预览页面，剪裁页面
        uiConfig.pickerUiProvider = CustomUiProvider()
        return uiConfig
    }

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    override fun tip(context: Context?, msg: String?) {
        if (context == null) {
            return
        }
        Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    override fun overMaxCountTip(context: Context?, maxCount: Int) {
        tip(context, "最多选择" + maxCount + "个文件")
    }

    /**
     * 显示loading加载框，注意需要调用show方法
     *
     * @param activity          启动加载框的activity
     * @param progressSceneEnum [ProgressSceneEnum]
     *
     *
     *
     * 当progressSceneEnum==当ProgressSceneEnum.loadMediaItem 时，代表在加载媒体文件时显示加载框
     * 目前框架内规定，当文件夹内媒体文件少于1000时，强制不显示加载框，大于1000时才会执行此方法
     *
     *
     *
     * 当progressSceneEnum==当ProgressSceneEnum.crop 时，代表是剪裁页面的加载框
     *
     * @return DialogInterface 对象，用于关闭加载框，返回null代表不显示加载框
     */
    override fun showProgressDialog(
        activity: Activity?,
        progressSceneEnum: ProgressSceneEnum
    ): DialogInterface? {
        return ProgressDialog.show(
            activity,
            null,
            if (progressSceneEnum == ProgressSceneEnum.crop) "正在剪裁..." else "正在加载..."
        )
    }

    /**
     * 拦截选择器完成按钮点击事件
     *
     * @param activity     当前选择器activity
     * @param selectedList 已选中的列表
     * @return true:则拦截选择器完成回调， false，执行默认的选择器回调
     */
    override fun interceptPickerCompleteClick(
        activity: Activity?,
        selectedList: ArrayList<ImageItem?>?,
        selectConfig: BaseSelectConfig?
    ): Boolean {
        /*tip(activity, "拦截了完成按钮点击" + selectedList.size());
        Intent intent = new Intent(activity, AlohaActivity.class);
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, selectedList);
        activity.startActivity(intent);*/
        return false
    }

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    override fun interceptPickerCancel(
        activity: Activity?,
        selectedList: ArrayList<ImageItem?>?
    ): Boolean {
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            return false
        }
        activity.finish()
        /*       AlertDialog.Builder builder = new AlertDialog.Builder(new WeakReference<>(activity).get());
        builder.setMessage("是否放弃选择？");
        builder.setPositiveButton("确定",
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                });
        builder.setNegativeButton("点错了",
                (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();*/return true
    }

    /**
     *
     *
     * 图片点击事件拦截，如果返回true，则不会执行选中操纵，如果要拦截此事件并且要执行选中
     * 请调用如下代码：
     *
     *
     * adapter.preformCheckItem()
     *
     *
     *
     *
     * 此方法可以用来跳转到任意一个页面，比如自定义的预览
     *
     * @param activity        上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param selectConfig    选择器配置项，如果是微信样式，则selectConfig继承自MultiSelectConfig
     * 如果是小红书剪裁样式，则继承自CropSelectConfig
     * @param adapter         当前列表适配器，用于刷新数据
     * @param isClickCheckBox 是否点击item右上角的选中框
     * @param reloadExecutor  刷新器
     * @return 是否拦截
     */
    override fun interceptItemClick(
        activity: Activity?,
        imageItem: ImageItem?,
        selectImageList: ArrayList<ImageItem?>?,
        allSetImageList: ArrayList<ImageItem?>?,
        selectConfig: BaseSelectConfig?,
        adapter: PickerItemAdapter?,
        isClickCheckBox: Boolean,
        reloadExecutor: IReloadExecutor?
    ): Boolean {
        return false
    }


    /**
     * 拍照点击事件拦截
     *
     * @param activity  当前activity
     * @param takePhoto 拍照接口
     * @return 是否拦截
     */
    override fun interceptCameraClick(activity: Activity?, takePhoto: ICameraExecutor): Boolean {
        if (activity == null || activity.isDestroyed) {
            return false
        }
        if (isImage) {
            takePhoto.takePhoto()
        } else {
            takePhoto.takeVideo()
        }
        return true
        /*    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setSingleChoiceItems(new String[]{"拍照", "录像"}, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    takePhoto.takePhoto();
                } else {
                    takePhoto.takeVideo();
                }
            }
        });
        builder.show();
        return true;*/
    }
}