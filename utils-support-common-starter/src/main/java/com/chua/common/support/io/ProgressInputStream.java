package com.chua.common.support.io;


import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.utils.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 进度流
 *
 * @author CH
 * @since 2021-12-31
 */
public class ProgressInputStream extends InputStream {

    private final DigestInputStream dis;
    private final ProgressBar progressBar;

    /**
     * Constructs a new ProgressInputStream with an input stream and progress.
     *
     * @param url the input stream
     */
    public ProgressInputStream(String url) throws IOException {
        this(new URL(url));
    }

    /**
     * Constructs a new ProgressInputStream with an input stream and progress.
     *
     * @param url the input stream
     */
    public ProgressInputStream(URL url) throws IOException {
        this(url.openStream(), new ProgressBar("task", UrlUtils.size(url.toExternalForm())));
    }

    /**
     * Constructs a new ProgressInputStream with an input stream and progress.
     *
     * @param is the input stream
     */
    public ProgressInputStream(InputStream is) throws IOException {
        this(is, new ProgressBar("task", is.available()));
    }

    /**
     * Constructs a new ProgressInputStream with an input stream and progress.
     *
     * @param is          the input stream
     * @param progressBar the (optionally null) progress tracker
     */
    public ProgressInputStream(InputStream is, ProgressBar progressBar) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA1 algorithm not found.", e);
        }
        dis = new DigestInputStream(is, md);
        this.progressBar = progressBar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        int ret = dis.read();
        if (progressBar != null) {
            if (ret >= 0) {
                progressBar.step();
            } else {
                progressBar.close();
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int size = dis.read(b, off, len);
        if (progressBar != null) {
            progressBar.stepBy(size);
        }
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        dis.close();
    }
}
