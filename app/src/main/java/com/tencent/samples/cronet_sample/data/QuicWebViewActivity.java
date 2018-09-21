package com.tencent.samples.cronet_sample.data;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.tencent.samples.cronet_sample.R;
import com.tencent.samples.cronet_sample.NetWork.SimpleUrlRequestCallback;
import com.tencent.samples.cronet_sample.data.HtmlElement.ChildTiming;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.tencent.samples.cronet_sample.NetWork.getCronetEngine;

public class QuicWebViewActivity extends AppCompatActivity {

    private static CronetEngine cronetEngine;
    private WebView wb;
    private HtmlElement elements;
    private WebViewClient wbClient = new WebViewClient() {
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

        private HashMap list = new HashMap();

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//                wb.loadUrl("javascript: function getTiming(){return performance.timing;}");
            Log.e("JerryZhu", "onPage 开始加载 ");
            elements.setNetStartTime(System.currentTimeMillis());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("timing_init"), "UTF-8"));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//                wb.evaluateJavascript("javascript:  function getTiming(){return JSON.stringify(performance.timing);}; getTiming();", value -> {
/*               wb.evaluateJavascript("javascript:  JSON.stringify(performance.timing);", value -> {
                String strTiming = value.replaceAll("\\\\", "");
                strTiming = strTiming.substring(1, strTiming.length() - 1);
                Timing timing = new Gson().fromJson(strTiming, Timing.class);
                Log.e("JerryZhu", "onPage 返回值: " + strTiming);
                long xuanran = timing.getLoadEventEnd() - timing.getResponseEnd();
                Log.e("JerryZhu", "onPage 渲染时间 : " + xuanran + "ms     网络数据获取: " + (timing.getResponseEnd() - timing.getFetchStart()));
                */
            wb.evaluateJavascript("javascript: " + stringBuilder, value -> {
                Log.e("JerryZhu", "onPageFinish:  " + value);
            });
            super.onPageFinished(view, url);
            elements.setNetEndTime(System.currentTimeMillis());
//                String s = new Gson().toJson(elements);
            Log.e("JerryZhu", "onPage 执行结果 : ");
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (request.getUrl().toString().endsWith("favicon.png") || request.getUrl().toString().endsWith("favicon.ico"))
                return super.shouldInterceptRequest(view, request);
            Executor executor = Executors.newSingleThreadExecutor();
            ChildTiming timing = new ChildTiming(request.getUrl().toString());
            Thread thread = Thread.currentThread();
            list.put(thread.getId(), thread.getName());
            SimpleUrlRequestCallback callback = new SimpleUrlRequestCallback(elements, timing, thread);
            callback.flag = true;
            UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(request.getUrl().toString()
                    , callback, executor);
            UrlRequest build = builder.build();
            timing.setNetStartTime(System.nanoTime());              //子元素开始
            Log.e("JerryZhu", "开始: " + System.currentTimeMillis());
            build.start();

            Log.e("JerryZhu1", "webView 拦截请求: " + thread.getName() + "   " + thread.getId()
                    + "  请求地址: " + request.getUrl());
            synchronized (thread) {
                try {
                    Log.e("JerryZhu", "shouldInterceptRequest: 睡觉" + thread.getId() + "   ==  " + request.getUrl());
                    thread.wait();
                    Log.e("JerryZhu", "shouldInterceptRequest: 醒了" + thread.getId() + "   ==  " + request.getUrl());
                } catch (InterruptedException e) {
                    Log.e("shouldInterceptRequest", "==  " + request.getUrl());
                    e.printStackTrace();
                }
            }

            timing.setNetEndTime(System.nanoTime());              //子元素结束
            elements.getDoms().add(timing);

            String contentType = callback.contentType;
            String contentEncoding = callback.contentEncoding;

               /*  if (request.getUrl().toString().endsWith("ico")) {
                    webResourceResponse = new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream("ico icon".getBytes()));
                } else {
                    webResourceResponse = new WebResourceResponse(callback.contentType, "utf-8", new ByteArrayInputStream(callback.bytesReceived.toByteArray()));
                }*/
            Log.e("JerryZhu1", "cronet 有响应 : " + thread.getId() + "  请求地址: " + request.getUrl() + System.currentTimeMillis());
            byte[] buf = callback.bytesReceived.toByteArray();
            Log.e("JerryZhu", "shouldInterceptRequest: " + buf.length);
            return buf.length > 0 ? new WebResourceResponse
                    (TextUtils.isEmpty(contentType) ? "text/html" : contentType, contentEncoding, new ByteArrayInputStream(buf)) : super.shouldInterceptRequest(view, request);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
    };
    private String wevUrl;
    private ConcurrentHashMap<Long, String> list = new ConcurrentHashMap();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Set<Long> longs = list.keySet();
            Log.e("JerryZhuT", "本次请求触发 " + list.size() + " 个线程：");
            for (Long next : longs) {
                String s1 = list.get(next);
                Log.e("JerryZhuT", "线程id=  " + next + "     名字:" + s1);
            }
            list.clear();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quic_webview);
        wb = (WebView) findViewById(R.id.web_x5_quic);
        WebView.setWebContentsDebuggingEnabled(true);
        cronetEngine = getCronetEngine(getApplicationContext());
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        wb.setWebViewClient(wbClient);
//        wb.setWebViewClient(new WebViewClient() {
//            private long id;
//            private int index;
//
//            @Override
//            public void onPageFinished(WebView webView, String s) {
//                Log.e("JerryZhuT", "onPageFinished  ");
//                super.onPageFinished(webView, s);
//                handler.sendEmptyMessageDelayed(1, 5000);
//            }
//
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                Thread thread = Thread.currentThread();
//               /* try {
//                    if (id == 0) {
//                        index++;
//                        if (index == 4) {
//                            id = thread.getId();
//                            Log.e("JerryZhuT", "这个要睡着: " + id);
//                            thread.wait();
//                        }
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }*/
//                Log.e("JerryZhuT", "current: " + thread.getId() + request.getUrl());
//                list.put(thread.getId(), thread.getName());
//                return super.shouldInterceptRequest(view, request);
//            }
//        });
        Log.e("JerryZhu", "onCreate: " + wb.getX5WebViewExtension());

        initWeb();
    }


    private void initWeb() {
        // String wevUrl = "https://translate.google.cn/";
        //    String wevUrl = "https://www.wolfcstech.com/";
        //   String wevUrl = "https://www.baidu.com/";
        //  String wevUrl = "https://wy.tytools.cn/";
        //       String wevUrl = "https://192.168.4.167:443/";
        //   wevUrl = "https://halfrost.com/tag/quic/";
        wevUrl = "https://www.litespeedtech.com/";
        //   wevUrl = "http://debugx5.qq.com";
        wb.loadUrl(wevUrl);
        //     String wevUrl = "http://debugtbs.qq.com";
        elements = new HtmlElement(wevUrl);
    }

   /* @Override
    public void onBackPressed() {
        if (wb != null) {
            wb.goBack();
        }
    }*/


}
