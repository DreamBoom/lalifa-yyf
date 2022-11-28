package cn.rongcloud.config.api
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.wrapper.Provide
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

data class GiftBean(
    val image: String,
    val name: String,
    val price: String
)

/**
 * @Des 房间详情
 */
data class RoomDetailBean(
    var userInfo: User?,
    var background: String,
    var Chatroom_id: String,
    var advertisement: List<Advertisement>,
    var blind_box: List<BlindBox>,
    var collection_type: Int,
    var end_time: String,
    var fleet: List<Any>,
    var gift: List<Gift>,
    var gift_bag: List<GiftBag>,
    var gift_box_frequency: List<GiftBoxFrequency>,
    var gift_frequency: List<GiftFrequency>,
    var id: Int,
    var image: String,
    var knapsack: List<Any>,
    var manage_type: Int,
    var office_type: Int,
    var password: String,
    var password_type: Int,
    var start_time: String,
    var subheading: String,
    var title: String,
    var total_price: Int,
    var uid: Int
): Provide {
    override fun getKey(): String {
        return Chatroom_id
    }
}

data class Advertisement(
    var details: String,
    var id: Int,
    var image: String
)

data class BlindBox(
    var id: Int,
    var image: String,
    var name: String,
    var price: String
)

data class Gift(
    var image: String,
    var name: String,
    var price: String
)

data class GiftBag(
    var id: Int,
    var image: String,
    var name: String,
    var price: String
)

data class GiftBoxFrequency(
    var frequency: String,
    var id: Int,
    var name: String
)

data class GiftFrequency(
    var frequency: String,
    var id: Int,
    var name: String
)




