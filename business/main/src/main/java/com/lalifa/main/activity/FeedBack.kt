package com.lalifa.main.activity

import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.che.api.upChe
import com.lalifa.che.api.upload
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.lalifa.extension.toast
import com.lalifa.main.api.feedBack
import com.lalifa.main.databinding.ActivityFeedBackBinding

class FeedBack : BaseTitleActivity<ActivityFeedBackBinding>() {
    override fun title() = "我要反馈"
    override fun getViewBinding() = ActivityFeedBackBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            up.onClick {
                val info = etInfo.text()
                if (info.isEmpty()) {
                    toast("请输入内容")
                    return@onClick
                }
                val pathList = arrayListOf<String>()
                val imgs = selectIm.getData()
                imgs.forEach {
                    scopeNetLife {
                        pathList.add(upload(it.path).url)
                        if (pathList.size == imgs.size) {
                            LogCat.e("1111")
                            scopeNetLife { feedBack(info, pathList) }
                        } else {
                            LogCat.e("2222")
                        }
                    }
                }
            }
        }
    }
}