package com.tencent.samples.cronet_sample.data;

import java.util.ArrayList;
import java.util.List;

public class HtmlElement {
    public int redirectCount;
    private String url;
    private long netStartTime;
    private long netEndTime;
    private List<Timing> doms = new ArrayList<>();

    public HtmlElement(String url) {
        this.url = url;
    }

    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }

    public long getNetStartTime() {
        return netStartTime;
    }

    public void setNetStartTime(long netStartTime) {
        this.netStartTime = netStartTime;
    }

    public long getNetEndTime() {
        return netEndTime;
    }

    public void setNetEndTime(long netEndTime) {
        this.netEndTime = netEndTime;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<Timing> getDoms() {
        if (doms == null) {
            return new ArrayList<>();
        }
        return doms;
    }

    public void setDoms(List<Timing> doms) {
        this.doms = doms;
    }

    public static class Timing {
        private String url;
        private long netStartTime;  //纳秒级别
        private long netEndTime;

        private String dnsLookup;   //DNS查询的时间
        private String blocking;    //浏览器发请求前本地的操作时间，比如去缓存中查看页面缓存等。     stalled

        //约等于收到真正响应，实际是解析完head开始接受body
        private long startWaitingResponse;  // 开始发送到收到响应的时间 中间包括dnslookup，blocking ，waiting重定向等

        private long connecting;  //建立TCP连接的时间，就相当于客户端从发请求开始到TCP握手结束这一段，包括DNS查询+Proxy时间+TCP握手时间
        private String waiting;     //发送请求完毕到接收请求开始的时间。
        private long contentDownload; //接收数据时间    Receiving

        public Timing(String url) {
            this.url = url;
        }

        public long getStartWaitingResponse() {
            return startWaitingResponse;
        }

        public void setStartWaitingResponse(long startWaitingResponse) {
            this.startWaitingResponse = startWaitingResponse;
        }

        public long getNetEndTime() {
            return netEndTime;
        }

        public void setNetEndTime(long netEndTime) {
            this.netEndTime = netEndTime;
        }

        public long getNetStartTime() {
            return netStartTime;
        }

        public void setNetStartTime(long netStartTime) {
            this.netStartTime = netStartTime;
        }

        public String getUrl() {
            return url == null ? "" : url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDnsLookup() {
            return dnsLookup == null ? "" : dnsLookup;
        }

        public void setDnsLookup(String dnsLookup) {
            this.dnsLookup = dnsLookup;
        }

        public String getBlocking() {
            return blocking == null ? "" : blocking;
        }

        public void setBlocking(String blocking) {
            this.blocking = blocking;
        }

        public long getContentDownload() {
            return contentDownload;
        }

        public void setContentDownload(long connecting) {
            this.contentDownload = connecting - startWaitingResponse;
        }

        public String getWaiting() {
            return waiting == null ? "" : waiting;
        }

        public void setWaiting(String waiting) {
            this.waiting = waiting;
        }


    }

}
