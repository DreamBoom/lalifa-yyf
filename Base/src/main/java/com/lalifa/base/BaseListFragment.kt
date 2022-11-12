package com.lalifa.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.page
import com.drake.brv.utils.pageCreate
import com.lalifa.base.databinding.LayoutCommonListBinding

/**
 *
 * @ClassName BaseListFragment
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/13 11:44
 * @Des
 */
abstract class BaseListFragment : BaseFragment<LayoutCommonListBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        LayoutCommonListBinding.inflate(inflater, container, false)

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
            refreshLayout.autoRefresh()
        }
    }

    open fun PageRefreshLayout.getData() {}
}