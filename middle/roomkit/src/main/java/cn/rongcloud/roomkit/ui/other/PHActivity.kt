package cn.rongcloud.roomkit.ui.other

import android.graphics.Color
import androidx.core.content.ContextCompat
import cn.rongcloud.roomkit.databinding.ActivityPhactivityBinding
import cn.rongcloud.roomkit.ui.other.fragment.PhFragment

import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener

class PHActivity : BaseActivity<ActivityPhactivityBinding>() {
    override fun getViewBinding()=ActivityPhactivityBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("财富榜", "魅力榜", "在线榜")
            ) {
                add(PhFragment(1))
                add(PhFragment(2))
                add(PhFragment(3))
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    this@PHActivity,
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
        binding.back.onClick { finish() }
    }

}