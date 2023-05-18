package com.chua.alibaba.support.view;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;
import com.chua.common.support.view.AbstractViewResolver;
import com.chua.common.support.view.ViewConfig;
import com.chua.common.support.view.ViewPreview;
import com.chua.common.support.view.ViewResolver;
import com.chua.common.support.view.viewer.Viewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * alibaba
 *
 * @author CH
 */
@Spi("alibaba")
public class AlibabaViewResolver extends AbstractViewResolver {

    /**
     * Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
     */
    static final String ENDPOINT = "https://%soss-cn-hangzhou.aliyuncs.com";
    OSS ossClient;
    /**
     * 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
     */
    String accessKeyId = "yourAccessKeyId";
    String accessKeySecret = "yourAccessKeySecret";

    String endpoint = ENDPOINT;

    @Override
    public ViewResolver setConfig(ViewConfig config) {
        Properties properties = config.getProperties();
        this.accessKeyId = config.getAppKey();
        this.accessKeySecret = config.getAppSecret();
        this.endpoint = String.format(StringUtils.defaultString(StringUtils.defaultString(config.getPath(), MapUtils.getString(properties, "host")), ENDPOINT), "");
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(accessKeyId, accessKeySecret), null);
        return super.setConfig(config);
    }

    @Override
    public void destroy() {
        if (null == ossClient) {
            return;
        }

        ossClient.shutdown();
    }

    @Override
    protected ViewPreview beforePreview(String bucket, String path, String mode, OutputStream os) {
        String contentType = null;
        if (null == os) {
            try {
                ObjectMetadata clientObject = ossClient.headObject(bucket, path);
                contentType = clientObject.getContentType();
            } catch (Exception ignored) {
            }
        } else {
            try (OSSObject clientObject = ossClient.getObject(bucket, path)) {
                contentType = clientObject.getObjectMetadata().getContentType();
                Viewer viewer = super.getViewer(contentType, mode, "");
                viewer.resolve(clientObject.getObjectContent(), os, mode, bucket + path);
            } catch (IOException ignored) {
            }
        }
        return new ViewPreview().setContentType(contentType);
    }

    @Override
    public Value<String> storage(InputStream is, String bucket, String name) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, name, is);
        ossClient.putObject(putObjectRequest);

        return Value.of(String.format(endpoint, bucket + ".") + "/" + name);
    }
}
