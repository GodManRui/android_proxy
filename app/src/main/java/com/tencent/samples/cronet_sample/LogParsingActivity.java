package com.tencent.samples.cronet_sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.tencent.samples.cronet_sample.adapter.DomAllLogAdapter;

public class LogParsingActivity extends AppCompatActivity {

    private ListView lvDomAllLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_log_parsing);
        lvDomAllLog = (ListView) findViewById(R.id.lv_dom_all_log);

        lvDomAllLog.setAdapter(new DomAllLogAdapter());
    }
}
