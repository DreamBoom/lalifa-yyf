package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.extension.load
import com.lalifa.main.R
import com.lalifa.main.api.FriendBean
import com.lalifa.main.api.friends
import com.lalifa.main.databinding.FragmentGzBinding
import com.lalifa.main.databinding.ItemFanBinding

class MyGzFragment : BaseFragment<FragmentGzBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGzBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val friends = friends(2)
            binding.apply {
                list.linear().setup {
                    addType<FriendBean>(R.layout.item_fan)
                    onBind {
                        getBinding<ItemFanBinding>().apply {
                            val bean = getModel<FriendBean>()
                            name.text = getModel<FriendBean>().userName
                            itemHeader.load(Config.FILE_PATH+bean.avatar)
                            if(bean.gender==1){
                                imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_gril)
                            }else{
                                imSex.setImageResource(com.lalifa.base.R.drawable.ic_icon_boy)
                            }
                        }
                    }
                }.models = friends
            }
        }
    }

    override fun onClick() {
        super.onClick()
    }
}