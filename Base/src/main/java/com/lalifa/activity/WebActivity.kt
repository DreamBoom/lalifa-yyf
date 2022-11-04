package com.lalifa.activity

import android.content.Context
import android.webkit.*
import com.lalifa.base.BaseTitleActivity
import com.lalifa.base.databinding.ActivityWebUrlBinding
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.start

/**
 *
 * @ClassName WebUrlActivity
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/6 17:34
 * @Des
 */
class WebActivity : BaseTitleActivity<ActivityWebUrlBinding>() {
    override fun darkMode() = false
    override fun getViewBinding() = ActivityWebUrlBinding.inflate(layoutInflater)

    companion object {
        fun forward(context: Context, title: String = "", url: String) {
            context.start(WebActivity::class.java) {
                putExtra("title", title)
                putExtra("url", url)
            }
        }
    }

    override fun initView() {
        val title = getIntentString("title")
        val url = getIntentString("url")
        if (title.isNotEmpty()) setTitle(title)
        if (url.isEmpty()) return
        binding.webView.apply {
            settings.apply {
                // 网页内容的宽度是否可大于WebView控件的宽度
                loadWithOverviewMode = false
                // 是否应该支持使用其屏幕缩放控件和手势缩放
                setSupportZoom(false)
                //  页面加载好以后，再放开图片
                blockNetworkImage = false
                // 排版适应屏幕
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                //设置字体大小
                textZoom = 80
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (url.startsWith("http")) {
                        view!!.loadUrl(url)
                    }
                    return true
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) binding.progress.gone()
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    setTitle(title ?: "")
                }
            }
            loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

}