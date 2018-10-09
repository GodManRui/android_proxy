package com.tencent.samples.cronet_sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.nc.gson.Gson;
import com.tencent.samples.cronet_sample.data.Timing;
import com.tencent.samples.cronet_sample.data.parse.QuicDataParse;
import com.tencent.samples.cronet_sample.data.parse.module.NetRecord;
import com.tencent.smtt.sdk.CacheManager;

import org.chromium.net.CronetEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.tencent.samples.cronet_sample.NetWork.getCronetEngine;

public class QuicWebViewActivity extends AppCompatActivity {

    static final int PAGE_LOAD_FINISH = 100;
    static CronetEngine cronetEngine;
    private final int LOAD_NEW_URL = 200;
    private WebView wb;
    private String wevUrl;
    private EditText edUrl;
    private boolean mPageFinished;
    private String mQuicLog;
    private boolean mShouldLoad = true;
    private String currentFile;
    private Timing timing;
    private MyWebClient myWebClient;
    private File outputFile;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAGE_LOAD_FINISH:
                    if (!mPageFinished) {
                        wb.clearCache(true);
                        handler.removeMessages(PAGE_LOAD_FINISH);
                        cronetEngine.stopNetLog();
//                        mPageFinished = true;
                        wb.evaluateJavascript("javascript:  JSON.stringify(performance.timing);", value -> {
                            /* view.evaluateJavascript("javascript: " + stringBuilder, value -> {*/
                            Gson gson = new Gson();
                            value = value.replaceAll("\\\\", "").replaceFirst("\"", "");
                            value = value.substring(0, value.length() - 1);
                            Log.e("JerryZhu", "  onPage js执行结果: 格式化 " + value);
                            timing = (Timing) gson.fromJson(value, Timing.class);

                            if (timing == null) timing = new Timing();
                            List<NetRecord> netRecords = QuicDataParse.parseLog(currentFile);
                            timing.setNetList(netRecords);
                            startLogParsingActivity(timing);
                        });
                    }
                    break;
                case LOAD_NEW_URL:
                    if (wb != null) {
                        wb.loadUrl("about:blank");
                        wb.clearHistory();
                        wb.clearFormData();
                        wb.clearCache(true);
//                        wb.loadUrl("about:blank");// 清空当前加载
//                        Utils.deleteFile(outputFile);

                        wb.loadUrl(wevUrl);

                        Message message = handler.obtainMessage();
                        message.what = PAGE_LOAD_FINISH;
                        handler.sendMessageDelayed(message, 30000);
                    }
                    break;
            }
        }
    };

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
        edUrl.setOnClickListener(v -> {
            Log.e("JerryZhu", "onClick: 点击");
            edUrl.setFocusableInTouchMode(true);
            edUrl.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(edUrl, 0);
        });
        findViewById(R.id.im_search).setOnClickListener(v -> {
            if (mShouldLoad)
                startBrowse();
            /*else
                startLogParsingActivity(netRecords);*/
        });
        //     wb = new WebView(this);
    }

    private void initCronet() {
        cronetEngine = getCronetEngine(getApplicationContext());
        startNetLog();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
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
        myWebClient = new MyWebClient(stringBuilder, handler);
        wb.setWebViewClient(myWebClient);
        wb.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.e("JerryZhu", "进度: " + newProgress);
            }
        });
    }

    private void startBrowse() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edUrl.getWindowToken(), 0);
        edUrl.setFocusable(false);
        edUrl.setFocusableInTouchMode(false);
        edUrl.clearFocus();
        wevUrl = this.edUrl.getText().toString();
        if (TextUtils.isEmpty(wevUrl)) {
            AlertDialog.Builder builder = new Builder(this);
//            String[] items = {"https://translate.google.cn/", "https://www.wolfcstech.com/", "https://halfrost.com/tag/quic/", "https://www.litespeedtech.com/", "https://wy.tytools.cn/"};
            String[] items = getResources().getStringArray(R.array.support_quic_url);
            builder.setItems(items, (dialog, which) -> {
                wevUrl = items[which];
                edUrl.setText(wevUrl);
                Message message = handler.obtainMessage();
                message.what = LOAD_NEW_URL;
                handler.sendMessage(message);
            });
            builder.create().show();
            return;
        }
        Message message = handler.obtainMessage();
        message.what = LOAD_NEW_URL;
        handler.sendMessage(message);
//        mShouldLoad = false;
        // String wevUrl = "https://translate.google.cn/";
        //    String wevUrl = "https://www.wolfcstech.com/";
        //   String wevUrl = "https://www.baidu.com/";
        //  String wevUrl = "https://wy.tytools.cn/";
        //       String wevUrl = "https://192.168.4.167:443/";
//        wevUrl = "https://halfrost.com/tag/quic/";
        //   wevUrl = "https://www.litespeedtech.com/";
        //   wevUrl = "http://debugx5.qq.com";
        //     String wevUrl = "http://debugtbs.qq.com";
    }

    private void startNetLog() {
        try {
            mQuicLog = Environment.getExternalStorageDirectory() + "/1QUIC_LOG";
            File directory = new File(mQuicLog);
            boolean mkdir = directory.mkdir();
            outputFile = File.createTempFile("cronet", ".txt",
                    directory);
            currentFile = outputFile.toString();
            Log.e("JerryZhu", mkdir + "  startNetLog: " + currentFile);
            cronetEngine.startNetLogToFile(outputFile.toString(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        onMyPageFinish();
        super.onDestroy();
    }

    public void onMyPageFinish() {
        cronetEngine.stopNetLog();
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

        //清空所有Cookie
        CookieSyncManager.createInstance(this);  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        wb.setWebChromeClient(null);
        wb.setWebViewClient(null);
        wb.getSettings().setJavaScriptEnabled(false);
        wb.clearCache(true);

        wb = null;
    }

    private void startLogParsingActivity(Timing timing) {
        Intent intent = new Intent(this, LogParsingActivity.class);
        intent.putExtra("list", timing);
        startActivity(intent);
    }

   /* @Override
    public void onBackPressed() {
        if (wb != null) {
            wb.goBack();
        }
    }*/


}
