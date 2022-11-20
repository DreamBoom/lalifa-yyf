package cn.rongcloud.config.ryutiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import com.drake.logcat.LogCat;

import cn.rongcloud.config.R;
import io.rong.imkit.conversation.extension.RongExtension;
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule;

public class MyPlugin implements IPluginModule {
      /**
     * 获取 plugin 图标
     *
     * @param context 上下文
     * @return 图片的 Drawable
     */
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_bubble_loading); //示例代码
    }
    /**
     * 获取 plugin 标题
     *
     * @param context 上下文
     * @return 标题的字符串
     */
    @Override
    public String obtainTitle(Context context) {
        return "礼物";//示例代码
    }
    /**
     * plugin 被点击时调用。
     * 1. 如果需要 Extension 中的数据，可以调用 Extension 相应的方法获取。
     * 2. 如果在点击后需要开启新的 activity，可以使用 {@link Activity#startActivityForResult(Intent, int)}
     * 或者 {@link RongExtension#startActivityForPluginResult(Intent, int, IPluginModule)} 方式。
     * <p/>
     * 注意：不要长期持有 fragment 或者 extension 对象，否则会有内存泄露。
     *
     * @param currentFragment plugin 所关联的 fragment。
     * @param extension RongExtension 对象
     * @param index plugin 在 plugin 面板中的序号。
     */
    @Override
    public void onClick(Fragment currentFragment, RongExtension extension, int index) {
        LogCat.e("-=====111111");
//            Intent intent = new Intent(currentFragment.getActivity(), MyPluginActivity.class);
//      extension.startActivityForPluginResult(intent, requestCode, MyPlugin.this);
    }
    /**
     * activity 结束时返回数据结果。
     * <p/>
     * 在 {@link #onClick(Fragment, RongExtension, int)} 中，您可能会开启新的 activity，您有两种开启方式：
     * <p/>
     * 1. 使用系统中 {@link Activity#startActivityForResult(Intent, int)} 开启方法
     * 这就需要自己在对应的 Activity 中接收处理 {@link Activity#onActivityResult(int, int, Intent)}  返回的结果。
     * <p/>
     * 2. 如果调用了 {@link RongExtension#startActivityForPluginResult(Intent, int, IPluginModule)} 开启方法
     * 则在 ConversationFragment 中接收到 {@link Activity#onActivityResult(int, int, Intent)} 后，
     * 必须调用 {@link RongExtension#onActivityPluginResult(int, int, Intent)} 方法，RongExtension 才会将数据结果
     * 通过 IPluginModule 中 onActivityResult 方法返回。
     * <p/>
     *
     * @param requestCode 开启 activity 时请求码，不会超过 255.
     * @param resultCode  activity 结束时返回的数据结果.
     * @param data        返回的数据.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}