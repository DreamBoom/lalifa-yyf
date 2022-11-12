package com.lalifa.main.api

import com.drake.net.Post
import com.lalifa.che.api.ImgBean
import com.lalifa.extension.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import java.io.File

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
 * 文件上传
 * @receiver CoroutineScope
 * @param path String
 * @return String
 */
suspend fun CoroutineScope.upload(path: String):ImgBean  {
    return Post<BaseBean<ImgBean>>("common/upload") {
        param("file", File(path))
    }.await().data!!
}
suspend fun CoroutineScope.upload(list: ArrayList<String>): ArrayList<String> {
    val deferredList = arrayListOf<Deferred<com.lalifa.che.api.BaseBean<ImgBean>>>().apply {
        list.forEach {
            val deferred = Post<com.lalifa.che.api.BaseBean<ImgBean>>("common/upload") {
                param("file", File(it))
            }
            add(deferred)
        }
    }
    return arrayListOf<String>().apply {
        deferredList.forEach {
            it.await().data?.let { it1 -> add(it1.url) }
        }
    }
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
 * @return Any?
 */
suspend fun CoroutineScope.feedBack(problem: String, image: List<String>): Any? {
    return Post<BaseBean<Any>>("user/feedback") {
        param("problem", problem)
        param("image", image.string())
    }.await().data
}

/**
 * 实名认证
 * @receiver CoroutineScope
 * @param name 姓名
 * @param phone 手机号
 * @param idNumber 身份证号
 * @param positiveImage 身份证正面照
 * @param backImage 身份证反面照
 * @return Any?
 */
suspend fun CoroutineScope.realName(
    name: String, phone: String, idNumber: String,
    positiveImage: String, backImage: String
): Any? {
    return Post<BaseBean<Any>>("user/authentication") {
        param("name", name)
        param("phone", phone)
        param("id_number", idNumber)
        param("positive_image", positiveImage)
        param("back_image", backImage)
    }.await().data
}

/**
 * 修改个人信息
 * @receiver CoroutineScope
 * @param username 姓名
 * @param gender 性别
 * @param birthday 出生年月日
 * @param age 年龄
 * @param bio 个性签名
 * @param avatar 头像
 * @return Any?
 */
suspend fun CoroutineScope.changeUserInfo(
    username: String, gender: String, birthday: String,
    age: String, bio: String,avatar:String
): Any? {
    return Post<BaseBean<Any>>("user/profile") {
        param("username", username)
        param("gender", gender)
        param("birthday", birthday)
        param("age", age)
        param("bio", bio)
        param("avatar", avatar)
    }.await().data
}


/**
 * 贵族权益
 * @receiver CoroutineScope
 * @return ArrayList<TitleBean>?
 */
suspend fun CoroutineScope.titles(): ArrayList<TitleBean>? {
    return Post<BaseBean<ArrayList<TitleBean>>>("user/titles") {
    }.await().data
}

/**
 * 我的发布
 * @receiver CoroutineScope
 * @param page 页码
 * @return ReleaseBean?
 */
suspend fun CoroutineScope.release(page:Int): ReleaseBean? {
    return Post<BaseBean<ReleaseBean>>("user/release") {
        param("offset", page)
    }.await().data
}

/**
 * 删除动态
 * @receiver CoroutineScope
 * @param id 动态ID
 * @return Any?
 */
suspend fun CoroutineScope.delDynamic(id:String): Any? {
    return Post<BaseBean<Any>>("user/del_dynamic") {
        param("id", id)
    }.await().data
}


/**
 * 活动列表
 * @receiver CoroutineScope
 * @return ArrayList<ActivityBean>?
 */
suspend fun CoroutineScope.getActivity(): ArrayList<ActivityBean>? {
    return Post<BaseBean<ArrayList<ActivityBean>>>("user/activity") {
    }.await().data
}

/**
 * 活动详情
 * @receiver CoroutineScope
 * @param id ID
 * @return ActivityInfoBean?
 */
suspend fun CoroutineScope.getActivityInfo(id:String): ActivityInfoBean? {
    return Post<BaseBean<ActivityInfoBean>>("user/activity_details") {
        param("id", id)
    }.await().data
}