package com.chua.minio.support.oss;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.oss.adaptor.AbstractOssResolver;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;
import io.minio.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * minio解析器
 *
 * @author CH
 */
@Spi("minio")
@SpiOption("minio")
public class MinioOssResolver extends AbstractOssResolver {

    private String endpoint;

    private static final Table<Integer, String, MinioClient> CACHE = new ConcurrentReferenceTable<>();

    public synchronized MinioClient initialConfig(OssSystem ossSystem) {
        Integer ossId = ossSystem.getOssId();
        String key = ossSystem.getOssPath() + ossSystem.getOssAppKey() + ossSystem.getOssAppSecret();
        MinioClient minioClient1 = CACHE.get(ossId, key);
        if (null != minioClient1) {
            return minioClient1;
        }
        Map<String, MinioClient> row = CACHE.row(ossId);
        if (null != row && !row.isEmpty()) {
            row.clear();
        }
        Properties properties = Converter.convertIfNecessary(ossSystem.getOssProperties(), Properties.class);
        this.endpoint = StringUtils.defaultString(ossSystem.getOssPath(), MapUtils.getString(properties, "host"));
        if (!endpoint.startsWith(HTTP)) {
            endpoint = HTTP + "://" + endpoint;
        }
        try {
            CACHE.put(ossId, key, (minioClient1 = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(ossSystem.getOssAppKey(), ossSystem.getOssAppSecret())
                    .build()));
        } catch (Exception e) {
            throw new RuntimeException("appKey和appSecret不能为空");
        }
        return minioClient1;
    }

    @Override
    public void preview(OssSystem ossSystem, String path, Mode mode, Range<Long> range, OutputStream os) {
        MinioClient minioClient = initialConfig(ossSystem);
        String endpoint = ossSystem.getOssPath();
        if (!endpoint.startsWith(HTTP)) {
            endpoint = HTTP + "://" + endpoint;
        }
        path = StringUtils.startWithMove(path, "/");
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
    public Value<String> storage(String parentPath, InputStream is, OssSystem ossSystem, String name) {
        MinioClient minioClient = initialConfig(ossSystem);
        String suffix = FileUtils.getExtension(name);
        name = StringUtils.defaultString(parentPath, "") + "/" + getNamedStrategy(ossSystem, name) + "." + suffix;
        String bucket = ossSystem.getOssBucket();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .contentType(MediaTypeFactory.getMediaType(name).get().toString());
            PutObjectArgs putObjectArgs = builder.stream(is, is.available(), -1).build();
            minioClient.putObject(putObjectArgs);
            return Value.of(endpoint + "/" + bucket + "/" + name);
        } catch (Exception e) {
            if ("1 : bucket name must be at least 3 and no more than 63 characters long".equals(e.getMessage())) {
                throw new RuntimeException("bucket长度在3~63之间");
            }
            throw new RuntimeException(e);
        }
    }
}
