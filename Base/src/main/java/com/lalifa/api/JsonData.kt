package com.lalifa.api

import java.io.Serializable


open class BaseBean<T> : Serializable {
    var code: Int = 0
    var msg: String = ""
    var data: T? = null
    var time: Long = 0
}
data class GiftBean(
    val image: String,
    val name: String,
    val price: String
)