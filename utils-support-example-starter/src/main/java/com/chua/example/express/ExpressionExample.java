package com.chua.example.express;

import com.chua.common.support.lang.expression.ExpressionProvider;
import com.chua.common.support.lang.expression.listener.DelegateRefreshListener;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.example.dynamic.TDemoInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author CH
 */
@Slf4j
public class ExpressionExample {

    public static void main(String[] args) throws IOException {
        testGroovyExpress();
        testJavaExpress();
//        testJavaSourceExpress();
//        testJsExpress();

    }

    private static void testGroovyExpress() throws IOException {
        ExpressionProvider provider = ExpressionProvider.newSource().scriptType("groovy")
                .listener(new DelegateRefreshListener(IoUtils.toString(new File("Z:\\workspace\\utils-support-parent-starter\\utils-support-example-starter\\target\\classes\\TDemoInfoImpl.groovy")))).build();
        TDemoInfo tDemoInfo = provider.createProxy(TDemoInfo.class);
        while (true) {
            ThreadUtils.sleepSecondsQuietly(1);
            System.out.println(tDemoInfo.getId());
        }
    }

    private static void testJavaSourceExpress() throws IOException {
        ExpressionProvider provider = ExpressionProvider.newBuilder("classpath:TDemoInfoImpl.java").build();
        TDemoInfo tDemoInfo = provider.createProxy(TDemoInfo.class);
        while (true) {
            ThreadUtils.sleepSecondsQuietly(1);
            System.out.println(tDemoInfo.getId());
        }
    }

    private static void testJavaExpress() {
//        ExpressionProvider provider = ExpressionProvider.newBuilder("classpath:SendDemoImpl.java").build();
//        SendDemo tDemoInfo = provider.createProxy(SendDemo.class);
//        while (true) {
//            ThreadUtils.sleepSecondsQuietly(1);
//            System.out.println(tDemoInfo.getId());
//        }
    }


    private static void testJsExpress() {
        ExpressionProvider provider = ExpressionProvider.newBuilder("classpath:TDemoInfoImpl.js").build();
        TDemoInfo tDemoInfo = provider.createProxy(TDemoInfo.class);

        while (true) {
            ThreadUtils.sleepSecondsQuietly(1);
            System.out.println(tDemoInfo.getId());
        }

    }
}
