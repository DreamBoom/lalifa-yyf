package cn.rongcloud.roomkit.ui.other.fragment

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.api.roomDetail
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.adapter.roomListAdapter
import cn.rongcloud.roomkit.api.Office
import cn.rongcloud.roomkit.api.VRApi
import cn.rongcloud.roomkit.api.roomIndex
import cn.rongcloud.roomkit.api.roomList
import cn.rongcloud.roomkit.databinding.FragmentRoomListBinding
import cn.rongcloud.roomkit.intent.IntentWrap
import cn.rongcloud.roomkit.provider.VoiceRoomProvider
import cn.rongcloud.roomkit.ui.RoomType
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager
import cn.rongcloud.roomkit.ui.other.MainSearchActivity
import cn.rongcloud.roomkit.ui.other.PHActivity
import cn.rongcloud.roomkit.ui.roomlist.CreateRoomDialog
import cn.rongcloud.roomkit.ui.roomlist.CreateRoomDialog.CreateRoomCallBack
import cn.rongcloud.roomkit.widget.InputPasswordDialog
import com.drake.brv.BindingAdapter
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.adapter.BannerImageAdapter
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.oklib.OkApi
import com.lalifa.oklib.OkParams
import com.lalifa.oklib.WrapperCallBack
import com.lalifa.oklib.wrapper.Wrapper
import com.lalifa.widget.dialog.dialog.VRCenterDialog
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import io.rong.imkit.picture.tools.ToastUtils

class RoomListFragment : BaseFragment<FragmentRoomListBinding>(), CreateRoomCallBack {
    private var mCreateRoomDialog: CreateRoomDialog? = null
    private var confirmDialog: VRCenterDialog? = null
    private var inputPasswordDialog: InputPasswordDialog? = null
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRoomListBinding.inflate(layoutInflater)

