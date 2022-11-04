package cn.rongcloud.roomkit.api

import com.drake.net.Get
import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope

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
 * @return RechargeBean?
 */
suspend fun CoroutineScope.pay(id:String,price:String,payType:String): RechargeBean? {
    return Post<BaseBean<RechargeBean>>("user/recharge") {
        param("id", id)
        param("price", price)
        param("payType", payType)
    }.await().data
}