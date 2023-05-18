package com.chua.common.support.file.transfer;

import com.chua.common.support.json.Json;
import com.chua.common.support.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

/**
 * properties -> json
 * √
 *
 * @author CH
 */
public class PropertiesToJsonFileConverter extends AbstractFileConverter {
    @Override
    public void convert(InputStream sourcePath, String suffix, OutputStream targetPath) {
        String charset = getString("charset", "utf-8");
        Properties properties;
        try (InputStreamReader isr = new InputStreamReader(sourcePath, charset)) {
            properties = new Properties();
            properties.load(isr);
            IoUtils.write(Json.prettyFormat(properties), targetPath, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String target() {
        return "json";
    }

    @Override
    public String source() {
        return "properties";
    }
}
