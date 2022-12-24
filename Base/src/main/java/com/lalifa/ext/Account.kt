package com.lalifa.ext

import android.text.TextUtils
import java.io.Serializable
import java.util.*

data class Account(
    var userId: String = "",
    var userName: String = "",
    var avatar: String = "",
    var imToken: String = "",
    //头像框
    var frame: String = "",
    //座驾
    var car: String = "",
    //气泡
    var bubble: String = "",
    //音波
    var sound: String = "",
    //随心值
    var sx: String = "",
    var select: Boolean = false,
    //麦序
    var seat: Int = -1
) : Serializable {
    @JvmName("getAvatar1")
    fun getAvatar(): String {
        return if (TextUtils.isEmpty(avatar)) "" else Config.FILE_PATH + avatar
    }

    @JvmName("setAvatar1")
    fun setAvatar(avatar: String) {
        this.avatar = avatar
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val accout = o as Account
        return userId == accout.userId
    }

    override fun hashCode(): Int {
        return Objects.hash(userId)
    }

    override fun toString(): String {
        return "Account(userId='$userId', userName='$userName', avatar='$avatar', imToken='$imToken', frame='$frame', car='$car', bubble='$bubble', sound='$sound', select=$select)"
    }


}