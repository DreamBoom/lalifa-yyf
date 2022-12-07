package com.lalifa.api

import android.content.Context
import android.text.TextUtils
import com.drake.channel.sendTag
import com.drake.logcat.LogCat
import com.drake.net.NetConfig
import com.drake.net.convert.NetConverter
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.interceptor.RequestInterceptor
import com.drake.net.interfaces.NetErrorHandler
import com.drake.net.okhttp.*
import com.drake.net.request.BaseRequest
import com.drake.net.utils.TipUtils
import com.drake.tooltip.dialog.BubbleDialog
import com.lalifa.base.BaseApplication
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.pk
import java.util.concurrent.TimeUnit

/**
 * 网络请求
 * @ClassName NetHttp
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/5/17 10:04
 * @Des
 */
object NetHttp {
    /**
     *
     * @param context Context
     * @param host String  域名
     * @param converter NetConverter  数据转换器
     * @param connectTime Long  连接超时时间
     * @param block [@kotlin.ExtensionFunctionType] Function1<BaseRequest, Unit>  请求拦截器
     * @param error Function1<[@kotlin.ParameterName] NetResponseException, Unit>  错误处理回调
     */
    fun init(
        context: Context, host: String, converter: NetConverter,
        connectTime: Long = 15000, block: BaseRequest.() -> Unit,
        error: (e: NetResponseException) -> Unit
    ) {
        NetConfig.initialize(host, context) {
            //超时时间
            connectTimeout(connectTime, TimeUnit.MILLISECONDS)
            readTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)
            setDebug(true) // 作用域发生异常是否打印
            //初始化全局加载框
            setDialogFactory {
                BubbleDialog(context, "")
            }
            setConverter(converter) // 转换器
            //配置请求拦截器, 适用于添加全局请求头/参数
            setRequestInterceptor(object : RequestInterceptor {
                override fun interceptor(request: BaseRequest) {
                    request.apply(block)
                }
            })
            trustSSLCertificate() // 信任所有证书
            addInterceptor(LogRecordInterceptor(true))
            //错误处理
            setErrorHandler(object : NetErrorHandler {
                override fun onError(e: Throwable) {
                    if (e is NetResponseException) {
                        error.invoke(e)
                    } else {
                        super.onError(e)
                    }
                }
            })
        }
    }
}