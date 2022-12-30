package com.lalifa.main.api

import com.drake.net.Get
import com.drake.net.Post
import com.lalifa.main.activity.room.ext.Member
import com.lalifa.main.activity.room.ext.User
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
suspend fun CoroutineScope.index(): IndexBean? {
    return Post<BaseBean<IndexBean>>("index/index") {
    }.await().data
}


/**
 * 文件上传
 * @receiver CoroutineScope
 * @param path String
 * @return String
 */
suspend fun CoroutineScope.upload(path: String): ImgBean {
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
suspend fun CoroutineScope.knapsack(): KnapsackBean? {
    return Post<BaseBean<KnapsackBean>>("user/knapsack") {
    }.await().data
}

/**
 * 使用背包物品
 * @receiver CoroutineScope
 * @return KnapsackBean?
 */
suspend fun CoroutineScope.useDress(id: String): String? {
    return Post<BaseBean<String>>("user/use_dress_up") {
        param("id", id)
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
 * 兑换随心钻
 * @receiver CoroutineScope
 * @return Any?
 */
suspend fun CoroutineScope.exchangeDrill(id: String): Any? {
    return Post<BaseBean<Any>>("user/exchange_drill") {
        param("id", id)
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
    age: String, bio: String, avatar: String
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
suspend fun CoroutineScope.release(page: Int): CheListBean? {
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
suspend fun CoroutineScope.delDynamic(id: String): Any? {
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
suspend fun CoroutineScope.getActivityInfo(id: String): ActivityInfoBean? {
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
suspend fun CoroutineScope.friendsList(id: String = "", name: String = ""): ArrayList<FriendBean>? {
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
suspend fun CoroutineScope.newFriendsList(): ArrayList<NewFriendBean>? {
    return Post<BaseBean<ArrayList<NewFriendBean>?>>("user/new_people") {
    }.await().data!!
}

/**
 * 申请列表
 * @receiver CoroutineScope
 * @return ArrayList<NewFriendBean>?
 */
suspend fun CoroutineScope.applyList(): ArrayList<ApplyBean>? {
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
suspend fun CoroutineScope.applyFriend(id: String, type: String): Any {
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
suspend fun CoroutineScope.addFriend(pid: String, postscript: String = ""): Any {
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

/**
 * 礼物墙
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.getGiftList(): GiftListBean? {
    return Post<BaseBean<GiftListBean>>("user/gift_wall") {
    }.await().data
}

/**
 * 礼物记录
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.giftHistory(): List<GiftHistoryBean>? {
    return Post<BaseBean<List<GiftHistoryBean>>>("user/gift_record") {
    }.await().data
}

/**
 * 守护
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.guard(): List<GuardBean>? {
    return Post<BaseBean<List<GuardBean>>>("user/guard") {
    }.await().data
}

/**
 * 客服
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.getCallList(): List<CallBean>? {
    return Post<BaseBean<List<CallBean>>>("user/customer_service") {
    }.await().data
}

/**
 * 主页信息
 * @receiver CoroutineScope
 * @return CheInfoBean?
 */
suspend fun CoroutineScope.homepage(id: String): MeInfoBean? {
    return Post<BaseBean<MeInfoBean>>("user/homepage") {
        param("id", id)
    }.await().data
}

/**
 * 商品详情
 * @receiver CoroutineScope
 * @return shopBean?
 */
suspend fun CoroutineScope.giftList(): List<GiftBean>? {
    return Post<BaseBean<List<GiftBean>>>("user/gift") {
    }.await().data
}

/**
 * 创建队伍
 * @receiver CoroutineScope
 * @param id 当前所在房间ID
 * @param type 类型 1-游戏 2-娱乐
 * @param label 标签
 * @param rank 段位
 * @param explain 说明
 * @param background 背景图
 * @param pattern 模式
 * @return RoomListBean?
 */
suspend fun CoroutineScope.createRoom(
    id: String, type: String, label: String, rank: String, explain: String,
    background: String, pattern: String,
): RoomDetailBean? {
    return Post<BaseBean<RoomDetailBean>>("chat_room/add_fleet") {
        param("id", id)
        param("type", type)
        param("label", label)
        param("rank", rank)
        param("explain", explain)
        param("background", background)
        param("pattern", pattern)
    }.await().data
}

/**
 * 聊天室详情
 * @receiver CoroutineScope
 * @return RoomListBean?
 */
suspend fun CoroutineScope.roomDetail(id: String): RoomDetailBean? {
    return Post<BaseBean<RoomDetailBean>>("chat_room/room_details") {
        param("id", id)
    }.await().data
}

/**
 * 获取房间内礼物列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.roomGift(): RoomGiftBean? {
    return Post<BaseBean<RoomGiftBean>>("chat_room/gift") {
    }.await().data
}

/**
 * 检查当前用户所属房间
 * @return
 */
suspend fun CoroutineScope.roomCheck(): RoomCheckBean? {
    return Post<BaseBean<RoomCheckBean>>("chat_room/check") {
    }.await().data
}

/**
 * 关注/取消关注好友
 * @return
 */
suspend fun CoroutineScope.follow(id: String): String? {
    return Post<BaseBean<String>>("user/follow") {
        param("id", id)
    }.await().data
}

/**
 * 收藏取消房间
 * @return
 */
suspend fun CoroutineScope.collection(id: String): String? {
    return Post<BaseBean<String>>("chat_room/collection") {
        param("office_id", id)
    }.await().data
}

/**
 * 编辑房间信息
 * @return
 */
suspend fun CoroutineScope.editRoom(
    id: String, title: String, image: String, background_id: String,
    background: String, passwordType: String,
    password: String, notice: String
): String? {
    return Post<BaseBean<String>>("chat_room/edit_room") {
        param("id", id)
        param("title", title)
        param("image", image)
        param("background", background)
        param("background_id", background_id)
        param("password_type", passwordType)
        param("password", password)
        param("notice", notice)
    }.await().data
}

/**
 * 关闭房间
 * @return
 */
suspend fun CoroutineScope.closeRoom(id: String): String? {
    return Post<BaseBean<String>>("chat_room/destruction") {
        param("id", id)
    }.await().data
}

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
 * @return String?
 */
suspend fun CoroutineScope.ZfbPay(id: String, price: String, payType: String): String? {
    return Post<BaseBean<String>>("user/recharge") {
        param("id", id)
        param("price", price)
        param("payType", payType)
    }.await().data
}

suspend fun CoroutineScope.WxPay(id: String, price: String, payType: String): WxPayBean? {
    return Post<BaseBean<WxPayBean>>("user/recharge") {
        param("id", id)
        param("price", price)
        param("payType", payType)
    }.await().data
}

/**
 * 聊天室首页
 * @receiver CoroutineScope
 * @return RoomIndexBean?
 */
suspend fun CoroutineScope.roomIndex(): RoomIndexBean? {
    return Post<BaseBean<RoomIndexBean>>("chat_room/index") {
    }.await().data
}

/**
 * 聊天室列表
 * @receiver CoroutineScope
 * @param id 分类id
 * @param offset 页码
 * @return RoomListBean?
 */
suspend fun CoroutineScope.roomList(id: String, name: String, offset: String): RoomListBean? {
    return Post<BaseBean<RoomListBean>>("chat_room/ChatRoom") {
        param("id", id)
        param("name", name)
        param("offset", offset)
    }.await().data
}

/**
 * 排行榜
 * @receiver CoroutineScope
 * @param type 1：财富榜  2：魅力榜
 * @param category 1：日榜  2：周榜 3月榜
 * @return RoomListBean?
 */
suspend fun CoroutineScope.ranking(type: String, category: String): List<RankBean>? {
    return Post<BaseBean<List<RankBean>>>("user/ranking") {
        param("category", category)
        param("type", type)
    }.await().data
}

/**
 * 用户信息
 * @receiver CoroutineScope
 * @param id 用户ID
 * @param office_id 房间ID
 * @return RoomListBean?
 */
suspend fun CoroutineScope.userInfo(id: String, office_id: String): Member? {
    return Post<BaseBean<Member>>("chat_room/usre_information") {
        param("id", id)
        param("office_id", office_id)
    }.await().data
}

/**
 * 获取房间内管理员列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.getManageList(id: String): MutableList<ManageListBean>? {
    return Post<BaseBean<MutableList<ManageListBean>>>("chat_room/manage") {
        param("office_id", id)
    }.await().data
}

/**
 * 搜索管理员
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.getManage(userId: String, roomId: String): MutableList<ManageListBean>? {
    return Post<BaseBean<MutableList<ManageListBean>>>("chat_room/search_manage") {
        param("userId", userId)
        param("office_id", roomId)
    }.await().data
}

/**
 * 添加管理员
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.addManage(userId: String, roomId: String): String? {
    return Post<BaseBean<String>>("chat_room/add_manage") {
        param("uid", userId)
        param("office_id", roomId)
    }.await().data
}

/**
 * 删除管理员
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.removeManage(userId: String, roomId: String): String? {
    return Post<BaseBean<String>>("chat_room/del_manage") {
        param("id", userId)
        param("office_id", roomId)
    }.await().data
}

/**
 * 获取房间内成员列表
 *
 * @param id
 * @return
 */
suspend fun CoroutineScope.getMembers(id: String): MutableList<User>? {
    return Post<BaseBean<MutableList<User>>>("chat_room/members") {
        param("id", id)
    }.await().data
}

/**
 * 获取房间背景列表
 *
 * @return
 */
suspend fun CoroutineScope.getRoomBg(): MutableList<RoomBgBean>? {
    return Post<BaseBean<MutableList<RoomBgBean>>>("chat_room/background") {
    }.await().data
}

/**
 * 举报房间
 *
 * @return
 */
suspend fun CoroutineScope.reportRoom(roomId: String, content: String): String? {
    return Post<BaseBean<String>>("chat_room/report") {
        param("room_id", roomId)
        param("content", content)
    }.await().data
}

/**
 * 房间送礼物
 * @return
 */
suspend fun CoroutineScope.sendRoomGift(
    type: String, uid: String, roomId: String,
    num: String, id: String, userType: String
): String? {
    return Post<BaseBean<String>>("chat_room/gifts") {
        param("office_id", roomId)
        param("uid", uid)
        param("type", type)
        param("id", id)
        param("num", num)
        param("user_type", userType)
    }.await().data
}

/**
 * 房间礼物排行榜
 * @return
 */
suspend fun CoroutineScope.roomRanking(
    type: String, id: String
): List<RoomRankBean>? {
    return Post<BaseBean<List<RoomRankBean>>>("chat_room/ranking") {
        param("category", type)
        param("id", id)
    }.await().data
}

/**
 * 房间礼物赠送记录
 * @return
 */
suspend fun CoroutineScope.roomGiftHistory(roomId: String): List<RoomGiftHistoryBean>? {
    return Post<BaseBean<List<RoomGiftHistoryBean>>>("chat_room/gift_record") {
        param("id", roomId)
    }.await().data
}

/**
 * 房间主题
 * @return
 */
suspend fun CoroutineScope.roomTheme(): List<RoomThemeBean>? {
    return Post<BaseBean<List<RoomThemeBean>>>("index/theme") {
    }.await().data
}






