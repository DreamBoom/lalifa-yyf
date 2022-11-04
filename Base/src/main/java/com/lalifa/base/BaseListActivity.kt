package com.lalifa.base

import com.drake.brv.PageRefreshLayout
import com.lalifa.base.databinding.LayoutCommonListBinding


/**
 *
 * @ClassName BaseListActivity
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/7/9 14:41
 * @Des
 */
abstract class BaseListActivity() : BaseTitleActivity<LayoutCommonListBinding>() {
    override fun getViewBinding() = LayoutCommonListBinding.inflate(layoutInflater)

    open fun hasRefresh(): Boolean = true
    open fun hasLoadMore(): Boolean = true
    override fun initView() {
        binding.apply {
            refreshLayout.apply {
                setEnableRefresh(hasRefresh())
                setEnableLoadMore(hasLoadMore())
                onRefresh {
                    getData()
                }
            }
        }
    }

    open fun PageRefreshLayout.getData() {}
}