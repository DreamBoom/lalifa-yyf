package com.lalifa.main.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import cn.rongcloud.roomkit.intent.IntentWrap
import cn.rongcloud.roomkit.ui.RoomType
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager
import cn.rongcloud.roomkit.ui.other.MainSearchActivity
import cn.rongcloud.roomkit.ui.other.MySxActivity
import cn.rongcloud.roomkit.ui.other.PHActivity
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.activity.*
import com.lalifa.main.api.index
import com.lalifa.main.databinding.ViewMainHomeBinding
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class MainFragment : BaseFragment<ViewMainHomeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainHomeBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            mList1.grid(3).setup {
                addType<String>(R.layout.item_home1)
            }.apply {
                R.id.itemRoom.onClick {
                    val list: ArrayList<String> = ArrayList()
                    list.add("1")
                    launchRoomActivity("1", list, 0, false)
                }
            }.models = arrayListOf("", "", "")
            mList2.grid(3).setup {
                addType<String>(R.layout.item_home1)
            }.models = arrayListOf("", "", "")
            mList3.grid(2).setup {
                addType<String>(R.layout.item_home2)
            }.models = arrayListOf("", "", "", "")
        }
        initData()
    }
    private fun launchRoomActivity(
        roomId: String, roomIds: ArrayList<String>, position: Int, isCreate: Boolean
    ) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(
            roomId
        ) {
            IntentWrap.launchRoom(
                requireContext(),
                RoomType.VOICE_ROOM,
                roomIds,
                position,
                isCreate
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        scopeNetLife {
            val index = index()
            binding.gg.text = "${index!!.notice.n_title}:${index.notice.n_message_content}"
            if(index.carousel.isNotEmpty()){
                val imgList = arrayListOf<String>()
                for ( item in index.carousel){
                    imgList.add(Config.FILE_PATH+item.image)
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