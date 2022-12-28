package com.lalifa.main.activity.room.ext

import android.net.Uri
import android.text.TextUtils
import com.lalifa.ext.Account
import java.io.Serializable

/**
 * 通用用户信息
 */
class User : Serializable {

    var id = 0//用户ID
    var userName: String= ""//用户名
    var mobile: String= "" //手机号
    var avatar: String= ""//头像
//        get() = field
//        set
    var frame: String = ""//头像框
    var car: String= ""//座驾
    var bubble: String= "" //气泡
    var sound: String= "" //音波
    var level: String= ""//爵位
    var score = 0.0//账户余额
    var imToken: String= ""//融云TOKEN
    var userId: String= ""
    var token: String= ""
    var user_id: String= ""
    var createtime: String= ""
    var expiretime: Long? = null
    var expires_in: Long? = null
    var gender = 0
    fun toAccount(): Account {
        return Account(
            userId,
            userName,
            avatar,
            imToken,
            frame,
            car,
            bubble,
            sound
        )
    }

    fun toMember(): Member {
        return Member(
            userId,
            userName,
            avatar,
            level,
            frame,
            car,
            bubble,
            sound,
            gender,
            manageType = 0,
            collectionType= 0,
            follow_type= 0,
            seatIndex = -1
        )
    }

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatar='" + avatar + '\'' +
                ", level='" + level + '\'' +
                ", score=" + score +
                ", imToken='" + imToken + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", user_id='" + user_id + '\'' +
                ", createtime='" + createtime + '\'' +
                ", expiretime=" + expiretime +
                ", expires_in=" + expires_in +
                ", gender=" + gender +
                '}'
    }

    var authorization: String?
        get() = ""
        set(authorization) {}
    val portraitUri: Uri
        get() = Uri.parse(avatar)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val user = o as User
        return !TextUtils.isEmpty(userId) && userId == user.userId
    }

    fun toUser(): User {
        val user = User()
        user.userId = userId
        user.userName = userName
        user.avatar = avatar
        return user
    }
}