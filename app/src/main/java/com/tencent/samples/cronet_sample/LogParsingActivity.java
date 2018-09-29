package com.tencent.samples.cronet_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.tencent.samples.cronet_sample.adapter.DomAllLogAdapter;
import com.tencent.samples.cronet_sample.data.Timing;
import com.tencent.samples.cronet_sample.data.parse.module.NetRecord;

import java.util.List;

public class LogParsingActivity extends AppCompatActivity {

    private ListView lvDomAllLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_log_parsing);
        lvDomAllLog = (ListView) findViewById(R.id.lv_dom_all_log);
        Timing timing = (Timing) getIntent().getSerializableExtra("list");
        List<NetRecord> netList = timing.getNetList();
        DomAllLogAdapter adapter = new DomAllLogAdapter(this, netList);
        lvDomAllLog.setAdapter(adapter);
        lvDomAllLog.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(LogParsingActivity.this, LogInfoActivity.class);
            intent.putExtra("logInfo", netList.get(position));
            startActivity(intent);
        });
    }
}
