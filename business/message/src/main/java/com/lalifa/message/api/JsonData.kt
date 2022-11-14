package com.lalifa.message.api
import java.io.Serializable

/**
 *
 * @ClassName JsonBean
 * @Des
 */

open class BaseBean<T> : Serializable {
    var code: Int = 0
    var msg: String = ""
    var data: T? = null
    var time: Long = 0
}
/**
 * @Des 好友
 */
data class FriendBean(
    val avatar: String,
    val gender: Int,
    val id: Int,
    val userName: String
)

/**
 * @Des 新人
 */
data class NewFriendBean(
    val avatar: String,
    val gender: Int,
    val id: Int,
    val userName: String
)
/**
 * @Des 申请列表
 */
data class ApplyBean(
    val avatar: String,
    val create_time: String,
    val gender: Int,
    val id: Int,
    val postscript: String,
    val uid: Int,
    val userName: String
)
