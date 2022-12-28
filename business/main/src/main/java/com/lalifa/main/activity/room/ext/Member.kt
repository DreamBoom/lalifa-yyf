package com.lalifa.main.activity.room.ext

import android.net.Uri
import android.text.TextUtils
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.alibaba.fastjson.JSONObject
import io.rong.imlib.model.UserInfo
import java.io.Serializable

/**
 * @author gyn
 * @date 2021/10/9
 */
open class Member : Serializable {
    var userId: String = ""
    var userName: String = ""
    var avatar: String = ""
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
    var manage_type = 0

    // 房间是否已关注
    var collection_type = 0

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
        avatar: String,
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
        this.avatar = avatar
        this.level = level
        this.frame = frame
        this.car = car
        this.bubble = bubble
        this.sound = sound
        this.gender = gender
        this.manage_type = manageType
        this.collection_type = collectionType
        this.follow_type = follow_type
        this.seatIndex = seatIndex
    }

    constructor() {

    }

    override fun toString(): String {
        return "Member(userId='$userId', userName='$userName', avatar='$avatar', level='$level', frame='$frame', car='$car', bubble='$bubble', sound='$sound', gender=$gender, manage_type=$manage_type, collection_type=$collection_type, follow_type=$follow_type, seatIndex=$seatIndex, status=$status, isMute=$isMute, select=$select)"
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

        fun memberEquals(o: Member): Boolean {
            val find = members.find { ac -> ac.userId == o.userId } ?: return false
            val toJSON = JSONObject.toJSONString(find)
            val toJSON1 = JSONObject.toJSONString(o)
            return if (TextUtils.equals(toJSON, toJSON1)) {
                true
            } else {
                members.remove(find)
                false
            }

        }

        fun setMember(a: Member?) {
            if (null == a) return
            if (a.userId==UserManager.get()!!.userId) {
                currentId = a.userId
            }
            if (!memberEquals(a)) {
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
            val find = seats.find { action -> a.userId == action.userId }
            if (null == find) {
                seats.add(a)
            }
        }
    }


}