package com.lalifa.main.activity

import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.databinding.ActivityMsgHdBinding

class MsgHd : BaseTitleActivity<ActivityMsgHdBinding>() {
    override fun title()="互动消息"
    override fun getViewBinding()= ActivityMsgHdBinding.inflate(layoutInflater)

    override fun initView() {

    }
}