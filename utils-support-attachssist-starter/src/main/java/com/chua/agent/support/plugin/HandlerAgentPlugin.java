package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.transform.Listener;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.util.concurrent.Callable;
import java.util.zip.ZipFile;

/**
 * 句柄
 *
 * @author CH
 */
public class HandlerAgentPlugin implements HtmlAgentPlugin {

    public static final HandlerAgentPlugin INSTANCE = new HandlerAgentPlugin();

    @Path("stream")
    public String html() {
        return "stream.html";
    }

    @Path("stream_data")
    public String data() {
        JSONObject rs = new JSONObject();
        rs.put("data", Listener.dump());
        rs.put("title", Listener.title());
        return rs.toJSONString();
    }

    @Override
    public String name() {
        return "handler";
    }

    @Override
    public Class<?> pluginType() {
        return HandlerAgentPlugin.class;
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(
                ElementMatchers.any()
        ).intercept(MethodDelegation.to(pluginType()));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.named(FileInputStream.class.getName())
                .or(ElementMatchers.named(FileOutputStream.class.getName()))
                .or(ElementMatchers.named(RandomAccessFile.class.getName()))
                .or(ElementMatchers.named(ZipFile.class.getName()))
                .or(ElementMatchers.named(AbstractSelectableChannel.class.getName()))
                .or(ElementMatchers.named(AbstractInterruptibleChannel.class.getName()))
                .or(ElementMatchers.named(FileChannel.class.getName()))
                .or(ElementMatchers.named(AbstractSelector.class.getName()))
                .or(ElementMatchers.named("java.net.PlainSocketImpl"))
                ;
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }

    @RuntimeType
    public static Object before(@AllArguments Object[] objects, @Origin Method method, @This Object obj, @SuperCall Callable<?> callable) throws Exception {
        return callable.call();
    }
}
