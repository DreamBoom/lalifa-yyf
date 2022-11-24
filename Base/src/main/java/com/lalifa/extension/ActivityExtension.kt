@file:Suppress("unused")

package com.lalifa.extension

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import cc.shinichi.library.ImagePreview
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.drake.logcat.LogCat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lalifa.utils.SoftKeyBroadManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Activity extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */
fun Activity.start(cls: Class<out Activity>) {
    startActivity(Intent(this, cls))
}

fun Activity.start(cls: Class<out Activity>, intent: Intent.() -> Unit) {
    val intent1 = Intent(this, cls)
    intent(intent1)
    startActivity(intent1)
}

fun Activity.startCode(cls: Class<out Activity>, intent: (Intent.() -> Unit) = {}) {
    startActivityForResult(Intent(this, cls).apply(intent), 200)
}

fun Activity.finish(code: Int, intent: (Intent.() -> Unit) = {}) {
    setResult(code, Intent().apply(intent))
    finish()
}

fun Activity.getIntentString(name: String): String {
    val stringExtra = intent.getStringExtra(name)
    return if (stringExtra.isNullOrEmpty()) {
        ""
    } else stringExtra
}

fun Activity.getIntentDouble(name: String, defaultValue: Double = 0.00): Double {
    return intent.getDoubleExtra(name, defaultValue)
}

fun Activity.getIntentBoolean(name: String, defaultValue: Boolean = false): Boolean {
    return intent.getBooleanExtra(name, defaultValue)
}

fun Activity.getIntentInt(name: String, defaultValue: Int = -1): Int {
    return intent.getIntExtra(name, defaultValue)
}

fun Activity.getIntentLong(name: String, defaultValue: Long = 0L): Long {
    return intent.getLongExtra(name, defaultValue)
}

fun <T> Activity.getIntentSerializable(name: String): T? {
    return intent.getSerializableExtra(name)?.let { it as T }
}

fun getNow(): Long {
    return System.currentTimeMillis()
}

/**
 * 显示软键盘
 */
