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
    }
}