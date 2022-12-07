package com.lalifa.api

import android.content.Context
import android.text.TextUtils
import com.drake.channel.sendTag
import com.drake.net.utils.TipUtils
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.pk

class InitNet {
    companion object {
        /**
         * 初始化网络请求，全局配置
         */
        fun initNetHttp(context: Context) {
            NetHttp.init(context, Config.HOST, JsonHttpConverter(),
                block = {
                    //全局请求头/参数
                    //addHeader("Content-Type","application/json; charset=utf-8")
                    if(UserManager.get()!=null&&!TextUtils.isEmpty(UserManager.get()!!.token)){
                        addHeader("token", UserManager.get()!!.token.pk(""))
                    }
                }, error = {
                    TipUtils.toast(it.message)
                    when (it.code) {
                        102 -> {
                            //token失效
                            sendTag("HttpLogout")
                        }
                    }
                })
        }
    }
}