fun Activity.showKeyboard(view: View?) {
    view?.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

/**
 * 关闭软键盘
 * @receiver Activity
 * @param view View?
 */
fun Activity.hideKeyboard(view: View?) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
    // imm.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * 屏幕宽
 * @receiver Activity
 * @return Int
 */
fun Activity.screenWidth(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

/**
 * 屏幕高
 * @receiver Activity
 * @return Int
 */
fun Activity.screenHeight(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

fun Activity.fullScreen() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

/**
 * 状态栏透明
 * @receiver Activity
 */
fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun Activity.isDestroy() = isFinishing || isDestroyed


val Activity.isInputOpen: Boolean
    get() = SoftKeyBroadManager((findViewById<ViewGroup>(R.id.content)).getChildAt(0)).isSoftKeyboardOpened

/**
 * 选择音乐文件
 * @receiver Activity
 * @param code Int
 */
fun Activity.startMusic(code: Int) {
    startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).also {
        it.type = "audio/mpeg"//音频文件
        it.addCategory(Intent.CATEGORY_OPENABLE)
    }, code)
}


/**
 * 性别选择
 * @receiver FragmentActivity
 * @param callback Function1<String, Unit>
 */
fun FragmentActivity.sexPickView(callback: (String) -> Unit) {
        val sexList = arrayListOf("男", "女")
        val optionView = OptionsPickerBuilder(
            this@sexPickView
        ) { options1, options2, options3, v ->
            callback.invoke(
                sexList[options1]
            )
        }.setLineSpacingMultiplier(2.0f)//条目间距
            .setSubmitColor(Color.WHITE)//确定按钮文字颜色
            .setCancelColor(Color.WHITE)//取消按钮文字颜色
            .setTitleSize(17)//标题文字大小
            .setSubCalSize(15)//取消，确定文字大小
            .setContentTextSize(17)//内容大小
            .setTitleText("选择性别")
            .setTitleColor(Color.WHITE)
            .setTitleBgColor(Color.parseColor("#1A1A1A"))
            .setDividerColor(Color.parseColor("#1AFFFFFF"))
            .setTextColorCenter(Color.WHITE) //设置选中项文字颜色
            .setTextColorOut(Color.parseColor("#FFFFFF"))
            .setBgColor(Color.parseColor("#1A1A1A"))
            .build<Any>()
        optionView.setPicker(
            sexList.toList()
        )
        optionView.show()
}
/**
 * 性别选择
 * @receiver FragmentActivity
 * @param callback Function1<String, Unit>
 */
fun Activity.sexPickView(callback: (String) -> Unit) {
    val sexList = arrayListOf("男", "女")
    val optionView = OptionsPickerBuilder(
        this@sexPickView
    ) { options1, options2, options3, v ->
        callback.invoke(
            sexList[options1]
        )
    }.setLineSpacingMultiplier(2.0f)//条目间距
        .setSubmitColor(Color.WHITE)//确定按钮文字颜色
        .setCancelColor(Color.WHITE)//取消按钮文字颜色
        .setTitleSize(17)//标题文字大小
        .setSubCalSize(15)//取消，确定文字大小
        .setContentTextSize(17)//内容大小
        .setTitleText("选择性别")
        .setTitleColor(Color.WHITE)
        .setTitleBgColor(Color.parseColor("#1A1A1A"))
        .setDividerColor(Color.parseColor("#1AFFFFFF"))
        .setTextColorCenter(Color.WHITE) //设置选中项文字颜色
        .setTextColorOut(Color.parseColor("#FFFFFF"))
        .setBgColor(Color.parseColor("#1A1A1A"))
        .build<Any>()
    optionView.setPicker(
        sexList.toList()
    )
    optionView.show()
}
/**
 * 时间选择器
 * @receiver Activity
 * @param callback Function1<String, Unit>
 */
fun Activity.timePickView(callback: (String) -> Unit) {
    //时间选择器
    val selectedDate = Calendar.getInstance()
    val startDate = Calendar.getInstance()
    //startDate.set(2013,1,1)
    val endDate = Calendar.getInstance()
    //endDate.set(2020,1,1)
    //正确设置方式 原因：注意事项有说明
    startDate.set(1922, 1, 1)
    val calendar = Calendar.getInstance() //取得当前时间的年月日 时分秒
    val year = calendar[Calendar.YEAR]
    endDate.set(year, 12, 31)

    val pvTime = TimePickerBuilder(
        this
    ) { date, v ->
        callback.invoke(
            getTime(date)
        )
    }
        .setType(booleanArrayOf(true, true, true, false, false, false))// 默认全部显示
        .setCancelText("取消")//取消按钮文字
        .setSubmitText("确认")//确认按钮文字
        .setContentTextSize(14)//滚轮文字大小
        .setTitleSize(14)//标题文字大小
        .setTitleText("选择日期")//标题文字
        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
        .isCyclic(true)//是否循环滚动
        .setTitleColor(Color.WHITE)//标题文字颜色
        .setSubmitColor(Color.WHITE)//确定按钮文字颜色
        .setCancelColor(Color.WHITE)//取消按钮文字颜色
        .setTitleBgColor(Color.parseColor("#402544"))//标题背景颜色 Night mode
        .setBgColor(Color.parseColor("#402544"))//滚轮背景颜色 Night mode
        .setTextColorCenter(Color.WHITE)
        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
        .setRangDate(startDate, endDate)//起始终止年月日设定
        .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
        .isDialog(true)//是否显示为对话框样式
        .setLineSpacingMultiplier(2.0f)
        .isAlphaGradient(true)
        .build()
    pvTime.show()
}
private fun getTime( date:Date):String {
    val format =  SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}

/**
 * 获取asset目录指定文件的内容
 * @receiver Activity
 * @param fileName String
 * @return String
 */
fun Activity.getAsset(fileName: String): String {
    try {
        assets?.apply {
            assets.list("")?.forEach {
                if (it.equals(fileName)) {
                    val stringBuilder = StringBuilder()
                    val buffer = BufferedReader(InputStreamReader(assets.open(fileName)))
                    buffer.readLines().forEach {
                        stringBuilder.append(it)
                    }
                    return stringBuilder.toString()
                }
            }
        }
    } catch (e: Exception) {
        LogCat.e(e)
    }
    return ""
}

/**
 * 申请权限
 * @receiver Activity
 * @param permissions [@kotlin.ExtensionFunctionType] Function1<ArrayList<String>, Unit> 权限列表
 * @param onGranted Function2<MutableList<String>, Boolean, Unit> 成功回调
 * @param onDenied Function2<MutableList<String>, Boolean, Unit> 失败回调
 */
fun Activity.requestPerm(
    permissions: ArrayList<String>.() -> Unit,
    onGranted: (MutableList<String>, Boolean) -> Unit,
    onDenied: (MutableList<String>, Boolean) -> Unit = { list, never -> }
) {
    val xxPermissions = XXPermissions.with(this)
    ArrayList<String>().apply(permissions).forEach {
        xxPermissions.permission(it)
    }
    xxPermissions.request(object : OnPermissionCallback {
        override fun onGranted(
            permissions: MutableList<String>,
            all: Boolean
        ) {
            onGranted.invoke(permissions, all)
        }

        override fun onDenied(
            permissions: MutableList<String>,
            never: Boolean
        ) {
            if (never) {
                toast("被永久拒绝授权，请手动授予读写权限")
                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                XXPermissions.startPermissionActivity(
                    this@requestPerm,
                    permissions
                )
            } else {
                toast("获取权限失败")
            }
            onDenied.invoke(permissions, never)
        }
    })
}

/**
 * 大图预览
 * @receiver ImageView
 * @param position Int
 * @param urlList List<String>
 */
fun Context.preview(position: Int = 0, urlList: List<String>) {
    ImagePreview.instance // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
        .setContext(this) // 从第几张图片开始，索引从0开始哦~
        .setIndex(position) //=================================================================================================
        // 有三种设置数据集合的方式，根据自己的需求进行三选一：
        // 1：第一步生成的imageInfo List
        //.setImageInfoList(imageInfoList)
        // 2：直接传url List
        .setImageList(urlList.toMutableList()) // 3：只有一张图片的情况，可以直接传入这张图片的url
        //.setImage(String image)
        //=================================================================================================
        // 加载策略，默认为手动模式（具体可看下面加载策略的详细说明）
        .setLoadStrategy(ImagePreview.LoadStrategy.Default) // 保存的文件夹名称，会在SD卡根目录进行文件夹的新建。
        // (你也可设置嵌套模式，比如："BigImageView/Download"，会在SD卡根目录新建BigImageView文件夹，并在BigImageView文件夹中新建Download文件夹)
        .setFolderName("BigImageView/Download") // 缩放动画时长，单位ms
        .setZoomTransitionDuration(300) // 是否启用点击图片关闭。默认启用
        .setEnableClickClose(true) // 是否启用上拉/下拉关闭。默认不启用
        .setEnableDragClose(true) // 是否显示关闭页面按钮，在页面左下角。默认不显示
        .setShowCloseButton(false) // 设置关闭按钮图片资源，可不填，默认为库中自带：R.drawable.ic_action_close
        .setCloseIconResId(cc.shinichi.library.R.drawable.ic_action_close) // 是否显示下载按钮，在页面右下角。默认显示
        .setShowDownButton(false) // 设置下载按钮图片资源，可不填，默认为库中自带：R.drawable.icon_download_new
        .setDownIconResId(cc.shinichi.library.R.drawable.icon_download_new) // 设置是否显示顶部的指示器（1/9）默认显示
        .setShowIndicator(true) // 设置失败时的占位图，默认为库中自带R.drawable.load_failed，设置为 0 时不显示
        .setErrorPlaceHolder(cc.shinichi.library.R.drawable.load_failed) // 点击回调
        .start()
}

/**
 * @param phoneNum 手机号码
 * @param isDirect 是否直接拨打电话
 */
 fun Context.callPhone(phoneNum: String, isDirect: Boolean = true) {
    if (phoneNum.isPhoneNum()) {
        var intent: Intent? = null
        intent = if (isDirect) {
            Intent(Intent.ACTION_CALL)
        } else {
            Intent(Intent.ACTION_DIAL)
        }
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } else {
        showToast(this, "手机号码不正确!")
    }
}
//判断是否为汉字
fun String.isHan(): Boolean {
    val pattern = Pattern.compile("^[\u4e00-\u9fa5]{0,}$")
    return pattern.matcher(this).matches()
}

//判断是否为正确的手机号
fun String.isPhoneNum(): Boolean {
    val pattern =
        Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}\$")
    return pattern.matcher(this).matches()
}
//空数据返回
fun String?.pk(def: String = ""): String {
    return if (this.isNullOrEmpty()) {
        def
    } else {
        this
    }
}




