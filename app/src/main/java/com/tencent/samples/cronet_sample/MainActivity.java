package com.tencent.samples.cronet_sample;/*
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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.tencent.samples.cronet_sample.data.ImageRepository;

import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.concurrent.atomic.AtomicLong;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "jerryzhuM";
    private SwipeRefreshLayout swipeRefreshLayout;
    private AtomicLong cronetLatency = new AtomicLong();
    private long quicTotalLatency;
    private long numberOfQuicImages;
    private long http2TotalLatency;
    private long numberOfHttp2Images;
    private ViewAdapter viewAdapter;

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.images_activity);
        setContentView(R.layout.fragment);
        setProxy();
        setUpToolbar();
        isWifiProxy();
        getProperty("ro.build.display.id", "unknown");
    }

    private void setProxy() {

        System.setProperty("192.host", "8888");

        final String authUser = "user";
        final String authPassword = "password";
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                authUser, authPassword.toCharArray());
                    }
                }
        );

        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.fragment_title)).setText(R.string.toolbar_title);
    }

    private boolean isWifiProxy() {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(this);
            proxyPort = android.net.Proxy.getPort(this);
        }
        Log.e("jerryzhu", "地址 : " + proxyAddress + "  端口: " + proxyPort);
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    public String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    private void enableWritingPermissionForLogging() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * This calculates and sets on the UI the latency of loading images with Cronet.
     *
     * @param cronetLatency
     */
    public void addCronetLatency(final long cronetLatency, int type) {

        if (type == 0) {
            quicTotalLatency += cronetLatency;
            numberOfQuicImages++;
        } else {
            http2TotalLatency += cronetLatency;
            numberOfHttp2Images++;
        }
        Log.i(TAG,
                numberOfQuicImages + "   Quic 请求结束, 总数量: " + ImageRepository.numberOfImages());
        Log.i(TAG,
                numberOfHttp2Images + "  Http2 请求结束, 总数量: " + ImageRepository.numberOfImages());

        if (numberOfQuicImages == ImageRepository.numberOfImages() || numberOfHttp2Images == ImageRepository.numberOfImages()) {
            final long averageLatency;
            if (type == 0) {
                averageLatency = quicTotalLatency / numberOfQuicImages;
                numberOfQuicImages = 0;
                quicTotalLatency = 0;
            } else {
                averageLatency = http2TotalLatency / numberOfHttp2Images;
                numberOfHttp2Images = 0;
                http2TotalLatency = 0;
            }
            android.util.Log.i(TAG,
                    "所有请求结束  , 平均延迟 " + averageLatency);
            final TextView quicTime = (TextView) findViewById(R.id.quic_time_label);
            final TextView httpsTime = (TextView) findViewById(R.id.https_time_label);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (type == 0) {
                        quicTime.setText(String.format(getResources()
                                .getString(R.string.quic_images_loaded), averageLatency));
                    } else {
                        httpsTime.setText(String.format(getResources()
                                .getString(R.string.https_images_loaded), averageLatency));

                    }
                }
            });

            this.cronetLatency.set(averageLatency);
        }
    }

}
