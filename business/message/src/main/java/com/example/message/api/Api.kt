package com.example.message.api

import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * 文件上传
 * @receiver CoroutineScope
 * @param id String 搜索ID
 * @param name String 搜索名称
 * @return ArrayList<FriendBean>?
 */
suspend fun CoroutineScope.friendsList(id: String = "",name: String = ""):ArrayList<FriendBean>? {
    return Post<BaseBean<ArrayList<FriendBean>>>("user/friends_list") {
        param("id", id)
        param("name", name)
    }.await().data!!
}