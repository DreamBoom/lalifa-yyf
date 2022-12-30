package com.lalifa.main.activity.me

import android.net.Uri
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.ext.showInputDialog
import com.lalifa.extension.*
import com.lalifa.main.activity.room.ext.UserManager
import com.lalifa.main.api.changeUserInfo
import com.lalifa.main.api.upload
import com.lalifa.main.api.userInfo
import com.lalifa.main.databinding.ActivityEditMyInfoBinding
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imlib.model.UserInfo

class EditMyInfoActivity : BaseTitleActivity<ActivityEditMyInfoBinding>() {
    override fun title() = "编辑个人资料"
    override fun getViewBinding() = ActivityEditMyInfoBinding.inflate(layoutInflater)

    override fun initView() {
        getInfo()
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            cHeader.onClick {
                imagePick(maxCount = 1) {
                    scopeNetLife {
                        val path = upload(it[0].path)
                        mAvatar = path.url
                        binding.header.load(upload(it[0].path).fullurl)
                        upInfo()
                    }
                }
            }
            cName.onClick {
                showInputDialog(hint = "请输入昵称", l = 12) {
                    mName = it
                    name.text = it
                    upInfo()
                }
            }
            cBio.onClick {
                showInputDialog(hint = "请输入个性签名", l = 120) {
                    mBio = it
                    bio.text = it
                    upInfo()
                }
            }
            cGender.onClick {
                sexPickView {
                    sex.text = it
                    mGender = if (it == "男") "0" else "1"
                }
            }
            cAge.onClick {
                timePickView {
                    mBirthday = it
                    age.text = it
                    mAge = getAge(it).toString()
                    xz.text = getConstellation(it)
                    upInfo()
                }
            }
        }
    }

    var mName = ""
    var mGender = ""
    var mBirthday = ""
    var mAge = ""
    var mBio = ""
    var mAvatar = ""
    private fun getInfo() {
        scopeNetLife {
            val bean = userInfo()
            binding.apply {
                header.load(Config.FILE_PATH + bean!!.avatar)
                name.text = bean.userName
                bio.text = bean.bio
                sex.text = if (bean.gender == 0) "男" else "女"
                age.text = bean.age.toString()
                mId.text = bean.id.toString()
                xz.text = getConstellation(bean.birthday)
                mName = bean.userName
                mGender = bean.gender.toString()
                mBirthday = bean.birthday
                mAge = bean.age.toString()
                mBio = bean.bio
                mAvatar = bean.avatar

            }
        }
    }

    private fun upInfo() {
        RongUserInfoManager.getInstance().refreshUserInfoCache(
            UserInfo(UserManager.get()!!.imToken,mName,
                Uri.parse(Config.FILE_PATH+mAvatar))
        )
        scopeNetLife {
            changeUserInfo(mName, mGender, mBirthday, mAge, mBio, mAvatar)
        }
    }
}