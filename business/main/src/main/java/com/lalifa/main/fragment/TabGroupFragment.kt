package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseFragment
import com.lalifa.main.R
import com.lalifa.main.databinding.TabGroupBinding

class TabGroupFragment : BaseFragment<TabGroupBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = TabGroupBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            list.linear().setup {
                addType<String>(R.layout.item_sys_msg)
            }.models = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "")
        }
    }
}