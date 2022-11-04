@file:Suppress("unused")

package com.lalifa.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T : Any> Gson.fromJson(json: String): T = fromJson(json, object : TypeToken<T>(){}.type)

val GSON by lazy { Gson() }

fun Any.toJson(): String = GSON.toJson(this)

