package cn.rongcloud.roomkit.ui.other.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.databinding.ItemSearchBinding
import cn.rongcloud.roomkit.databinding.SearchPeopleBinding

import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.lalifa.base.BaseFragment
import com.lalifa.extension.dp

class SearchPeopleFragment : BaseFragment<SearchPeopleBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SearchPeopleBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            list.grid(4).dividerSpace(12.dp)
                .dividerSpace(10.dp, DividerOrientation.VERTICAL).setup {
                    addType<String>(R.layout.item_search)
                    onBind {
                        getBinding<ItemSearchBinding>().name.text = getModel()
                    }
                }.models = arrayListOf("游戏", "相亲", "接待", "电影", "电竞", "女生", "男生",
                "交友", "声鉴", "音乐", "电台")
        }
    }
}