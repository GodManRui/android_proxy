package com.bonree.android.data.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonree.android.data.parse.module.EventData;
import com.bonree.android.data.parse.module.NetRecord;
import com.bonree.android.data.parse.module.ParamsData;
import com.bonree.android.data.parse.module.SourceData;
import com.bonree.android.data.parse.type.LogSourceType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QuicDataParse {
	static Map<Integer, List<String>> mapList = new HashMap<Integer, List<String>>();
	static Map<Integer, List<EventData>> dataMap = new HashMap<Integer, List<EventData>>();
	static long startTime = 0;
	static String loadUrl = "https://www.gstatic.cn/images/icons/material/system/2x/mic_black_24dp.png";
	static List<EventData> edList;
	static List<NetRecord> netList = new ArrayList<NetRecord>();

	public static List<NetRecord> parseLog(String logpath) {
		File file = new File(logpath);
		try {
			BufferedInputStream inputStream = new BufferedInputStream(
					new FileInputStream(file));
			byte[] b = new byte[1024];
			String a = "";
			int length = 0;
			while ((length = inputStream.read(b)) != -1) {
				a += new String(b, 0, length);
			}
			System.out.println(a.length());

			inputStream.close();
			return parseJson(a);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<NetRecord> parseJson(String a) {
		JSONObject json = JSONObject.parseObject(a);
		JSONArray array = json.getJSONArray("events");
		for (int i = 0; i < array.size(); i++) {
			int id = JSONObject.parseObject(JSONObject.parseObject(array.get(i).toString()).get("source").toString()).getInteger("id");
			if (mapList.containsKey(id)) {
				mapList.get(id).add(array.get(i).toString());
			} else {
				List<String> list = new ArrayList<String>();
				list.add(array.get(i).toString());
				mapList.put(id, list);
			}
		}

		for (Entry<Integer, List<String>> entry : mapList.entrySet()) {
			handleId(entry.getKey(), entry.getValue());
		}
		
		for (Entry<Integer, List<EventData>> entry : dataMap.entrySet()) {
			ParseEvent(entry.getKey(), entry.getValue());
		}
		return netList;
//		System.out.println(startTime);
//		System.out.println(netList.size());
//		for (int i = 0; i <netList.size(); i++) {
//			System.out.println(netList.get(i).toString());
//			System.out.println(netList.get(i).getElement().toString());
//		}
	}

	//统一
	private static void handleId(int key, List<String> list) {
		edList = new ArrayList<EventData>();
		for (int i = 0; i < list.size(); i++) {
			edList.add(eventJson(list.get(i)));
		}
		if (edList.get(0).getSource().getType() == LogSourceType.URL_REQUEST) {//获取起始时间
			for (int i = 0; i < edList.size(); i++) {
				if (edList.get(i).getType() == 2) {
					if(edList.get(i).getParams()!=null&&edList.get(i).getParams().getUrl()!=null&&edList.get(i).getParams().getUrl().equals(QuicDataParse.loadUrl)){
						if(Long.parseLong(edList.get(i).getTime())<QuicDataParse.startTime||QuicDataParse.startTime == 0){					
							QuicDataParse.startTime = Long.parseLong(edList.get(i).getTime());
						}
					}
				} 
			}
		}
		dataMap.put(key, edList);
	}

	//判断event的类型 处理URL_REQUEST
	private static void ParseEvent(int key,List<EventData> dataList) {
		switch (dataList.get(0).getSource().getType()) {
		case LogSourceType.URL_REQUEST:
			netList.addAll(EventTypesParse.ParseEventTypes(key, dataList));
			break;
		default:
			break;
		}
	}

	private static EventData eventJson(String eventStr) {
		JSONObject jsonObject = JSONObject.parseObject(eventStr);
		EventData data = new EventData();
		if (jsonObject.containsKey("time")) {
			data.setTime(jsonObject.getString("time"));
		}
		if (jsonObject.containsKey("source")) {
			data.setSource(sourceJson(jsonObject.getString("source")));
		}
		if (jsonObject.containsKey("params")) {
			data.setParams(paramsJson(jsonObject.getString("params")));
		}
		if (jsonObject.containsKey("type")) {
			data.setType(jsonObject.getInteger("type"));
		}
		if (jsonObject.containsKey("phase")) {
			data.setPhase(jsonObject.getInteger("phase"));
		}

		return data;
	}

	private static SourceData sourceJson(String sourceStr) {
		JSONObject jsonObject = JSONObject.parseObject(sourceStr);
		SourceData data = new SourceData();
		if (jsonObject.containsKey("id")) {
			data.setId(jsonObject.getInteger("id"));
		}
		if (jsonObject.containsKey("type")) {
			data.setType(jsonObject.getInteger("type"));
		}
		return data;
	}

	private static ParamsData paramsJson(String paramJson) {
		JSONObject jsonObject = JSONObject.parseObject(paramJson);
		ParamsData data = new ParamsData();
		if (jsonObject.containsKey("headers")) {
			data.setHeaders(jsonObject.getString("headers"));
		}
		if (jsonObject.containsKey("source")) {
			data.setSource_dependency(sourceJson(jsonObject.getString("source")));
		}// byte_count
		if (jsonObject.containsKey("byte_count")) {
			data.setByte_count(jsonObject.getInteger("byte_count"));
		}
		if (jsonObject.containsKey("url")) {
			data.setUrl(jsonObject.getString("url"));
		}
		
		if (jsonObject.containsKey("address_list")) {
			data.setAddress_list(jsonObject.getString("address_list"));
		}
		
		if (jsonObject.containsKey("host")) {
			data.setHost(jsonObject.getString("host"));
		}
		
		if(jsonObject.containsKey("net_error")){
			data.setNet_error(jsonObject.getInteger("net_error"));
		}
		
		if(jsonObject.containsKey("source_dependency")){
			data.setSource_dependency(sourceJson(jsonObject.get("source_dependency").toString()));
		}
		if (jsonObject.containsKey("address")) {
			data.setAddress(jsonObject.getString("address"));
		}
		if (jsonObject.containsKey("using_quic")) {
			data.setUsing_quic(jsonObject.getString("using_quic"));
		}
		
		data.setParsmsStr(paramJson);
		return data;
	}
}
