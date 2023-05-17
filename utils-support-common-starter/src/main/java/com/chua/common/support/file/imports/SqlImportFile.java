package com.chua.common.support.file.imports;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.file.resource.LineFile;
import com.chua.common.support.file.resource.ResourceConfiguration;
import com.chua.common.support.file.resource.line.TextFile;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * sql
 *
 * @author CH
 */
@Spi("sql")
public class SqlImportFile implements ImportFile {

    static final Pattern PATTERN = Pattern.compile("(value)|(values)", Pattern.CASE_INSENSITIVE);

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        try (InputStream inputStream1 = inputStream) {
            LineFile<String> lineFile = new TextFile(ResourceConfiguration.builder().byteSource(new ByteSourceArray(IoUtils.toByteArray(inputStream1))).build());
            AtomicInteger index = new AtomicInteger(0);
            lineFile.line(s -> {
                if (Strings.isNullOrEmpty(s)) {
                    return false;
                }

                if (PATTERN.matcher(s).find()) {
                    doItem(s, type, listener);
                    return listener.isEnd(index.getAndIncrement());
                }

                return false;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 单条数据
     *
     * @param s        数据
     * @param type     类型
     * @param listener 监听
     */
    private <T> void doItem(String s, Class<T> type, ImportListener<T> listener) {
        Map<String, Object> item = new LinkedHashMap<>();
        String[] split = PATTERN.split(s);
        List<String> attribute = Splitter.on(',').trimResults().splitToList(StringUtils.split(split[0].replace(")", ""), '(')[1]);
        String[] split1 = split[1].split("[),(]{3}");
        for (String item1 : split1) {
            List<String> values = Splitter.on(',').trimResults().splitToList(item1.replaceAll("[()]{1,}", ""));
            for (int i = 0; i < attribute.size(); i++) {
                String s1 = attribute.get(i);
                String s2 = values.get(i);
                item.put(s1.replace("`", ""), s2.replace("'", ""));
            }

            T t = BeanUtils.copyProperties(item, type);
            if (null == t) {
                return;
            }

            listener.accept(t);
        }
    }
}
