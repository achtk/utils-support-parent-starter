package com.chua.agent.support.plugin;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.sun.management.DiagnosticCommandMBean;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import sun.management.ManagementFactoryHelper;

import java.text.DecimalFormat;
import java.util.Optional;

/**
 * 线程
 *
 * @author CH
 */
public class ThreadAgentPlugin implements HtmlAgentPlugin {

    public static final ThreadAgentPlugin INSTANCE = new ThreadAgentPlugin();
    public static DecimalFormat format = new DecimalFormat("###.000");
    DiagnosticCommandMBean diagnosticCommandMBean = ManagementFactoryHelper.getDiagnosticCommandMBean();
    private JSONObject parameter;


    @Path("thread")
    public String html() {
        return "thread.html";
    }

    @Path("thread_info")
    public String info() {
        try {
            Object res = diagnosticCommandMBean.invoke("threadPrint", new Object[]{new String[]{}}, new String[]{String[].class.getName()});
            return JSON.toJSONString(res);
        } catch (Exception ignored) {
        }
        return null;
    }


    @Override
    public String name() {
        return "thread";
    }


    @Override
    public Class<?> pluginType() {
        return ThreadAgentPlugin.class;
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {
        this.parameter = parameter;
    }


    public String getParam(String name) {
        return Optional.ofNullable(parameter.getString(name)).orElse("");
    }
}
