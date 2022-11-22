package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lalifa.base.BaseFragment
import com.lalifa.main.databinding.MeTab1FragmentBinding

class MeTab1Fragment : BaseFragment<MeTab1FragmentBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = MeTab1FragmentBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {

        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {

        }
    }
}