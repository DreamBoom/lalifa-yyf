package com.lalifa.main.activity

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityModelBinding

class ModelActivity : BaseTitleActivity<ActivityModelBinding>() {
    override fun title() = "青少年模式"
    override fun getViewBinding() = ActivityModelBinding.inflate(layoutInflater)

    override fun initView() {

    }

}