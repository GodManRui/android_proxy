package com.bonree.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class WebViewActivity extends AppCompatActivity {

    private WebView wb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nomal_webview);
        wb = (WebView) findViewById(R.id.wb_nomal);
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        wb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                return false;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                Log.e("JerryZhu", "开始加载: " + s);
                super.onPageStarted(webView, s, bitmap);
            }
        });
        wb.loadUrl("http://www.baidu.com");
    }
}
