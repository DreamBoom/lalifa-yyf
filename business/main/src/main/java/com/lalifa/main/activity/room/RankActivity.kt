package com.lalifa.main.activity.room

import androidx.core.content.ContextCompat
import com.drake.net.utils.scopeNet
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.showTipDialog
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.visible
import com.lalifa.main.R
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
            about.onClick {
                showTipDialog("展示该房间礼物赠送排行榜","排行榜",
                    isShowCancelBtn = false)
            }
            b1.onClick {
                getData(1)
                background()
                b1.background = ContextCompat.getDrawable(
                    this@RankActivity, R.drawable.bg_bk_white30)
            }
            b2.onClick {
                getData(2)
                background()
                b2.background = ContextCompat.getDrawable(
                    this@RankActivity, R.drawable.bg_bk_white30)
            }
            b3.onClick {
                getData(3)
                background()
                b3.background = ContextCompat.getDrawable(
                    this@RankActivity, R.drawable.bg_bk_white30)
            }
        }
    }

    private fun background(){
        binding.apply {
            b1.background = ContextCompat.getDrawable(
                this@RankActivity, R.drawable.bg_tr)
            b2.background = ContextCompat.getDrawable(
                this@RankActivity, R.drawable.bg_tr)
            b3.background = ContextCompat.getDrawable(
                this@RankActivity, R.drawable.bg_tr)
        }
    }
}