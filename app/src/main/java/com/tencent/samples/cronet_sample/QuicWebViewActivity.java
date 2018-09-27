package com.tencent.samples.cronet_sample;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.tencent.samples.cronet_sample.NetWork.SimpleUrlRequestCallback;
import com.tencent.samples.cronet_sample.data.HtmlElement;
import com.tencent.samples.cronet_sample.data.HtmlElement.ChildTiming;
import com.tencent.smtt.sdk.CacheManager;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.tencent.samples.cronet_sample.NetWork.getCronetEngine;

public class QuicWebViewActivity extends AppCompatActivity {

    private static CronetEngine cronetEngine;
    private WebView wb;
    private HtmlElement elements;
    private Executor executor = Executors.newSingleThreadExecutor();
    private WebViewClient wbClient = new WebViewClient() {

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
            HtmlElement.ChildTiming timing = new ChildTiming(request.getUrl().toString());
            Thread thread = Thread.currentThread();
            SimpleUrlRequestCallback callback = new SimpleUrlRequestCallback(elements, timing, thread);
            callback.flag = true;
            Log.e("JerryZhu", "requestUrl: " + request.getUrl());
            UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(request.getUrl().toString()
                    , callback, executor);
            UrlRequest build = builder.build();
            timing.setNetStartTime(System.nanoTime());              //子元素开始
            build.start();
            synchronized (thread) {
                try {
                    thread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            timing.setNetEndTime(System.nanoTime());              //子元素结束
            elements.getDoms().add(timing);

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
    };
    private String wevUrl;
    private EditText edUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quic_webview);

        initView();
        initCronet();
        initWebView();

//        Log.e("JerryZhu", "onCreate: " + wb.getX5WebViewExtension());
    }

    private void initView() {
        wb = (WebView) findViewById(R.id.web_x5_quic);
        edUrl = (EditText) findViewById(R.id.ed_url);
        findViewById(R.id.im_search).setOnClickListener(v -> {
            startBrowse();
        });
        //     wb = new WebView(this);
    }

    private void initCronet() {
        cronetEngine = getCronetEngine(getApplicationContext());
        startNetLog();
    }

    private void initWebView() {
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        wb.setWebViewClient(wbClient);
    }

    private void startBrowse() {
        edUrl.setFocusable(false);
        String edUrl = this.edUrl.getText().toString();
        if (TextUtils.isEmpty(edUrl)) {

        }
        // String wevUrl = "https://translate.google.cn/";
        //    String wevUrl = "https://www.wolfcstech.com/";
        //   String wevUrl = "https://www.baidu.com/";
        //  String wevUrl = "https://wy.tytools.cn/";
        //       String wevUrl = "https://192.168.4.167:443/";
        wevUrl = "https://halfrost.com/tag/quic/";
        //   wevUrl = "https://www.litespeedtech.com/";
        //   wevUrl = "http://debugx5.qq.com";
        wb.loadUrl(wevUrl);
        //     String wevUrl = "http://debugtbs.qq.com";
        elements = new HtmlElement(wevUrl);
    }

    private void startNetLog() {
        File outputFile;
        try {
            outputFile = File.createTempFile("cronet", ".txt",
                    new File(Environment.getExternalStorageDirectory() + "/1QUIC_LOG"));
            cronetEngine.startNetLogToFile(outputFile.toString(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        cronetEngine.stopNetLog();
        super.onDestroy();
        onMyPageFinish();
    }

    public void onMyPageFinish() {
        File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File f : file.listFiles())
                f.delete();
        }
        wb.clearHistory();
        wb.clearFormData();
        deleteDatabase("webview.db");
        deleteDatabase("webviewCookiesChromium.db");
        deleteDatabase("webviewCache.db");
//        wb.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        wb = null;
    }

   /* @Override
    public void onBackPressed() {
        if (wb != null) {
            wb.goBack();
        }
    }*/


}
