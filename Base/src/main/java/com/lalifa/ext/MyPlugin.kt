package com.lalifa.ext

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeNet
import com.lalifa.api.giftList
import com.lalifa.base.R
import com.lalifa.utils.DownloadFileUtils
import io.rong.imkit.RongIM
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
            if (giftList != null && giftList.isNotEmpty() && !isShow) {
                isShow = true
                showGiftDialog(giftList) { path ->
                    DownloadFileUtils.downloadImg(
                        currentFragment.context,
                        Config.FILE_PATH + path.image,
                        "${System.currentTimeMillis()}"
                    ) { localImg ->
                        isShow = false
                        val localUri: Uri = Uri.parse(localImg)
                        val imageMessage = ImageMessage.obtain(localUri, localUri)
                        val targetId = extension.targetId
                        val conversationType: Conversation.ConversationType =
                            Conversation.ConversationType.PRIVATE
                        val message: Message =
                            Message.obtain(targetId, conversationType, imageMessage)
                        RongIM.getInstance().sendMediaMessage(
                            message,
                            null,
                            null,
                            object : IRongCallback.ISendMediaMessageCallbackWithUploader() {
                                override fun onAttached(message: Message?, uploader: IRongCallback.MediaMessageUploader) {
                                    /*上传图片到自己的服务器*/
                                    uploader.success(Uri.parse(Config.FILE_PATH + path.image))
                                }

                                override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
                                    //发送失败
                                }

                                override fun onCanceled(message: Message?) {
                                    TODO("Not yet implemented")
                                }

                                override fun onSuccess(message: Message?) {
                                    //发送成功
                                }

                                override fun onProgress(message: Message?, progress: Int) {
                                    //发送进度
                                }
                            })
                    }

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