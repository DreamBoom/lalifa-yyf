package cn.rongcloud.config.api

import cn.rongcloud.config.provider.user.LoginUserBean
import com.drake.logcat.LogCat
import com.drake.net.Post
import com.lalifa.extension.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import java.io.File
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