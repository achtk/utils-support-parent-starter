package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.annotations.Extension;
import com.chua.starter.core.support.factory.ServiceProvider;
import com.chua.starter.core.support.profile.value.ProfileValue;
import com.chua.starter.core.support.utils.UrlUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.chua.starter.core.support.constant.CommonConstant.FILE;
import static com.chua.starter.core.support.constant.CommonConstant.JAR_URL_SEPARATOR;


/**
 * 解释器
 *
 * @author CH
 */
@Extension({"zip"})
public class ZipProfileResolver implements ProfileResolver {
    @Override
    public List<ProfileValue> resolve(String resourceUrl) {
        List<ProfileValue> rs = new ArrayList<>();

        try {
            URL url = UrlUtils.createUrl(resourceUrl);
            String protocol = url.getProtocol();
            if (FILE.equals(protocol)) {
                rs.addAll(analysis(new ZipFile(url.getFile())));
            } else {
                rs.addAll(resolve(url.toExternalForm(), url.openStream()));
            }
        } catch (IOException e) {
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
        File file = new File(".", UUID.randomUUID() + "." + getSuffix());
        try (BufferedInputStream isr = new BufferedInputStream(inputStream);
             FileOutputStream zipOutputStream = new FileOutputStream(file);
        ) {
            byte[] bytes = new byte[2048];
            int read = 0;
            while ((read = isr.read(bytes)) != 0) {
                zipOutputStream.write(bytes, 0, read);
            }
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        try {
            return analysis(new ZipFile(file));
        } catch (IOException ignored) {
        } finally {
            try {
                file.delete();
            } catch (Exception ignored) {
            }
        }

        return Collections.emptyList();
    }


    /**
     * 解析
     *
     * @param zipFile 文件
     * @return 结果
     */
    private List<ProfileValue> analysis(ZipFile zipFile) {
        List<ProfileValue> rs = new ArrayList<>();
        ServiceProvider<ProfileResolver> provider = ServiceProvider.of(ProfileResolver.class);
        try (ZipFile zipFile1 = zipFile) {
            Enumeration<? extends ZipEntry> entries = zipFile1.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String name = zipEntry.getName();
                String suffix = FilenameUtils.getExtension(name);
                ProfileResolver profileResolver = provider.getExtension(suffix);
                if (null == profileResolver) {
                    continue;
                }
                List<ProfileValue> resolve = profileResolver.resolve(zipFile.getName() + JAR_URL_SEPARATOR + name, zipFile1.getInputStream(zipEntry));
                rs.addAll(resolve);
            }
        } catch (IOException ignored) {
        }
        return rs;
    }
}
