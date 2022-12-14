package com.lalifa.main.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lalifa.base.BaseFragment
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.pageChangedListener
import com.lalifa.main.databinding.FragmentPhBinding

/**
 * @param type 1--财富榜  2-魅力榜
 * */
class PhFragment(val type: Int) : BaseFragment<FragmentPhBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPhBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            viewPager.fragmentAdapter(
                childFragmentManager,
                arrayListOf("日榜", "周榜", "月榜")
            ) {
                add(PhBoyFragment(1, type))
                add(PhBoyFragment(2, type))
                add(PhBoyFragment(3, type))
            }.pageChangedListener {
                tabLayout.indicatorColor = Color.TRANSPARENT
                tabLayout.textSelectColor = ContextCompat.getColor(
                    requireContext(), com.lalifa.base.R.color.textColor2
                )
                tabLayout.textUnselectColor = Color.WHITE
            }
            tabLayout.setViewPager(viewPager)
            tabLayout.currentTab = 0
        }
    }
}