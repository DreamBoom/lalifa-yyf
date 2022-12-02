package com.lalifa.main.wxapi

import android.app.Activity
import android.os.Bundle
import com.drake.logcat.LogCat.i
import com.lalifa.ext.Config
import com.lalifa.extension.toast
import com.lalifa.main.R
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp

class WXPayEntryActivity : Activity(), IWXAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wx_pay_entry)
        val api = WXAPIFactory.createWXAPI(this, Config.WXKey, false)
        api.handleIntent(intent, this)
    }

    override fun onReq(baseReq: BaseReq) {
        i("onReq" + baseReq.type)
    }

    override fun onResp(baseResp: BaseResp) {
        when (baseResp.type) {
            ConstantsAPI.COMMAND_PAY_BY_WX -> {
                i("baseResp.errCode====" + baseResp.errCode)
                when (baseResp.errCode) {
                    0 -> toast("支付成功")
                    -1 -> toast("支付异常")
                    -2 -> toast("支付取消")
                }
                finish()
            }
        }
    }
}