package cn.rongcloud.config.ryutiles

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.fragment.app.Fragment
import cn.rongcloud.config.R
import cn.rongcloud.config.api.giftList
import cn.rongcloud.config.dialog.showGiftDialog
import com.drake.logcat.LogCat
import com.drake.net.utils.scopeNet
import com.lalifa.ext.Config
import io.rong.imkit.IMCenter
import io.rong.imkit.conversation.extension.RongExtension
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.ImageMessage


class MyPlugin : IPluginModule {
    /**
     * 获取 plugin 图标
     *
     * @param context 上下文
     * @return 图片的 Drawable
     */
    override fun obtainDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.sendgift) //示例代码
    }

    /**
     * 获取 plugin 标题
     *
     * @param context 上下文
     * @return 标题的字符串
     */
    override fun obtainTitle(context: Context): String {
        return "礼物" //示例代码
    }

    /**
     * plugin 被点击时调用。
     * 1. 如果需要 Extension 中的数据，可以调用 Extension 相应的方法获取。
     * 2. 如果在点击后需要开启新的 activity，可以使用 [Activity.startActivityForResult]
     * 或者 [RongExtension.startActivityForPluginResult] 方式。
     *
     *
     * 注意：不要长期持有 fragment 或者 extension 对象，否则会有内存泄露。
     *
     * @param currentFragment plugin 所关联的 fragment。
     * @param extension RongExtension 对象
     * @param index plugin 在 plugin 面板中的序号。
     */
    var isShow = false
    override fun onClick(currentFragment: Fragment, extension: RongExtension, index: Int) {
        scopeNet {
            val giftList = giftList()
            if(giftList!=null&&giftList.isNotEmpty()&&!isShow){
                isShow = true
                showGiftDialog(giftList) {
                    isShow = false
                    LogCat.e("===file=="+Config.FILE_PATH + it.image)
                    val localUri: Uri = Uri.parse(Config.FILE_PATH + it.image)
                    val imageMessage = MyMediaMessageContent.obtain(localUri)
                    val targetId = extension.targetId
                    val conversationType: Conversation.ConversationType =
                        Conversation.ConversationType.PRIVATE
                    val message: Message = Message.obtain(targetId, conversationType, imageMessage)
                    IMCenter.getInstance().sendMediaMessage(
                        message,
                        null,
                        null,
                        object : IRongCallback.ISendMediaMessageCallback {
                            override fun onProgress(message: Message?, i: Int) {
                                LogCat.e("=111===="+message.toString())
                            }
                            override fun onCanceled(message: Message?) {
                                LogCat.e("=222===="+message.toString())
                            }
                            override fun onAttached(message: Message?) {
                                LogCat.e("=333===="+message.toString())
                            }
                            override fun onSuccess(message: Message?) {
                                LogCat.e("=444===="+message.toString())
                            }
                            override fun onError(
                                message: Message?,
                                errorCode: RongIMClient.ErrorCode?
                            ) {
                                LogCat.e("=555===${errorCode!!.code}errorCode="+message.toString())
                            }
                        })
                }
            }
        }
    }

    /**
     * activity 结束时返回数据结果。
     *
     *
     * 在 [.onClick] 中，您可能会开启新的 activity，您有两种开启方式：
     *
     *
     * 1. 使用系统中 [Activity.startActivityForResult] 开启方法
     * 这就需要自己在对应的 Activity 中接收处理 [Activity.onActivityResult]  返回的结果。
     *
     *
     * 2. 如果调用了 [RongExtension.startActivityForPluginResult] 开启方法
     * 则在 ConversationFragment 中接收到 [Activity.onActivityResult] 后，
     * 必须调用 [RongExtension.onActivityPluginResult] 方法，RongExtension 才会将数据结果
     * 通过 IPluginModule 中 onActivityResult 方法返回。
     *
     *
     *
     * @param requestCode 开启 activity 时请求码，不会超过 255.
     * @param resultCode  activity 结束时返回的数据结果.
     * @param data        返回的数据.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}
}