package com.chua.minio.support.oss;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.oss.adaptor.AbstractOssResolver;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.FileTypeUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;
import io.minio.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * minio解析器
 *
 * @author CH
 */
@Spi("minio")
public class MinioOssResolver extends AbstractOssResolver {

    private MinioClient minioClient;
    private String endpoint;

    public void initialConfig(OssSystem ossSystem) {
        if (null == minioClient) {
            synchronized (this) {
                if (null == minioClient) {
                    Properties properties = Converter.convertIfNecessary(ossSystem.getOssProperties(), Properties.class);
                    this.endpoint = StringUtils.defaultString(ossSystem.getOssPath(), MapUtils.getString(properties, "host"));
                    if (!endpoint.startsWith(HTTP)) {
                        endpoint = HTTP + "://" + endpoint;
                    }
                    this.minioClient = MinioClient.builder()
                            .endpoint(endpoint)
                            .credentials(ossSystem.getOssAppKey(), ossSystem.getOssAppSecret())
                            .build();
                }
            }
        }
    }

    @Override
    public void preview(OssSystem ossSystem, String path, Mode mode, Range<Long> range, OutputStream os) {
        initialConfig(ossSystem);
        StringBuilder sb = new StringBuilder(endpoint);
        String bucket = ossSystem.getOssBucket();
        sb.append("/").append(bucket);
        sb.append("/").append(path);

        String contentType = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
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
                if (mode == Mode.DOWNLOAD && null != range) {
                    rangeRead(response, ossSystem, range, byteArrayOutputStream);
                } else {
                    IoUtils.copy(response, byteArrayOutputStream);
                }
            }
            Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(path);
            writeTo(mediaType.get(), mode, range, byteArrayOutputStream.toByteArray(), os, ossSystem);
        } catch (Exception ignored) {
        }

    }

    @Override
    public Value<String> storage(InputStream is, OssSystem ossSystem, String name) {
        initialConfig(ossSystem);
        String bucket = ossSystem.getOssBucket();
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
