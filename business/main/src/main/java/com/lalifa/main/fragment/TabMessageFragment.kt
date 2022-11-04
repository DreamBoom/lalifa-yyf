package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseFragment
import com.lalifa.main.R
import com.lalifa.main.databinding.TabMessageBinding

class TabMessageFragment : BaseFragment<TabMessageBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = TabMessageBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            list.linear().setup {
                addType<String>(R.layout.item_sys_msg)
            }.models = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "")
        }
    }
}