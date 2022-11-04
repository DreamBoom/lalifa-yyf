package com.lalifa.yyf.ui.login

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import cn.rongcloud.config.router.RouterPath
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.main.activity.MainActivity
import com.lalifa.utils.SPUtil
import com.lalifa.yyf.databinding.ActivityLoginBinding
//import com.mob.MobSDK


/**
 * 登录界面
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override fun getViewBinding() = ActivityLoginBinding.inflate(layoutInflater)
    override fun initView() {
//        MobSDK.init(this, "36cdb3c927c4c", "19587758b802435324dcdff55261544f")
//        MobSDK.submitPolicyGrantResult(true, null)
        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val length = s.toString().length
                binding.etCode.setSendEnable(length == 11)
            }
        })

        binding.etCode.setSendListener { code ->
            //        scopeNetLife { // 创建作用域
//            // 这个大括号内就属于作用域内部
//            val data =
//                Get<String>("qq.php/") {
//                    param("qq", "80548262")
//                }.await() // 发起GET请求并返回`String`类型数据
//        }
        }

    }


    override fun onClick() {
        super.onClick()
        binding.apply {
//            foe.onClick {
//                //忘记密码
//                start(ForgetPasswordActivity::class.java)
//            }
            login.onClick {
                //登录
                login()
            }
            register.onClick {
                start(RegisterActivity::class.java)
            }
            passLogin.onClick {
                //密码登录
                start(LoginPassActivity::class.java)
            }
            agree.onClick { agree.isSelected = !agree.isSelected }
            agreeInfo.onClick {
                //查看用户服务协议
            }
            qq.onClick { getQQ() }
            wx.onClick { getWeiXin() }
        }
    }

    private fun login() {
        SPUtil.set(Config.IS_LOGIN, true)
        SPUtil.set(Config.TOKEN,"aa0d3b38-26e0-402d-9f52-feff90e1b47f")
        start(MainActivity::class.java)
        finish()
    }

    /*
   * 第三方登录QQ
   * */
    fun getQQ() {
        val qq: Platform = ShareSDK.getPlatform(QQ.NAME)
        qq.removeAccount(true) //移除授权状态和本地缓存，下次授权会重新授权
        qq.SSOSetting(false) //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        qq.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform?, i: Int, hashMap: HashMap<String?, Any?>) {
                val thirdLoginId = platform!!.db.userId
                println("qq登录测试thirdLoginId：$thirdLoginId")
                val name = platform.db.userName
                println("qq登录测试name：$name")
                val image = platform.db.userIcon
                println("qq登录测试image：$image")
                val sex = qq.db.userGender
                println("qq登录测试sex：$sex")
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable?) {
                println("qq测试onError：" + "登录失败")
            }

            override fun onCancel(platform: Platform, i: Int) {
                println("qq测试onCancel：" + "登录取消")
            }
        }
        if (qq.isClientValid) {
            //判断是否存在授权凭条的客户端，true是有客户端，false是无
        }
        if (qq.isAuthValid) {
            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
            Toast.makeText(this, "已经授权过了", Toast.LENGTH_SHORT).show()
            return
        }
        //plat.authorize();    //要功能，不要数据
        qq.showUser(null) //要数据不要功能，主要体现在不会重复出现授权界面
    }

    /*
* 第三方微信登录
* */
    fun getWeiXin() {
        val wx: Platform = ShareSDK.getPlatform(Wechat.NAME)
        wx.removeAccount(true) //移除授权状态和本地缓存，下次授权会重新授权
        wx.SSOSetting(false) //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        wx.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform?, i: Int, hashMap: HashMap<String?, Any?>) {
                val thirdLoginId = platform!!.db.userId
                println("wx登录测试thirdLoginId：$thirdLoginId")
                val name = platform.db.userName
                println("wx登录测试name：$name")
                val image = platform.db.userIcon
                println("wx登录测试image：$image")
                val sex = wx.db.userGender
                println("wx登录测试sex：$sex")
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable?) {
                println("wx测试onError：" + "登录失败")
            }

            override fun onCancel(platform: Platform, i: Int) {
                println("wx测试onCancel：" + "登录取消")
            }
        }
        if (wx.isClientValid) {
            //判断是否存在授权凭条的客户端，true是有客户端，false是无
        }
        if (wx.isAuthValid) {
            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
            Toast.makeText(this, "已经授权过了", Toast.LENGTH_SHORT).show()
            return
        }
        //plat.authorize();    //要功能，不要数据
        wx.showUser(null) //要数据不要功能，主要体现在不会重复出现授权界面
    }

}