package com.lalifa.api

import android.text.TextUtils
import com.drake.logcat.LogCat
import com.drake.net.convert.NetConverter
import com.drake.net.exception.ConvertException
import com.drake.net.exception.RequestParamsException
import com.drake.net.exception.ServerResponseException
import com.google.gson.GsonBuilder
import com.lalifa.api.NetResponseException
import com.lalifa.utils.KToast
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

class JsonHttpConverter : NetConverter {
    val gson = GsonBuilder().serializeNulls().create()
    override fun <R> onConvert(succeed: Type, response: Response): R? {
        try {
            return NetConverter.onConvert<R>(succeed, response)
        } catch (e: ConvertException) {
            val code = response.code
            when {
                code in 200..299 -> { // 请求成功
                    val bodyString = response.body?.string() ?: return null
                    return try {
                        val json = JSONObject(bodyString) // 获取JSON中后端定义的错误码和错误信息
                        when (json.getInt("code")) {
                            200 -> {
                                //数据请求成功
                                bodyString.parseBody<R>(succeed)
                            }
                            else -> {
                                if(!TextUtils.isEmpty(json.getString("msg"))){
                                    KToast.show( json.getString("msg"))
                                }
                                //失败
                                throw NetResponseException(
                                    json.getInt("code"),
                                    response,
                                    "请求异常"
                                )
                            }
                        }
                    } catch (e: JSONException) { // 固定格式JSON分析失败直接解析JSON
                        bodyString.parseBody<R>(succeed)
                    }
                }
                code in 400..499 -> throw RequestParamsException(
                    response,
                    code.toString()
                ) // 请求参数错误
                code >= 500 -> throw ServerResponseException(response, code.toString()) // 服务器异常错误
                else -> throw ConvertException(response)
            }
        }
    }

    /**
     * 反序列化JSON
     *
     * @param succeed JSON对象的类型
     * @receiver 原始字符串
     */
    fun <R> String.parseBody(succeed: Type): R? {
        return gson.fromJson(this, succeed)
    }
}