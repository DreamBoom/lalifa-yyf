package com.lalifa.main.api

import cn.rongcloud.config.provider.user.LoginUserBean
import com.drake.logcat.LogCat
import com.drake.net.Post
import com.lalifa.extension.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import java.io.File
/**
 * 注册
 * @receiver CoroutineScope
 * @param mobile String  手机号
 * @param code String  验证码
 * @param password String  密码
 * @param confirmPassword String  确认密码
 * @param gender Int  性别：0：男生  1：女生
 * @param nickname String  昵称
 * @param avatar String  头像
 * @param age Int  年龄
 * @param birthday String 生日
 * @return LoginBean?
 */
suspend fun CoroutineScope.register(
    mobile: String, code: String,
    password: String, confirmPassword: String,
    gender: Int, nickname: String,
    avatar: String, age: Int, birthday: String,
): LoginUserBean? {
    return Post<BaseBean<LoginUserBean>>("user/register") {
        param("mobile", mobile)
        param("code", code)
        param("password", password)
        param("confirm_password", confirmPassword)
        param("gender", gender)
        param("nickname", nickname)
        param("avatar", avatar)
        param("age", age)
        param("birthday", birthday)
    }.await().data
}


/**
 * 登录
 * @receiver CoroutineScope
 * @param account String  手机号
 * @param password String  密码
 * @return LoginBean?
 */
suspend fun CoroutineScope.login(account: String, password: String): LoginUserBean? {
    return Post<BaseBean<LoginUserBean>>("user/login") {
        param("account", account)
        param("password", password)
    }.await().data
}

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
    val deferredList = arrayListOf<Deferred<BaseBean<ImgBean>>>().apply {
        list.forEach {
            val deferred = Post<BaseBean<ImgBean>>("common/upload") {
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
suspend fun CoroutineScope.release(page:Int): CheListBean? {
    return Post<BaseBean<CheListBean>>("user/release") {
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

/**
 * 好友列表
 * @receiver CoroutineScope
 * @param id String 搜索ID
 * @param name String 搜索名称
 * @return ArrayList<FriendBean>?
 */
suspend fun CoroutineScope.friendsList(id: String = "",name: String = ""):ArrayList<FriendBean>? {
    return Post<BaseBean<ArrayList<FriendBean>>>("user/friends_list") {
        param("id", id)
        param("name", name)
    }.await().data!!
}

/**
 * 新人列表
 * @receiver CoroutineScope
 * @return ArrayList<NewFriendBean>?
 */
suspend fun CoroutineScope.newFriendsList():ArrayList<NewFriendBean>? {
    return Post<BaseBean<ArrayList<NewFriendBean>?>>("user/new_people") {
    }.await().data!!
}

/**
 * 申请列表
 * @receiver CoroutineScope
 * @return ArrayList<NewFriendBean>?
 */
suspend fun CoroutineScope.applyList():ArrayList<ApplyBean>? {
    return Post<BaseBean<ArrayList<ApplyBean>?>>("user/examine_friends_list") {
    }.await().data!!
}

/**
 * 审核好友添加
 * @receiver CoroutineScope
 * @param id String 审核ID
 * @param type String 状态 1：通过  2：拒绝
 * @return Any
 */
suspend fun CoroutineScope.applyFriend(id: String,type: String):Any{
    return Post<BaseBean<Any>>("user/examine_friends") {
        param("id", id)
        param("type", type)
    }.await().data!!
}

/**
 * 申请好友添加
 * @receiver CoroutineScope
 * @param pid String 添加id
 * @param postscript String 申请附言
 * @return Any
 */
suspend fun CoroutineScope.addFriend(pid: String,postscript: String=""):Any{
    return Post<BaseBean<Any>>("user/add_friends") {
        param("pid", pid)
        param("postscript", postscript)
    }.await().data!!
}
/**
 * 发现页列表
 * @receiver CoroutineScope
 * @return CheListBean?
 */
suspend fun CoroutineScope.cheList(type: Int, offset: Int): CheListBean? {
    return Post<BaseBean<CheListBean>>("user/community") {
        param("type", type)
        param("offset", offset)
    }.await().data
}

/**
 * 发现页详情
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.cheInfo(id: Int): CheInfoBean? {
    return Post<BaseBean<CheInfoBean>>("user/DynamicDetails") {
        param("id", id)
    }.await().data
}

/**
 * 发布动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.upChe(content: String, image: List<String>): CheInfoBean? {
    LogCat.e("==111===" + image.string())
    return Post<BaseBean<CheInfoBean>>("user/dynamic") {
        param("content", content)
        param("image", image.string())
    }.await().data
}

/**
 * 点赞动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.dzChe(id: Int): CheInfoBean? {
    return Post<BaseBean<CheInfoBean>>("user/fabulous") {
        param("id", id)
    }.await().data
}

/**
 * 评论动态
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.plChe(id: String, note: String, pid: String): Any? {
    return Post<BaseBean<Any>>("user/comment") {
        param("id", id)
        param("note", note)
        param("pid", pid)
    }.await().data
}

/**
 * 点赞评论
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.dzPl(id: String): Any? {
    return Post<BaseBean<Any>>("user/FabulousComment") {
        param("id", id)
    }.await().data
}



