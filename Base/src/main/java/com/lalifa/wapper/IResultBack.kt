package com.lalifa.wapper

/**
 * 统一返回接口
 *
 * @param <T>
</T> */
interface IResultBack<T> {
    fun onResult(t: T?)
}