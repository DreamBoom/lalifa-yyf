package com.lalifa.main.api

import android.net.Uri
import android.text.TextUtils
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.lalifa.ext.User
import io.rong.imlib.model.UserInfo
import java.io.Serializable
import javax.sql.StatementEvent

/**
 * @author gyn
 * @date 2021/10/9
 */
open class Member : Serializable {
    var userId: String = ""
    var userName: String = ""
    var portraitUrl: String = ""
    var level: String = ""
    var frame //头像框
            : String = ""
    var car //座驾
            : String = ""
    var bubble //气泡
            : String = ""
    var sound //音波
            : String = ""
    var gender = 0

    // 是否是管理
    var manageType = 0

    // 房间是否已关注
    var collectionType = 0

    // 用户是否已关注
    var follow_type = 0

    // 麦位
    var seatIndex = -1
    // 麦位状态
    var status = RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
    // 麦位是否静音
    var isMute = false
    var select = false
    constructor(
        userId: String,
        userName: String,
        portraitUrl: String,
        level: String,
        frame: String,
        car: String,
        bubble: String,
        sound: String,
        gender: Int,
        manageType: Int,
        collectionType: Int,
        follow_type: Int,
        seatIndex: Int
    ) {
        this.userId = userId
        this.userName = userName
        this.portraitUrl = portraitUrl
        this.level = level
        this.frame = frame
        this.car = car
        this.bubble = bubble
        this.sound = sound
        this.gender = gender
        this.manageType = manageType
        this.collectionType = collectionType
        this.follow_type = follow_type
        this.seatIndex = seatIndex
    }

    constructor() {

    }


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val member = o as Member
        return !TextUtils.isEmpty(userId) && userId == member.userId
    }

    fun toUserInfo(): UserInfo {
        return UserInfo(userId, userName, Uri.parse(portraitUrl))
    }

    fun toUser(): User {
        val user = User()
        user.userId = userId!!
        user.userName = userName!!
        user.avatar = portraitUrl!!
        return user
    }

    override fun toString(): String {
        return "Member(userId=$userId, userName=$userName, portraitUrl=$portraitUrl, level=$level, frame=$frame, car=$car, bubble=$bubble, sound=$sound, gender=$gender, manageType=$manageType, collectionType=$collectionType, follow_type=$follow_type, seatIndex=$seatIndex, status=$status, isMute=$isMute)"
    }

    companion object {
        private val members: MutableList<Member> = ArrayList()
        private val seats: ArrayList<Member> = ArrayList()
        var currentId //当前账号
                : String? = null
            private set

        fun getMembers(): List<Member> {
            return members
        }

        fun setMember(a: Member?, mine: Boolean) {
            if (null == a) return
            if (mine) {
                currentId = a.userId
            }
            val find = members.find { it.userId == a.userId }
            if (null == find) {
                members.add(a)
            }
        }

        fun getMember(userId: String?): Member? {
            val size = members.size
            var result: Member? = null
            for (i in 0 until size) {
                val acc = members[i]
                if (acc.userId == userId) {
                    result = acc
                    break
                }
            }
            return result
        }

        fun getMemberName(userId: String?): String {
            val member = getMember(userId)
            return if (null == member) userId!! else member.userName!!
        }

        fun getSeats(): ArrayList<Member> {
            return seats
        }

        fun setSeat(a: Member) {
            seats.add(a)
        }
    }


}