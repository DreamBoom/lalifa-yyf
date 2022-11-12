package com.lalifa.main.activity

import android.graphics.Color
import com.drake.brv.PageRefreshLayout
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseListActivity
import com.lalifa.che.activity.CheInfoActivity
import com.lalifa.che.api.dzChe
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.adapter.cheList
import com.lalifa.main.api.Dynamic
import com.lalifa.main.api.delDynamic
import com.lalifa.main.api.release
import com.lalifa.yyf.ext.showTipDialog

class MyChe : BaseListActivity() {
    override fun title() = "我的发布"
    override fun iniView() {
        recyclerView.cheList().apply {
            R.id.item_info.onClick {
                start(CheInfoActivity::class.java) {
                    putExtra("id", getModel<Dynamic>().id)
                }
            }
            R.id.delete.onClick {
                val bean = getModel<Dynamic>()
                showTipDialog(content = "确定删除该动态？",sureText = "确定"){
                    scopeNetLife {
                        delDynamic(bean.id.toString())
                        refreshLayout.refresh()
                    }
                }
            }
            R.id.like.onClick {
                val bean = getModel<Dynamic>()
                scopeNetLife {
                    dzChe(bean.id)
                    refreshLayout.refresh()
                }
            }
            R.id.share.onClick {
                val bean = getModel<Dynamic>()

            }
            R.id.pl.onClick {
                start(CheInfoActivity::class.java) {
                    putExtra("id", getModel<Dynamic>().id)
                }
            }
        }
        refreshLayout.apply {
            setBackgroundColor(Color.parseColor("#F7F7F7"))
            autoRefresh()
        }
    }

    override fun PageRefreshLayout.getData() {
        scopeNetLife {
            addData(release(index)!!.dynamic)
        }
    }
}