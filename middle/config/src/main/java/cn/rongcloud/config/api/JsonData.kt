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
data class ImgBean(
    val fullurl: String,
    val url: String
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
    var notice:String = "",
    var establish_type:Int, //1 可以创建队伍 else 不可以
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
/**
 * @Des 房间详情
 */
data class RoomGiftBean(
    val blind_box: List<RoomBlindBox>,
    val gift: List<RoomGift>,
    val gift_bag: List<RoomGiftBag>,
    val gift_box_frequency: List<RoomGiftBoxFrequency>,
    val gift_frequency: List<RoomGiftFrequency>,
    val knapsack: List<Any>,
    val total_price: Int
)

data class RoomBlindBox(
    val id: Int,
    val image: String,
    val name: String,
    val price: String
)

data class RoomGift(
    val image: String,
    val name: String,
    val price: String
)

data class RoomGiftBag(
    val id: Int,
    val image: String,
    val name: String,
    val price: String
)

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




