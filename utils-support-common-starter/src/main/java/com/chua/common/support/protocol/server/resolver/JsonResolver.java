package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.json.Json;
import com.chua.common.support.objects.ConfigureObjectContext;

/**
 * 解析器
 * @author CH
 */
@Spi({"application/json", "json"})
public class JsonResolver extends AbstractResolver {

    public JsonResolver(ConfigureObjectContext beanFactory) {
        super(beanFactory);
    }

    @Override
    public byte[] resolve(Object obj) {
        if(obj instanceof byte[]) {
            return (byte[]) obj;
        }

        return Json.toJsonByte(obj);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
