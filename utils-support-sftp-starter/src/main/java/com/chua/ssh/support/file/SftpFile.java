package com.chua.ssh.support.file;

import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.UrlFile;
import com.chua.common.support.resource.ResourceConfiguration;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * sftp
 *
 * @author CH
 */
public class SftpFile extends AbstractResourceFile implements UrlFile {
    private ChannelSftp channel;
    private ChannelSftp.LsEntry entrySelector;


    public SftpFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    public SftpFile(ChannelSftp channel, ChannelSftp.LsEntry entrySelector) {
        super(ResourceFileConfiguration.builder().build());
        this.channel = channel;
        this.entrySelector = entrySelector;
    }


    @Override
    public File toFile() {
        return super.toFile();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        try {
            return channel.get(entrySelector.getLongname());
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }
}
