package com.tencent.samples.cronet_sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.samples.cronet_sample.R;
import com.tencent.samples.cronet_sample.data.parse.module.NetRecord;

import java.util.List;

public class DomAllLogAdapter extends BaseAdapter {
    private Context context;
    private List<NetRecord> mDomList;

    public DomAllLogAdapter(Context context, List<NetRecord> mDomList) {
        this.mDomList = mDomList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mDomList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetRecord netRecord = mDomList.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_dom_all_log, null);
        TextView tvHost = (TextView) convertView.findViewById(R.id.tv_host);
        TextView tvPreview = (TextView) convertView.findViewById(R.id.tv_preview);
        String url = netRecord.getUrl();
        tvHost.setText((position + 1) + ": " + url);

        int receiveCount = netRecord.getReceiveCount();
        int conn = netRecord.getElement().getTotal();
        tvPreview.setText("Status:" + 200 + "   Received:" + receiveCount + "bytes   Cost:" + conn);
        return convertView;
    }
}
