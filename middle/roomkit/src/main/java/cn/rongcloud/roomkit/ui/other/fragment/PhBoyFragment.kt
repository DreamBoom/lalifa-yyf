package cn.rongcloud.roomkit.ui.other.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.rongcloud.roomkit.databinding.FragmentPhBoyBinding
import com.lalifa.base.BaseFragment

/**
 * @param type 1--财富榜  2-魅力榜  3-在线榜
 * */
class PhBoyFragment(type:Int): BaseFragment<FragmentPhBoyBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPhBoyBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {

        }
    }
}