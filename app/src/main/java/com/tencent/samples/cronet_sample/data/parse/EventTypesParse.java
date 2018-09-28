package com.tencent.samples.cronet_sample.data.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

import com.tencent.samples.cronet_sample.data.parse.module.EventData;
import com.tencent.samples.cronet_sample.data.parse.module.NetRecord;
import com.tencent.samples.cronet_sample.data.parse.module.SourceData;
import com.tencent.samples.cronet_sample.data.parse.type.LogEventTypes;
import com.tencent.samples.cronet_sample.data.parse.type.LogSourceType;

public class EventTypesParse {
    public static List<NetRecord> ParseEventTypes(int key, List<EventData> dataList) {
        List<NetRecord> list = new ArrayList<NetRecord>();
        NetRecord netRecord = new NetRecord();
        Map<Integer, String> idMap = new HashMap<Integer, String>();
        idMap.put(key, "");
        netRecord.setKey(key);
        if (dataList.size() > 0) {
            netRecord.setHostStartTime(Long.parseLong(dataList.get(0).getTime()));
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (!parseData(dataList.get(i), netRecord, idMap)) {
                netRecord.setElement();
                list.add(netRecord);
                netRecord = new NetRecord();
                netRecord.setHostStartTime(Long.parseLong(dataList.get(0).getTime()));
                netRecord.setKey(key);
                netRecord.setRedirceted(true);
            }
        }
        netRecord.setElement();
        list.add(netRecord);
        return list;
    }

    /**
     * 判断是否有关联events
     *
     * @param data
     * @param netRecord
     * @param map
     */
    private static void hasSourceDependency(EventData data, NetRecord netRecord, Map<Integer, String> map) {
        if (data.getParams() != null && data.getParams().getSource_dependency() != null) {
            SourceData sourceData = data.getParams().getSource_dependency();
            if (map.containsKey(sourceData.getId()) || sourceData.getType() == LogSourceType.URL_REQUEST/*||sourceData.getType() == LogSourceType.HOST_RESOLVER_IMPL_JOB*/) {

            } else {
                map.put(sourceData.getId(), "");
                if (QuicDataParse.dataMap.containsKey(sourceData.getId())) {
                    ParseEvent(QuicDataParse.dataMap.get(sourceData.getId()), netRecord, map);
                }
            }
        }
    }

