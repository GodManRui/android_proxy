package com.tencent.samples.cronet_sample;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.samples.cronet_sample.NetWork.SimpleUrlRequestCallback;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.tencent.samples.cronet_sample.QuicWebViewActivity.PAGE_LOAD_FINISH;

public class MyWebClient extends WebViewClient {
    private StringBuilder stringBuilder;
    private CronetEngine cronetEngine = QuicWebViewActivity.cronetEngine;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler;

    MyWebClient(StringBuilder stringBuilder, Handler handler) {
        this.stringBuilder = stringBuilder;
        this.handler = handler;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//                wb.loadUrl("javascript: function getTiming(){return performance.timing;}");
        Log.e("JerryZhu", "onPage 开始加载 " + url);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
//         wb.evaluateJavascript("javascript:  function getTiming(){return JSON.stringify(performance.timing);}; getTiming();", value -> {
        Message message = handler.obtainMessage();
        message.what = PAGE_LOAD_FINISH;
        handler.sendMessageDelayed(message, 5000);
        super.onPageFinished(view, url);
        Log.e("JerryZhu", url + " onPage 加载完成 : ");
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (request.getUrl().toString().endsWith("favicon.png") || request.getUrl().toString().endsWith("favicon.ico"))
            return super.shouldInterceptRequest(view, request);

        Thread thread = Thread.currentThread();
        SimpleUrlRequestCallback callback = new SimpleUrlRequestCallback(thread);
        callback.flag = true;
        Log.e("JerryZhu", "requestUrl: " + request.getUrl());
        UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(request.getUrl().toString()
                , callback, executor);
        UrlRequest build = builder.build();
        build.start();
        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String contentType = callback.contentType;
        String contentEncoding = callback.contentEncoding;

        byte[] buf = callback.bytesReceived.toByteArray();
        return buf.length > 0 ? new WebResourceResponse
                (TextUtils.isEmpty(contentType) ? "text/html" : contentType, contentEncoding, new ByteArrayInputStream(buf)) : super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }
}
