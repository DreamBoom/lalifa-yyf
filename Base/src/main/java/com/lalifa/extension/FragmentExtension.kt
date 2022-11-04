@file:Suppress("unused")

package com.lalifa.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * Fragment extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */
fun Fragment.start(cls: Class<out Activity>) {
    startActivity(Intent(requireActivity(), cls))
}

fun Fragment.start(cls: Class<out Activity>, intent: Intent.() -> Unit) {
    val intent1 = Intent(requireActivity(), cls)
    intent(intent1)
    startActivity(intent1)
}


/** 添加参数 */
fun Fragment.addStringArgument(key: String, value: String): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putString(key, value)
    this.arguments = bundle
    return this
}

fun Fragment.addIntArgument(key: String, value: Int): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putInt(key, value)
    this.arguments = bundle
    return this
}

fun Fragment.addLongArgument(key: String, value: Long): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putLong(key, value)
    this.arguments = bundle
    return this
}

fun Fragment.addDoubleArgument(key: String, value: Double): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putDouble(key, value)
    this.arguments = bundle
    return this
}

fun Fragment.addBooleanArgument(key: String, value: Boolean): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putBoolean(key, value)
    this.arguments = bundle
    return this
}

fun Fragment.addSerializableArgument(key: String, value: Serializable): Fragment {
    val bundle = if (this.arguments != null) this.arguments else Bundle()
    bundle?.putSerializable(key, value)
    this.arguments = bundle
    return this
}

/** 获取参数 */
fun Fragment.getStringArgument(key: String): String {
    return this.arguments?.getString(key) ?: ""
}

fun Fragment.getIntArgument(key: String, defaultValue: Int = 0): Int {
    return this.arguments?.getInt(key, defaultValue) ?: defaultValue
}

fun Fragment.getLongArgument(key: String, defaultValue: Long = 0): Long {
    return this.arguments?.getLong(key, defaultValue) ?: defaultValue
}

fun Fragment.getDoubleArgument(key: String, defaultValue: Double = 0.0): Double {
    return this.arguments?.getDouble(key, defaultValue) ?: defaultValue
}

fun Fragment.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    return this.arguments?.getBoolean(key, defaultValue) ?: defaultValue
}

fun <T> Fragment.getSerializableArgument(key: String): T? {
    return this.arguments?.getSerializable(key)?.let { it as T }
}

val Fragment.sharedPreferences: SharedPreferences
    get() = requireActivity().getSharedPreferences(
        "config",
        Context.MODE_PRIVATE
    )
val Fragment.isLogin: Boolean get() = sharedPreferences["isLogin", false]!!


