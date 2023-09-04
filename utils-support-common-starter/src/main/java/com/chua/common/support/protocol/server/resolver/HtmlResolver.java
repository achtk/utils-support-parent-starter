package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.template.Template;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 解析器
 *
 * @author CH
 */
@Spi({"text/html", "html"})
public class HtmlResolver extends AbstractResolver {

    final Repository repository = Repository.current().add(Repository.classpath(true));

    final Metadata notfound = Repository.classpath().first("static/404.html");
    private static final String MODEL_AND_VIEW = "org.springframework.web.servlet.ModelAndView";

    public HtmlResolver(ConfigureObjectContext beanFactory) {
        super(beanFactory);
    }


    @Override
    public byte[] resolve(Object obj) {
        byte[] source = null;
        if (obj instanceof byte[]) {
            source = (byte[]) obj;
        } else if (obj instanceof Model) {
            source = StringUtils.utf8Bytes(resolveModel((Model) obj));
        } else if (obj instanceof String) {
            source = StringUtils.utf8Bytes(resolveString(obj.toString()));
        } else if(MODEL_AND_VIEW.equals(obj.getClass().getTypeName())) {
            source = StringUtils.utf8Bytes(resolveString(Objects.requireNonNull(ClassUtils.getFieldValue("view", obj)).toString()));
        }

        if(null != source && source.length != 0) {
            return source;
        }
        try {
            return IoUtils.toByteArray(notfound.toUrl());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private String resolveString(String toString) {
        Repository resolve = repository.resolve(toString);
        List<Metadata> metadata = resolve.getMetadata();
        if (metadata.isEmpty()) {
            return "";
        }

        try {
            return IoUtils.toString(metadata.get(0).toUrl(), UTF_8);
        } catch (IOException ignored) {
        }
        return "";
    }

    private String resolveModel(Model obj) {
        Template template = beanFactory.getBean(Template.class).get();
        String view = obj.getView();
        String resolveString = resolveString(view);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            template.resolve(resolveString, byteArrayOutputStream, obj.getModel());
            return new String(byteArrayOutputStream.toByteArray(), UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }
}
