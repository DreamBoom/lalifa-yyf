package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.setup
import com.google.android.flexbox.FlexboxLayoutManager
import com.lalifa.base.BaseFragment
import com.lalifa.main.R
import com.lalifa.main.databinding.ItemSearchBinding
import com.lalifa.main.databinding.SearchPeopleBinding

class SearchPeopleFragment : BaseFragment<SearchPeopleBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SearchPeopleBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            list.apply {
                layoutManager = FlexboxLayoutManager(context)
                setup {
                    addType<String>(R.layout.item_search)
                    onBind {
                        getBinding<ItemSearchBinding>().name.text = getModel()
                    }
                }.models = arrayListOf("游戏", "相亲", "接待", "电影VIP", "电竞", "女生", "男生",
                    "交友", "声鉴", "音乐", "电台")
            }
        }
    }
}