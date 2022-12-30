package com.lalifa.main.activity

import android.graphics.Color
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseActivity
import com.lalifa.extension.*
import com.lalifa.main.activity.friend.FriendList
import com.lalifa.main.activity.room.SearchRoomList
import com.lalifa.main.databinding.ActivityMainSearchBinding
import com.lalifa.main.fragment.SearchPeopleFragment
import com.lalifa.main.fragment.SearchRoomFragment

class MainSearchActivity : BaseActivity<ActivityMainSearchBinding>() {
    override fun getViewBinding() = ActivityMainSearchBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("房间", "用户")
            ) {
                add(SearchRoomFragment())
                add(SearchPeopleFragment())
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    this@MainSearchActivity,
                    com.lalifa.base.R.color.textColor2
                )
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }
            search.onClick {
                if(TextUtils.isEmpty(etSearch.text())){
                    toast("请输入搜索ID或名称")
                    return@onClick
                }
                if(tabLayout.currentTab == 0){
                    start(SearchRoomList::class.java){
                        putExtra("name",etSearch.text())
                    }
                }else{
                    start(FriendList::class.java){
                        putExtra("name",etSearch.text())
                    }
                }
            }
        }
    }
}