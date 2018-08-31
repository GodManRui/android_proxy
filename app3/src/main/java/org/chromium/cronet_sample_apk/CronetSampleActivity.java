// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cronet_sample_apk;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlRequestException;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity for managing the Cronet Sample.
 * Cronet version %2F55.0.2879.0%2F
 */
public class CronetSampleActivity extends Activity {
    private static final String TAG = "CronetSample";
    private static final String logPath = "/sdcard/cronetSample";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private CronetEngine mCronetEngine;
    private OkHttpClient mOKHttpClient;

    private String mUrl;
    private TextView mQuicResultText;
    private TextView mQuicReceiveDataText;
    private TextView mOkhttpResultText;
    private TextView mOkhttpReceiveDataText;

    private EditText mUrlInput;
    private EditText mPostInput;
    private EditText mQuicTimes;
    private EditText mOKHttpTimes;
    private Button mQuicLoadBtn;
    private Button mQuicBtn;
    private Button mOkhttpLoadBtn;
    private Button mOkhttpSynchronousBtn;
    private Boolean QuicEnable = true;
    private Boolean OkHttpSynchronousFlag = true;
    private Boolean QuicFinishFlag = true;
    private Boolean OKHttpFinishFlag = true;
    private Integer mQuicRuntimes = 1;
    private Integer mOKHttpRuntimes = 1;

    class SimpleUrlRequestCallback extends UrlRequest.Callback {
        private ByteArrayOutputStream mBytesReceived = new ByteArrayOutputStream();
        private WritableByteChannel mReceiveChannel = Channels.newChannel(mBytesReceived);

        @Override
        public void onRedirectReceived(
                UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
            Log.i(TAG, "****** onRedirectReceived ******");
            request.followRedirect();
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "****** Response Started ******");
            Log.i(TAG, "*** Headers Are ***: " + info.getAllHeaders().toString());

            request.read(ByteBuffer.allocateDirect(32 * 1024));
        }

        @Override
        public void onReadCompleted(
                UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            byteBuffer.flip();
            Log.i(TAG, "****** onReadCompleted ******  " + byteBuffer.toString());

            try {
                mReceiveChannel.write(byteBuffer);
            } catch (IOException e) {
                Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
            }
            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "****** Request Completed, status code is: " + info.getHttpStatusCode() + " , total received bytes is " + info.getReceivedBytesCount());

