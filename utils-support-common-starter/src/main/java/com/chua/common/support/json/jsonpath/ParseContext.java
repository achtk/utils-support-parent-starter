/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chua.common.support.json.jsonpath;

import com.chua.common.support.json.jsonpath.spi.json.JsonProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Parses JSON as specified by the used {@link JsonProvider}.
 *
 * @author Administrator
 */
public interface ParseContext {
    /**
     * 解析json
     *
     * @param json json
     * @return doc
     */
    DocumentContext parse(String json);

    /**
     * 解析json
     *
     * @param json json
     * @return doc
     */
    DocumentContext parse(Object json);

    /**
     * 解析json
     *
     * @param json json
     * @return doc
     */
    DocumentContext parse(InputStream json);

    /**
     * 解析json
     *
     * @param json    json
     * @param charset 编码
     * @return doc
     */
    DocumentContext parse(InputStream json, String charset);

    /**
     * 解析json
     *
     * @param json json
     * @return doc
     * @throws IOException ex
     */
    DocumentContext parse(File json) throws IOException;

    /**
     * 解析json
     *
     * @param json json
     * @return doc
     */
    DocumentContext parseUtf8(byte[] json);

    /**
     * 解析json
     *
     * @param json json
     * @return doc
     * @throws IOException ex
     */
    DocumentContext parse(URL json) throws IOException;
}
