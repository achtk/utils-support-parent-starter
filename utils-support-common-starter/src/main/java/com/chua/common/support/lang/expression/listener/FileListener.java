package com.chua.common.support.lang.expression.listener;

import com.chua.common.support.utils.IoUtils;

import java.io.File;
import java.io.IOException;

/**
 * 监听
 *
 * @author CH
 */
public class FileListener implements Listener {

    private final File file;
    private long lastModified;
    public FileListener(String file) {
        this(new File(file));
    }

    public FileListener(File file) {
        this.file = file;
        this.lastModified = file.lastModified();
    }

    @Override
    public boolean isChange() {
        if(file.lastModified() != lastModified) {
            this.lastModified = file.lastModified();
            return true;
        }
        return false;
    }

    @Override
    public String getSource() {
        try {
            return IoUtils.toString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
