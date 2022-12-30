package com.lalifa.main.activity.room

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.toast
import com.lalifa.base.BaseListActivity
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.getIntentString
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.main.api.*
import com.lalifa.main.ext.inputPasswordDialog
import com.lalifa.main.fragment.adapter.friendList
import com.lalifa.main.fragment.adapter.roomListAdapter
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class SearchRoomList : BaseListActivity() {
    override fun title() = "语音房"
    var roomTheme: Int = -1
    var name = ""
    override fun iniView() {
        roomTheme = getIntentInt("theme", 1)
        name = getIntentString("name")
        recyclerView.roomListAdapter().apply {
            R.id.itemRoom.onClick {
                clickItem(getModel())
            }
        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }


    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            if(roomTheme==-1){
                addData(roomList("", name,index.toString())!!.office)
            }else{
                addData(roomList("$roomTheme", name,index.toString())!!.office)
            }
        }
    }

    /**
     * 跳转到语聊房界面
     *
     * @param roomId 房间Id
     * @param owner  是不是房主
     */
    private fun clickItem(item: Office) {
        if (TextUtils.equals(item.userId, UserManager.get()!!.userId)) {
            RoomActivity.joinVoiceRoom(this, item.roomid, true)
        } else if (item.password_type == 1) {
            inputPasswordDialog() {
                scopeNetLife {
                    val roomDetail = roomDetail(item.id.toString())
                    if (roomDetail != null && it == roomDetail.password) {
                        RoomActivity.joinVoiceRoom(this@SearchRoomList, item.roomid, false)
                    } else {
                        toast("密码错误")
                    }
                }
            }
        } else {
            RoomActivity.joinVoiceRoom(this, item.roomid, false)
        }
    }

}