    /**
     * 解析具体指标项
     */
    private static boolean parseData(EventData data, NetRecord netRecord, Map<Integer, String> map) {
        hasSourceDependency(data, netRecord, map);
        switch (data.getType()) {
            case LogEventTypes.REQUEST_ALIVE:
            case LogEventTypes.URL_REQUEST_START_JOB:
                if (data.getPhase() == 1 && netRecord.getUrl() == null) {
                    if (data.getParams() != null && data.getParams().getUrl() != null) {
                        netRecord.setUrl(data.getParams().getUrl());
                    }
                }
                break;
            case LogEventTypes.TCP_CONNECT:
                if (data.getPhase() == 1) {
                    netRecord.setConnStart(Integer.parseInt(data.getTime()));
                } else if (data.getPhase() == 2) {
                    netRecord.setConnEnd(Integer.parseInt(data.getTime()));
                }
                break;
            case LogEventTypes.SSL_CONNECT:
                if (data.getPhase() == 1) {
                    netRecord.setSslStart(Integer.parseInt(data.getTime()));
                } else if (data.getPhase() == 2) {
                    netRecord.setSslEnd(Integer.parseInt(data.getTime()));
                }
                break;

            case LogEventTypes.TCP_CONNECT_ATTEMPT:
                if (data.getParams() != null && data.getParams().getAddress() != null) {
                    netRecord.setIp(data.getParams().getAddress());
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_SEND_REQUEST:// 请求时间
                if (data.getPhase() == 1) {
                    netRecord.setRequestStart(Integer.parseInt(data.getTime()));
                } else if (data.getPhase() == 2) {
                    netRecord.setRequestEnd(Integer.parseInt(data.getTime()));
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_READ_HEADERS:// 响应时间
                if (data.getPhase() == 1) {
                    netRecord.setResponseStart(Integer.parseInt(data.getTime()));
                } else if (data.getPhase() == 2) {
                    netRecord.setResponseEnd(Integer.parseInt(data.getTime()));
                }
                break;
            case LogEventTypes.URL_REQUEST_JOB_FILTERED_BYTES_READ:
            case LogEventTypes.URL_REQUEST_JOB_BYTES_READ:
                netRecord.setReceiveEnd(Integer.parseInt(data.getTime()));
                if (data.getParams() != null
                        && data.getParams().getByte_count() != 0) {
                    netRecord.setReceiveCount(netRecord.getReceiveCount()
                            + data.getParams().getByte_count());
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_HTTP2_SEND_REQUEST_HEADERS:
                if (data.getParams() != null && data.getParams().getHeaders() != null) {
                    netRecord.setRequestHeader(data.getParams().getHeaders());
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_QUIC_SEND_REQUEST_HEADERS:
                if (data.getParams() != null && data.getParams().getHeaders() != null) {
                    netRecord.setRequestHeader(data.getParams().getHeaders());
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_SEND_REQUEST_HEADERS:
                if (data.getParams() != null && data.getParams().getHeaders() != null) {
                    netRecord.setRequestHeader(data.getParams().getHeaders());
                }
                break;
            case LogEventTypes.HTTP_TRANSACTION_READ_RESPONSE_HEADERS:
                if (data.getParams() != null && data.getParams().getHeaders() != null) {
                    netRecord.setResponseHeader(data.getParams().getHeaders());
                } else if (data.getParams() != null && data.getParams().getNet_error() != 0) {
                    netRecord.setErrorCode(data.getParams().getNet_error());
                }
                break;

//		case LogEventTypes.HTTP_STREAM_JOB_CONTROLLER_BOUND://关联events
//		case LogEventTypes.HTTP_STREAM_REQUEST_BOUND_TO_JOB:
//			if(data.getParams()!= null  && data.getParams().getSource_dependency()!= null){
//				SourceData sourceData = data.getParams().getSource_dependency();
//				if(map.containsKey(sourceData.getId())){
//					
//				}else{
//					map.put(sourceData.getId(), "");
//					if(QuicDataParse.dataMap.containsKey(sourceData.getId())){						
//						ParseEvent(QuicDataParse.dataMap.get(sourceData.getId()),netRecord,map);
//					}
//				}
//			}
//			break;
            case LogEventTypes.HTTP_STREAM_JOB:
                if (data.getParams() != null && data.getParams().getUsing_quic() != null && data.getParams().getUsing_quic().equals("true")) {
                    netRecord.setIsquic(true);
                }
                break;
            case LogEventTypes.QUIC_SESSION:
                netRecord.setIsquic(true);
                break;

            case LogEventTypes.HOST_RESOLVER_IMPL_REQUEST:
                if (data.getParams() != null && data.getParams().getHost() != null && netRecord.getHostname() == null) {
                    netRecord.setHostname(data.getParams().getHost());
                }

                break;
            case LogEventTypes.HOST_RESOLVER_IMPL_CACHE_HIT://解析hosts

                if (data.getParams() != null && data.getParams().getAddress_list() != null) {
                    if (netRecord.getDnsEnd() == 0 || netRecord.getDnsEnd() > Long.parseLong(data.getTime())) {
                        netRecord.setDnsEnd(Long.parseLong(data.getTime()));
                        netRecord.setIpList(parseAddressList(data.getParams().getAddress_list()));
                    }
                } else if (data.getParams() != null && data.getParams().getNet_error() != 0) {
                    netRecord.setErrorCode(data.getParams().getNet_error());
                }
                break;

            case LogEventTypes.URL_REQUEST_REDIRECTED://重定向解析

                return false;
            default:
                break;
        }
        return true;
    }

    private static void ParseEvent(List<EventData> dataList, NetRecord netRecord, Map<Integer, String> map) {
        ParseEventTypes(dataList, netRecord, map);
    }

    //解析request后面的
    public static void ParseEventTypes(List<EventData> dataList, NetRecord netRecord, Map<Integer, String> map) {
        for (int i = 0; i < dataList.size(); i++) {
            parseData(dataList.get(i), netRecord, map);
        }
    }


    private static List<String> parseAddressList(String addressList) {
        JSONArray jsonArray = JSONArray.parseArray(addressList);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

}
