package com.lalifa.main.activity.room

import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.kit.utils.KToast
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.room.ext.NotificationService
import com.lalifa.main.activity.room.ext.VoiceRoomApi
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityRoomSettingBinding
import com.lalifa.main.ext.inputPasswordDialog
import com.lalifa.main.fragment.adapter.roomBgAdapter
import com.lalifa.widget.loading.LoadTag
import com.lalifa.yyf.ext.showInputDialog
import com.lalifa.yyf.ext.showTipDialog

class RoomSettingActivity : BaseActivity<ActivityRoomSettingBinding>() {
    override fun getViewBinding() = ActivityRoomSettingBinding.inflate(layoutInflater)
    private var id = ""
    private var mTitle = ""
    private var image = ""
    private var background = ""
    private var background_id = ""
    //0：未加密  1：加密
    private var password_type = ""
    private var password = ""
    private var notice = ""

    var loadTag: LoadTag?=null
    override fun initView() {
        val room = getIntentSerializable<RoomDetailBean>("room")
        loadTag = LoadTag(
            this, this.getString(
                R.string.text_loading
            )
        )
        id = room!!.id.toString()
        mTitle = room.title
        image = room.image
        background = room.background
        background_id = room.background_id
        password_type = room.password_type.toString()
        password = room.password
        notice = room.notice
        binding.apply {
            roomHeader.load(Config.FILE_PATH + room.image)
            tvName.text = mTitle
            roomGg.text = notice
            tvPass.text = password
            if (password_type == "0") open.isChecked = true else noOpen.isChecked = true
            apply = binding.bgList.roomBgAdapter().apply {
                R.id.itemRoomBg.onClick {
                    data.forEach {
                        it.check = false
                    }
                    data[layoutPosition].check = true
                    background_id = data[layoutPosition].id.toString()
                    background = data[layoutPosition].image
                    apply!!.models = data
                    up()
                }
            }
        }
        getBg()
    }

    var apply:BindingAdapter?=null
    var data :MutableList<RoomBgBean> = ArrayList()
    private fun getBg(){
        scopeNetLife {
            val roomBg = getRoomBg()
            if(null!=roomBg&&roomBg.isNotEmpty()){
                data = roomBg
                apply!!.models = data
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            close.onClick {
                val intent = Intent()
                intent.putExtra("isChange",isChange)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            roomHeader.onClick { changeImg() }
            llName.onClick {
                showInputDialog(l = 24) {
                    mTitle = it
                    tvName.text = it
                    up()
                }
            }
            open.onClick {
                llPass.visible()
                password_type = "0"
                up()
            }
            noOpen.onClick {
                password_type = "1"
                llPass.gone()
                if (!TextUtils.isEmpty(password)) {
                    up()
                }
            }
            llPass.onClick {
                inputPasswordDialog {
                    password = it
                    up()
                }
            }
            roomGg.onClick {
                showInputDialog(l = 120) {
                    notice = it
                    roomGg.text = it
                    up()
                }
            }
        }
    }

    private fun changeImg() {
        loadTag!!.show()
        imagePick(maxCount = 1) {
            scopeNetLife {
                val upload = upload(it[0].path)
                if (null != upload) {
                    binding.roomHeader.load(it[0].path)
                    image = upload.url
                    up()
                } else {
                    toast("修改失败，请重新尝试")
                }
            }
        }
    }

    var isChange = false
    private fun up() {
       loadTag!!.show()
        scopeNetLife {
            val editRoom = editRoom(id, mTitle, image, background_id,background,
                password_type, password, notice)
            if (null != editRoom) {
                isChange = true
                loadTag!!.dismiss()
                toast("更新成功")
            }else{
                loadTag!!.dismiss()
            }
        }
    }

    //点击返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            val intent = Intent()
            intent.putExtra("isChange",isChange)
            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}