package com.chua.common.support.lang.pinyin.jpinyin;

import com.chua.common.support.utils.IoUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinResource {

    private PinyinResource() {
    }

    static Reader newClassPathReader(String classpath) {
        try {
            return IoUtils.newClassPathReader(classpath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Reader newFileReader(String path) throws FileNotFoundException {
        try {
            return IoUtils.newFileReader(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, String> getResource(Reader reader) {
        Map<String, String> map = new ConcurrentHashMap<>(1 << 4);
        try {
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("=");
                map.put(tokens[0], tokens[1]);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    static Map<String, String> getPinyinResource() {
        return getResource(newClassPathReader("/pinyin/pinyin.dict"));
    }

    static Map<String, String> getMutilPinyinResource() {
        return getResource(newClassPathReader("/pinyin/mutil_pinyin.dict"));
    }

    static Map<String, String> getChineseResource() {
        return getResource(newClassPathReader("/pinyin/chinese.dict"));
    }
}
