package com.lalifa.main.api

import com.drake.net.Post
import com.lalifa.extension.string
import kotlinx.coroutines.CoroutineScope

/**
 * 首页
 * @receiver CoroutineScope
 * @return indexBean?
 */
suspend fun CoroutineScope.index(): indexBean? {
    return Post<BaseBean<indexBean>>("index/index") {
    }.await().data
}

/**
 * 背包
 * @receiver CoroutineScope
 * @return KnapsackBean?
 */
suspend fun CoroutineScope.knapsack(): List<KnapsackBean>? {
    return Post<BaseBean<List<KnapsackBean>>>("user/knapsack") {
    }.await().data
}

/**
 * 商城
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.shop(): List<shopBean>? {
    return Post<BaseBean<List<shopBean>>>("wares/wares") {
    }.await().data
}

/**
 * 商品详情
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.goodInfo(id: Int): GoodInfoBean? {
    return Post<BaseBean<GoodInfoBean>>("wares/WaresDetails") {
        param("id", id)
    }.await().data
}

/**
 * 购买商品
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.buyGood(id: Int): String? {
    return Post<BaseBean<String>>("wares/buy_wares") {
        param("id", id)
    }.await().data
}

/**
 * 个人中心
 * @receiver CoroutineScope
 * @return UserInfoBean?
 */
suspend fun CoroutineScope.userInfo(): UserInfoBean? {
    return Post<BaseBean<UserInfoBean>>("user/user") {
    }.await().data
}

/**
 * 好友
 * @receiver CoroutineScope
 * @param type 1：关注  2：粉丝
 * @return FriendBean?
 */
suspend fun CoroutineScope.friends(type: Int): List<FriendBean>? {
    return Post<BaseBean<List<FriendBean>>>("user/Friends") {
        param("type", type)
    }.await().data
}

/**
 * 我的钱包
 * @receiver CoroutineScope
 * @param type 0：收入  1：支出
 * @param offset 页码
 * @return MoneyListBean?
 */
suspend fun CoroutineScope.wallet(type: String, offset: String): MoneyListBean? {
    return Post<BaseBean<MoneyListBean>>("user/wallet") {
        param("type", type)
        param("offset", offset)
    }.await().data
}

/**
 * 意见反馈
 * @receiver CoroutineScope
 * @param problem 反馈问题
 * @param image 图片
 * @return MoneyListBean?
 */
suspend fun CoroutineScope.feedBack(problem: String, image: List<String>): MoneyListBean? {
    return Post<BaseBean<MoneyListBean>>("user/feedback") {
        param("problem", problem)
        param("image", image.string())
    }.await().data
}
