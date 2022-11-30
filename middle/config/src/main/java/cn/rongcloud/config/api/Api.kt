package cn.rongcloud.config.api

import cn.rongcloud.config.provider.user.User
import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope

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
 * 聊天室详情
 * @receiver CoroutineScope
 * @return RoomListBean?
 */
suspend fun CoroutineScope.roomDetail(id:String): RoomDetailBean? {
    return Post<BaseBean<RoomDetailBean>>("chat_room/room_details") {
        param("id", id)
    }.await().data
}

/**
 * 获取房间内成员列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.getMembers(id:String): MutableList<User>? {
    return Post<BaseBean<MutableList<User>>>("chat_room/members") {
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
suspend fun CoroutineScope.follow(id:String): RoomCheckBean? {
    return Post<BaseBean<RoomCheckBean>>("user/follow") {
        param("id", id)
    }.await().data
}