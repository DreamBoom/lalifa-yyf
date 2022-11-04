package com.lalifa.che.api
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

data class ImgBean(
    val fullurl: String,
    val url: String
)

/**
 * @Des 发现页列表
 */
data class CheListBean(
    val count: Int,
    val `dynamic`: List<Dynamic>
)

data class Dynamic(
    val avatar: String,
    val browse: String,
    val comment_count: Int,
    val content: String,
    val create_time: String,
    val fabulous: Int,
    val fabulous_type: Int,
    val gender: Int,
    val id: Int,
    val image: List<String>,
    val level: Int,
    val num: Int,
    val share: Int,
    val status: Int,
    val uid: Int,
    val userName: String
)
/**
 *
 * @ClassName 发现页详情
 * @Des
 */
data class CheInfoBean(
    val avatar: String,
    val browse: Int,
    val comment: List<Comment>,
    val comment_count: Int,
    val content: String,
    val create_time: String,
    val fabulous: Int,
    val fabulous_type: Int,
    val id: Int,
    val image: List<String>,
    val share: Int,
    val status: Int,
    val uid: Int,
    val userName: String
)

data class Comment(
    val avatar: String,
    val child: List<ChildInfo>,
    val content: String,
    val create_time: String,
    val fabulous: Int,
    val id: Int,
    val uid: Int,
    val userName: String
)

data class ChildInfo(
    val avatar: String,
    val content: String,
    val create_time: String,
    val fabulous: Int,
    val id: Int,
    val path: String,
    val pid: Int,
    val pid_name: String,
    val uid: Int,
    val userName: String
)