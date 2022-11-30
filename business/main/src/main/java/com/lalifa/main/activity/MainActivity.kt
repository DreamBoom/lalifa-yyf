package com.lalifa.main.activity

import android.view.KeyEvent
import android.widget.Toast
import cn.rongcloud.config.router.RouterPath
import cn.rongcloud.roomkit.ui.other.fragment.RoomListFragment
import cn.rongcloud.voice.roomlist.VoiceRoomListFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseApplication
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.extension.start
import com.lalifa.main.activity.che.CheActivity
import com.lalifa.main.databinding.ActivityHomeBinding
import com.lalifa.main.fragment.MainFragment
import com.lalifa.main.fragment.MeFragment
import com.lalifa.main.fragment.MessageFragment
@Route(path = RouterPath.ROUTER_MAIN)
class MainActivity : BaseActivity<ActivityHomeBinding>() {
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
    }

    override fun onClick() {
        super.onClick()
        binding.btnChe.onClick {
            start(CheActivity::class.java)
        }
    }

    override fun getViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isCanExit()) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(
                        applicationContext,
                        "再按一次退出",
                        Toast.LENGTH_SHORT
                    ).show()
                    exitTime = System.currentTimeMillis()
                } else {
                    BaseApplication.get().exit()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}