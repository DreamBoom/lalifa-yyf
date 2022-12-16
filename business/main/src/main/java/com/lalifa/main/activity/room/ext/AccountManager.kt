package com.lalifa.main.activity.room.ext

import com.drake.logcat.LogCat
import com.lalifa.ext.Account

object AccountManager {
    private val accounts: MutableList<Account> = ArrayList(500)
    private val seats: ArrayList<Account> = ArrayList(10)

    @JvmStatic
    var currentId //当前账号
            : String? = null
        private set

    fun getAccounts(): List<Account> {
        return accounts
    }

    fun setAccount(a: Account?, mine: Boolean) {
        if (null == a) return
        if (mine) {
            currentId = a.userId
        }
        val find = accounts.find { it.userId == a.userId }
        if (null == find) {
            accounts.add(a)
        }
    }

    fun getAccount(userId: String?): Account? {
        val size = accounts.size
        var result: Account? = null
        for (i in 0 until size) {
            val acc = accounts[i]
            if (acc.userId == userId) {
                result = acc
                break
            }
        }
        return result
    }

    @JvmStatic
    fun getAccountName(userId: String?): String {
        val accout = getAccount(userId)
        return if (null == accout) userId!! else accout.userName
    }

    fun getSeats(): ArrayList<Account> {
        return seats
    }

    fun removeSeats() {
        seats.clear()
    }

    fun setSeat(a: Account) {
        seats.add(a)
    }
}