            final String receivedData = mBytesReceived.toString();
            Log.d(TAG, receivedData);
            final String url = info.getUrl();
            final String text = "Completed " + url + " (" + info.getHttpStatusCode() + ")";
            CronetSampleActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mQuicResultText.setText(text);
                    mQuicReceiveDataText.setText(receivedData);
                    promptForURL(url);
                }
            });
            if (!QuicFinishFlag) {
                QuicFinishFlag = true;
                Log.d(TAG, "set quic finish flag to true");
            }
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, UrlRequestException error) {
            Log.i(TAG, "****** onFailed, error is: " + error.getMessage());

            final String url = mUrl;
            final String text = "Failed " + mUrl + " (" + error.getMessage() + ")";
            CronetSampleActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mQuicResultText.setText(text);
                    promptForURL(url);
                }
            });
            if (!QuicFinishFlag) {
                QuicFinishFlag = true;
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQuicResultText = (TextView) findViewById(R.id.Quic_resultView);
        mQuicReceiveDataText = (TextView) findViewById(R.id.Quic_dataView);
        mQuicReceiveDataText.setMovementMethod(new ScrollingMovementMethod());

        mOkhttpResultText = (TextView) findViewById(R.id.OKHttp_resultView);
        mOkhttpReceiveDataText = (TextView) findViewById(R.id.OKHttp_dataView);
        mOkhttpReceiveDataText.setMovementMethod(new ScrollingMovementMethod());

        mUrlInput = (EditText) findViewById(R.id.urlText);
        mPostInput = (EditText) findViewById(R.id.postText);

        mQuicTimes = (EditText) findViewById(R.id.QuicTimes);
        mOKHttpTimes = (EditText) findViewById(R.id.OKHttpTimes);

        mQuicLoadBtn = (Button) findViewById(R.id.Quic_Load_Switch);
        mQuicBtn = (Button) findViewById(R.id.Quic_switch);
        mOkhttpLoadBtn = (Button) findViewById(R.id.OKHttp_switch);
        mOkhttpSynchronousBtn = (Button) findViewById(R.id.OKHttp_Synchronous_switch);

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);

        //myBuilder.enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 100 * 1024);
        //myBuilder.enableHttp2(true);
        myBuilder.enableQuic(true);
        QuicEnable = true;

        mCronetEngine = myBuilder.build();
        mOKHttpClient = new OkHttpClient();

        String appUrl = (getIntent() != null ? getIntent().getDataString() : null);
        if (appUrl == null) {
            promptForURL("https://104.199.184.104:8081");
        } else {
            startWithURL(appUrl);
        }
    }

    private void promptForURL(String url) {
        Log.i(TAG, "No URL provided via intent, prompting user...");

        mUrlInput.setText(url);

    }

    public void QuicLoadSwitch(View view) throws IOException {
        mQuicLoadBtn.setEnabled(false);
        mQuicLoadBtn.setText("Load ...");
        String url = mUrlInput.getText().toString();
        String postData = mPostInput.getText().toString();
        mQuicRuntimes = Integer.parseInt(mQuicTimes.getText().toString());
        Log.d(TAG, "Quic run times: " + mQuicRuntimes.toString());
//        for (int i = 0; i< mQuicRuntimes; i++) {
//            Log.d(TAG, "quic runtimes: " + String.valueOf(i));
//            int ii = 0;
//            while (!QuicFinishFlag) {
//
//                try{
//                    Log.d(TAG, "while times: " + String.valueOf(ii));
//                    Thread.sleep(1000);
//                } catch(InterruptedException e){
//                    Log.d(TAG, e.getMessage());
//                }
//            }
//            startWithURL(url, postData);
//        }
        startWithURL(url, postData);
        mQuicLoadBtn.setEnabled(true);
        mQuicLoadBtn.setText("Load");

    }

    public void OKHttpSwitch(View view) throws IOException {
        mOkhttpLoadBtn.setEnabled(false);
        mOkhttpLoadBtn.setText("Load ...");
        String url = mUrlInput.getText().toString();
        String postData = mPostInput.getText().toString();
        mOKHttpRuntimes = Integer.parseInt(mOKHttpTimes.getText().toString());
        Log.d(TAG, "okhttp run times: " + mOKHttpRuntimes.toString());
//        for (int i = 0; i< mOKHttpRuntimes; i++) {
//            Log.d(TAG, "okhttp runtimes: " + String.valueOf(i));
//            int ii = 0;
//            while (!OKHttpFinishFlag) {
//
//                try{
//                    Log.d(TAG, "OKHTTP while times: " + String.valueOf(ii));
//                    Thread.sleep(1000);
//                } catch(InterruptedException e){
//                    Log.d(TAG, e.getMessage());
//                }
//            }
//            makeOKHttpRequest(url, postData);
//        }
        makeOKHttpRequest(url, postData);
        mOkhttpLoadBtn.setEnabled(true);
        mOkhttpLoadBtn.setText("Load");

    }

    public void OKHttpSynchronousSwitch(View view) throws IOException {
        if (OkHttpSynchronousFlag) {
            OkHttpSynchronousFlag = false;
            mOkhttpSynchronousBtn.setText("ASYN");
        } else {
            OkHttpSynchronousFlag = true;
            mOkhttpSynchronousBtn.setText("SYN");
        }
    }

    public void QuicSwitch(View view) throws IOException {
        if (QuicEnable) {
            Log.d(TAG, "quic switch disable quic");
            QuicEnable = false;
            mQuicBtn.setText("QuicDisabled");
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);
            //myBuilder.enableHttp2(true);

            mCronetEngine = myBuilder.build();

        }
        else {
            Log.d(TAG, "quic switch enable quic");
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);
            //myBuilder.enableHttp2(true);
            myBuilder.enableQuic(true);

            mCronetEngine = myBuilder.build();
            QuicEnable = true;
            mQuicBtn.setText("QuicEnabled");
        }

    }

    private void applyPostDataToUrlRequestBuilder(
            UrlRequest.Builder builder, Executor executor, String postData) {
        if (postData != null && postData.length() > 0) {
            builder.setHttpMethod("POST");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
            builder.setUploadDataProvider(
                    UploadDataProviders.create(postData.getBytes()), executor);
        }
    }

    private void startWithURL(String url) {
        startWithURL(url, null);
    }

    private void startWithURL(String url, String postData) {
        QuicFinishFlag = false;
        Log.i(TAG, "Cronet started: " + url);
        mUrl = url;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Callback callback = new SimpleUrlRequestCallback();
        UrlRequest.Builder builder = new UrlRequest.Builder(url, callback, executor, mCronetEngine);
        applyPostDataToUrlRequestBuilder(builder, executor, postData);
        builder.build().start();
    }

    private void makeOKHttpRequest(String url, String postData) {
        Log.d(TAG, "okhttp request starts");
        OKHttpFinishFlag = false;
        if (postData.length() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            makeRequest(request);

        } else {
            RequestBody body = RequestBody.create(JSON, postData);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            makeRequest(request);
        }
    }



    private void makeRequest(Request request) {
        final Request mRequest = request;
        if (OkHttpSynchronousFlag) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Response response = mOKHttpClient.newCall(mRequest).execute();
                        final String receivedData = response.body().string();
                        final String url = mRequest.urlString();
                        final String text = "Completed " + url + " (" + response.code() + ")";
                        CronetSampleActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                            mOkhttpResultText.setText(text);
                            mOkhttpReceiveDataText.setText(receivedData);

                    }});
                        if (!OKHttpFinishFlag) {
                            OKHttpFinishFlag = true;
                            Log.d(TAG, "set okhttp finish flag to true");
                        }
                    } catch (IOException e) {
                        final String url = mRequest.urlString();
                        final String text = "Failed " + url + " (" + e.getMessage() + ")";
                        CronetSampleActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mOkhttpResultText.setText(text);

                            }
                        });
                        if (!OKHttpFinishFlag) {
                            OKHttpFinishFlag = true;
                            Log.d(TAG, "set okhttp finish flag to true");
                        }
                    }
                }
            }).start();
        } else {
            mOKHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    final String url = mUrl;
                    final String text = "Failed " + url + " (" + e.getMessage() + ")";
                    CronetSampleActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mOkhttpResultText.setText(text);

                        }
                    });
                    if (!OKHttpFinishFlag) {
                        OKHttpFinishFlag = true;
                        Log.d(TAG, "set okhttp finish flag to true");
                    }

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    final String receivedData = response.body().string();
                    Log.d(TAG, receivedData);
                    final String url = mUrl;
                    final String text = "Completed " + url + " (" + response.code() + ")";
                    CronetSampleActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mOkhttpResultText.setText(text);
                            mOkhttpReceiveDataText.setText(receivedData);
                        }
                    });
                    if (!OKHttpFinishFlag) {
                        OKHttpFinishFlag = true;
                        Log.d(TAG, "set okhttp finish flag to true");
                    }

                }
            });
        }
    }


    // Starts writing NetLog to disk. startNetLog() should be called afterwards.
    private void startNetLog() {
        mCronetEngine.startNetLogToFile(logPath + "/netlog.json", false);
    }

    // Stops writing NetLog to disk. Should be called after calling startNetLog().
    // NetLog can be downloaded afterwards via:
    //   adb root
    //   adb pull /data/data/org.chromium.cronet_sample_apk/cache/netlog.json
    // netlog.json can then be viewed in a Chrome tab navigated to chrome://net-internals/#import
    private void stopNetLog() {
        mCronetEngine.stopNetLog();
    }
}
