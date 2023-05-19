package com.chua.pytorch.support.http;//package com.chua.pytorch.support.http;
//
//import com.chua.common.support.definition.processor.AutoInject;
//import com.chua.server.support.server.request.Request;
//import com.chua.server.support.server.annotations.Mapping;
//import com.chua.server.support.server.annotations.Param;
//
///**
// * 过滤器
// */
//public class HttpFilter {
//
//    @AutoInject
//    private HttpAdaptor httpAdaptor;
//
//    /**
//     * 首页
//     * @return 首页
//     */
//    @Mapping(value = "/", produces = "text/html")
//    public String index(@Param("test") Integer test, Request request) {
//        return "/index1.html";
//    }
//}
