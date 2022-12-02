package com.lalifa.main.activity

import cn.rongcloud.config.router.RouterPath
import cn.rongcloud.voice.room.RoomListFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.lalifa.base.BaseActivity
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
    }

    override fun onClick() {
        super.onClick()
        binding.btnChe.onClick {
            start(CheActivity::class.java)
        }
    }

    override fun getViewBinding() = ActivityHomeBinding.inflate(layoutInflater)
}