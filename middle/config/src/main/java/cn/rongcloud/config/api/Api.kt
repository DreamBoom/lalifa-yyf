package cn.rongcloud.config.api

import cn.rongcloud.config.provider.user.User
import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.lang.reflect.Member

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
 * 商品详情
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.giftList(): List<GiftBean>? {
    return Post<BaseBean<List<GiftBean>>>("user/gift") {
    }.await().data
}

/**
 * 创建队伍
 * @receiver CoroutineScope
 * @param id 当前所在房间ID
 * @param type 类型 1-游戏 2-娱乐
 * @param label 标签
 * @param rank 段位
 * @param explain 说明
 * @param background 背景图
 * @param pattern 模式
 * @return RoomListBean?
 */
suspend fun CoroutineScope.createRoom(
    id: String, type: String, label: String, rank: String, explain: String,
    background: String, pattern: String,
): RoomDetailBean? {
    return Post<BaseBean<RoomDetailBean>>("chat_room/add_fleet") {
        param("id", id)
        param("type", type)
        param("label", label)
        param("rank", rank)
        param("explain", explain)
        param("background", background)
        param("pattern", pattern)
    }.await().data
}

/**
 * 聊天室详情
 * @receiver CoroutineScope
 * @return RoomListBean?
 */
suspend fun CoroutineScope.roomDetail(id: String): RoomDetailBean? {
    return Post<BaseBean<RoomDetailBean>>("chat_room/room_details") {
        param("id", id)
    }.await().data
}

/**
 * 获取房间内礼物列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.roomGift(): RoomGiftBean? {
    return Post<BaseBean<RoomGiftBean>>("chat_room/gift") {
    }.await().data
}

/**
 * 检查当前用户所属房间
 * @return
 */
suspend fun CoroutineScope.roomCheck(): RoomCheckBean? {
    return Post<BaseBean<RoomCheckBean>>("chat_room/check") {
    }.await().data
}

/**
 * 关注/取消关注好友
 * @return
 */
suspend fun CoroutineScope.follow(id: String): String? {
    return Post<BaseBean<String>>("user/follow") {
        param("id", id)
    }.await().data
}

/**
 * 收藏取消房间
 * @return
 */
suspend fun CoroutineScope.collection(id: String): String? {
    return Post<BaseBean<String>>("chat_room/collection") {
        param("office_id", id)
    }.await().data
}

/**
 * 收藏取消房间
 * @return
 */
suspend fun CoroutineScope.editRoom(
    id: String, title: String, image: String,
    background: String, passwordType: String,
    password: String, notice: String
): String? {
    return Post<BaseBean<String>>("chat_room/edit_room") {
        param("id", id)
        param("title", title)
        param("image", image)
        param("background", background)
        param("password_type", passwordType)
        param("password", password)
        param("notice", notice)
    }.await().data
}

