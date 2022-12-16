package com.lalifa.main.activity.me

import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.load
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.giftAdapter
import com.lalifa.main.fragment.adapter.giftGroupAdapter
import com.lalifa.main.api.Theme
import com.lalifa.main.api.getGiftList
import com.lalifa.main.databinding.ActivityGiftBinding

class GiftActivity : BaseTitleActivity<ActivityGiftBinding>() {
    override fun getViewBinding() = ActivityGiftBinding.inflate(layoutInflater)
    override fun title() = "礼物墙"
    override fun rightText()="礼物记录"
    var giftAdapter:BindingAdapter?=null
    override fun initView() {
        binding.apply {
            UserManager.get().apply {
                header.load(Config.FILE_PATH + this!!.avatar)
                name.text = this?.userName
            }
            scopeNetLife {
                val list = getGiftList()
                if(list!=null){
                    num.text = "已收集${list.gift_count}件礼物"
                    giftGroup.giftGroupAdapter().apply {
                        R.id.groupName.onClick {
                            giftAdapter!!.models = getModel<Theme>().gift
                        }
                    }.models = list.theme
                    giftAdapter = giftList.giftAdapter()
                    giftAdapter!!.models = list.theme[0].gift
                }
            }

        }
    }

    override fun rightClick() {
        super.rightClick()
        start(GiftHistoryActivity::class.java)
    }
}