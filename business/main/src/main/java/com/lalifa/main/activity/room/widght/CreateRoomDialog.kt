package com.lalifa.main.activity.room.widght

import android.app.Activity
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.toast
import com.google.android.material.imageview.ShapeableImageView
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.api.RoomBgBean
import com.lalifa.main.api.createRoom
import com.lalifa.main.api.getRoomBg
import com.lalifa.main.api.upload
import com.lalifa.main.fragment.adapter.roomBgAdapter
import com.lalifa.utils.UiUtils
import com.lalifa.widget.ChineseLengthFilter
import com.lalifa.widget.dialog.BottomDialog
import com.lalifa.widget.loading.LoadTag
import io.rong.imkit.picture.tools.ToastUtils
import org.angmarch.views.NiceSpinner


/**
 * @author gyn
 * @date 2021/9/16
 */
class CreateRoomDialog(
    val roomId: String?,
    val activity: Activity?,
    private val mCreateRoomCallBack: CreateRoomCallBack
) : BottomDialog(activity as FragmentActivity?) {
    //封面
    private var mImUrl: String = ""
    //背景
    private var mBgUrl: String = ""
    private var mImView: ShapeableImageView? = null
    private var mRoomNameEditText: EditText? = null
    private var mLoading: LoadTag? = null
    private var background_id = ""
    private var background = ""
    var type = 1
    var mPattern = "微信"
    var mRank = "青铜"
    var mLabel = "颜值担当"

    init {
        setContentView(R.layout.dialog_create_room, -1, UiUtils.dp2px(590f))
        initView()
    }


    private fun initView() {
        // 关闭
        contentView.findViewById<View>(R.id.iv_fold).setOnClickListener { v: View? -> dismiss() }
        // 房间封面
        mImView = contentView.findViewById(R.id.iv_image)
        // 房间背景
        mImView!!.onClick {
            activity!!.imagePick(maxCount = 1) {
                scopeNet {
                    val upload = upload(it[0].path)
                    if (null != upload) {
                        mImUrl = upload.fullurl
                        mImView!!.load(mImUrl!!, R.drawable.bg_create_room)
                    }
                }
            }
        }
        val rbGame = contentView.findViewById<RadioButton>(R.id.rb_game)
        val rbHappy = contentView.findViewById<RadioButton>(R.id.rb_happy)
        val group = contentView.findViewById<Group>(R.id.group)
        val pattern = contentView.findViewById<NiceSpinner>(R.id.pattern)
        val rank = contentView.findViewById<NiceSpinner>(R.id.rank)
        val label = contentView.findViewById<NiceSpinner>(R.id.label)
        val listPattern = activity!!.resources.getStringArray(R.array.pattern_list)
        val listRank = activity.resources.getStringArray(R.array.rank_list)
        val listLabel = activity.resources.getStringArray(R.array.label_list)
        pattern.attachDataSource(listPattern.toList())
        rank.attachDataSource(listRank.toList())
        label.attachDataSource(listLabel.toList())
        pattern.addOnItemClickListener { _, _, position, _ ->
            mPattern = listPattern[position]
        }
        pattern.addOnItemClickListener { _, _, position, _ ->
            mRank = listRank[position]
        }
        pattern.addOnItemClickListener { _, _, position, _ ->
            mLabel = listLabel[position]
        }
        rbGame.onClick {
            group.visible()
            type = 1
        }
        rbHappy.onClick {
            group.gone()
            type = 2
        }
        // 创建房间
        contentView.findViewById<View>(R.id.btn_create_room)
            .setOnClickListener { v: View? -> preCreateRoom() }
        mRoomNameEditText = contentView.findViewById(R.id.et_room_name)
        mRoomNameEditText!!.filters = arrayOf<InputFilter>(ChineseLengthFilter(20))
        mLoading = LoadTag(mActivity, "加载中")
        val bgList = contentView.findViewById<RecyclerView>(R.id.bgList)
        apply = bgList.roomBgAdapter().apply {
            R.id.itemRoomBg.onClick {
                data.forEach {
                    it.check = false
                }
                data[layoutPosition].check = true
                background_id = data[layoutPosition].id.toString()
                background = data[layoutPosition].image
                apply!!.models = data
            }
        }
        getBg()
    }

    var apply: BindingAdapter?=null
    var data :MutableList<RoomBgBean> = ArrayList()
    private fun getBg(){
        mLoading!!.show()
        scopeNet {
            val roomBg = getRoomBg()
            if(null!=roomBg&&roomBg.isNotEmpty()){
                data = roomBg
                apply!!.models = data
                mLoading!!.dismiss()
            } else{
                mLoading!!.dismiss()
            }
        }
    }

    /**
     * 创建房间前逻辑判断
     */
    private fun preCreateRoom() {
        // 房间名检测
        val roomName =
            if (mRoomNameEditText!!.text == null) "" else mRoomNameEditText!!.text.toString()
        if (TextUtils.isEmpty(roomName)) {
            ToastUtils.s(mActivity, "请输入房间名称")
            return
        }
        uploadThemePic(roomName)
    }

    private fun uploadThemePic(roomName: String) {
        // 选择本地图片后，先上传本地图片
        if (!TextUtils.isEmpty(mImUrl)) {
            create(roomName, mImUrl!!)
        } else {
            toast("请上传背景图")
        }
    }

    /**
     * 创建房间
     *
     * @param roomName
     * @param themeUrl
     */
    private fun create(roomName: String, mImUrl: String) {
        mLoading!!.show()
        scopeNet {
            val createRoom =
                createRoom(
                    roomId!!.noEN(), type.toString(),
                    mLabel, mRank, roomName, mImUrl, mPattern
                )
            if (null != createRoom) {
                dismiss()
                mLoading!!.dismiss()
                mCreateRoomCallBack.onCreateSuccess(createRoom.Chatroom_id)
            } else {
                mLoading!!.dismiss()
            }
        }
    }

    interface CreateRoomCallBack {
        fun onCreateSuccess(roomId: String?)
        fun onCreateExist(roomId: String?)
    }
}