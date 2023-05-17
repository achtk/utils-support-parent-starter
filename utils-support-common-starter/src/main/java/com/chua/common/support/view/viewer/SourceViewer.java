package com.chua.common.support.view.viewer;

import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.ContentTypeValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 试图解析器
 * @author ACHTK
 */
@SpiDefault
public class SourceViewer implements Viewer {

    static SourceViewer INSTANCE = new SourceViewer();
    static SourceViewer INSTANCE_OCET = new SourceViewer(){
        @Override
        public ContentTypeValue<byte[]> resolve(InputStream inputStream, OutputStream os, String mode, String uri) {
            return ContentTypeValue.OCET;
        }
    };

    public static Viewer empty() {
        return INSTANCE;
    }

    public static Viewer download() {
        return INSTANCE_OCET;
    }

    @Override
    public ContentTypeValue<byte[]> resolve(InputStream inputStream, OutputStream os, String mode, String uri) {
        try {
            IoUtils.copy(inputStream, os);
        } catch (IOException ignored) {
        }
        return ContentTypeValue.empty();
    }
}
