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
 * ???????????????
 */
fun Activity.showKeyboard(view: View?) {
    view?.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

/**
 * ???????????????
 * @receiver Activity
 * @param view View?
 */
fun Activity.hideKeyboard(view: View?) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
    // imm.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * ?????????
 * @receiver Activity
 * @return Int
 */
fun Activity.screenWidth(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

/**
 * ?????????
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
 * ???????????????
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
 * ??????????????????
 * @receiver Activity
 * @param code Int
 */
fun Activity.startMusic(code: Int) {
    startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).also {
        it.type = "audio/mpeg"//????????????
        it.addCategory(Intent.CATEGORY_OPENABLE)
    }, code)
}


/**
 * ????????????
 * @receiver FragmentActivity
 * @param callback Function1<String, Unit>
 */
fun FragmentActivity.sexPickView(callback: (String) -> Unit) {
        val sexList = arrayListOf("???", "???")
        val optionView = OptionsPickerBuilder(
            this@sexPickView
        ) { options1, options2, options3, v ->
            callback.invoke(
                sexList[options1]
            )
        }.setLineSpacingMultiplier(2.0f)//????????????
            .setSubmitColor(Color.WHITE)//????????????????????????
            .setCancelColor(Color.WHITE)//????????????????????????
            .setTitleSize(17)//??????????????????
            .setSubCalSize(15)//???????????????????????????
            .setContentTextSize(17)//????????????
            .setTitleText("????????????")
            .setTitleColor(Color.WHITE)
            .setTitleBgColor(Color.parseColor("#1A1A1A"))
            .setDividerColor(Color.parseColor("#1AFFFFFF"))
            .setTextColorCenter(Color.WHITE) //???????????????????????????
            .setTextColorOut(Color.parseColor("#FFFFFF"))
            .setBgColor(Color.parseColor("#1A1A1A"))
            .build<Any>()
        optionView.setPicker(
            sexList.toList()
        )
        optionView.show()
}
/**
 * ????????????
 * @receiver FragmentActivity
 * @param callback Function1<String, Unit>
 */
fun Activity.sexPickView(callback: (String) -> Unit) {
    val sexList = arrayListOf("???", "???")
    val optionView = OptionsPickerBuilder(
        this@sexPickView
    ) { options1, options2, options3, v ->
        callback.invoke(
            sexList[options1]
        )
    }.setLineSpacingMultiplier(2.0f)//????????????
        .setSubmitColor(Color.WHITE)//????????????????????????
        .setCancelColor(Color.WHITE)//????????????????????????
        .setTitleSize(17)//??????????????????
        .setSubCalSize(15)//???????????????????????????
        .setContentTextSize(17)//????????????
        .setTitleText("????????????")
        .setTitleColor(Color.WHITE)
        .setTitleBgColor(Color.parseColor("#1A1A1A"))
        .setDividerColor(Color.parseColor("#1AFFFFFF"))
        .setTextColorCenter(Color.WHITE) //???????????????????????????
        .setTextColorOut(Color.parseColor("#FFFFFF"))
        .setBgColor(Color.parseColor("#1A1A1A"))
        .build<Any>()
    optionView.setPicker(
        sexList.toList()
    )
    optionView.show()
}
/**
 * ???????????????
 * @receiver Activity
 * @param callback Function1<String, Unit>
 */
