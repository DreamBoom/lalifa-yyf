package cn.rongcloud.voice.room;


import com.lalifa.ui.mvp.IBasePresent;

import cn.rongcloud.config.api.RoomDetailBean;
import cn.rongcloud.voice.model.UiSeatModel;

interface IVoiceRoomPresent extends IBasePresent {

    void onNetworkStatus(int i);

    void setCurrentRoom(RoomDetailBean mVoiceRoomBean);

    RoomDetailBean getmVoiceRoomBean();

    /**
     * 监听时间
     *
     * @param roomId
     */
    void initListener(String roomId);

    /**
     * 空座位被点击 观众
     */
    void enterSeatViewer(int position);

    /**
     * 空座位被点击 房主
     *
     * @param seatStatus
     * @param
     */
    void enterSeatOwner(UiSeatModel seatStatus);
}
