package com.lalifa.main.api

import com.lalifa.main.activity.room.ext.Member
import com.lalifa.main.activity.room.ext.User
import com.lalifa.main.activity.room.ext.UserManager
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

//用户信息
data class UserInfo(
    val id: Long,//用户ID
    var username: String,//用户名
    var nickname: String,//昵称
    var mobile: String,//手机号
    var avatar: String,//头像
    val score: Int,//账户余额
    val imToken: String,//融云TOKEN
    val imId: String,
    val token: String,
    val user_id: String,
    val createtime: Long,
    val expiretime: Long,
    var expires_in: Long,
) : Serializable

/**
 * @Des 首页
 */
data class IndexBean(
    val captain: List<Captain>,//热门队长
    val carousel: List<Carousel>,
    val host: List<Host>, //热门主持
    val notice: Notice,
    val room: List<Room>//热门房间
) : Serializable

data class Carousel(
    val image: String
) : Serializable

data class Captain(
    val avatar: String,
    val end_time: String,
    val id: Int,
    val isPrivate: Int,
    val password: String,
    val password_type: Int,
    val roomid: String,
    val start_time: String,
    val uid: Int,
    val userId: String,
    val userName: String,
    val user_id: Int
)

data class Notice(
    val id: Int,
    val n_message_content: String,
    val n_title: String
) : Serializable

data class Room(
    val avatar: String,
    val background: String,
    val create_time: String,
    val end_time: String,
    val guild_id: Int,
    val hot: Int,
    val id: Int,
    val image: String,
    val loginfailure: Int,
    val loginip: String,
    val logintime: Int,
    val nickname: String,
    val notice: String,
    val office_id: Int,
    val password: String,
    val password_type: Int,
    val phone: String,
    val ranking: Int,
    val roomid: String,
    val start_time: String,
    val state: Int,
    val status: Int,
    val subheading: String,
    val title: String,
    val token: String,
    val type_id: Int,
    val uid: Int,
    val updatetime: String
)

data class Host(
    val avatar: String,
    val end_time: String,
    val id: Int,
    val isPrivate: Int,
    val password: String,
    val password_type: Int,
    val roomid: String,
    val start_time: String,
    val uid: Int,
    val userId: String,
    val userName: String,
    val user_id: Int
)

/**
 * @Des 商城
 */
data class shopBean(
    val id: Int,
    val image: String,
    val name: String,
    val status: Int,
    val wares: List<Ware>
)

data class Ware(
    val id: Int,
    val image: String,
    val name: String,
    val price: String,
    val days: Int
)

/**
 * @Des 商品详情
 */
data class GoodInfoBean(
    val effect_image: String,
    val id: Int,
    val image: String,
    val name: String,
    val specs: List<Spec>
)

data class Spec(
    val days: Int,
    val id: Int,
    val price: String
)

/**
 * @Des 背包列标
 */
data class KnapsackBean(
    val classify: ArrayList<Classify>
)

data class Classify(
    val id: Int,
    val knapsack: ArrayList<KnapsackInfo>,
    val name: String,
    val image: String
)

data class KnapsackInfo(
    val id: Int,
    var type: Int,
    val name: String,
    val image: String
)

/**
 * @Des 个人中心
 */
data class UserInfoBean(
    val age: Int,
    val avatar: String,
    val bio: String,
    val birthday: String,
    val bubble: String,
    val car: String,
    val core_currency: String,
    val core_drill: String,
    val fans: Int,
    val follow: Int,
    val frame: String,
    val friends: Int,
    val gender: Int,
    val id: Int,
    val jointime_text: String,
    val level: String,
    val logintime_text: String,
    val patron_saint: Int,
    val prevtime_text: String,
    val sound: String,
    val userName: String
){
     fun toUser(){
        UserManager.get()!!.userName = userName
        UserManager.get()!!.avatar = avatar
        UserManager.get()!!.frame = frame
        UserManager.get()!!.car = car
        UserManager.get()!!.bubble = bubble
        UserManager.get()!!.sound = sound
        UserManager.get()!!.level = level
        UserManager.get()!!.score = core_drill.toDouble()
        UserManager.get()!!.sound = sound
        UserManager.get()!!.sound = sound
    }
        fun toMember(): Member {
            return Member(
                UserManager.get()!!.userId,
                userName,
                avatar,
                level,
                frame,
                car,
                bubble,
                sound,
                gender,
                0,
                0,
                0,
                -1
            )
        }
}

