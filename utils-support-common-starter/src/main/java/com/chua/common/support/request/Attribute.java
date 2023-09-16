package com.chua.common.support.request;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 属性
 *
 * @author CH
 * @since 2023/09/16
 */
public interface Attribute extends Map<String, Object> {


    public class MapAttribute implements Attribute {
        private final Map<String, Object> attribute;

        public MapAttribute(Map<String, Object> attribute) {
            this.attribute = attribute;
        }

        @Override
        public int size() {
            return attribute.size();
        }

        @Override
        public boolean isEmpty() {
            return attribute.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return attribute.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return attribute.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return attribute.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            return attribute.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return attribute.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            attribute.putAll(m);
        }

        @Override
        public void clear() {
            attribute.clear();
        }

        @Override
        public Set<String> keySet() {
            return attribute.keySet();
        }

        @Override
        public Collection<Object> values() {
            return attribute.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return attribute.entrySet();
        }
    }
}
