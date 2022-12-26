package com.lalifa.main.fragment

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.drake.net.utils.scopeNetLife
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.activity.*
import com.lalifa.main.activity.room.RoomActivity
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ViewMainHomeBinding
import com.lalifa.main.fragment.adapter.mainList1
import com.lalifa.main.fragment.adapter.mainList2
import com.lalifa.main.fragment.adapter.mainList3
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class MainFragment : BaseFragment<ViewMainHomeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainHomeBinding.inflate(layoutInflater)
    override fun initView() {
        initData()
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

    @SuppressLint("SetTextI18n")
    private fun initData() {
        loadTag!!.show()
        scopeNetLife {
            val index = index()
            binding.apply {
                mList1.mainList1().apply {
                    R.id.itemRoom.onClick {
                        val room = getModel<Captain>()
                        if(TextUtils.equals(room.userId, Member.currentId)){
                            jumpRoom(true, room.roomid)
                        }else{
                            jumpRoom(false, room.roomid)
                        }
                    }
                }.models = index!!.captain
                mList2.mainList2().apply {
                    R.id.itemRoom.onClick {
                        val room = getModel<Host>()
                        if(TextUtils.equals(room.userId, Member.currentId)){
                            jumpRoom(true, room.roomid)
                        }else{
                            jumpRoom(false, room.roomid)
                        }
                    }
                }.models = index.host
                mList3.mainList3().apply {
                    R.id.rl.onClick {
                        val room = getModel<Room>()
                        if(room.uid==UserManager.get()!!.id){
                            jumpRoom(true, room.roomid)
                        }else{
                            jumpRoom(false, room.roomid)
                        }
                    }
                }.models = index.room
                gg.text = "${index.notice.n_title}:${index.notice.n_message_content}"
                if (index.carousel.isNotEmpty()) {
                    val imgList = arrayListOf<String>()
                    for (item in index.carousel) {
                        imgList.add(Config.FILE_PATH + item.image)
                    }
                    banner.addBannerLifecycleObserver(requireActivity())
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
                        })
                        .indicator = CircleIndicator(context)
                }
                loadTag!!.dismiss()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            shop.onClick { start(ShopActivity::class.java) }
            active.onClick { start(ActiveActivity::class.java) }
            search.onClick { start(MainSearchActivity::class.java) }
            ph.onClick { start(PHActivity::class.java) }
            money.onClick { start(MySxActivity::class.java) }
        }
    }
}