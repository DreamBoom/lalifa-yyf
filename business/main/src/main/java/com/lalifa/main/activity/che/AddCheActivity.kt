package com.lalifa.main.activity.che

import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.*
import com.lalifa.main.api.upChe
import com.lalifa.main.api.upload
import com.lalifa.main.databinding.ActivityAddCheBinding
import kotlinx.coroutines.*

class AddCheActivity : BaseTitleActivity<ActivityAddCheBinding>() {
    override fun title() = "发布帖子"
    override fun getViewBinding() = ActivityAddCheBinding.inflate(layoutInflater)

    override fun initView() {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onClick() {
        super.onClick()
        binding.apply {
            send.onClick {
                val info = etInfo.text()
                if (info.isEmpty()) {
                    toast("请输入内容")
                    return@onClick
                }
                scopeNetLife {
                    val imgs = imgView.getData()
                    val list = if (imgs.isNotEmpty()) upload(imgs) else arrayListOf()
                    LogCat.e(list.toString())
                    upChe(binding.etInfo.text(), list)
                    toast("提交成功")
                    finish()
                }
            }
        }
    }

}