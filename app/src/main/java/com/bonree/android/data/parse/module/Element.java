package com.bonree.android.data.parse.module;

import java.io.Serializable;

public class Element implements Serializable {
	private int dns;//
	private int conn;//建联时间
	private int ssl;
	private int request;	//发送请求耗时   send
	private int response;	//服务器开始响应耗时  wait
	private int receive;	//下载耗时
	private int total;		//子元素总耗时
	
	public Element(NetRecord netRecord){
		if(netRecord.getHostStartTime() != 0 && netRecord.getDnsEnd() != 0&&netRecord.getDnsEnd() >= netRecord.getHostStartTime()){
			this.dns = (int) (netRecord.getDnsEnd() - netRecord.getHostStartTime());
		}
		if(netRecord.getConnStart() != 0 && netRecord.getConnEnd() != 0 && netRecord.getConnEnd() >= netRecord.getConnStart()){
			this.conn = netRecord.getConnEnd() - netRecord.getConnStart();
		}
		if(netRecord.getSslStart()!= 0 && netRecord.getSslEnd() != 0 &&netRecord.getSslEnd() >= netRecord.getSslStart()){
			this.ssl = netRecord.getSslEnd() - netRecord.getSslStart();
		}
		if(netRecord.getRequestStart() != 0 && netRecord.getRequestEnd() != 0 && netRecord.getRequestEnd() >= netRecord.getRequestStart()){
			this.request = netRecord.getRequestEnd() - netRecord.getRequestStart();
		}
		if(netRecord.getResponseStart()!= 0 && netRecord.getResponseEnd()!= 0&&netRecord.getResponseEnd() >= netRecord.getResponseStart()){
			this.response = netRecord.getResponseEnd() - netRecord.getResponseStart();
		}
		if(netRecord.getReceiveEnd()!=null&&netRecord.getReceiveEnd().size() != 0 && netRecord.getResponseEnd() != 0){
			this.receive = netRecord.getReceiveEnd().get(netRecord.getReceiveEnd().size()-1) - netRecord.getResponseEnd();
		}
		if(netRecord.getReceiveEnd()!=null&&netRecord.getReceiveEnd().size() != 0 && netRecord.getHostStartTime() != 0){
			this.total = (int) (netRecord.getReceiveEnd().get(netRecord.getReceiveEnd().size()-1) - netRecord.getHostStartTime());
		}
	}

	public int getDns() {
		return dns;
	}

	public int getConn() {
		return conn;
	}

	public int getSsl() {
		return ssl;
	}

	public int getRequest() {
		return request;
	}

	public int getResponse() {
		return response;
	}

	public int getReceive() {
		return receive;
	}

	public int getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return "Element [dns=" + dns + ", conn=" + conn + ", ssl=" + ssl
				+ ", request=" + request + ", response=" + response
				+ ", receive=" + receive + ", total=" + total + "]";
	}
}
