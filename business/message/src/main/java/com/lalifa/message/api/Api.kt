package com.lalifa.message.api

import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * 好友列表
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

/**
 * 新人列表
 * @receiver CoroutineScope
 * @return ArrayList<NewFriendBean>?
 */
suspend fun CoroutineScope.newFriendsList():ArrayList<NewFriendBean>? {
    return Post<BaseBean<ArrayList<NewFriendBean>?>>("user/new_people") {
    }.await().data!!
}

/**
 * 申请列表
 * @receiver CoroutineScope
 * @return ArrayList<NewFriendBean>?
 */
suspend fun CoroutineScope.applyList():ArrayList<ApplyBean>? {
    return Post<BaseBean<ArrayList<ApplyBean>?>>("user/examine_friends_list") {
    }.await().data!!
}

/**
 * 审核好友添加
 * @receiver CoroutineScope
 * @param id String 审核ID
 * @param type String 状态 1：通过  2：拒绝
 * @return Any
 */
suspend fun CoroutineScope.addFriend(id: String,type: String):Any{
    return Post<BaseBean<Any>>("user/examine_friends") {
        param("id", id)
        param("type", type)
    }.await().data!!
}

