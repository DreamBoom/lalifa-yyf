package cn.rongcloud.roomkit.api

import cn.rongcloud.config.provider.user.User
import cn.rongcloud.roomkit.ui.room.model.Member
import com.drake.net.Get
import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * 文件上传
 * @receiver CoroutineScope
 * @param path String
 * @return String
 */
suspend fun CoroutineScope.uploadIm(path: String):ImgBean  {
    return Post<BaseBean<ImgBean>>("common/upload") {
        param("file", File(path))
    }.await().data!!
}
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
suspend fun CoroutineScope.ZfbPay(id:String,price:String,payType:String): String? {
    return Post<BaseBean<String>>("user/recharge") {
        param("id", id)
        param("price", price)
        param("payType", payType)
    }.await().data
}

suspend fun CoroutineScope.WxPay(id:String,price:String,payType:String): WxPayBean? {
    return Post<BaseBean<WxPayBean>>("user/recharge") {
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

/**
 * 用户信息
 * @receiver CoroutineScope
 * @param id 用户ID
 * @param office_id 房间ID
 * @return RoomListBean?
 */
suspend fun CoroutineScope.userInfo(id:String,office_id:String,): Member? {
    return Post<BaseBean<Member>>("chat_room/usre_information") {
        param("id", id)
        param("office_id", office_id)
    }.await().data
}

/**
 * 获取房间内管理员列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.getManage(id:String): MutableList<User>? {
    return Post<BaseBean<MutableList<User>>>("chat_room/manage") {
        param("office_id", id)
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

