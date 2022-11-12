package com.example.message.api
import java.io.Serializable

/**
 *
 * @ClassName JsonBean
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/5/21 11:14
 * @Des
 */

open class BaseBean<T> : Serializable {
    var code: Int = 0
    var msg: String = ""
    var data: T? = null
    var time: Long = 0
}

data class FriendBean(
    val avatar: String,
    val gender: Int,
    val id: Int,
    val userName: String
)
