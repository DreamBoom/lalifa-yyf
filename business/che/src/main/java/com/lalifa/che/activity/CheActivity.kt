package com.lalifa.che.activity

import android.graphics.Color
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.drake.brv.PageRefreshLayout
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseListFragment
import com.lalifa.che.R
import com.lalifa.che.adapter.cheList
import com.lalifa.che.api.Dynamic
import com.lalifa.che.api.cheList
import com.lalifa.che.api.dzChe
import com.lalifa.che.databinding.ActivityCheBinding
import com.lalifa.extension.*

class CheActivity : BaseActivity<ActivityCheBinding>() {

    override fun getViewBinding() = ActivityCheBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                supportFragmentManager,
                arrayListOf("最新", "热门", "关注")
            ) {
                add(CheFragment(0))
                add(CheFragment(1))
                add(CheFragment(2))
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(this@CheActivity, R.color.textColor2)
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
        }
    }

    override fun onClick() {
        super.onClick()
        binding.notice.onClick { start(NoticeActivity::class.java) }
        binding.addChe.onClick { start(AddCheActivity::class.java) }
    }
}

class CheFragment(val type:Int) : BaseListFragment() {
    override fun initView() {
        super.initView()
        binding.recyclerView.cheList().apply {
            R.id.item_info.onClick {
                start(CheInfoActivity::class.java){
                    putExtra("id",getModel<Dynamic>().id)
                }
            }
            R.id.more.onClick {
                val bean = getModel<Dynamic>()
            }
            R.id.like.onClick {
                val bean = getModel<Dynamic>()
                scopeNetLife {
                    dzChe(bean.id)
                    binding.refreshLayout.refresh()
                }
            }
            R.id.share.onClick {
                val bean = getModel<Dynamic>()

            }
            R.id.pl.onClick {
                start(CheInfoActivity::class.java){
                    putExtra("id",getModel<Dynamic>().id)
                }
            }
        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            val dynamic = cheList(type, index)!!.dynamic
            addData(dynamic)
        }
    }
}