package com.lalifa.yyf.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomWebView extends WebView {

    public CustomWebView(Context context) {
        super(getFixedContext(context));
        initWebView();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
        initWebView();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
        initWebView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
        initWebView();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(getFixedContext(context), attrs, defStyleAttr, privateBrowsing);
        initWebView();
    }


    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23)   // Android Lollipop 5.0 & 5.1 防止报错
            return context.createConfigurationContext(new Configuration());
        return context;
    }

    private void initWebView() {
        this.setWebViewClient(new CustomWebViewClient());
        this.setWebChromeClient(new CustomWebWebChromeClient());
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    private ProgressBar mProgressBar;

    public void setProgressBar(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    private class CustomWebWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mProgressBar == null) {
                return;
            }
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                mProgressBar.setProgress(newProgress);//设置进度值
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private static class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
        }


        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }


        private static void addImageClickListener(WebView webView) {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                    "    }  " +
                    "}" +
                    "})()");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            LogUtils.d("shouldOverrideUrlLoading", request.get().toString());
            view.loadUrl(request.getUrl().toString()); //在当前的webview中跳转到新的url
            return true;
        }*/

       /* @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtils.d("shouldOverrideUrlLoading", url);
            return super.shouldOverrideUrlLoading(view, url);
        }*/
    }

    //回调图片的点击
    public class MJavascriptInterface {
        private String[] imageUrls;
        private Context context;

        public MJavascriptInterface(Context context, String[] imageUrls) {
            this.imageUrls = imageUrls;
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            int current = 0;
            for (int i = 0; i < imageUrls.length; i++) {
                if (img.equals(imageUrls[i])) {
                    current = i;
                }
            }
            if (openImageListener != null) {
                openImageListener.onResult(imageUrls, current);
            }
        }
    }

    //获取到所HtML中的图片地址
    public static String[] returnImageUrlsFromHtml(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }
        if (imageSrcList == null || imageSrcList.size() == 0) {
            Log.e("imageSrcList", "未匹配到图片链接");
            return null;
        }
        return imageSrcList.toArray(new String[imageSrcList.size()]);
    }

    ////直接加载HTML 设置百分比
    public void loadDataWithBaseURLJsoup(String content) {
        if (TextUtils.isEmpty(content)) {
            Log.e("imageSrcList", "HTML字符串为空");
            return;
        }
        //content = SystemUtil.unicodeToString(content);
        String htmlStr = JsoupUtil.getNewContent(content);        //设置监听
        addJavascriptInterface(new MJavascriptInterface(this.getContext(), returnImageUrlsFromHtml(htmlStr)), "imagelistener");
        this.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
    }

    //直接加载HTML
    public void loadDataWithBaseURLNo(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        //content = SystemUtil.unicodeToString(content);
        this.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    public interface OnOpenImageListener {
        void onResult(String[] imageUrls, int img);

    }

    public OnOpenImageListener openImageListener;

    public void setOpenImageListener(OnOpenImageListener openImageListener) {
        this.openImageListener = openImageListener;
    }

}
