package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseFragment
import com.lalifa.extension.dp
import com.lalifa.main.R
import com.lalifa.main.databinding.*

class MoneyOfGetFragment : BaseFragment<FragmentMoneyGetBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMoneyGetBinding.inflate(layoutInflater)

    override fun initView() {


        binding.apply {
            list.linear().setup {
                    addType<String>(R.layout.item_money)
                    onBind {
                       // getBinding<ItemMoneyBinding>().name.text = getModel()
                    }
                }.models = arrayListOf("游戏", "相亲", "接待", "电影", "电竞", "女生", "男生",
                "交友", "声鉴", "音乐", "电台")
        }
    }
}