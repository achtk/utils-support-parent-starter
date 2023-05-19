package com.chua.freemarker.support.template;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.lang.template.Template;
import com.chua.common.support.utils.IoUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker
 *
 * @author CH
 */
@SpiDefault
@Spi("ftl")
public class FreemarkerTemplate implements Template {

    private static final String DEFAULT_CHARACTER = "UTF-8";
    private static final String NAME = "template";

    protected Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

    {
        configuration.setDefaultEncoding(DEFAULT_CHARACTER);
        configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
    }

    @Override
    public void resolve(InputStream inputStream, OutputStream outputStream, Map<String, Object> templateData) {
        try {
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate(NAME, IoUtils.toString(inputStream, DEFAULT_CHARACTER));
            configuration.setTemplateLoader(stringTemplateLoader);
            freemarker.template.Template template = configuration.getTemplate(NAME, DEFAULT_CHARACTER);
            template.process(templateData, new OutputStreamWriter(outputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