fun Activity.timePickView(callback: (String) -> Unit) {
    //???????????????
    val selectedDate = Calendar.getInstance()
    val startDate = Calendar.getInstance()
    //startDate.set(2013,1,1)
    val endDate = Calendar.getInstance()
    //endDate.set(2020,1,1)
    //?????????????????? ??????????????????????????????
    startDate.set(1922, 1, 1)
    val calendar = Calendar.getInstance() //?????????????????????????????? ?????????
    val year = calendar[Calendar.YEAR]
    endDate.set(year, 12, 31)

    val pvTime = TimePickerBuilder(
        this
    ) { date, v ->
        callback.invoke(
            getTime(date)
        )
    }
        .setType(booleanArrayOf(true, true, true, false, false, false))// ??????????????????
        .setCancelText("??????")//??????????????????
        .setSubmitText("??????")//??????????????????
        .setContentTextSize(14)//??????????????????
        .setTitleSize(14)//??????????????????
        .setTitleText("????????????")//????????????
        .setOutSideCancelable(true)//???????????????????????????????????????????????????????????????
        .isCyclic(true)//??????????????????
        .setTitleColor(Color.WHITE)//??????????????????
        .setSubmitColor(Color.WHITE)//????????????????????????
        .setCancelColor(Color.WHITE)//????????????????????????
        .setTitleBgColor(Color.parseColor("#402544"))//?????????????????? Night mode
        .setBgColor(Color.parseColor("#402544"))//?????????????????? Night mode
        .setTextColorCenter(Color.WHITE)
        .setDate(selectedDate)// ?????????????????????????????????????????????*/
        .setRangDate(startDate, endDate)//???????????????????????????
        .setLabel("???", "???", "???", "???", "???", "???")//?????????????????????????????????
        .isCenterLabel(false) //?????????????????????????????????label?????????false?????????item???????????????label???
        .isDialog(true)//??????????????????????????????
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
 * ??????asset???????????????????????????
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
 * ????????????
 * @receiver Activity
 * @param permissions [@kotlin.ExtensionFunctionType] Function1<ArrayList<String>, Unit> ????????????
 * @param onGranted Function2<MutableList<String>, Boolean, Unit> ????????????
 * @param onDenied Function2<MutableList<String>, Boolean, Unit> ????????????
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
                toast("???????????????????????????????????????????????????")
                // ??????????????????????????????????????????????????????????????????
                XXPermissions.startPermissionActivity(
                    this@requestPerm,
                    permissions
                )
            } else {
                toast("??????????????????")
            }
            onDenied.invoke(permissions, never)
        }
    })
}

/**
 * ????????????
 * @receiver ImageView
 * @param position Int
 * @param urlList List<String>
 */
fun Context.preview(position: Int = 0, urlList: List<String>) {
    ImagePreview.instance // ?????????????????????activity?????????????????????????????????????????????????????????
        .setContext(this) // ????????????????????????????????????0?????????~
        .setIndex(position) //=================================================================================================
        // ??????????????????????????????????????????????????????????????????????????????
        // 1?????????????????????imageInfo List
        //.setImageInfoList(imageInfoList)
        // 2????????????url List
        .setImageList(urlList.toMutableList()) // 3??????????????????????????????????????????????????????????????????url
        //.setImage(String image)
        //=================================================================================================
        // ???????????????????????????????????????????????????????????????????????????????????????
        .setLoadStrategy(ImagePreview.LoadStrategy.Default) // ?????????????????????????????????SD???????????????????????????????????????
        // (???????????????????????????????????????"BigImageView/Download"?????????SD??????????????????BigImageView??????????????????BigImageView??????????????????Download?????????)
        .setFolderName("BigImageView/Download") // ???????????????????????????ms
        .setZoomTransitionDuration(300) // ?????????????????????????????????????????????
        .setEnableClickClose(true) // ??????????????????/??????????????????????????????
        .setEnableDragClose(true) // ?????????????????????????????????????????????????????????????????????
        .setShowCloseButton(false) // ?????????????????????????????????????????????????????????????????????R.drawable.ic_action_close
        .setCloseIconResId(cc.shinichi.library.R.drawable.ic_action_close) // ????????????????????????????????????????????????????????????
        .setShowDownButton(false) // ?????????????????????????????????????????????????????????????????????R.drawable.icon_download_new
        .setDownIconResId(cc.shinichi.library.R.drawable.icon_download_new) // ???????????????????????????????????????1/9???????????????
        .setShowIndicator(true) // ???????????????????????????????????????????????????R.drawable.load_failed???????????? 0 ????????????
        .setErrorPlaceHolder(cc.shinichi.library.R.drawable.load_failed) // ????????????
        .start()
}

/**
 * @param phoneNum ????????????
 */
 fun Context.callPhone(phoneNum: String) {
    if (phoneNum.isPhoneNum()) {
        val  intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    } else {
        showToast(this, "?????????????????????!")
    }
}
//?????????????????????
fun String.isHan(): Boolean {
    val pattern = Pattern.compile("^[\u4e00-\u9fa5]{0,}$")
    return pattern.matcher(this).matches()
}

//?????????????????????????????????
fun String.isPhoneNum(): Boolean {
    val pattern =
        Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}\$")
    return pattern.matcher(this).matches()
}
//???????????????
fun String?.pk(def: String = ""): String {
    return if (this.isNullOrEmpty()) {
        def
    } else {
        this
    }
}




