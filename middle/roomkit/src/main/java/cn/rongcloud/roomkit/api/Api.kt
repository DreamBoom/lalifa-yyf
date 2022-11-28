package cn.rongcloud.roomkit.api

import com.drake.net.Get
import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope

/**
 * 充值列表
 * @receiver CoroutineScope
 * @return RechargeBean?
 */
suspend fun CoroutineScope.recharge(): RechargeBean? {
    return Get<BaseBean<RechargeBean>>("user/recharge") {
    }.await().data
}

/**
 * 充值钻石
 * @receiver CoroutineScope
 * @return String?
 */
suspend fun CoroutineScope.pay(id:String,price:String,payType:String): String? {
    return Post<BaseBean<String>>("user/recharge") {
        param("id", id)
        param("price", price)
        param("payType", payType)
    }.await().data
}

/**
 * 聊天室首页
 * @receiver CoroutineScope
 * @return RoomIndexBean?
 */
suspend fun CoroutineScope.roomIndex(): RoomIndexBean? {
    return Post<BaseBean<RoomIndexBean>>("chat_room/index") {
    }.await().data
}

/**
 * 聊天室列表
 * @receiver CoroutineScope
 * @param id 分类id
 * @param offset 页码
 * @return RoomListBean?
 */
suspend fun CoroutineScope.roomList(id:String,offset:String): RoomListBean? {
    return Post<BaseBean<RoomListBean>>("chat_room/ChatRoom") {
        param("id", id)
        param("offset", offset)
    }.await().data
}

/**
 * 排行榜
 * @receiver CoroutineScope
 * @param type 1：财富榜  2：魅力榜
 * @param category 1：日榜  2：周榜 3月榜
 * @return RoomListBean?
 */
suspend fun CoroutineScope.ranking(type:String,category:String,): List<RankBean>? {
    return Post<BaseBean<List<RankBean>>>("user/ranking") {
        param("category", category)
        param("type", type)
    }.await().data
}
