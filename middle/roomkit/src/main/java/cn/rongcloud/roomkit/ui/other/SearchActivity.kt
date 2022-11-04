package cn.rongcloud.roomkit.ui.other

import android.graphics.Color
import androidx.core.content.ContextCompat
import cn.rongcloud.roomkit.databinding.ActivitySearchBinding
import cn.rongcloud.roomkit.ui.other.fragment.SearchPeopleFragment
import cn.rongcloud.roomkit.ui.other.fragment.SearchRoomFragment
import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override fun getViewBinding() = ActivitySearchBinding.inflate(layoutInflater)

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
                tabLayout.textSelectColor = ContextCompat.getColor(this@SearchActivity, com.lalifa.base.R.color.white)
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
        }
    }
}