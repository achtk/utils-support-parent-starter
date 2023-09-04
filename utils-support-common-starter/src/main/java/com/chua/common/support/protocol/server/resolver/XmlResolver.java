package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.transfer.JsonToXmlFileConverter;
import com.chua.common.support.json.Json;
import com.chua.common.support.objects.ConfigureObjectContext;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 解析器
 * @author CH
 */
@Spi({"text/xml", "xml"})
public class XmlResolver extends AbstractResolver {


    public XmlResolver(ConfigureObjectContext beanFactory) {
        super(beanFactory);
    }

    @Override
    public byte[] resolve(Object obj) {
        if(obj instanceof byte[]) {
            return (byte[]) obj;
        }

        StringBuffer stringBuffer = new StringBuffer();
        JsonToXmlFileConverter.json2Xml(Json.getJsonObject(Json.toJson(obj)), stringBuffer);
        return stringBuffer.toString().getBytes(UTF_8);
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }
}
