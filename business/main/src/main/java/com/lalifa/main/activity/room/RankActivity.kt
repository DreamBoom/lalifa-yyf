package com.lalifa.main.activity.room

import com.drake.net.utils.scopeNet
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.visible
import com.lalifa.main.api.roomRanking
import com.lalifa.main.databinding.ActivityRankBinding
import com.lalifa.main.fragment.adapter.rankAdapter

class RankActivity : BaseActivity<ActivityRankBinding>() {
    override fun getViewBinding()= ActivityRankBinding.inflate(layoutInflater)
    var roomId = ""
    override fun initView() {
        roomId = getIntentString("roomId")
        getData(1)
    }

    private fun getData(type:Int){
        scopeNet {
            val roomRanking = roomRanking("$type", roomId)
            if(null!=roomRanking&&roomRanking.isNotEmpty()){
                binding.empty.gone()
                binding.list.visible()
                binding.list.rankAdapter().models = roomRanking
            }else{
                binding.empty.visible()
                binding.list.gone()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            back.onClick { finish() }
            about.onClick {  }
        }
    }
}