/**
 * @Des 钱包列表
 */
data class MoneyListBean(
    val core_currency: String,
    val core_drill: String,
    val count: Int,
    val exchange: ArrayList<Exchange>,
    val record: ArrayList<MoneyRecord>,
    val subscription_ratio: String
)

data class Exchange(
    val drill: Int,
    val id: Int,
    val price: String
)

data class MoneyRecord(
    val balance: String,
    val consume_id: Int,
    val create_time: String,
    val id: Int,
    val note: String,
    val pid: Int,
    val price: String,
    val sort: Int,
    val type: Int,
    val uid: Int
)

/**
 * @Des 充值列表
 */
data class RechargeBean(
    val alipay_status: String,
    val recharge_proportion: String,
    val rule: List<Rule>,
    val wechat_status: String
)

data class Rule(
    val id: Int,
    val price: String,
    val receipt_price: String
)

/**
 * @Des 贵族权益
 */
data class TitleBean(
    val background_image: String,
    val consumption: String,
    val headdress: List<Headdres>,
    val id: Int,
    val image: String,
    val mount: List<Mount>,
    val name: String,
    val special_effects: List<SpecialEffect>,
    val status: Int,
    val surplus: Int
)

data class Headdres(
    val effect_image: String,
    val image: String,
    val name: String
)

data class Mount(
    val effect_image: String,
    val image: String,
    val name: String
)

data class SpecialEffect(
    val effect_image: String,
    val image: String,
    val name: String
)

/**
 * @Des 活动列表
 */
data class ActivityBean(
    val end_time: String,
    val id: Int,
    val name: String,
    val start_time: String,
    val state: Int
)

/**
 * @Des 活动详情
 */
data class ActivityInfoBean(
    val activity_list: List<Activity>,
    val end_time: String,
    val id: Int,
    val name: String,
    val price: Int,
    val rule: String,
    val start_time: String
)

data class Activity(
    val activity_details: List<ActivityDetail>,
    val id: Int,
    val price: String
)

data class ActivityDetail(
    val activity_id: Int,
    val days: Int,
    val goods: Goods,
    val goods_id: Int,
    val id: Int,
    val type: Int
)

data class Goods(
    val image: String,
    val name: String
)

/**
 * @Des 好友
 */
