package com.lalifa.api

import com.drake.net.Net
import com.lalifa.ext.Config
import com.lalifa.utils.SPUtil
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

/**
 * 演示如何自动刷新token令牌
 */
class RefreshTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request) // 如果token失效

        return synchronized(RefreshTokenInterceptor::class.java) {
            val isLogin = SPUtil.getBoolean(Config.IS_LOGIN, false)
            if (response.code == 401 && isLogin && !request.url.pathSegments.contains("token")) {
                chain.proceed(request)
            } else {
                response
            }
        }
    }
}
