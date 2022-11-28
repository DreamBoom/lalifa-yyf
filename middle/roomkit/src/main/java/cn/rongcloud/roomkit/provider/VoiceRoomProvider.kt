package cn.rongcloud.roomkit.provider

import android.text.TextUtils
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.RoomDetailBean
import cn.rongcloud.config.api.roomDetail
import cn.rongcloud.config.provider.wrapper.AbsProvider
import cn.rongcloud.roomkit.api.roomList
import cn.rongcloud.roomkit.manager.LocalDataManager
import cn.rongcloud.roomkit.ui.RoomOwnerType
import cn.rongcloud.roomkit.ui.RoomType
import com.drake.net.utils.scopeNet
import com.lalifa.wapper.IResultBack

class VoiceRoomProvider private constructor() : AbsProvider<RoomDetailBean?>(-1),
    IListProvider<RoomDetailBean?> {
    private val bgImages: MutableList<String> = ArrayList()
    var page = 1
        private set
    override fun provideFromService(
        ids: List<String>,
        resultBack: IResultBack<List<RoomDetailBean?>>?
    ) {
        if (null == ids || ids.isEmpty()) {
            resultBack?.onResult(ArrayList())
            return
        }
        // TODO
        val roomId = ids[0]
        scopeNet {
            val roomDetail = roomDetail(roomId)
            if(null!=roomDetail){
                //todo 测试
              //  resultBack?.onResult(roomDetail)
            }else{
                resultBack?.onResult(null)
            }
        }
    }

//     override fun onUpdateComplete(RoomDetailBeans: List<RoomDetailBean?>) {
//        val users: MutableList<UserInfo> = ArrayList()
//        val count = RoomDetailBeans.size
//        for (i in 0 until count) {
//            if (RoomDetailBeans[i]!!.createUser != null) {
//                users.add(RoomDetailBeans[i]!!.createUser.toUserInfo())
//            }
//        }
//        UserProvider.provider().update(users)
//    }

    val images: List<String>
        get() = ArrayList(bgImages)

    override fun loadPage(
        isRefresh: Boolean,
        roomType: RoomType,
        resultBack: IResultBack<List<RoomDetailBean?>>
    ) {
        if (isRefresh) {
            page = 1
        }
        if (page < 1) page = 1
        scopeNet {
            val roomList = roomList("", page.toString())
            if(null!=roomList){
                if (null != roomList.office) {
                    bgImages.clear()
                    roomList.office.forEach{
                        bgImages.add(it.image)
                    }
                    LocalDataManager.saveBackGroundUrl(images)
                }
                //todo 测试
               // updateCache(roomList.office)
                if (roomList.count==10) {
                    page++
                }
                //todo 测试
              //  resultBack.onResult(roomList.office)
            }else{
                resultBack.onResult(null)
            }
        }
    }

    /**
     * 获取房间类型
     *
     * @param RoomDetailBean 当前房间
     * @return 房间类型
     */
    fun getRoomOwnerType(RoomDetailBean: RoomDetailBean?): RoomOwnerType {
        if (RoomDetailBean == null || RoomDetailBean.userInfo == null) {
            throw NullPointerException("RoomDetailBean is null")
        }
        val userId = UserManager.get()!!.userId
        return if (TextUtils.equals(userId, RoomDetailBean.userInfo!!.userId)) {
            RoomOwnerType.VOICE_OWNER
        } else {
            RoomOwnerType.VOICE_VIEWER
        }
    }

    companion object {
        private val _provider = VoiceRoomProvider()
        @JvmStatic
        fun provider(): VoiceRoomProvider {
            return _provider
        }
    }


}