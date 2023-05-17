package com.chua.common.support.io;

import com.chua.common.support.lang.process.ProgressBar;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 进度流
 *
 * @author CH
 * @since 2021-12-31
 */
@NoArgsConstructor
public class ProgressOutputStream extends ByteArrayOutputStream {

    private final ByteArrayOutputStream dis = new ByteArrayOutputStream();
    private ProgressBar progressBar = new ProgressBar("download", 0);

    public ProgressOutputStream(String name) {
        this.progressBar = new ProgressBar(name, 0);
    }

    public ProgressOutputStream(String name, long max) {
        this.progressBar = new ProgressBar(name, max);
    }

    @Override
    public void write(int b) {
        dis.write(b);
        if (progressBar != null) {
            progressBar.step();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        dis.write(b);
        if (progressBar != null) {
            progressBar.stepBy(b.length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) {
        dis.write(b, off, len);
        if (progressBar != null) {
            progressBar.stepBy(len);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        dis.close();
        progressBar.close();
    }
}
