package com.lalifa.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.drake.brv.utils.page
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.ext.showTipDialog
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.MainSearchActivity
import com.lalifa.main.activity.PHActivity
import com.lalifa.main.activity.room.RoomActivity
import com.lalifa.main.activity.room.ext.Member
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.main.api.*
import com.lalifa.main.databinding.FragmentRoomListBinding
import com.lalifa.main.ext.inputPasswordDialog
import com.lalifa.main.fragment.adapter.roomListAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class RoomListFragment : BaseFragment<FragmentRoomListBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRoomListBinding.inflate(layoutInflater)

    private var type = 1
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            scopeNetLife {
                val roomList = roomIndex()
                binding.gg.text =
                    "${roomList!!.notice.n_title}:${roomList.notice.n_message_content}"
                if (roomList.carousel.isNotEmpty()) {
                    val imgList = arrayListOf<String>()
                    for (item in roomList.carousel) {
                        imgList.add(Config.FILE_PATH + item.image)
                    }
                    binding.banner.addBannerLifecycleObserver(requireActivity())
                        .setAdapter(object : BannerImageAdapter<String>(imgList.toList()) {
                            override fun onBindView(
                                holder: BannerImageHolder?,
                                data: String?,
                                position: Int,
                                size: Int
                            ) {
                                holder?.imageView?.apply {
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                    data?.let { it1 -> load(it1) }
                                }
                            }
                        }).indicator = CircleIndicator(context)
                }
            }
            fgRoomList.apply {
                roomListAdapter().apply {
                    R.id.itemRoom.onClick {
                        clickItem(getModel(), false)
                    }
                }
                page().onRefresh {
                    scopeNetLife {
                        addData(roomList(type.toString(), index.toString())!!.office)
                    }
//                    if (isRefreshing) {
//                        loadRoomList()
//                    }
                }.autoRefresh()
            }
        }
        checkUserRoom()
    }


    override fun onClick() {
        super.onClick()
        binding.apply {
            search.onClick { start(MainSearchActivity::class.java) }
            ph.onClick { start(PHActivity::class.java) }
        }
    }

    /**
     * 检查用户之前是否在某个房间内
     */
    private fun checkUserRoom() {
        //222
//        if (MiniRoomManager.getInstance().isShowing) {
//            // 如果有小窗口存在的情况下，不显示
//            return
//        }
        scopeNetLife {
            val roomCheck = roomCheck()
            if (null != roomCheck && !TextUtils.isEmpty(roomCheck.RoomId)) {
                // 说明已经在房间内了，那么给弹窗
                showTipDialog("您正在直播的房间中\n是否返回？"){
                    val userId = roomCheck.userId
                    if(TextUtils.equals(userId, Member.currentId)){
                        jumpRoom(true, roomCheck.RoomId)
                    }else{
                        jumpRoom(false, roomCheck.RoomId)
                    }
                }
            }
        }
    }

    /**
     * 跳转到语聊房界面
     *
     * @param roomId 房间Id
     * @param owner  是不是房主
     */
    private fun jumpRoom(owner: Boolean, roomId: String) {
        RoomActivity.joinVoiceRoom(requireActivity(), roomId, owner)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RoomActivity.ACTION_ROOM) {
         //   loadRoomList()
        }
    }

    private fun clickItem(
        item: Office,
        isCreate: Boolean
    ) {
        if (TextUtils.equals(item.userId, UserManager.get()!!.userId)) {
            val list: ArrayList<String> = ArrayList()
            list.add(item.roomid)
            jumpRoom(true, item.roomid)
            LogCat.e("进入自己创建的房间====${item.roomid}")
        } else if (item.password_type == 1) {
            inputPasswordDialog() {
                scopeNetLife {
//                    val roomDetail = roomDetail(item.id.toString())
//                    if (roomDetail != null&&) {
//                        jumpRoom(item.userId, item.roomid)
//                    }
                }
            }
            LogCat.e("进入密码房间====${item.roomid}")
        } else {
            LogCat.e("进入非创建无密码房间====${item.roomid}")
            jumpRoom(false, item.roomid)
        }
    }


    companion object {
        /**
         * 通过服务api接口销毁房间
         *
         * @param activity activity
         * @param roomId   待销毁的房间id
         */
        @JvmStatic
        fun closeRoomByService(roomActivity: RoomActivity, roomId: String) {
//            val url: String = Api.DELETE_ROOM.replace(Api.KEY_ROOM_ID, roomId)
//            val tag = LoadTag(activity, "关闭房间...")
//            tag.show()
//            OkApi.get(url, null, object : WrapperCallBack() {
//                fun onError(code: Int, msg: String?) {
//                    super.onError(code, msg)
//                    if (null != tag) tag.dismiss()
//                    KToast.show("关闭房间失败")
//                }
//
//                fun onResult(wrapper: Wrapper) {
//                    if (null != tag) tag.dismiss()
//                    if (wrapper.ok()) {
//                        KToast.show("关闭房间成功")
//                    } else {
//                        KToast.show("关闭房间失败")
//                    }
//                }
//            })
        }
    }
}