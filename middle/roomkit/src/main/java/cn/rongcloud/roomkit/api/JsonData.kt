package cn.rongcloud.roomkit.api
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.wrapper.Provide
import java.io.Serializable

data class ImgBean(
    val fullurl: String,
    val url: String
)
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
    var alipay_status: String,
    var recharge_proportion: String,
    var rule: List<Rule>,
    var wechat_status: String
)

data class Rule(
    var id: Int,
    var price: String,
    var receipt_price: String
)
/**
 * @Des 聊天室首页
 */
data class RoomIndexBean(
    var carousel: List<Carousel>,
    var classify: List<Classify>,
    var notice: Notice
)

data class Carousel(
    var create_time: String,
    var id: Int,
    var image: String,
    var sort: Int
)

data class Classify(
    var id: Int,
    var name: String
)

data class Notice(
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
):Provide{
    override fun getKey(): String {
        return ""
    }
}

data class Office (
    var uid: Int,
    var id: Int,
    var image: String,
    var notice: String,
    var password_type: Int, //0开放 1加密
    var roomid: String,
    var title: String,
    var type_id: Int,
    var type_name: String,
    var users: List<Any>
):Provide{
    override fun getKey(): String {
        return "$id"
    }
}
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

data class WxPayBean(
    val appid: String,
    val noncestr: String,
    val `package`: String,
    val partnerid: String,
    val prepayid: String,
    val sign: String,
    val timestamp: String
)