package com.tencent.samples.cronet_sample.data;

import com.tencent.samples.cronet_sample.data.parse.module.NetRecord;

import java.util.ArrayList;
import java.util.List;

public class Timing {

    /**
     * navigationStart : 1535624596116      准备加载新页面
     * unloadEventStart : 0
     * unloadEventEnd : 0
     * redirectStart : 0
     * redirectEnd : 0
     * fetchStart : 1535624596124           8ms
     * domainLookupStart : 1535624596124
     * domainLookupEnd : 1535624596124
     * connectStart : 1535624596124
     * connectEnd : 1535624596124
     * secureConnectionStart : 0
     * requestStart : 1535624596124
     * responseStart : 1535624596124
     * responseEnd : 1535624596389       265ms
     * domLoading : 1535624596392         3m                    Dom树开始解析
     * domInteractive : 1535624598214       1822ms              DOM结构结束解析、开始加载内嵌资源时
     * domContentLoadedEventStart : 1535624598215   1ms         DOM结构解析完毕、所有脚本开始运行 JS CSS 开始加载
     * domContentLoadedEventEnd : 1535624598260     45ms        js脚本 CSS解析完毕
     * domComplete : 1535624598631                  371ms       Dom树完成解析
     * loadEventStart : 1535624598631
     * loadEventEnd : 1535624598632                 1ms
     */

    private long navigationStart;
    private long unloadEventStart;
    private long unloadEventEnd;
    private long redirectStart;
    private long redirectEnd;
    private long fetchStart;
    private long domainLookupStart;
    private long domainLookupEnd;
    private long connectStart;
    private long connectEnd;
    private long secureConnectionStart;
    private long requestStart;
    private long responseStart;
    private long responseEnd;
    private long domLoading;
    private long domInteractive;
    private long domContentLoadedEventStart;
    private long domContentLoadedEventEnd;
    private long domComplete;
    private long loadEventStart;
    private long loadEventEnd;
    List<NetRecord> netList = new ArrayList<NetRecord>();

    public List<NetRecord> getNetList() {
        return netList;
    }

    public void setNetList(List<NetRecord> netList) {
        this.netList = netList;
    }

    public long getNavigationStart() {
        return navigationStart;
    }

    public void setNavigationStart(long navigationStart) {
        this.navigationStart = navigationStart;
    }

    public long getUnloadEventStart() {
        return unloadEventStart;
    }

    public void setUnloadEventStart(int unloadEventStart) {
        this.unloadEventStart = unloadEventStart;
    }

    public long getUnloadEventEnd() {
        return unloadEventEnd;
    }

    public void setUnloadEventEnd(int unloadEventEnd) {
        this.unloadEventEnd = unloadEventEnd;
    }

    public long getRedirectStart() {
        return redirectStart;
    }

    public void setRedirectStart(int redirectStart) {
        this.redirectStart = redirectStart;
    }

    public long getRedirectEnd() {
        return redirectEnd;
    }

    public void setRedirectEnd(int redirectEnd) {
        this.redirectEnd = redirectEnd;
    }

    public long getFetchStart() {
        return fetchStart;
    }

    public void setFetchStart(long fetchStart) {
        this.fetchStart = fetchStart;
    }

    public long getDomainLookupStart() {
        return domainLookupStart;
    }

    public void setDomainLookupStart(long domainLookupStart) {
        this.domainLookupStart = domainLookupStart;
    }

    public long getDomainLookupEnd() {
        return domainLookupEnd;
    }

    public void setDomainLookupEnd(long domainLookupEnd) {
        this.domainLookupEnd = domainLookupEnd;
    }

    public long getConnectStart() {
        return connectStart;
    }

    public void setConnectStart(long connectStart) {
        this.connectStart = connectStart;
    }

    public long getConnectEnd() {
        return connectEnd;
    }

    public void setConnectEnd(long connectEnd) {
        this.connectEnd = connectEnd;
    }

    public long getSecureConnectionStart() {
        return secureConnectionStart;
    }

    public void setSecureConnectionStart(int secureConnectionStart) {
        this.secureConnectionStart = secureConnectionStart;
    }

    public long getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(long requestStart) {
        this.requestStart = requestStart;
    }

    public long getResponseStart() {
        return responseStart;
    }

    public void setResponseStart(long responseStart) {
        this.responseStart = responseStart;
    }

    public long getResponseEnd() {
        return responseEnd;
    }

    public void setResponseEnd(long responseEnd) {
        this.responseEnd = responseEnd;
    }

    public long getDomLoading() {
        return domLoading;
    }

    public void setDomLoading(long domLoading) {
        this.domLoading = domLoading;
    }

    public long getDomInteractive() {
        return domInteractive;
    }

    public void setDomInteractive(long domInteractive) {
        this.domInteractive = domInteractive;
    }

    public long getDomContentLoadedEventStart() {
        return domContentLoadedEventStart;
    }

    public void setDomContentLoadedEventStart(long domContentLoadedEventStart) {
        this.domContentLoadedEventStart = domContentLoadedEventStart;
    }

    public long getDomContentLoadedEventEnd() {
        return domContentLoadedEventEnd;
    }

    public void setDomContentLoadedEventEnd(long domContentLoadedEventEnd) {
        this.domContentLoadedEventEnd = domContentLoadedEventEnd;
    }

    public long getDomComplete() {
        return domComplete;
    }

    public void setDomComplete(long domComplete) {
        this.domComplete = domComplete;
    }

    public long getLoadEventStart() {
        return loadEventStart;
    }

    public void setLoadEventStart(long loadEventStart) {
        this.loadEventStart = loadEventStart;
    }

    public long getLoadEventEnd() {
        return loadEventEnd;
    }

    public void setLoadEventEnd(long loadEventEnd) {
        this.loadEventEnd = loadEventEnd;
    }
}
