package com.lalifa.main.activity

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityEditMyInfoBinding

class EditMyInfoActivity : BaseTitleActivity<ActivityEditMyInfoBinding>() {
    override fun title() = "编辑个人资料"
    override fun getViewBinding() = ActivityEditMyInfoBinding.inflate(layoutInflater)

    override fun initView() {

    }

}