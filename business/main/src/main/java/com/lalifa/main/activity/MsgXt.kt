package com.lalifa.main.activity

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityMsgXtBinding

class MsgXt : BaseTitleActivity<ActivityMsgXtBinding>() {
    override fun title()="系统消息"
    override fun getViewBinding()= ActivityMsgXtBinding.inflate(layoutInflater)

    override fun initView() {

    }
}