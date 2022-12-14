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
import com.drake.tooltip.toast
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
                        clickItem(getModel())
                    }
                }
                page().onRefresh {
                    scopeNetLife {
                        addData(roomList("","", index.toString())!!.office)
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
     * ??????????????????????????????????????????
     */
    private fun checkUserRoom() {
        //222
//        if (MiniRoomManager.getInstance().isShowing) {
//            // ????????????????????????????????????????????????
//            return
//        }
        scopeNetLife {
            val roomCheck = roomCheck()
            if (null != roomCheck && !TextUtils.isEmpty(roomCheck.RoomId)) {
                // ?????????????????????????????????????????????
                showTipDialog("???????????????????????????\n???????????????"){
                    val userId = roomCheck.userId
                    if(TextUtils.equals(userId, Member.currentId)){
                        RoomActivity.joinVoiceRoom(requireActivity(),
                            roomCheck.RoomId, true)
                    }else{
                        RoomActivity.joinVoiceRoom(requireActivity(),
                            roomCheck.RoomId, false)
                    }
                }
            }
        }
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


    /**
     * ????????????????????????
     *
     * @param roomId ??????Id
     * @param owner  ???????????????
     */
    private fun clickItem(item: Office) {
        if (TextUtils.equals(item.userId, UserManager.get()!!.userId)) {
            RoomActivity.joinVoiceRoom(requireActivity(), item.roomid, true)
        } else if (item.password_type == 1) {
            inputPasswordDialog() {
                scopeNetLife {
                    val roomDetail = roomDetail(item.id.toString())
                    if (roomDetail != null&&it == roomDetail.password) {
                        RoomActivity.joinVoiceRoom(requireActivity(), item.roomid, false)
                    }else{
                        toast("????????????")
                    }
                }
            }
        } else {
            RoomActivity.joinVoiceRoom(requireActivity(), item.roomid, false)
        }
    }


    companion object {
        /**
         * ????????????api??????????????????
         *
         * @param activity activity
         * @param roomId   ??????????????????id
         */
        @JvmStatic
        fun closeRoomByService(roomActivity: RoomActivity, roomId: String) {
//            val url: String = Api.DELETE_ROOM.replace(Api.KEY_ROOM_ID, roomId)
//            val tag = LoadTag(activity, "????????????...")
//            tag.show()
//            OkApi.get(url, null, object : WrapperCallBack() {
//                fun onError(code: Int, msg: String?) {
//                    super.onError(code, msg)
//                    if (null != tag) tag.dismiss()
//                    KToast.show("??????????????????")
//                }
//
//                fun onResult(wrapper: Wrapper) {
//                    if (null != tag) tag.dismiss()
//                    if (wrapper.ok()) {
//                        KToast.show("??????????????????")
//                    } else {
//                        KToast.show("??????????????????")
//                    }
//                }
//            })
        }
    }
}