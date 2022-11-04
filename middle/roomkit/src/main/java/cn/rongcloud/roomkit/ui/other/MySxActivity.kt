package cn.rongcloud.roomkit.ui.other

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.api.Rule
import cn.rongcloud.roomkit.api.pay
import cn.rongcloud.roomkit.api.recharge
import cn.rongcloud.roomkit.databinding.ActivityMySxBinding
import cn.rongcloud.roomkit.databinding.ItemCzBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.AbsTextWatcher
import com.lalifa.extension.onClick
import com.lalifa.extension.toast


class MySxActivity : BaseTitleActivity<ActivityMySxBinding>() {
    override fun title() = "我的随心币"
    override fun rightText() = "说明"
    override fun getViewBinding() = ActivityMySxBinding.inflate(layoutInflater)
    override fun rightClick() {
        super.rightClick()

    }

    override fun initView() {
        scopeNetLife {
            binding.apply {
                imZfb.isSelected = true
                list.grid(2).setup {
                    addType<Rule>(R.layout.item_cz)
                    onBind {
                        val bean = getModel<Rule>()
                        getBinding<ItemCzBinding>().apply {
                            num.text = bean.receipt_price
                            money.text = bean.price
                        }
                    }
                    R.id.itemZs.onClick {
                        LogCat.e("22222")
                        money = getModel<Rule>().price.toDouble()
                        payId = getModel<Rule>().id
                        LogCat.e(""+getModel<Rule>().id+"====="+payId)
                    }
                }.models = recharge()!!.rule
                etMoney.addTextChangedListener(
                    object : AbsTextWatcher() {
                        override fun afterTextChanged(s: Editable) {
                            if (!TextUtils.isEmpty(s.toString())) {
                                payId = 0
                                money = s.toString().toDouble()
                            } else {
                                payId = 0
                                money = 0.0
                            }
                        }
                    }
                )
            }
        }

    }

    /**
     * payId 规则id
     * payType支付方式：1：支付宝   2：微信
     * money 充值金额
     */
    var payId = 0
    var payType = 1
    var money = 0.0
    override fun onClick() {
        super.onClick()
        binding.apply {
            zfb.onClick {
                payType = 1
                imZfb.isSelected = true
                imWx.isSelected = false
            }
            wx.onClick {
                payType = 2
                imWx.isSelected = true
                imZfb.isSelected = false
            }
            pay.onClick {
                if (payId == 0) {
                    if (money <= 0) {
                        toast("请选择充值金额或输入金额")
                        return@onClick
                    }
                }
                scopeNetLife { pay(payId.toString(), money.toString(), payType.toString()) }
            }
        }
    }
}