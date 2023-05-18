package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * html静态页
 *
 * @author CH
 */
public class HtmlHtmlAgentPlugin implements HtmlAgentPlugin {

    @Path("html_cron")
    public String htmlCron() {
        return "html_cron.html";
    }

    @Path("about")
    public String about() {
        return "about.html";
    }

    @Path("html_format")
    public String htmlFormat() {
        return "html_format.html";
    }

    @Path("html_shell")
    public String shellFormat() {
        return "webssh.html";
    }

    @Path("webssh1.html")
    public String shellFormat2() {
        return "webssh1.html";
    }

    @Override
    public String name() {
        return "html";
    }

    @Override
    public Class<?> pluginType() {
        return HtmlHtmlAgentPlugin.class;
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

    }
}
