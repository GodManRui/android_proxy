(function(w){
	var resultObj = {};
	//初始化相关
	function TestTiming(timing){
		var timerArr = [];
		var dnsTimer = {key:"DNS查询耗时" , value:timing.domainLookupEnd - timing.domainLookupStart + "ms"};
		var tcpTimer = {key:"TCP链接耗时" , value:timing.connectEnd - timing.connectStart + "ms"};
		var requestTimer = {key:"request请求耗时" , value:timing.responseEnd - timing.responseStart + "ms"};
		var domTimer = {key:"解析dom树耗时" , value:timing.domComplete - timing.domInteractive + "ms"};
		var pageEmptyTimer = {key:"白屏时间" , value:timing.responseStart - timing.navigationStart + "ms"};
		var domReadyTimer = {key:"domready时间" , value:timing.domContentLoadedEventEnd - timing.navigationStart + "ms"};
		var onloadTimer = {key:"onload时间" , value:timing.loadEventEnd - timing.navigationStart + "ms"};

		timerArr = timerArr.concat(dnsTimer, tcpTimer, requestTimer, domTimer, pageEmptyTimer, domReadyTimer, onloadTimer);
		return timerArr;
	}
	//请求的各种资源（js,图片，样式等）
	function TestResource(resourcesObj){
		var resourceArr = [];
		var len = resourcesObj.length;
		for(var i = len - 1;i >0;i--){
			var temp = {};
			var cur = resourcesObj[i];
			temp.key = cur.name;
			temp.resValue = cur.responseEnd - cur.requestStart + "ms";
			temp.conValue = cur.connectEnd - cur.connectStart + "ms";
			resourceArr.push(temp);
		}
		return resourceArr;
	}
	//页面的加载方式
	function pageLoadMethod(type){
		var arr = [];
		var loadMethod = {};
		loadMethod.name = "进入页面的方式";
		var str = "";
		switch(type){
			case 0:
				str = '点击链接、地址栏输入、表单提交、脚本操作等方式加载';
				break;
			case 1:
				str = '通过“重新加载”按钮或者location.reload()方法加载';
				break;
			case 2:
				str = '网页通过“前进”或“后退”按钮加载';
				break;
			default:
				str = '任何其他来源的加载';
				break;
		}
		loadMethod.value = str;
		arr.push(loadMethod);
		return arr;
	}
	//输出性能数据
	function outPutData(perObj){
		var timerArr = TestTiming(perObj.timing);
		var resourcesArr = TestResource(perObj.getEntries());
		var loadMethodArr = pageLoadMethod(perObj.navigation.type);
		console.log("jerryzhu-js-------页面初始化------------------------");
		console.table(timerArr);
		console.log("jerryzhu-js-------页面请求------------------------");
		console.table(resourcesArr);
		console.log("jerryzhu-js-------页面加载方式------------------------");
		console.table(loadMethodArr);
	}

	w.perTestResult = outPutData;
})(window);