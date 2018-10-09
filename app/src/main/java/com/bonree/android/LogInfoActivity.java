package com.bonree.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonree.android.data.parse.module.NetRecord;

public class LogInfoActivity extends AppCompatActivity {

    private NetRecord mNetRecordData;
    private LinearLayout lvLogInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_parsing_info);
        lvLogInfo = (LinearLayout) findViewById(R.id.ll_log_info);
        /*ListView lvLogInfo = (ListView) findViewById(R.id.lv_dom_all_log);*/
        mNetRecordData = (NetRecord) getIntent().getSerializableExtra("logInfo");
        for (int i = 0; i < 9; i++) {
            TextView tv = new TextView(this);
            tv.setText(createText(i));
            tv.setTextSize(16f);
            lvLogInfo.addView(tv);
        }
    }

    private String createText(int i) {
        switch (i) {
            case 0:
                return "\n  URL    " + mNetRecordData.getUrl() + "\n";
            case 1:
                return "    Destionation   " + mNetRecordData.getIp() + "\n";
            case 2:
                return "    DNS Lookup   " + mNetRecordData.getElement().getDns() + " ms \n";
            case 3:
                return "    Send   " + mNetRecordData.getElement().getRequest() + " ms \n";
            case 4:
                return "    Wait   " + mNetRecordData.getElement().getResponse() + " ms \n";
            case 5:
                return "    Receive   " + mNetRecordData.getElement().getReceive() + " ms \n";
            case 6:
                return "    Total   " + mNetRecordData.getReceiveCount() + " bytes \n";
            case 7:
                return "    Request   \n  " + mNetRecordData.getRequestHeader() + "\n";
            case 8:
                return "    Response   \n  " + mNetRecordData.getResponseHeader() + "\n";
        }
        return "";
    }
}
