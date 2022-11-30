package cn.rongcloud.roomkit.ui.room.model

import androidx.lifecycle.MutableLiveData
import cn.rongcloud.config.api.getMembers
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.user.UserProvider
import com.drake.logcat.LogCat.e
import kotlin.jvm.JvmOverloads
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback
import com.drake.net.utils.scopeNet

/**
 * @author gyn
 * @date 2021/9/27
 */
class MemberCache {
    val memberList = MutableLiveData<MutableList<User>>(ArrayList(0))
    val adminList = MutableLiveData<MutableList<String>>(ArrayList(0))

    /**
     * 拉取房间成员和管理员
     *
     * @param roomId
     */
    fun fetchData(roomId: String?) {
        refreshMemberData(roomId)
        refreshAdminData(roomId)
    }

    /**
     * 拉取成员列表
     *
     * @param roomId
     */
    @JvmOverloads
    fun refreshMemberData(roomId: String?, callback: ClickCallback<Boolean>? = null) {
        scopeNet {
            val member = getMembers(roomId!!)
            if (member != null) {
                memberList.value = member!!
                member.forEach { user ->
                    UserProvider.provider().update(user.toUserInfo())
                }
                if (callback != null) callback.onResult(true, "");
            }
        }
    }

    /**
     * 拉取管理员列表
     *
     * @param roomId
     */
    fun refreshAdminData(roomId: String?) {
        scopeNet {
            val member = getMembers(roomId!!)
            if (member != null) {
                val list = ArrayList<String>()
                member.forEach {
                    list.add(it.userId)
                }
                adminList.value = list
            }
        }
    }

    /**
     * 判断一个用户是否是管理员
     *
     * @param userId
     * @return
     */
    fun isAdmin(userId: String): Boolean {
        val ids: List<String>? = adminList.value
        return ids?.contains(userId) ?: false
    }

    /**
     * 删除某个成员
     *
     * @param user
     */
    fun removeMember(user: User) {
        e("removeMember")
        val list = members
        if (list.contains(user)) {
            list.remove(user)
            memberList.value = list
        }
    }

    /**
     * 通过userId，拿到对应的成员
     */
    fun getMember(userId: String): User? {
        val members: List<User> = members
        for (member in members) {
            if (member.userId == userId) {
                return member
            }
        }
        return null
    }

    /**
     * 添加成员
     *
     * @param user
     */
    fun addMember(user: User) {
        e("addMember")
        val list = members
        if (!list.contains(user)) {
            list.add(user)
            memberList.value = list
        }
    }

    fun addAdmin(id: String) {
        val ids = adminList.value!!
        if (!ids.contains(id)) {
            ids.add(id)
            adminList.value = ids
        }
    }

    fun removeAdmin(id: String) {
        val ids = adminList.value!!
        if (ids.contains(id)) {
            ids.remove(id)
            adminList.value = ids
        }
    }

    private val members: MutableList<User>
        private get() = memberList.value!!

    companion object {
        private var INSTANCE = MemberCache()
        fun get() = INSTANCE
    }
}