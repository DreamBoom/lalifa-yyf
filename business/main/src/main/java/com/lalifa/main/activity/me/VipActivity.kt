package com.lalifa.main.activity.me

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.preview
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.tiList
import com.lalifa.main.fragment.adapter.titlesList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityVipBinding

class VipActivity : BaseTitleActivity<ActivityVipBinding>() {
    override fun title() = "贵族爵位"
    override fun getViewBinding() = ActivityVipBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val titles = titles()
            binding.apply {
                val apply1 = li1.tiList(1).apply {
                    R.id.bigIm.onClick {
                        preview(
                            0,
                            arrayListOf(Config.FILE_PATH + getModel<SpecialEffect>().effect_image)
                        )
                    }
                }

                val apply2 = li2.tiList(2).apply {
                    R.id.bigIm.onClick {
                        preview(
                            0,
                            arrayListOf(Config.FILE_PATH + getModel<Headdres>().effect_image)
                        )
                    }
                }
                val apply3 = li3.tiList(3).apply {
                    R.id.bigIm.onClick {
                        preview(
                            0,
                            arrayListOf(Config.FILE_PATH + getModel<Mount>().effect_image)
                        )
                    }
                }
                apply1.models = titles!![0].special_effects
                apply2.models = titles[0].headdress
                apply3.models = titles[0].mount
                list.titlesList().apply {
                    R.id.itemJw.onClick {
                        apply1.models = getModel<TitleBean>().special_effects
                        apply2.models = getModel<TitleBean>().headdress
                        apply3.models = getModel<TitleBean>().mount
                    }
                }.models = titles
            }
        }
    }
}