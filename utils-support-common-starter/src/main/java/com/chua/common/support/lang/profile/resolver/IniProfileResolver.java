package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.ini.IniFile;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解释器
 *
 * @author CH
 */
@Spi("ini")
public class IniProfileResolver implements ProfileResolver {

    final IniFile iniFile = new IniFile();

    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            iniFile.load(reader);
            List<String> strings = iniFile.sectionNames();
            Map<String, Object> value = new HashMap<>(strings.size());
            for (String string : strings) {
                value.put(string, iniFile.getSectionMap(string));
            }
            return Collections.singletonList(new MapProfileValue(resourceUrl, value));
        } catch (IOException ignored) {
        }
        return null;
    }
}
