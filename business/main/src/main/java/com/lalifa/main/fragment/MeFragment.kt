package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.activity.*
import com.lalifa.main.api.userInfo
import com.lalifa.main.databinding.ViewMainMeBinding

class MeFragment : BaseFragment<ViewMainMeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ViewMainMeBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val bean = userInfo()
            binding.apply {
                header.load(Config.FILE_PATH+bean!!.avatar)
                vip.text = bean.level.toString()
                name.text = bean.userName
                tvId.text = bean.id.toString()
                num1.text = bean.fans.toString()
                num2.text = bean.friends.toString()
                num3.text = bean.friends.toString()
                num4.text = bean.friends.toString()
                drill.text = bean.core_drill
                money.text = bean.core_currency
            }
        }
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            info.onClick { start(MeInfoActivity::class.java) }
            setting.onClick { start(SettingActivity::class.java) }
            co3.onClick { start(MyWalletActivity::class.java){
                putExtra("drill",drill.text.toString())
                putExtra("money",money.text.toString())
            } }
            vip.onClick { start(VipActivity::class.java) }
            header.onClick { start(EditMyInfoActivity::class.java) }
            num1.onClick { start(FanActivity::class.java) }
            nm1.onClick { start(FanActivity::class.java) }
            num3.onClick { start(FanActivity::class.java) }
            nm3.onClick { start(FanActivity::class.java) }
            pack.onClick { start(Knapsack::class.java) }
            feelBack.onClick { start(FeedBack::class.java) }
            kf.onClick { start(CallUs::class.java) }
            share.onClick { start(MyChe::class.java) }
        }
    }
}