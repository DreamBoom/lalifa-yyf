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
    val avatar: String,
    val core_currency: String,
    val core_drill: String,
    val fans: Int,
    val follow: Int,
    val friends: Int,
    val id: Int,
    val jointime_text: String,
    val level: Int,
    val logintime_text: String,
    val patron_saint: Int,
    val prevtime_text: String,
    val userName: String
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