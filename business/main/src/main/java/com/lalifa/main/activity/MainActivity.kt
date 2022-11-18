package com.lalifa.main.activity

import cn.rongcloud.voice.roomlist.VoiceRoomListFragment
import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.extension.start
import com.lalifa.main.databinding.ActivityHomeBinding
import com.lalifa.main.fragment.MainFragment
import com.lalifa.main.fragment.MeFragment
import com.lalifa.main.fragment.MessageFragment

class MainActivity : BaseActivity<ActivityHomeBinding>() {
    override fun initView() {
        binding.apply {
            vp.offscreenPageLimit = 4
            vp.fragmentAdapter(supportFragmentManager) {
                add(MainFragment())
                add(VoiceRoomListFragment())
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