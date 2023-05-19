package com.chua.ssh.support.operate;

import com.chua.common.support.file.ResourceFile;
import com.chua.ssh.support.file.SftpFile;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * sftp
 *
 * @author CH
 */
public class Sftp {
    private final ChannelSftp channel;

    public Sftp(ChannelSftp channel) {
        this.channel = channel;
    }


    /**
     * cd
     *
     * @param path path
     * @return 结果
     */
    public void cd(String path) {
        try {
            channel.cd(path);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    /**
     * put
     *
     * @param inputStream stream
     * @param newPath     path
     * @return 结果
     */
    public void put(InputStream inputStream, String newPath) {
        try {
            channel.put(inputStream, newPath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    /**
     * put
     *
     * @param inputStream stream
     * @param newPath     path
     * @param mode        模式
     * @return 结果
     */
    public void put(InputStream inputStream, String newPath, int mode) {
        try {
            channel.put(inputStream, newPath, mode);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * get
     *
     * @param outputStream stream
     * @param newPath      path
     * @return 结果
     */
    public void get(String newPath, OutputStream outputStream) {
        try {
            channel.get(newPath, outputStream);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * get
     *
     * @param newPath path
     * @return 结果
     */
    public InputStream get(String newPath) {
        try {
            return channel.get(newPath);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        return null;
    }

    /**
     * chown
     *
     * @param uid     uid
     * @param newPath path
     * @return 结果
     */
    public void chown(int uid, String newPath) {
        try {
            channel.chown(uid, newPath);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * chmod
     *
     * @param permissions permissions
     * @param newPath     path
     * @return 结果
     */
    public void chmod(int permissions, String newPath) {
        try {
            channel.chmod(permissions, newPath);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * rename
     *
     * @param oldPath path
     * @param newPath path
     * @return 结果
     */
    public void rename(String oldPath, String newPath) {
        try {
            channel.rename(oldPath, newPath);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * mkdir
     *
     * @param path path
     * @return 结果
     */
    public void mkdir(String path) {
        try {
            channel.mkdir(path);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * rmdir
     *
     * @param path path
     * @return 结果
     */
    public void rmdir(String path) {
        try {
            channel.rmdir(path);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * rm
     *
     * @param path path
     * @return 结果
     */
    public void rm(String path) {
        try {
            channel.rm(path);
        } catch (SftpException e) {
             e.printStackTrace();
        }
        
    }

    /**
     * pwd
     *
     * @return 结果
     */
    public String pwd() {
        try {
            return channel.pwd();
        } catch (SftpException e) {
             e.printStackTrace();
        }
        return null;
    }

    /**
     * ls
     *
     * @param path path
     * @return 结果
     */
    public List<ResourceFile> ls(String path) {
        List<ResourceFile> rs = new LinkedList<>();
        try {
            Vector vector = channel.ls(path);
            for (int i = 0; i < vector.size(); i++) {
                ChannelSftp.LsEntry entrySelector = (ChannelSftp.LsEntry) vector.get(i);
                rs.add(new SftpFile(channel, entrySelector));
            }
        } catch (SftpException e) {
             e.printStackTrace();
        }
        return rs;
    }
}
