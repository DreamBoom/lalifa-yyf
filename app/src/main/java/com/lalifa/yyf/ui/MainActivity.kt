package com.lalifa.yyf.ui

import android.view.View
import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.yyf.databinding.ActivityMainBinding


/**
 * 主界面
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    override fun isCanExit()= true
    private lateinit var tabs: Array<View>
    override fun initView() {
        binding.apply {
            tabs = arrayOf(tabMap, tabChat, tabExplore, tabUser, tabVideo)
            viewPager.fragmentAdapter(supportFragmentManager) {
//                add(FragmentMap())
//                add(ChatFragment())
//                add(ExploreFragment())
//                add(StoryFragment())
//                add(LightFragment())
            }.pageChangedListener {
                tabs.forEachIndexed { index, view ->
                    view.isSelected = it == index
                }
            }
            viewPager.currentItem = 0
            tabs[0].isSelected = true
        }
//        start(ApplyFindFriendsActivity::class.java)
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            tabs.forEachIndexed { index, view ->
                view.onClick {
                    viewPager.currentItem = index
                }
            }
        }
    }



}