data class FriendBean(
    val avatar: String,
    val follow: String,
    val follow_type: Int,
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

data class ImgBean(
    val fullurl: String,
    val url: String
)

/**
 * @Des 发现页列表
 */
data class CheListBean(
    val count: Int,
    val dynamic: List<Dynamic>
)

data class Dynamic(
    val avatar: String? = "",
    val browse: String = "",
    val comment_count: Int = 0,
    val content: String = "",
    val create_time: String = "",
    val fabulous: Int = 0,
    val fabulous_type: Int = 0,
    val gender: Int? = 0,
    val id: Int,
    val image: List<String>,
    val level: String = "",
    val num: Int = 0,
    val share: Int = 0,
    val status: Int = 0,
    val uid: Int = 0,
    val userName: String? = ""
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

/**
 *
 * @ClassName 礼物墙
 * @Des
 */
data class GiftListBean(
    val count: Int,
    val gift_count: Int,
    val theme: List<Theme>
)

data class Theme(
    val gift: List<Gift>,
    val id: Int,
    val name: String
)

data class Gift(
    val count: Int,
    val id: Int,
    val image: String,
    val name: String
)

/**
 *
 * @ClassName 礼物记录
 * @Des
 */
data class GiftHistoryBean(
    val create_time: String,
    val id: Int,
    var image: String,
    var avatar: String,
    val name: String,
    val pid: Int,
    val uid: Int,
    val userName: String
){
    override fun toString(): String {
        return "GiftHistoryBean(create_time='$create_time', id=$id, image='$image', avatar='$avatar', name='$name', pid=$pid, uid=$uid, userName='$userName')"
    }
}

/**
 *
 * @ClassName 守护
 * @Des
 */
data class GuardBean(
    val avatar: String,
    val create_time: String,
    val gender: Int,
    val member_id: Int,
    val userName: String,
    val user_id: Int,
    val yield: String
)

/**
 *
 * @ClassName 客服
 * @Des
 */
data class CallBean(
    val avatar: String,
    val follow: String,
    val gender: Int,
    val id: Int,
    val userId: String,
    val userName: String
)

/**
 *
 * @ClassName 我的主页
 * @Des
 */
data class MeInfoBean(
    val age: Int,
    val avatar: String,
    val background: String,
    val bio: String,
    val birthday: String,
    val constellation: String,
    val count: Int,
    val `dynamic`: List<Dynamic>,
    val fans: Int,
    val follow: Int,
    val frame: String,
    val gender: Int,
    val gift_count: Int,
    val id: Int,
    val level: Int,
    val medal: List<MeXz>,
    val medal_count: Int,
    val member_id: Int,
    val patron_saint: List<GuardBean>,
    val room_record: List<MeRoom>,
    val theme: List<Theme>,
    val userId: String,
    val userName: String
)

data class MeRoom(
    val title: String,
    val image: String
)

data class MeXz(
    val image: String,
    val name: String
)

data class GiftBean(
    val image: String,
    val name: String,
    val price: String
)

/**
 * @Des 房间详情
 */
data class RoomDetailBean(
    /**
     * 1:可加入零号麦 0不可加入零号麦
     */
    var wheat_type: Int,
    /**
     * 公告
     */
    var notice: String = "",
    /**
     * 1 可以创建队伍 else 不可以
     */
    var establish_type: Int,
    /**
     * 房主信息
     */
    var userInfo: User?,
    /**
     * 房间背景
     */
    var background: String,
    /**
     * 房间背景id
     */
    var background_id: String,

    /**
     * 融云房间id
     */
    var Chatroom_id: String,
    /**
     * 广告位信息
     */
    var advertisement: List<Advertisement>,
    /**
     * 1：收藏  0：未收藏
     */
    var collection_type: Int,
    /**
     * 结束时间
     */
    var end_time: String,
    /**
     * 车队房间列表
     */
    var fleet: List<Fleet>,

    /**
     * 礼盒礼包开启次数
     */
    var gift_box_frequency: List<GiftBoxFrequency>,
    /**
     * 赠送礼物次数
     */
    var gift_frequency: List<GiftFrequency>,
    /**
     * id
     */
    var id: Int,
    /**
     * 房间头像
     */
    var image: String,

    /**
     * 1：管理员  0：不是
     */
    var manage_type: Int,
    /**
     * 1：厅管  0：不是
     */
    var office_type: Int,
    /**
     * 密码
     */
    var password: String,
    /**
     * 0：未加密  1：加密
     */
    var password_type: Int,
    /**
     * 开始时间
     */
    var start_time: String,
    /**
     * 副标题
     */
    var subheading: String,
    /**
     * 房间名称
     */
    var title: String,
    /**
     * 房主ID
     */
    var uid: Int
) : Serializable

data class Fleet(
    val avatar: String,
    val background: String,
    val explain: String,
    val gender: Int,
    val id: Int,
    val pattern: String,
    val rank: String,
    val type: Int,
    val uid: Int,
    val userName: String,
    var userId:String,
    val roomid:String
)

data class Advertisement(
    var details: String,
    var id: Int,
    var image: String
) : Serializable

data class BlindBox(
    var id: Int,
    var image: String,
    var name: String,
    var price: String
) : Serializable

data class Gift1(
    var image: String,
    var name: String,
    var price: String
) : Serializable

data class GiftBag(
    var id: Int,
    var image: String,
    var name: String,
    var price: String
) : Serializable

data class GiftBoxFrequency(
    var frequency: String,
    var id: Int,
    var name: String
) : Serializable

data class GiftFrequency(
    var frequency: String,
    var id: Int,
    var name: String
) : Serializable

/**
 * @Des 房间详情
 */
data class RoomGiftBean(
    val blind_box: List<RoomGift>,//盲盒
    val gift: List<RoomGift>,//礼物
    val gift_bag: List<RoomGift>, //礼包
    val knapsack: List<RoomGift>, //背包
    val gift_box_frequency: List<RoomGiftBoxFrequency>,
    val gift_frequency: List<RoomGiftFrequency>,
    val total_price: Int
)

data class RoomGift(
    val id: Int,
    val image: String,
    val name: String,
    val price: String,
    val effect_image: String,
    var choose:Boolean = false

) {
    override fun toString(): String {
        return "RoomGift(id=$id, image='$image', name='$name', price='$price', effect_image='$effect_image', choose=$choose)"
    }
}

data class RoomGiftBoxFrequency(
    val frequency: String,
    val id: Int,
    val name: String
)

data class RoomGiftFrequency(
    val frequency: String,
    val id: Int,
    val name: String
)

data class RoomCheckBean(
    val RoomId: String,
    val userId: String
)

/**
 * @Des 聊天室首页
 */
data class RoomIndexBean(
    var carousel: List<Carousel1>,
    var classify: List<Classify1>,
    var notice: Notice1
)

data class Carousel1(
    var create_time: String,
    var id: Int,
    var image: String,
    var sort: Int
)

data class Classify1(
    var id: Int,
    var name: String
)

data class Notice1(
    var create_time: String,
    var id: Int,
    var image: String,
    var n_message_content: String,
    var n_title: String,
    var status: Int
)

/**
 * @Des 聊天室列表
 */
data class RoomListBean(
    var count: Int,
    var office: List<Office>
)

data class Office(
    var uid: Int,
    var id: Int,
    var image: String,
    var notice: String,
    var password_type: Int, //0开放 1加密
    var roomid: String,
    var userId: String,
    var title: String,
    var type_id: Int,
    var type_name: String,
    var users: List<User1>
)
data class User1(
    val id: String,
    val time: String
)
/**
 * @Des 排行榜
 */
data class RankBean(
    var avatar: String,
    var create_time: String,
    var gender: Int,
    var member_id: Int,
    var userName: String,
    var user_id: Int,
    var yield: String
)
/**
 * @Des 微信支付
 */
data class WxPayBean(
    val appid: String,
    val noncestr: String,
    val `package`: String,
    val partnerid: String,
    val prepayid: String,
    val sign: String,
    val timestamp: String
)
/**
 * @Des 房间背景
 */
data class RoomBgBean(
    var check: Boolean = false,
    val id: Int,
    val image: String
)

/**
 * @Des 管理员列表
 */
data class ManageListBean(
    val avatar: String,
    val id: Int,
    val manage_type: Int,
    val member_id: Int,
    val userName: String
)
/**
 * @Des 房间消费排行榜
 */
data class RoomRankBean(
    val avatar: String,
    val create_time: String,
    val gender: Int,
    val member_id: Int,
    val userName: String,
    val user_id: Int,
    val yield: String
)

data class RoomGiftHistoryBean(
    val avatar: String,
    val gift_name: String,
    val image: String,
    val name: String,
    val pd_name: String,
    val num:String
)