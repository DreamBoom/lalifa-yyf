package com.lalifa.main.activity.me

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.*
import com.lalifa.main.api.realName
import com.lalifa.main.api.upload
import com.lalifa.main.databinding.ActivityRealNameBinding

class RealName : BaseTitleActivity<ActivityRealNameBinding>() {
    override fun getViewBinding() = ActivityRealNameBinding.inflate(layoutInflater)
    var zm = ""
    var fm = ""
    override fun title() = "实名认证"
    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            zmImg.onClick {
                imagePick(maxCount = 1) {
                    scopeNetLife {
                        val upload = upload(it[0].path)
                        zm = upload.url
                        zmImg.load(it[0].path)
                    }
                }
            }
            fmImg.onClick {
                imagePick(maxCount = 1) {
                    scopeNetLife {
                        val upload = upload(it[0].path)
                        fm = upload.url
                        fmImg.load(it[0].path)
                    }
                }
            }
            cancelBut.onClick { finish() }
            commitBut.onClick {
                if(zm.isEmpty()||fm.isEmpty()){
                    toast("请上传身份证")
                    return@onClick
                }
                if(nameEdit.isEmp()){
                    toast("请输入姓名")
                    return@onClick
                }
                if(phoneEdit.isEmp()){
                    toast("请输入手机号")
                    return@onClick
                }
                if(IDCardEdit.isEmp()){
                    toast("请输入身份证号")
                    return@onClick
                }
                scopeNetLife {
                    val realName = realName(nameEdit.text(), phoneEdit.text(),
                            IDCardEdit.text(), zm, fm)
                    finish()
                }
            }
        }
    }
}