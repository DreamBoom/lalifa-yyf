package cn.rongcloud.voice.room

import androidx.lifecycle.MutableLiveData
import cn.rongcloud.config.api.RoomDetailBean
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun.BaseFun
import cn.rongcloud.roomkit.ui.room.model.Member
import cn.rongcloud.voice.model.UiSeatModel
import com.lalifa.ui.mvp.IBaseView
import io.rong.imlib.model.MessageContent

interface IVoiceRoomFragmentView : IBaseView {
    fun setRoomData(voiceRoomBean: RoomDetailBean?)
    fun enterSeatSuccess()

    /**
     * 通知指定坐席信息发生了改变，刷新之
     */
    fun onSeatInfoChange(index: Int, uiSeatModel: UiSeatModel)
    fun onSeatListChange(uiSeatModelList: List<UiSeatModel>)
    fun changeStatus(status: Int)
    fun showUnReadRequestNumber(number: Int)
    fun showRevokeSeatRequest()
    fun onSpeakingStateChanged(isSpeaking: Boolean)
    fun onNetworkStatus(i: Int)
    fun finish()
    fun refreshRoomOwner(uiSeatModel: UiSeatModel)
    fun setNotice(notice: String)
    fun showMessage(messageContent: MessageContent?, isRefresh: Boolean)
    fun showVideoGift()
    fun showMessageList(messageContentList: List<MessageContent>, isRefresh: Boolean)
    fun showSettingDialog(funList: List<MutableLiveData<BaseFun>>)
    fun showSetPasswordDialog(item: MutableLiveData<BaseFun?>)
    fun showSetRoomNameDialog(name: String)
    fun setVoiceName(name: String)
    fun showShieldDialog(roomId: String)
    fun showSelectBackgroundDialog(url: String)
    fun showNoticeDialog(isEdit: Boolean)
    fun setRoomBackground(url: String)
    fun refreshSeat()
    fun showSendGiftDialog(
        voiceRoomBean: RoomDetailBean,
        selectUserId: String,
        members: List<Member>
    )

    fun showUserSetting(member: Member, uiSeatModel: UiSeatModel)
    fun showMusicDialog()
    fun showFinishView()
    fun showLikeAnimation()
    fun setOnlineCount(OnlineCount: Int)
    fun switchOtherRoom(roomId: String)
    fun setTitleFollow(isFollow: Boolean)
    fun refreshMessageList()
    fun refreshMusicView(show: Boolean, name: String, url: String)
}