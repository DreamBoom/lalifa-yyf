package cn.rongcloud.roomkit.ui.other

import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.databinding.ActivityMySxBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lalifa.base.BaseTitleActivity


class MySxActivity : BaseTitleActivity<ActivityMySxBinding>() {
    override fun title()="我的随心币"
    override fun rightText()= "说明"
    override fun getViewBinding() = ActivityMySxBinding.inflate(layoutInflater)
    override fun rightClick() {
        super.rightClick()

    }
    override fun initView() {
        binding.apply {
            list.grid(2).setup {
                addType<String>(R.layout.item_cz)
            }.models = arrayListOf("", "", "", "", "", "")
        }
    }
}