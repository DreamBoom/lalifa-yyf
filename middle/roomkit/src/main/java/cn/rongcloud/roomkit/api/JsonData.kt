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