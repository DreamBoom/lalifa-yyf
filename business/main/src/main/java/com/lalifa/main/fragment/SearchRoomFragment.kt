package com.lalifa.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup

import com.lalifa.base.databinding.*
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.google.android.flexbox.FlexboxLayoutManager
import com.lalifa.base.BaseFragment
import com.lalifa.extension.start
import com.lalifa.main.R
import com.lalifa.main.activity.room.SearchRoomList
import com.lalifa.main.api.RoomThemeBean
import com.lalifa.main.api.roomTheme
import com.lalifa.main.databinding.ItemSearchBinding
import com.lalifa.main.databinding.SearchRoomBinding

class SearchRoomFragment : BaseFragment<SearchRoomBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SearchRoomBinding.inflate(layoutInflater)

    override fun initView() {
        scopeNetLife {
            val roomTheme = roomTheme()
            binding.apply {
                list.apply {
                    layoutManager = FlexboxLayoutManager(context)
                    setup {
                        addType<RoomThemeBean>(R.layout.item_search)
                        onBind {
                            getBinding<ItemSearchBinding>().name.text =
                                getModel<RoomThemeBean>().name
                        }
                    }.apply {
                        R.id.name.onClick {
                            start(SearchRoomList::class.java){
                                putExtra("theme",getModel<RoomThemeBean>().id)
                            }
                        }
                    }.models = roomTheme
                }
            }
        }
    }
}