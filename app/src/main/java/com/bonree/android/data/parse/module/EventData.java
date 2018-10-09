package com.bonree.android.data.parse.module;

public class EventData {
    private ParamsData params;
    private int phase = 0;  //标志位  1为开始，2为结束
    private SourceData source;
    private String time;
    private int type = 0;

    public ParamsData getParams() {
        return params;
    }

    public void setParams(ParamsData params) {
        this.params = params;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public SourceData getSource() {
        return source;
    }

    public void setSource(SourceData source) {
        this.source = source;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
