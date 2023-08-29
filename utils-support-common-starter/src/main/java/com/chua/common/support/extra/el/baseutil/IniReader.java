package com.chua.common.support.extra.el.baseutil;

import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基础类
 *
 * @author CH
 */
public class IniReader {
    public static IniFile read(InputStream inputStream, Charset charset) {
        class Helper {
            /**
             * 从index位置开始（包含）,找寻/n的坐标。并且返回
             *
             * @param src
             * @param index
             * @return
             */
            int currentLine(byte[] src, int index) {
                for (int i = index; i < src.length; i++) {
                    if (src[i] == '\n') {
                        if (i > index && src[i - 1] == '\r') {
                            return i - 1;
                        } else {
                            return i;
                        }
                    }
                }
                return src.length - 1;
            }
        }
        Helper helper = new Helper();
        SectionImpl preSection = null;
        IniFileImpl iniFileImpl = new IniFileImpl();
        try {
            byte[] src = new byte[inputStream.available()];
            inputStream.read(src);
            int index = 0;
            while (true) {
                int end = helper.currentLine(src, index);
                if (index > end) {
                    break;
                } else if (end == index) {
                    index += 1;
                    continue;
                }
                int skip = -1;
                if (src[end] == '\r') {
                    skip = 2;
                } else if (src[end] == '\n') {
                    skip = 1;
                } else {
                    skip = 0;
                }
                String value = skip > 0 ? new String(src, index, end - index, charset) : new String(src, index, end - index + 1, charset);
                value = value.trim();
                char c = value.charAt(0);
                // 忽略注释
                if (c == ';' || c == '#') {
                }
                // 发现是一个新的节点
                else if (c == '[' && value.charAt(value.length() - 1) == ']') {
                    String sectionName = value.substring(1, value.length() - 1);
                    preSection = new SectionImpl(sectionName);
                    iniFileImpl.addSection(preSection);
                } else {
                    int splitIndex = value.indexOf('=');
                    if (splitIndex > 0 && splitIndex < src.length) {
                        // 属性节点
                        String k = value.substring(0, splitIndex).trim();
                        String v = value.substring(splitIndex + 1).trim();
                        iniFileImpl.putProperty(k, v);
                        if (preSection != null) {
                            preSection.putProperty(k, v);
                        }
                    }
                }
                index = end + skip;
                if (skip == 0) {
                    break;
                }
            }
            return iniFileImpl;
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    interface PropertyValueStore {
        /**
         * 返回该属性的第一个值
         *
         * @param property
         * @return
         */
        String getValue(String property);

        /**
         * 返回一个属性的所有值
         *
         * @param property name
         * @return value
         */
        String[] getValues(String property);

        /**
         * keys
         * @return keys
         */
        Set<String> keySet();
    }

    public interface Section extends PropertyValueStore {
        /**
         * name
         * @return name
         */
        String name();
    }

    public interface IniFile extends PropertyValueStore {
        /**
         * Section
         * @param name name
         * @return Section
         */
        Section getSection(String name);
    }

    static class PropertyValueStoreImpl implements PropertyValueStore {
        Map<String, String[]> store = new HashMap<String, String[]>();

        @Override
        public String getValue(String property) {
            String[] result = store.get(property);
            if (result != null) {
                return result[0];
            } else {
                return null;
            }
        }

        public void putProperty(String property, String value) {
            if (store.containsKey(property)) {
                String[] pred = store.get(property);
                String[] now = new String[pred.length + 1];
                System.arraycopy(pred, 0, now, 0, pred.length);
                now[pred.length] = value;
                store.put(property, now);
            } else {
                store.put(property, new String[]{value});
            }
        }

        @Override
        public Set<String> keySet() {
            return store.keySet();
        }

        @Override
        public String[] getValues(String property) {
            return store.get(property);
        }
    }

    static class IniFileImpl extends PropertyValueStoreImpl implements IniFile {
        Map<String, Section> sections = new HashMap<String, Section>();

        @Override
        public Section getSection(String name) {
            return sections.get(name);
        }

        void addSection(Section section) {
            sections.put(section.name(), section);
        }
    }

    static class SectionImpl extends PropertyValueStoreImpl implements Section {
        final String name;
        protected Map<String, String[]> store = new HashMap<String, String[]>();

        public SectionImpl(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }
}
