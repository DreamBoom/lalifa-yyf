package cn.rongcloud.config.ryutiles

import io.rong.imkit.conversation.extension.DefaultExtensionConfig
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule
import io.rong.imkit.conversation.extension.component.plugin.FilePlugin
import cn.rongcloud.config.ryutiles.MyPlugin
import io.rong.imlib.model.Conversation

/**
 *自定义面板
 */
class MyGiftConfig : DefaultExtensionConfig() {
    override fun getPluginModules(
        conversationType: Conversation.ConversationType,
        targetId: String
    ): List<IPluginModule> {
        val pluginModules = super.getPluginModules(conversationType, targetId)
        val iterator = pluginModules.listIterator()

        // 删除扩展项
        while (iterator.hasNext()) {
            val integer = iterator.next()
            // 以删除 FilePlugin 为例
            if (integer is FilePlugin) {
                iterator.remove()
            }
        }
        // 增加扩展项
        //礼物
        pluginModules.add(MyPlugin())
        return pluginModules
    }
}