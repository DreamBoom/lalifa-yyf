package com.lalifa.api

import com.drake.net.exception.HttpResponseException
import okhttp3.Response
import java.lang.Exception

/**
 *
 * @ClassName NetResponseException
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/6 14:46
 * @Des
 */
class NetResponseException(
    var code: Int,
    response: Response,
    msg: String? = null,
    cause: Throwable? = null
) : HttpResponseException(response, msg, cause)