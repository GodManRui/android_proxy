package com.bonree.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.bonree.android.adapter.DomAllLogAdapter;
import com.bonree.android.data.Timing;
import com.bonree.android.data.parse.module.NetRecord;

import java.util.List;

public class LogParsingActivity extends AppCompatActivity {

    private ListView lvDomAllLog;
    private TextView tvTiming;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_log_parsing);
        lvDomAllLog = (ListView) findViewById(R.id.lv_dom_all_log);
        tvTiming = (TextView) findViewById(R.id.tv_timing);

        Timing timing = (Timing) getIntent().getSerializableExtra("list");
        String text = "     Dom Loading : " + (timing.getDomComplete() - timing.getDomInteractive())
                + "ms \n     JS CSS loading :" + (timing.getDomContentLoadedEventEnd() - timing.getDomContentLoadedEventStart())
                + "ms  \n     白屏时间: " + (timing.getDomInteractive() - timing.getNavigationStart())
                + "ms \n      onLoad执行时间:" + (timing.getLoadEventEnd() - timing.getLoadEventStart() + "ms");
        tvTiming.setText(text);
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
