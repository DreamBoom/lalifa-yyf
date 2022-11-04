package com.lalifa.che.api

import com.drake.logcat.LogCat
import com.drake.net.Post
import com.lalifa.extension.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import java.io.File

/**
 * 发现页列表
 * @receiver CoroutineScope
 * @return CheListBean?
 */
suspend fun CoroutineScope.cheList(type: Int, offset: Int): CheListBean? {
    return Post<BaseBean<CheListBean>>("user/community") {
        param("type", type)
        param("offset", offset)
    }.await().data
}

/**
 * 发现页详情
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.cheInfo(id: Int): CheInfoBean? {
    return Post<BaseBean<CheInfoBean>>("user/DynamicDetails") {
        param("id", id)
    }.await().data
}

/**
 * 文件上传
 * @receiver CoroutineScope
 * @param path String
 * @return String
 */
suspend fun CoroutineScope.upload(path: String): ImgBean {
    return Post<BaseBean<ImgBean>>("common/upload") {
        param("file", File(path))
    }.await().data!!
}

/**
 * 发布动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.upChe(content: String, image: List<String>): CheInfoBean? {
    LogCat.e("==111===" + image.string())
    return Post<BaseBean<CheInfoBean>>("user/dynamic") {
        param("content", content)
        param("image", image.string())
    }.await().data
}

/**
 * 点赞动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.dzChe(id: Int): CheInfoBean? {
    return Post<BaseBean<CheInfoBean>>("user/fabulous") {
        param("id", id)
    }.await().data
}

/**
 * 评论动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.plChe(id: String, note: String, pid: String): Any? {
    return Post<BaseBean<Any>>("user/comment") {
        param("id", id)
        param("note", note)
        param("pid", pid)
    }.await().data
}

/**
 * 点赞评论
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.dzPl(id: String): Any? {
    return Post<BaseBean<Any>>("user/FabulousComment") {
        param("id", id)
    }.await().data
}



