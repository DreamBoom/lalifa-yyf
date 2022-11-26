package cn.rongcloud.roomkit.api
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
 * @Des 聊天室首页
 */
data class RoomIndexBean(
    val carousel: List<Carousel>,
    val classify: List<Classify>,
    val notice: Notice
)

data class Carousel(
    val create_time: String,
    val id: Int,
    val image: String,
    val sort: Int
)

data class Classify(
    val id: Int,
    val name: String
)

data class Notice(
    val create_time: String,
    val id: Int,
    val image: String,
    val n_message_content: String,
    val n_title: String,
    val status: Int
)
/**
 * @Des 聊天室列表
 */
data class RoomListBean(
    val count: Int,
    val office: List<Office>
)

data class Office(
    val id: Int,
    val image: String,
    val notice: String,
    val roomid: String,
    val title: String,
    val type_id: Int,
    val type_name: String,
    val users: List<Any>
)
/**
 * @Des 排行榜
 */
data class RankBean(
    val avatar: String,
    val create_time: String,
    val gender: Int,
    val member_id: Int,
    val userName: String,
    val user_id: Int,
    val yield: String
)