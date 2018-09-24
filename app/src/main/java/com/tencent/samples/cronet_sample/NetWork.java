package com.tencent.samples.cronet_sample;

import android.content.Context;
import android.os.Environment;

import com.tencent.samples.cronet_sample.data.HtmlElement;
import com.tencent.samples.cronet_sample.data.HtmlElement.ChildTiming;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

public class NetWork {
    public static synchronized CronetEngine getCronetEngine(Context context) {

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        String value = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1QUIC";
        CronetEngine cronetEngine = myBuilder
//                    .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISABLED, 100 * 1024)
                /*  .setStoragePath(value)
                  .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISK, 100 * 1024)*/
                .addQuicHint("www.wolfcstech.com", 443, 443)
                .addQuicHint("wy.tytools.cn", 443, 443)
                .addQuicHint("translate.google.cn", 443, 443)
                .addQuicHint("halfrost.com", 443, 443)
//                    .enableHttp2(true)
                .enableQuic(true)
                .build();
        return cronetEngine;
    }


    public static class SimpleUrlRequestCallback extends UrlRequest.Callback {
        private final Thread thread;
        public boolean flag = true;
        public ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
        public String contentType = "";
        public String contentEncoding = "";
        ChildTiming mTiming;
        HtmlElement elements;
        private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

        public SimpleUrlRequestCallback(HtmlElement elements, ChildTiming mTiming, Thread thread) {
            this.elements = elements;
            this.mTiming = mTiming;
            this.thread = thread;
        }

        @Override
        public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) throws Exception {
            elements.redirectCount++;
            urlRequest.followRedirect();
        }

        // 在所有的重定向都处理完了，且http响应的header都接收完，需要读取response的body时这个方法会被调用。一个请求的整个处理过程中，这个方法只会被调用一次。在这个方法中需要分配ByteBuffer，以用于http response body的读取过程。Response的body内容会首先被读取到这里分配的ByteBuffer中
        @Override
        public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
            urlRequest.read(ByteBuffer.allocateDirect(32 * 1024));
            mTiming.setStartWaitingResponse(System.nanoTime());
            Map<String, List<String>> allHeaders = urlResponseInfo.getAllHeaders();
            List<String> type = allHeaders.get("content-type");
//            List<String> encoding = allHeaders.get("content-encoding");
            for (int i = 0; i < type.size(); i++) {
                contentType += type.get(i);
                String[] split = contentType.split(";");
                if (split.length >= 2) {
                    contentType = split[0];
                }
                break;
            }
        }

        //   http response body读取完成，或者ByteBuffer读满的时候，这个回调方法会被调用。在这个回调方法中，需要将ByteBuffer中的数据copy出来，清空ByteBuffer，然后重新启动读取。这里的回调方法实现，是将ByteBuffer的内容copy出来，借助于WritableByteChannel放进ByteArrayOutputStream中
        @Override
        public void onReadCompleted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) throws Exception {
            byteBuffer.flip();  //将ByteBuffer中的数据copy出来，清空ByteBuffer，然后重新启动读取
            try {
                receiveChannel.write(byteBuffer);
            } catch (IOException e) {
            }
            byteBuffer.clear();
            urlRequest.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo info) {
            mTiming.setContentDownload(System.nanoTime());
            String negotiatedProtocol = info.getNegotiatedProtocol();
            android.util.Log.i("JerryZhu", negotiatedProtocol +
                    "  请求完成  状态码 " + info.getHttpStatusCode() + "    URL:" + info.getUrl()
                    + ", 总接收字节数为  " + info.getReceivedByteCount() + "   KB为: " + info.getReceivedByteCount() / 1024);

            byte[] byteArray = bytesReceived.toByteArray();
            flag = false;
            synchronized (thread) {
                thread.notify();
            }
//            Log.i("JerryZhu", "onSucceeded: " + Thread.currentThread().getName() + " 总接收字节数为  " + byteArray.length + "   KB为: " + byteArray.length / 1024);
        }

        @Override
        public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {
            boolean isNull = urlResponseInfo == null;
            flag = false;
            synchronized (thread) {
                thread.notify();
            }
        }
    }
}
