package com.lalifa.main.api
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
/**
 * @Des 首页
 */
data class indexBean(
    val captain: List<Any>,
    val carousel: List<Carousel>,
    val host: List<Any>,
    val notice: Notice,
    val room: List<Any>
): Serializable

data class Carousel(
    val image: String
): Serializable

data class Notice(
    val id: Int,
    val n_message_content: String,
    val n_title: String
): Serializable

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
    val classify: List<Classify>
)

data class Classify(
    val id: Int,
    val knapsack: List<KnapsackInfo>,
    val name: String
)
data class KnapsackInfo(
    val id: Int,
    val knapsack: List<Any>,
    val name: String
)
/**
 * @Des 个人中心
 */
data class UserInfoBean(
    val age: Int,
    val avatar: String,
    val bio: String,
    val birthday: String,
    val core_currency: String,
    val core_drill: String,
    val fans: Int,
    val follow: Int,
    val friends: Int,
    val gender: Int,
    val id: Int,
    val jointime_text: String,
    val level: String,
    val logintime_text: String,
    val patron_saint: Int,
    val prevtime_text: String,
    val userName: String
)

/**
 * @Des 钱包列表
 */
data class MoneyListBean(
    val core_currency: String,
    val core_drill: String,
    val count: Int,
    val exchange: List<Exchange>,
    val record: List<MoneyRecord>,
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
 * @Des 我的发布
 */
data class ReleaseBean(
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
    val level: String,
    val share: Int,
    val status: Int,
    val uid: Int,
    val userName: String
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
    val `dynamic`: List<Dynamic1>
)

data class Dynamic1(
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