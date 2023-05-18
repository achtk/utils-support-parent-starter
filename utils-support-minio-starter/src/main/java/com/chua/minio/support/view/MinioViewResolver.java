package com.chua.minio.support.view;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.*;
import com.chua.common.support.value.Value;
import com.chua.common.support.view.AbstractViewResolver;
import com.chua.common.support.view.ViewConfig;
import com.chua.common.support.view.ViewPreview;
import com.chua.common.support.view.ViewResolver;
import com.chua.common.support.view.viewer.Viewer;
import io.minio.*;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * minio解析器
 *
 * @author CH
 */
@Spi("minio")
public class MinioViewResolver extends AbstractViewResolver {

    private MinioClient minioClient;
    private String endpoint;

    @Override
    public ViewResolver setConfig(ViewConfig config) {
        this.config = config;
        if (null == minioClient) {
            synchronized (this) {
                if (null == minioClient) {
                    Properties properties = config.getProperties();
                    this.endpoint = StringUtils.defaultString(config.getPath(), MapUtils.getString(properties, "host"));
                    if (!endpoint.startsWith(HTTP)) {
                        endpoint = HTTP + "://" + endpoint;
                    }
                    this.minioClient = MinioClient.builder()
                            .endpoint(endpoint)
                            .credentials(config.getAppKey(), config.getAppSecret())
                            .build();
                }
            }
        }
        return super.setConfig(config);
    }

    @Override
    public ViewPreview beforePreview(String bucket, String path, String mode, OutputStream os) {
        StringBuilder sb = new StringBuilder(endpoint);
        sb.append("/").append(bucket);
        sb.append("/").append(path);

        String contentType = null;
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());
            contentType = response.headers().get("Content-Type");
            if (null != os) {
                Viewer viewer = super.getViewer(contentType, mode, "");
                viewer.resolve(response, os, mode, bucket + path);
            }
        } catch (Exception ignored) {
        }

        return new ViewPreview().setContentType(contentType);
    }

    @SneakyThrows
    @Override
    public Value<String> storage(InputStream is, String bucket, String name) {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        try (InputStream inputStream = IoUtils.copy(is)) {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .contentType(FileTypeUtils.getType(inputStream));
            PutObjectArgs putObjectArgs = builder.stream(is, is.available(), -1).build();
            minioClient.putObject(putObjectArgs);
        }
        return Value.of(endpoint + "/" + bucket + "/" + name);
    }

    @Override
    public void destroy() {
    }

}
