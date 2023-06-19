package com.chua.minio.support.oss;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.oss.adaptor.AbstractOssResolver;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;
import io.minio.*;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
                    writeRangeToOutStream(response, ossSystem, range, byteArrayOutputStream);
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
    public Value<String> storage(String parentPath, byte[] is, OssSystem ossSystem, String name) {
        MinioClient minioClient = initialConfig(ossSystem);
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
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(is)) {
                PutObjectArgs putObjectArgs = builder.stream(byteArrayInputStream, is.length, -1).build();
                minioClient.putObject(putObjectArgs);
            }
            return Value.of(endpoint + "/" + bucket + "/" + name);
        } catch (Exception e) {
            if ("1 : bucket name must be at least 3 and no more than 63 characters long".equals(e.getMessage())) {
                throw new RuntimeException("bucket长度在3~63之间");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OssNode> getChildren(OssSystem ossSystem, String id, String name) {
        MinioClient minioClient = initialConfig(ossSystem);
        String bucket = ossSystem.getOssBucket();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                return Collections.emptyList();
            }
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucket)
                    .recursive(false)
                    .build());
            List<OssNode> rs = new LinkedList<>();
            results.forEach(itemResult -> {
                try {
                    Item item = itemResult.get();
                    rs.add(new OssNode(name + "/" + item.objectName(),
                            name + "/" + item.objectName(),
                            item.objectName(),
                            item.lastModified().toLocalDateTime(),
                            !item.isDir(),
                            item.isDir()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return rs;
        } catch (Exception e) {
            if ("1 : bucket name must be at least 3 and no more than 63 characters long".equals(e.getMessage())) {
                throw new RuntimeException("bucket长度在3~63之间");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean deleteObject(OssSystem ossSystem, String id, String name) {
        return null;
    }
}
