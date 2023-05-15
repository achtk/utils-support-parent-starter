package com.chua.common.support.json.jsonpath.internal;

import com.chua.common.support.json.jsonpath.DocumentContext;
import com.chua.common.support.json.jsonpath.JsonConfiguration;
import com.chua.common.support.json.jsonpath.ParseContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.chua.common.support.json.jsonpath.internal.Utils.notEmpty;
import static com.chua.common.support.json.jsonpath.internal.Utils.notNull;

/**
 * @author Administrator
 */
public class ParseContextImpl implements ParseContext {

    private final JsonConfiguration configuration;

    public ParseContextImpl() {
        this(JsonConfiguration.defaultConfiguration());
    }

    public ParseContextImpl(JsonConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public DocumentContext parse(Object json) {
        notNull(json, "json object can not be null");
        return new JsonContext(json, configuration);
    }

    @Override
    public DocumentContext parse(String json) {
        notEmpty(json, "json string can not be null or empty");
        Object obj = configuration.jsonProvider().parse(json);
        return new JsonContext(obj, configuration);
    }

    @Override
    public DocumentContext parseUtf8(byte[] json) {
        notEmpty(json, "json bytes can not be null or empty");
        Object obj = configuration.jsonProvider().parse(json);
        return new JsonContext(obj, configuration);
    }

    @Override
    public DocumentContext parse(InputStream json) {
        return parse(json, "UTF-8");
    }

    @Override
    public DocumentContext parse(InputStream json, String charset) {
        notNull(json, "json input stream can not be null");
        notNull(charset, "charset can not be null");
        try {
            Object obj = configuration.jsonProvider().parse(json, charset);
            return new JsonContext(obj, configuration);
        } finally {
            Utils.closeQuietly(json);
        }
    }

    @Override
    public DocumentContext parse(File json) throws IOException {
        notNull(json, "json file can not be null");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(json);
            return parse(fis);
        } finally {
            Utils.closeQuietly(fis);
        }
    }

    @Override
    public DocumentContext parse(URL url) throws IOException {
        notNull(url, "url can not be null");
        InputStream fis = null;
        try {
            fis = url.openStream();
            return parse(fis);
        } finally {
            Utils.closeQuietly(fis);
        }
    }

}
