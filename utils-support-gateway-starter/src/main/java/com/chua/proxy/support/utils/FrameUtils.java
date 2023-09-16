//package com.chua.proxy.support.utils;
//
//import com.chua.common.support.collection.ImmutableBuilder;
//import com.chua.common.support.net.frame.Frame;
//import com.chua.common.support.net.frame.HttpFrame;
//import io.netty.handler.codec.http.FullHttpRequest;
//import io.netty.handler.codec.http.HttpHeaders;
//
///**
// * tools
// *
// * @author CH
// * @since 2023/09/13
// */
//public class FrameUtils {
//
//    /**
//     * 创造帧
//     *
//     * @param request 要求
//     * @return {@link Frame}
//     */
//    public static Frame createFrame(Object request) {
//        if (request instanceof FullHttpRequest) {
//            FullHttpRequest fullHttpRequest = (FullHttpRequest) request;
//            HttpFrame frame = new HttpFrame();
//            frame.setMethod(fullHttpRequest.method().name());
//            frame.setUri(fullHttpRequest.uri());
//            HttpHeaders headers = fullHttpRequest.headers();
//            frame.setHeader(ImmutableBuilder.<String, String>builderOfMap()
//                    .put(headers)
//                    .build()
//            );
//            return frame;
//        }
//
//        return null;
//
//    }
//}
