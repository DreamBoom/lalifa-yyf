package com.lalifa.che.activity

import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.che.api.upChe
import com.lalifa.che.api.upload
import com.lalifa.che.databinding.ActivityAddCheBinding
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.lalifa.extension.toast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                if(info.isEmpty()){
                    toast("请输入内容")
                    return@onClick
                }
                val pathList = arrayListOf<String>()
                val imgs = imgView.getData()
                imgs.forEach {
                    scopeNetLife {
                        val upload = upload(it.path)
                        if(upload!=null){
                            runOnUiThread {
                                pathList.add(upload.url)
                                if(pathList.size == imgs.size){
                                    LogCat.e("1111")
                                    scopeNetLife {upChe(info,pathList) }
                                }else{
                                    LogCat.e("2222")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}