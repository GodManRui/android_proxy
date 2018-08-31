/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.samples.cronet_sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.samples.cronet_sample.data.HtmlElement;
import com.tencent.samples.cronet_sample.data.HtmlElement.Timing;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class WelcomeActivity extends AppCompatActivity {

    private static CronetEngine cronetEngine;
    private LinearLayout ll;
    private WebView wb;

    @RequiresApi(api = VERSION_CODES.KITKAT)
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.welcome_layout);
        setUpToolbar();
        WebView.setWebContentsDebuggingEnabled(true);
        final Button imagesButton = (Button) findViewById(R.id.images_button);
        ((TextView) findViewById(R.id.welcome_introduction))
                .setText(R.string.welcome_introduction_text);
        ((TextView) findViewById(R.id.cronet_load_images))
                .setText(R.string.cronet_load_images_text);
        ll = (LinearLayout) findViewById(R.id.proto_fragment);
        getCronetEngine(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.welcome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.welcome_title)).setText(R.string.welcome_activity);

    }

    private static synchronized CronetEngine getCronetEngine(Context context) {
        // Lazily create the Cronet engine.
        if (cronetEngine == null) {
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
            // Enable caching of HTTP data and
            // other information like QUIC server information, HTTP/2 protocol and QUIC protocol.
            cronetEngine = myBuilder
                    .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISABLED, 100 * 1024)
                    .addQuicHint("www.wolfcstech.com", 443, 443)
//                    .addQuicHint("translate.google.cn", 443, 443)
                    .enableHttp2(true)
                    .enableQuic(true)
                    .build();
            //    .setUserAgent("clb_quic_demo")
        }
        return cronetEngine;
    }

    @Override
    public void onBackPressed() {
        if (wb != null) {
            wb.goBack();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void openImages(View view) {
        /*String url = "https://translate.google.cn/";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));*/
      /*  Intent mpdIntent = new Intent(this, MainActivity.class);
        startActivity(mpdIntent);*/
        wb = new WebView(this);
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.supportMultipleWindows();  //多窗口(true);
        ll.removeAllViews();
        ll.addView(wb);
//        String wevUrl = "https://translate.google.cn/";
        String wevUrl = "https://www.baidu.com/";
        HtmlElement elements = new HtmlElement(wevUrl);

        wb.setWebViewClient(new WebViewClient() {
         /*   @Override
            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("/mproduct-")) {
                    Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(i);
                    return true;
                } else {
                    return false;
                }
            }*/

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                wb.loadUrl("javascript: function getTiming(){return performance.timing;}");
                Log.e("JerryZhu", "onPage 开始加载 ");
                elements.setNetStartTime(System.currentTimeMillis());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                wb.evaluateJavascript("javascript:  function getTiming(){return JSON.stringify(performance.timing);}; getTiming();", value -> {
                wb.evaluateJavascript("javascript:  JSON.stringify(performance.timing);", value -> {
                    String strTiming = value.replaceAll("\\\\", "");
                    strTiming = strTiming.substring(1, strTiming.length() - 1);
//                    Timing timing = new Gson().fromJson(strTiming, Timing.class);
                    Log.e("JerryZhu", "onPage 返回值: " + strTiming);
                });
                super.onPageFinished(view, url);
                elements.setNetEndTime(System.currentTimeMillis());
//                String s = new Gson().toJson(elements);
                Log.e("JerryZhu", "onPage 执行结果 : ");
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().endsWith("favicon.ico")) return null;
                Executor executor = Executors.newSingleThreadExecutor();
                HtmlElement.Timing timing = new Timing(request.getUrl().toString());
                SimpleUrlRequestCallback callback = new SimpleUrlRequestCallback(elements, timing);
                callback.flag = true;
                UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(request.getUrl().toString()
                        , callback, executor);
                UrlRequest build = builder.build();
                timing.setNetStartTime(System.nanoTime());              //子元素开始
                build.start();

                Log.e("JerryZhu1", "webView 拦截请求: " + Thread.currentThread().getName() + "   " + Thread.currentThread().getId()
                        + "  请求地址: " + request.getUrl());
                while (callback.flag) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.e("JerryZhu", " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + request.getUrl());
                        e.printStackTrace();
                    }
                    Log.e("JerryZhu", "s等待中: " + callback.mTiming.getUrl());
                }
                timing.setNetEndTime(System.nanoTime());              //子元素结束
                elements.getDoms().add(timing);

                String contentType = callback.contentType;
                String contentEncoding = callback.contentEncoding;
                /*   if (request.getUrl().toString().endsWith("ico")) {
                    webResourceResponse = new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream("ico icon".getBytes()));
                } else {
                    webResourceResponse = new WebResourceResponse(callback.contentType, "utf-8", new ByteArrayInputStream(callback.bytesReceived.toByteArray()));
                }*/
                Log.e("JerryZhu1", "cronet 有响应 : " + Thread.currentThread().getId() + "  请求地址: " + request.getUrl());
                return new WebResourceResponse
                        (TextUtils.isEmpty(contentType) ? "text/html" : contentType, "utf-8", new ByteArrayInputStream(callback.bytesReceived.toByteArray()));
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }
        });
//        wb.loadUrl("https://translate.google.cn/");
        wb.loadUrl(wevUrl);
    }


    class SimpleUrlRequestCallback extends UrlRequest.Callback {
        public boolean flag = true;
        public ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
        public String contentType = "";
        public String contentEncoding = "";
        Timing mTiming;
        HtmlElement elements;
        private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

        SimpleUrlRequestCallback(HtmlElement elements, Timing mTiming) {
            this.elements = elements;
            this.mTiming = mTiming;
        }

        @Override
        public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) throws Exception {
            elements.redirectCount++;
            Log.i("JerryZhu", "Redirect： " + urlResponseInfo.getHttpStatusCode() + "  " + urlResponseInfo.getUrl() + "   重定向地址:  = " + s);
            urlRequest.followRedirect();
        }

        //        在所有的重定向都处理完了，且http响应的header都接收完，需要读取response的body时这个方法会被调用。一个请求的整个处理过程中，这个方法只会被调用一次。在这个方法中需要分配ByteBuffer，以用于http response body的读取过程。Response的body内容会首先被读取到这里分配的ByteBuffer中
        @Override
        public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
            urlRequest.read(ByteBuffer.allocateDirect(32 * 1024));
            mTiming.setStartWaitingResponse(System.nanoTime());
            Map<String, List<String>> allHeaders = urlResponseInfo.getAllHeaders();
            List<String> type = allHeaders.get("content-type");
//            List<String> encoding = allHeaders.get("content-encoding");
            for (int i = 0; i < type.size(); i++) {
                contentType += type.get(i);
                String[] split = contentType.split(";");
                if (split.length >= 2) {
                    contentType = split[0];
                }
                break;
            }
        /*    for (int i = 0; i < encoding.size(); i++) {
                contentEncoding += encoding.get(i);
            }*/
//            Log.i("JerryZhu", "都处理完了，且http响应的header都接收完，需要读取response的body时 " + "\n 响应头: " + urlResponseInfo.getUrl() + urlResponseInfo.getAllHeaders());
        }

        //        http response body读取完成，或者ByteBuffer读满的时候，这个回调方法会被调用。在这个回调方法中，需要将ByteBuffer中的数据copy出来，清空ByteBuffer，然后重新启动读取。这里的回调方法实现，是将ByteBuffer的内容copy出来，借助于WritableByteChannel放进ByteArrayOutputStream中
        @Override
        public void onReadCompleted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) throws Exception {
            byteBuffer.flip();  //将ByteBuffer中的数据copy出来，清空ByteBuffer，然后重新启动读取
            try {
                receiveChannel.write(byteBuffer);
            } catch (IOException e) {
                android.util.Log.i("JerryZhu", " IO异常 QUIC  IOException during ByteBuffer read. Details: ", e);
            }
//            Log.i("JerryZhu", "此次 byteBuffer size=: " + byteBuffer.array().length + "  目前总长度: " + bytesReceived.toByteArray().length);
            byteBuffer.clear();
            urlRequest.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo info) {
            mTiming.setContentDownload(System.nanoTime());
            String negotiatedProtocol = info.getNegotiatedProtocol();
            android.util.Log.i("JerryZhu", negotiatedProtocol +
                    "  请求完成  状态码 " + info.getHttpStatusCode() + "    URL:" + info.getUrl()
                    + ", 总接收字节数为  " + info.getReceivedByteCount() + "   KB为: " + info.getReceivedByteCount() / 1024);

            byte[] byteArray = bytesReceived.toByteArray();
            flag = false;
//            Log.i("JerryZhu", "onSucceeded: " + Thread.currentThread().getName() + " 总接收字节数为  " + byteArray.length + "   KB为: " + byteArray.length / 1024);
        }

        @Override
        public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {
            boolean isNull = urlResponseInfo == null;
            Log.e("JerryZhu", "请求失败！: " + (isNull ? " 返回体为空" : urlResponseInfo.getUrl()) + "  类型: " + contentType + contentEncoding + "  " + e);
            flag = false;
        }
    }
}
