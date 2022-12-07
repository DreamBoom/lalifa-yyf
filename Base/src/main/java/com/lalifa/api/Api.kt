package com.lalifa.api

import com.drake.net.Post
import kotlinx.coroutines.CoroutineScope

/**
 * 商品详情
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.giftList(): List<GiftBean>? {
    return Post<BaseBean<List<GiftBean>>>("user/gift") {
    }.await().data
}