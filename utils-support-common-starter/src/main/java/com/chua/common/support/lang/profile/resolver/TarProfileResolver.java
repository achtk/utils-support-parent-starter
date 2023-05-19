package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.JAR_URL_SEPARATOR;


/**
 * 解释器
 *
 * @author CH
 */
@Spi({"tar"})
public class TarProfileResolver implements ProfileResolver, ServiceFactory<ProfileResolver> {

    @Override
    public List<ProfileValue> resolve(String resourceUrl) {
        List<ProfileValue> rs = new ArrayList<>();

        try {
            URL url = UrlUtils.createUrl(resourceUrl);
            rs.addAll(resolve(url.toExternalForm(), url.openStream()));
        } catch (Throwable e) {
            return Collections.emptyList();
        }
        return rs;
    }

    /**
     * 后缀
     *
     * @return 后缀
     */
    protected String getSuffix() {
        return "zip";
    }

    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        List<ProfileValue> rs = new LinkedList<>();
        try (TarInputStream tarInputStream = openStream(inputStream)) {
            TarEntry entry;
            while ((entry = tarInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    continue;
                }
                rs.addAll(resolve(resourceUrl, tarInputStream, name));
            }
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        return rs;
    }

    /**
     * 打开流
     *
     * @param inputStream 流
     * @return 流
     * @throws Exception ex
     */
    protected TarInputStream openStream(InputStream inputStream) throws IOException {
        return new TarInputStream(inputStream);
    }

    /**
     * 解析對象
     *
     * @param resourceUrl    资源文件
     * @param tarInputStream 流
     * @param name           名称
     * @return 结果
     */
    private Collection<? extends ProfileValue> resolve(String resourceUrl, TarInputStream tarInputStream, String name) {
        String suffix = name;
        ProfileResolver profileResolver = null;
        while (!StringUtils.isNullOrEmpty(suffix = FileUtils.getExtension(suffix))) {
            profileResolver = getExtension(suffix);
            if (null != profileResolver) {
                break;
            }
        }

        if (null == profileResolver) {
            return Collections.emptyList();
        }

        return Optional.ofNullable(profileResolver.resolve(resourceUrl + JAR_URL_SEPARATOR + name,
                IoUtils.toInputStream(tarInputStream))).orElse(Collections.emptyList());
    }


}
