package com.chua.email.support.entity;

import com.chua.common.support.utils.IoUtils;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

/**
 * 邮件
 *
 * @author CH
 */
public class MailPart {
    /**
     * 文本
     *
     * @param body    内容
     * @param subtype 类型
     * @return 结果
     */
    public static BodyPart createText(String body, String subtype) throws MessagingException {
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, subtype);
        return bodyPart;
    }

    /**
     * 文件
     *
     * @param name        附件名称
     * @param inputStream 附件
     * @return 结果
     */
    public static BodyPart createFile(String name, InputStream inputStream) throws Exception {
        BodyPart part = new MimeBodyPart();
        part.setFileName(name);
        try (InputStream is = inputStream) {
            part.setDataHandler(new DataHandler(new ByteArrayDataSource(IoUtils.toByteArray(is), "application/octet-stream")));
        }
        return part;
    }

    /**
     * 文件
     *
     * @param name 附件名称
     * @param url  附件
     * @return 结果
     */
    public static BodyPart createFile(String name, URL url) throws Exception {
        return createFile(name, url.openStream());
    }

    /**
     * 文件
     *
     * @param name 附件名称
     * @param file 附件
     * @return 结果
     */
    public static BodyPart createFile(String name, File file) throws Exception {
        return createFile(name, Files.newInputStream(file.toPath()));
    }

    /**
     * 文件
     *
     * @param name          附件名称
     * @param fileByteArray 附件
     * @return 结果
     */
    public static BodyPart createFile(String name, byte[] fileByteArray) throws MessagingException {
        BodyPart part = new MimeBodyPart();
        part.setFileName(name);
        part.setDataHandler(new DataHandler(new ByteArrayDataSource(fileByteArray, "application/octet-stream")));
        return part;
    }
}
