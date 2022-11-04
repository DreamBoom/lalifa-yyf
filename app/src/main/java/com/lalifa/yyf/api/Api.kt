package com.lalifa.yyf.api

import cn.rongcloud.config.provider.user.LoginUserBean
import com.drake.net.Post
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