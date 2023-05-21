package com.chua.common.support.lang.template.basis;

import com.chua.common.support.lang.template.Template;
import com.chua.common.support.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * template
 * @author CH
 */
public class DelegateTemplate  implements Template {
    @Override
    public void resolve(InputStream inputStream,
                        OutputStream outputStream,
                        Map<String, Object> templateData) {
        TemplateContext context = new TemplateContext();
        templateData.forEach(context::set);
        TemplateLoader loader = new TemplateLoader.SourceTemplateLoader();
        String content = null;
        try {
            content = IoUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BasisTemplate template = loader.load(content);
        template.render(context, outputStream);
    }
}
