package com.lalifa.yyf.api
import com.lalifa.extension.splitComma
import java.io.Serializable

/**
 *
 * @ClassName JsonBean
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/5/21 11:14
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

//用户信息
data class UserInfo(
    val id: Long,//用户ID
    var username: String,//用户名
    var nickname: String,//昵称
    var mobile: String,//手机号
    var avatar: String,//头像
    val score: Int,//账户余额
    val imToken: String,//融云TOKEN
    val imId: String,
    val token: String,
    val user_id: String,
    val createtime: Long,
    val expiretime: Long,
    var expires_in: Long,
) : Serializable