    var apply: BindingAdapter? = null

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
            apply = xrvRoom.roomListAdapter().apply {
                R.id.itemRoom.onClick {
                    val list = ArrayList<Office>()
                    list.add(getModel())
                    clickItem(getModel(), false, list)
                }
            }
            layoutRefresh.setOnRefreshListener {
                page = 1
                loadRoomList()
            }
            layoutRefresh.setOnLoadMoreListener {
                loadRoomList()
            }
        }
        checkUserRoom()
        loadRoomList()
    }

    private var page = 1
    private var type = 1
    private var list = ArrayList<Office>()
    private fun loadRoomList() {
        scopeNetLife {
            val roomList = roomList(type.toString(), page.toString())
            if (roomList != null) {
                if (page == 1) {
                    binding.layoutRefresh.finishRefresh()
                    list.clear()
                } else {
                    binding.layoutRefresh.finishLoadMore()
                }
                if (roomList.count == 10) {
                    page++
                }
                list.addAll(roomList.office)
                LogCat.e(list.toString())
                apply!!.models = list
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            search.onClick { start(MainSearchActivity::class.java) }
            ph.onClick { start(PHActivity::class.java) }
            layoutEmpty.root.onClick { loadRoomList() }
            ivCreateRoom.onClick { createRoom() }
        }
    }

    private fun createRoom() {
        // 创建之前检查是否已有创建的房间
        OkApi.put(
            VRApi.ROOM_CREATE_CHECK,
            null,
            object : WrapperCallBack() {
                override fun onResult(result: Wrapper) {
                    if (result.ok()) {
                        showCreateRoomDialog()
                    } else if (result.code == 30016) {
                        val voiceRoomBean = result.get(Office::class.java)
                        if (voiceRoomBean != null) {
                            onCreateExist(voiceRoomBean)
                        } else {
                            showCreateRoomDialog()
                        }
                    }
                }
            })
    }

    fun launchRoomActivity(
        roomId: String, roomIds: ArrayList<String>, position: Int, isCreate: Boolean
    ) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(
            roomId
        ) {
            IntentWrap.launchRoom(
                requireContext(),
                roomIds, position, isCreate
            )
        }
    }

    /**
     * 检查用户之前是否在某个房间内
     */
    private fun checkUserRoom() {
        if (MiniRoomManager.getInstance().isShowing) {
            // 如果有小窗口存在的情况下，不显示
            return
        }
        val params: Map<String, Any> = HashMap(2)
        OkApi.get(VRApi.USER_ROOM_CHECK, params, object : WrapperCallBack() {
            override fun onResult(result: Wrapper) {
                if (result.ok()) {
                    val voiceRoomBean = result.get(Office::class.java)
                    if (voiceRoomBean != null) {
                        // 说明已经在房间内了，那么给弹窗
                        confirmDialog = VRCenterDialog(requireActivity(), null)
                        confirmDialog!!.replaceContent(
                            "您正在直播的房间中\n是否返回？", getString(R.string.cancel),
                            { changeUserRoom() },
                            getString(R.string.confirm),
                            { jumpRoom(voiceRoomBean) }, null
                        )
                        confirmDialog!!.show()
                    }
                }
            }
        })
    }

    /**
     * 展示创建房间弹窗
     */
    private var mLauncher: ActivityResultLauncher<*>? = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result != null && result.data != null && result.data!!.data != null && mCreateRoomDialog != null
        ) {
            mCreateRoomDialog!!.setCoverUri(result.data!!.data)
        }
    }

    private fun showCreateRoomDialog() {
        mCreateRoomDialog = CreateRoomDialog(
            requireActivity(),
            mLauncher,
            RoomType.VOICE_ROOM,
            this@RoomListFragment
        )
        mCreateRoomDialog!!.show()
    }

    /**
     * 跳转到相应的房间
     *
     * @param voiceRoomBean
     */
    private fun jumpRoom(voiceRoomBean: Office) {
        IntentWrap.launchRoom(requireContext(), voiceRoomBean.roomid)
    }

    override fun onDestroy() {
        super.onDestroy()
        VoiceRoomProvider.provider().clear()
    }

    // 更改所属房间
    private fun changeUserRoom() {
        val params = OkParams().add("roomId", "").build()
        OkApi.get(VRApi.USER_ROOM_CHANGE, params, object : WrapperCallBack() {
            override fun onResult(result: Wrapper) {}
        })
    }

    override fun onCreateSuccess(voiceRoomBean: Office?) {
//        mAdapter.getData().add(0, voiceRoomBean)
//        mAdapter.notifyItemInserted(0)
        val arrayList = ArrayList<Office>()
        arrayList.add(voiceRoomBean!!)
        clickItem(voiceRoomBean, true, arrayList)
    }

    override fun onCreateExist(voiceRoomBean: Office?) {
        confirmDialog = VRCenterDialog(requireActivity(), null)
        confirmDialog!!.replaceContent(
            getString(R.string.text_you_have_created_room),
            getString(R.string.cancel),
            null,
            getString(R.string.confirm),
            { jumpRoom(voiceRoomBean!!) },
            null
        )
        confirmDialog!!.show()
    }

    private fun clickItem(
        item: Office,
        isCreate: Boolean,
        voiceRoomBeans: ArrayList<Office>
    ) {
        if (TextUtils.equals(item.uid.toString(), UserManager.get()!!.userId)) {
            val list: ArrayList<String> = ArrayList()
            list.add(item.roomid)
            launchRoomActivity(item.roomid, list, 0, isCreate)
            LogCat.e("进入自己创建的房间====${item.roomid}")
        } else if (item.password_type == 1) {
            inputPasswordDialog = InputPasswordDialog(
                requireContext(),
                false,
                object : InputPasswordDialog.OnClickListener {
                    override fun clickCancel() {}
                    override fun clickConfirm(password: String) {
                        if (TextUtils.isEmpty(password)) {
                            return
                        }
                        if (password.length < 4) {
                            ToastUtils.s(
                                requireContext(),
                                requireContext().getString(R.string.text_please_input_four_number)
                            )
                            return
                        }
                        scopeNetLife {
                            val roomDetail = roomDetail(item.id.toString())
                            if (roomDetail != null) {
                                inputPasswordDialog!!.dismiss()
                                val list: ArrayList<String> = ArrayList()
                                list.add(item.roomid)
                                launchRoomActivity(item.roomid, list, 0, false)
                            }
                        }
                    }
                })
            inputPasswordDialog!!.show()
            LogCat.e("进入密码房间====${item.roomid}")
        } else {
            val list = ArrayList<String>()
            for (voiceRoomBean in voiceRoomBeans) {
                if (voiceRoomBean.uid.toString() != UserManager.get()!!.userId
                    && voiceRoomBean.password_type == 1
                ) {
                    // 过滤掉上锁的房间和自己创建的房间
                    list.add(voiceRoomBean.roomid)
                }
            }
            var p = list.indexOf(item.roomid)
            if (p < 0) p = 0
            LogCat.e("进入非创建无密码房间====${item.roomid}")
            launchRoomActivity(item.roomid, list, p, false)
        }
    }
}