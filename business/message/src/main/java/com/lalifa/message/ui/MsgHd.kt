package com.lalifa.message.ui

import com.lalifa.base.BaseTitleActivity
import com.lalifa.message.databinding.ActivityMsgHdBinding

class MsgHd : BaseTitleActivity<ActivityMsgHdBinding>() {
    override fun title()="互动消息"
    override fun getViewBinding()= ActivityMsgHdBinding.inflate(layoutInflater)

    override fun initView() {

    }
}