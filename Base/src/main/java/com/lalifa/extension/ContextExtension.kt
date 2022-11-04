@file:Suppress("unused")

package com.lalifa.extension

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lalifa.style.CustomPresenter
import com.lalifa.style.WeChatPresenter
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.data.OnImagePickCompleteListener
import java.lang.reflect.Field
import java.util.HashSet

/**
 * Context extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */
val Number.dp: Int
    get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

val Number.sp: Float
    get() = toFloat() * Resources.getSystem().displayMetrics.scaledDensity + 0.5f

fun Context.px(dimenResId: Int): Int = resources.getDimensionPixelSize(dimenResId)

fun Context.dp2px(dpValue: Float): Float {
    val scale = resources.displayMetrics.density
    return dpValue * scale + 0.5f
}

fun Context.screenSize(): DisplayMetrics {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics
}

fun Context.packageInfo(): PackageInfo = packageManager.getPackageInfo(packageName, 0)

//版本名
fun Context.versionName(): String = packageInfo().versionName

//版本号
fun Context.versionCode(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    packageInfo().longVersionCode
} else {
    @Suppress("DEPRECATION")
    packageInfo().versionCode.toLong()
}

fun Context.inflate(
    layoutResId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View =
    LayoutInflater.from(this).inflate(layoutResId, parent, attachToRoot)

fun showToast(context: Context, resId: Int) {
    Toast.makeText(context.applicationContext, resId, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, content: String) {
    Toast.makeText(context.applicationContext, content, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context, resId: Int) {
    Toast.makeText(context.applicationContext, resId, Toast.LENGTH_LONG).show()
}

fun showLongToast(context: Context, content: String) {
    Toast.makeText(context.applicationContext, content, Toast.LENGTH_LONG).show()
}

fun Context.toast(resId: Int) {
    showToast(applicationContext, resId)
}

fun Context.toast(content: String) {
    showToast(applicationContext, content)
}


fun Context.longToast(resId: Int) {
    showLongToast(applicationContext, resId)
}

fun Context.longToast(content: String) {
    showLongToast(applicationContext, content)
}

fun Context.customToast(content: String, time: Long = 5000) {
    val toast = Toast.makeText(applicationContext, content, Toast.LENGTH_LONG)
    toast.show()
    globalUITask(time) { toast.cancel() }
}

fun Context.color(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.drawable(resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

fun Context.string(resId: Int): String {
    return resources.getString(resId)
}

fun Context.string(resId: Int, vararg params: Any): String {
    return resources.getString(resId, *params)
}

// 获取剪切板文本
fun Context.getClipText(): String {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    if (null == clipboardManager || !clipboardManager.hasPrimaryClip()) {
        return ""
    }
    val clipData = clipboardManager.primaryClip
    if (null == clipData || clipData.itemCount < 1) {
        return ""
    }
    val clipText = clipData.getItemAt(0)?.text ?: ""
    return clipText.toString()
}

// 清空剪切板文本
fun Context.clearClipText() {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    clipboardManager?.setPrimaryClip(ClipData.newPlainText(null, ""))
}

fun Context.isNetworkConnected(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = manager.activeNetworkInfo
    @Suppress("DEPRECATION")
    return networkInfo != null && networkInfo.isAvailable
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasExternalPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun Context.hasLocationPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
}


/**
 * 修复输入法内存泄漏问题
 */
fun Context.fixInputMethodMemoryLeak() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val viewArr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
    var field: Field?
    var fieldObj: Any?
    for (view in viewArr) {
        try {
            field = inputMethodManager.javaClass.getDeclaredField(view)
            if (null != field && !field.isAccessible) {
                field.isAccessible = true
            }
            fieldObj = field.get(inputMethodManager)
            if (fieldObj != null && fieldObj is View) {
                if (fieldObj.context === this) {
                    //注意需要判断View关联的Context是不是当前Activity，否则有可能造成正常的输入框输入失效
                    field.set(inputMethodManager, null)
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Activity.imageChoose(
    canVideo: Boolean = false,
    maxCount: Int = 9,
    lastImageList: ArrayList<ImageItem>? = null,
    showCamera: Boolean = true,
    videoSingle: Boolean = true,
    maxVideoDuration: Long = 16000L,
    listener: OnImagePickCompleteListener
) {
    imagePick(
        canVideo,
        maxCount,
        lastImageList,
        showCamera,
        videoSingle,
        maxVideoDuration,
        listener
    )
}

fun Fragment.imagePick(
    canVideo: Boolean = false,
    maxCount: Int = 9,
    lastImageList: ArrayList<ImageItem>? = null,
    showCamera: Boolean = true,
    videoSingle: Boolean = true,
    maxVideoDuration: Long = 16000L,
    listener: OnImagePickCompleteListener
) {
    requireActivity().imagePick(
        canVideo,
        maxCount,
        lastImageList,
        showCamera,
        videoSingle,
        maxVideoDuration,
        listener
    )
}

/**
 * 图片选择扩展
 * @receiver Context
 * @param maxCount Int  最大选择数
 * @param showCamera Boolean  是否显示拍照
 * @param videoSingle Boolean  视频是否单选
 * @param maxVideoDuration Long 视频最大时长
 * @param listener OnImagePickCompleteListener
 */
fun Context.imagePick(
    canVideo: Boolean = false,
    maxCount: Int = 9,
    lastImageList: ArrayList<ImageItem>? = null,
    showCamera: Boolean = true,
    videoSingle: Boolean = true,
    maxVideoDuration: Long = 16000L,
    listener: OnImagePickCompleteListener
) {
    val mimeTypes: MutableSet<MimeType> = HashSet()
    if (canVideo) {
        mimeTypes.add(MimeType.MP4)
    } else {
        mimeTypes.add(MimeType.JPEG)
        mimeTypes.add(MimeType.PNG)
    }
    ImagePicker.withMulti(WeChatPresenter(!canVideo)) //指定presenter
        .setMaxCount(maxCount) //设置选择的最大数
        .setColumnCount(4) //设置列数
        .setOriginal(false)
        .mimeTypes(mimeTypes) //设置要加载的文件类型，可指定单一类型
        .setSelectMode(SelectMode.MODE_MULTI) //选择模式
        .setDefaultOriginal(false)
        .setPreviewVideo(true)
        .showCamera(showCamera) //显示拍照
        .showCameraOnlyInAllMediaSet(showCamera)
        .setPreview(true) //是否开启预览
        .setVideoSinglePick(videoSingle) //设置视频单选
        .setSinglePickWithAutoComplete(false)
        .setSinglePickImageOrVideoType(true) //设置图片和视频单一类型选择
        .setMaxVideoDuration(maxVideoDuration) //设置视频可选取的最大时长
        .setMinVideoDuration(5000L)
        .setSingleCropCutNeedTop(true) //设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
        .setLastImageList(lastImageList) //设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
        .setShieldList<Any>(null).pick(this as Activity?, listener)
}

fun Context.start(cls: Class<out Activity>) {
    startActivity(Intent(this, cls))
}

fun Context.start(cls: Class<out Activity>, intent: Intent.() -> Unit) {
    val intent1 = Intent(this, cls)
    intent(intent1)
    startActivity(intent1)
}

fun Context.startOrder(cls: Class<out Activity>, type: Int) {
    val intent1 = Intent(this, cls)
    intent1.putExtra("type",type)
    startActivity(intent1)
}

fun Context.asActivity() = this as Activity

fun Context.finish() {
    (this as Activity).finish()
}

