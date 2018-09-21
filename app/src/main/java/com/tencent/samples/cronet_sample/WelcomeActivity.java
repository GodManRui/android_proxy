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
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebView;


public class WelcomeActivity extends AppCompatActivity {


    private LinearLayout ll;

    @RequiresApi(api = VERSION_CODES.KITKAT)
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.welcome_layout);
        setUpToolbar();
        WebView.setWebContentsDebuggingEnabled(true);
        ((TextView) findViewById(R.id.welcome_introduction))
                .setText(R.string.welcome_introduction_text);
        ((TextView) findViewById(R.id.cronet_load_images))
                .setText(R.string.cronet_load_images_text);
        ll = (LinearLayout) findViewById(R.id.proto_fragment);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.welcome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.welcome_title)).setText(R.string.welcome_activity);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void openImages(View view) {
        /*String url = "https://translate.google.cn/";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));*/
      /*  Intent mpdIntent = new Intent(this, MainActivity.class);
        startActivity(mpdIntent);*/
        startActivity(new Intent(this, QuicWebViewActivity.class));

    }


}
