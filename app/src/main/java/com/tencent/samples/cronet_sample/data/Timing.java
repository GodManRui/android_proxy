package com.tencent.samples.cronet_sample.data;

public class Timing {

    /**
     * navigationStart : 1535624596116
     * unloadEventStart : 0
     * unloadEventEnd : 0
     * redirectStart : 0
     * redirectEnd : 0
     * fetchStart : 1535624596124
     * domainLookupStart : 1535624596124
     * domainLookupEnd : 1535624596124
     * connectStart : 1535624596124
     * connectEnd : 1535624596124
     * secureConnectionStart : 0
     * requestStart : 1535624596124
     * responseStart : 1535624596124
     * responseEnd : 1535624596389
     * domLoading : 1535624596392
     * domInteractive : 1535624598214
     * domContentLoadedEventStart : 1535624598215
     * domContentLoadedEventEnd : 1535624598260
     * domComplete : 1535624598631
     * loadEventStart : 1535624598631
     * loadEventEnd : 1535624598632
     */

    private long navigationStart;
    private int unloadEventStart;
    private int unloadEventEnd;
    private int redirectStart;
    private int redirectEnd;
    private long fetchStart;
    private long domainLookupStart;
    private long domainLookupEnd;
    private long connectStart;
    private long connectEnd;
    private int secureConnectionStart;
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

    public long getNavigationStart() {
        return navigationStart;
    }

    public void setNavigationStart(long navigationStart) {
        this.navigationStart = navigationStart;
    }

    public int getUnloadEventStart() {
        return unloadEventStart;
    }

    public void setUnloadEventStart(int unloadEventStart) {
        this.unloadEventStart = unloadEventStart;
    }

    public int getUnloadEventEnd() {
        return unloadEventEnd;
    }

    public void setUnloadEventEnd(int unloadEventEnd) {
        this.unloadEventEnd = unloadEventEnd;
    }

    public int getRedirectStart() {
        return redirectStart;
    }

    public void setRedirectStart(int redirectStart) {
        this.redirectStart = redirectStart;
    }

    public int getRedirectEnd() {
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

    public int getSecureConnectionStart() {
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
