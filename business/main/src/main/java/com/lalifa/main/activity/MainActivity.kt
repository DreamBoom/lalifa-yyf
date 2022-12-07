package com.lalifa.main.activity

import cn.jpush.android.api.JPushInterface
import com.drake.logcat.LogCat
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.*
import com.lalifa.main.activity.che.CheActivity
import com.lalifa.main.activity.room.ext.AccountManager
import com.lalifa.main.databinding.ActivityHomeBinding
import com.lalifa.main.fragment.MainFragment
import com.lalifa.main.fragment.MeFragment
import com.lalifa.main.fragment.MessageFragment
import com.lalifa.main.fragment.RoomListFragment
import com.lalifa.utils.SPUtil
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus

class MainActivity : BaseActivity<ActivityHomeBinding>() {
    override fun isCanExit() = true
    override fun initView() {
        binding.apply {
            vp.offscreenPageLimit = 4
            vp.fragmentAdapter(supportFragmentManager) {
                add(MainFragment())
                add(RoomListFragment())
                add(MessageFragment())
                add(MeFragment())
            }.pageChangedListener {

            }
            vp.currentItem = 0
            tabGroup.setCurPosition(0)

            tabGroup.setViewPager(vp)
        }
        val initIm = getIntentBoolean("initIm", false)
        if(!initIm){
            toInitIm()
        }
    }

    private fun toInitIm() {
        RongIM.connect(UserManager.get()!!.imToken, object : RongIMClient.ConnectCallback() {
            override fun onSuccess(t: String) {

            }

            override fun onError(e: RongIMClient.ConnectionErrorCode?) {
                toast("Im链接异常：${e.toString()}")
            }

            override fun onDatabaseOpened(code: RongIMClient.DatabaseOpenStatus?) {

            }
        })
    }

    override fun onClick() {
        super.onClick()
        binding.btnChe.onClick {
            start(CheActivity::class.java)
        }
    }

    override fun getViewBinding() = ActivityHomeBinding.inflate(layoutInflater)
}