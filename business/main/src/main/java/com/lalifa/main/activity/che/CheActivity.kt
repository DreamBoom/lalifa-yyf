package com.lalifa.main.activity.che

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseListFragment
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.activity.NoticeActivity
import com.lalifa.main.fragment.adapter.cheList
import com.lalifa.main.api.Dynamic
import com.lalifa.main.api.cheList
import com.lalifa.main.api.dzChe
import com.lalifa.main.databinding.ActivityCheBinding

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