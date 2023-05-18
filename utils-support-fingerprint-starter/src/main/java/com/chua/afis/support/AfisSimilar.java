package com.chua.afis.support;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 指纹相似性
 *
 * @author CH
 * @since 2021-12-15
 */
public class AfisSimilar implements Similar {

    private final Map<String, Object> environment = new HashMap<>();

    @Override
    public Similar environment(String key, Object value) {
        environment.put(key, value);
        return this;
    }

    @Override
    public double match(String source, String target) throws Exception {
        Integer dpi = Integer.valueOf(environment.getOrDefault("dpi", 500).toString());

        FingerprintTemplate probe = new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(dpi)
                        .decode(Files.readAllBytes(Paths.get(source))));

        FingerprintTemplate candidate = new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(dpi)
                        .decode(Files.readAllBytes(Paths.get(source))));
        return new FingerprintMatcher()
                .index(probe)
                .match(candidate);
    }

    @Override
    public double match(URL source, URL target) throws Exception {
        Integer dpi = Integer.valueOf(environment.getOrDefault("dpi", 500).toString());

        FingerprintTemplate probe = new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(dpi)
                        .decode(IOUtils.toByteArray(source)));

        FingerprintTemplate candidate = new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(dpi)
                        .decode(IOUtils.toByteArray(target)));
        return new FingerprintMatcher()
                .index(probe)
                .match(candidate);
    }
}
