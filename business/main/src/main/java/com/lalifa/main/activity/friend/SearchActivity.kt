package com.lalifa.main.activity.friend

import com.lalifa.base.BaseActivity
import com.lalifa.extension.*
import com.lalifa.main.databinding.ActivitySearchBinding

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override fun getViewBinding() = ActivitySearchBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick {
                hideKeyboard(binding.etSearch)
                finish()
            }
            search.onClick {
                if(etSearch.isEmpty()){
                    toast("请输入搜索昵称/ID")
                    return@onClick
                }

            }
        }
    }

}