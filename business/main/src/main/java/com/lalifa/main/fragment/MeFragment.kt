package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.ext.UserManager
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.activity.*
import com.lalifa.main.activity.che.MyChe
import com.lalifa.main.activity.me.*
import com.lalifa.main.activity.room.ext.AccountManager
import com.lalifa.main.api.UserInfoBean
import com.lalifa.main.api.userInfo
import com.lalifa.main.databinding.ViewMainMeBinding
import com.lalifa.main.ext.MUtils
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL

class MeFragment : BaseFragment<ViewMainMeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainMeBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !svgLoad) {
            svgLoad = true
            MUtils.loadSvg(binding.svg,bean!!.frame){

            }
        }
    }

    var svgLoad = false
    override fun onResume() {
        super.onResume()
        getData()
    }

    var bean: UserInfoBean? = null
    private fun getData() {
        scopeNetLife {
            bean = userInfo()
            bean!!.toUser()
            AccountManager.setAccount(bean!!.toAccount(), true)
            binding.apply {
                header.load(Config.FILE_PATH + bean!!.avatar)
                UserManager.get()!!.frame = bean!!.frame
                vip.text = bean!!.level
                name.text = bean!!.userName
                tvId.text = bean!!.id.toString()
                num1.text = bean!!.fans.toString()
                num2.text = bean!!.friends.toString()
                num3.text = bean!!.friends.toString()
                num4.text = bean!!.friends.toString()
                drill.text = bean!!.core_drill
                money.text = bean!!.core_currency
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.svg.stopAnimation()
        binding.svg.clear()
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            info.onClick { start(MeInfoActivity::class.java) }
            setting.onClick { start(SettingActivity::class.java) }
            co3.onClick {
                start(MyWalletActivity::class.java) {
                    putExtra("drill", drill.text.toString())
                    putExtra("money", money.text.toString())
                }
            }
            gifs.onClick { start(GiftActivity::class.java) }
            vip.onClick { start(VipActivity::class.java) }
            num1.onClick { start(FanActivity::class.java) }
            nm1.onClick { start(FanActivity::class.java) }
            num3.onClick { start(FanActivity::class.java) }
            nm3.onClick { start(FanActivity::class.java) }
            num4.onClick { start(WardActivity::class.java) }
            nm4.onClick { start(WardActivity::class.java) }
            pack.onClick { start(Knapsack::class.java) }
            kf.onClick { start(CallUs::class.java) }
            share.onClick { start(MyChe::class.java) }
            shop.onClick { start(ShopActivity::class.java) }
        }
    }
}