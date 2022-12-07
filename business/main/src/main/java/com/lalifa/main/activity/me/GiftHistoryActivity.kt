package com.lalifa.main.activity.me

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.main.fragment.adapter.giftHistoryAdapter
import com.lalifa.main.api.giftHistory
import com.lalifa.main.databinding.ActivityGiftHistoryBinding

class GiftHistoryActivity : BaseTitleActivity<ActivityGiftHistoryBinding>() {
    override fun title()="礼物记录"
    override fun getViewBinding()= ActivityGiftHistoryBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val giftHistory = giftHistory()
            if(null!=giftHistory){
                binding.list.giftHistoryAdapter().models = giftHistory
            }
        }
    }
}