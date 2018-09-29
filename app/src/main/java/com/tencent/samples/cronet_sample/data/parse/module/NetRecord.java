package com.tencent.samples.cronet_sample.data.parse.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NetRecord implements Serializable {
    private int key;//eventType key
    private long hostStartTime;// 这个域名起始时间
    private String url;//单个测试地址                     ****
    private String status;

    private String hostname;//域名
    private String ip;//ip
    private String port;//端口

    private boolean isquic = false;//是否quic
    private boolean isRedirceted = false;//是否重定向

    private int connStart;//建联起始时间
    private int connEnd;//建联结束时间

    private int requestStart;//请求起始时间
    private int requestEnd;//请求起始时间
    private int responseStart;//响应起始时间
    private int responseEnd;//响应起始时间

    private List<Integer> receiveStart = new ArrayList<Integer>();
    private List<Integer> receiveEnd = new ArrayList<Integer>();//每个包下载结束时间

    private int sslStart;//ssl起始时间
    private int sslEnd;//ssl请求起始时间

    private int receiveCount = 0;

    private String requestHeader;
    private String responseHeader;

    private int errorCode;

    private long dnsStart;
    private long dnsEnd = 0;

    private List<String> ipList = new ArrayList<String>();// 一个域名可能对应对个Ip
    private int dnserrorCode;

    private Element element;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Element getElement() {
        return element;
    }

    public void setElement() {
        this.element = new Element(this);
    }

    public boolean isRedirceted() {
        return isRedirceted;
    }

    public void setRedirceted(boolean isRedirceted) {
        this.isRedirceted = isRedirceted;
    }

    public boolean isIsquic() {
        return isquic;
    }

    public void setIsquic(boolean isquic) {
        this.isquic = isquic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDnsStart() {
        return dnsStart;
    }

    public void setDnsStart(long dnsStart) {
        this.dnsStart = dnsStart;
    }

    public long getDnsEnd() {
        return dnsEnd;
    }

    public void setDnsEnd(long dnsEnd) {
        this.dnsEnd = dnsEnd;
    }

    public List<String> getIpList() {
        return ipList;
    }

    public void setIpList(List<String> ipList) {
        this.ipList = ipList;
    }

    public int getDnserrorCode() {
        return dnserrorCode;
    }

    public void setDnserrorCode(int dnserrorCode) {
        this.dnserrorCode = dnserrorCode;
    }

    public long getHostStartTime() {
        return hostStartTime;
    }

    public void setHostStartTime(long hostStartTime) {
        this.hostStartTime = hostStartTime;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getConnStart() {
        return connStart;
    }

    public void setConnStart(int connStart) {
        this.connStart = connStart;
    }

    public int getConnEnd() {
        return connEnd;
    }

    public void setConnEnd(int connEnd) {
        this.connEnd = connEnd;
    }

    public int getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(int requestStart) {
        this.requestStart = requestStart;
    }

    public int getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(int requestEnd) {
        this.requestEnd = requestEnd;
    }

    public int getResponseStart() {
        return responseStart;
    }

    public void setResponseStart(int responseStart) {
        this.responseStart = responseStart;
    }

    public int getResponseEnd() {
        return responseEnd;
    }

    public void setResponseEnd(int responseEnd) {
        this.responseEnd = responseEnd;
    }

    public List<Integer> getReceiveStart() {
        return receiveStart;
    }

    public void setReceiveStart(int receiveStart) {
        this.receiveStart.add(receiveStart);
    }

    public List<Integer> getReceiveEnd() {
        return receiveEnd;
    }

    public void setReceiveEnd(int receiveEnd) {
        this.receiveEnd.add(receiveEnd);
    }

    public int getSslStart() {
        return sslStart;
    }

    public void setSslStart(int sslStart) {
        this.sslStart = sslStart;
    }

    public int getSslEnd() {
        return sslEnd;
    }

    public void setSslEnd(int sslEnd) {
        this.sslEnd = sslEnd;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "NetRecord [key=" + key + ", hostStartTime=" + hostStartTime
                + ", url=" + url + ", hostname=" + hostname + ", ip=" + ip
                + ", port=" + port + ", isquic=" + isquic + ", isRedirceted="
                + isRedirceted + ", connStart=" + connStart + ", connEnd="
                + connEnd + ", requestStart=" + requestStart + ", requestEnd="
                + requestEnd + ", responseStart=" + responseStart
                + ", responseEnd=" + responseEnd + ", receiveStart="
                + receiveStart + ", receiveEnd=" + receiveEnd + ", sslStart="
                + sslStart + ", sslEnd=" + sslEnd + ", receiveCount="
                + receiveCount + ", requestHeader=" + requestHeader
                + ", responseHeader=" + responseHeader + ", errorCode="
                + errorCode + ", dnsStart=" + dnsStart + ", dnsEnd=" + dnsEnd
                + ", ipList=" + ipList + ", dnserrorCode=" + dnserrorCode + "]";
    }

}
