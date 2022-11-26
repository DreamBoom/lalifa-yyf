package cn.rongcloud.roomkit.ui.other

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import cn.rongcloud.roomkit.R
import cn.rongcloud.roomkit.api.Rule
import cn.rongcloud.roomkit.api.pay
import cn.rongcloud.roomkit.api.recharge
import cn.rongcloud.roomkit.databinding.ActivityMySxBinding
import cn.rongcloud.roomkit.databinding.ItemCzBinding
import com.alipay.sdk.app.PayTask
import com.drake.brv.utils.grid
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
       binding.etMoney.addTextChangedListener(
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
                        money = getModel<Rule>().price.toDouble()
                        payId = getModel<Rule>().id
                        binding.etMoney.setText(getModel<Rule>().price)
                    }
                }.models = recharge()!!.rule
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
    private val SDK_PAY_FLAG = 1
    val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val payResult = PayResult(msg.obj as Map<String?, String?>)
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            val resultInfo: String = payResult.result // 同步返回需要验证的信息
            val resultStatus: String = payResult.resultStatus
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                if(!TextUtils.isEmpty(resultInfo)){
                    toast(resultInfo)
                }
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                if(!TextUtils.isEmpty(resultInfo)){
                    toast(resultInfo)
                }
            }
        }
    }
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
                scopeNetLife {
                    if(payType==2){
                        toast("微信支付正在开发...")
                        return@scopeNetLife
                    }
                    val pay1 = pay(payId.toString(), money.toString(), payType.toString())
                    if(payType == 1){
                        val orderInfo = pay1 // 订单信息
                        val payRunnable = Runnable {
                            val alipay = PayTask(this@MySxActivity)
                            val result = alipay.payV2(orderInfo, true)
                            val msg = Message()
                            msg.what = SDK_PAY_FLAG
                            msg.obj = result
                            mHandler.sendMessage(msg)
                        }
                        // 必须异步调用
                        val payThread = Thread(payRunnable)
                        payThread.start()
                    }
                }
            }
        }
    }
}