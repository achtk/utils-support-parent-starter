package com.chua.common.support.file.transfer;

import com.chua.common.support.file.xml.XML;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.utils.IoUtils;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * xml -> json
 *
 * @author CH
 */
public class XmlToJsonFileConverter extends AbstractFileConverter {
    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        String charset = getString("charset", "utf-8");
        JsonObject jsonObject;
        try (InputStreamReader isr = new InputStreamReader(sourcePath, charset)) {
            jsonObject = XML.toJsonObject(isr);
        }
        IoUtils.write(jsonObject.toString(), targetPath, charset);
    }

    @Override
    public String target() {
        return "json";
    }

    @Override
    public String source() {
        return "xml";
    }
}
