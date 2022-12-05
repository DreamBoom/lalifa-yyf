package com.lalifa.main.activity.login

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import cn.jpush.android.api.JPushInterface
import cn.rongcloud.config.AppConfig
import cn.rongcloud.config.UserManager
import cn.rongcloud.config.provider.user.User
import cn.rongcloud.config.provider.user.UserProvider
import cn.rongcloud.config.router.RouterPath
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import com.alibaba.android.arouter.facade.annotation.Route
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.activity.MainActivity
import com.lalifa.main.api.login
import com.lalifa.main.databinding.ActivityLoginBinding
import com.lalifa.utils.SPUtil
import com.mob.MobSDK
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient


/**
 * 登录界面
 */
@Route(path = RouterPath.ROUTER_LOGIN)
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override fun getViewBinding() = ActivityLoginBinding.inflate(layoutInflater)
    override fun initView() {
        MobSDK.init(this, "36cdb3c927c4c", "19587758b802435324dcdff55261544f")
        MobSDK.submitPolicyGrantResult(true, null)
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
//                toast("请进行密码登录")
//                return@onClick
                login.disable()
                //登录
                loginUser()
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

    private fun loginUser() {
        scopeNetLife {
            val user = login("13462439645", "111111")
          //  val user = login("15500000003", "123456")
            if (null != user) {
                binding.login.enable()
                //在jpush上设置别名
                JPushInterface.setAlias(
                    this@LoginActivity, user.userinfo.userId
                ) { i, s, set ->
                    if (i == 0) {
                        LogCat.e("设置别名成功")
                    } else {
                        LogCat.e("设置别名失败")
                    }
                }
                UserManager.save(user.userinfo)
                UserProvider.provider().update(user.userinfo.toUserInfo())
                SPUtil.set(Config.IS_LOGIN, true)
                AppConfig.initNetHttp(this@LoginActivity)
                initRongIM(user.userinfo)
            }else{
                binding.login.enable()
            }
        }
    }

    private fun initRongIM(user: User) {
        if (!TextUtils.isEmpty(user.imToken)) {
            RongIM.connect(user.imToken, 0, object : RongIMClient.ConnectCallback() {
                override fun onSuccess(t: String) {
                    //连接融云成功后返回当前UserId
                    LogCat.e("连接成功用户ID->$t")
                    UserManager.get()!!.userId = t
                    start(MainActivity::class.java)
                    finish()
                }

                override fun onError(e: RongIMClient.ConnectionErrorCode?) {
                    if (e!! == RongIMClient.ConnectionErrorCode.RC_CONN_TOKEN_EXPIRE) {
                        //从 APP 服务请求新 token，获取到新 token 后重新 connect()
                        LogCat.e("融云建立链接异常===>从 APP 服务请求新 token，获取到新 token 后重新 connect()")
                    } else if (e == RongIMClient.ConnectionErrorCode.RC_CONNECT_TIMEOUT) {
                        //连接超时，弹出提示，可以引导用户等待网络正常的时候再次点击进行连接
                        LogCat.e("融云建立链接异常===>连接超时，弹出提示")
                    } else if (e.toString() == "RC_CONNECTION_EXIST") {
                        LogCat.e("融云建立链接异常===>退出当前用户")
                        UserManager.logout()
                    }else{
                        //其它业务错误码，请根据相应的错误码作出对应处理。
                        LogCat.e("融云建立链接异常===>$e")
                    }
                }

                override fun onDatabaseOpened(code: RongIMClient.DatabaseOpenStatus?) {
                    if(RongIMClient.DatabaseOpenStatus.DATABASE_OPEN_SUCCESS == code) {
                        //本地数据库打开，跳转到会话列表页面
                        LogCat.e("融云本地数据库打开===>")
                    } else {
                        //数据库打开失败，可以弹出 toast 提示。
                        LogCat.e("融云数据库打开失败===>")
                    }
                }
            })
        }
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