package com.chua.common.support.collection;


import com.chua.common.support.unit.name.NamingCase;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * key翻译
 *
 * @author CH
 */
public class KeyValue implements Iterable<String> {

    private final Set<String> keys = new LinkedHashSet<>();

    public KeyValue(String key) {
        this.keys.add(key);
        this.keys.add(NamingCase.toCamelCase(key));
        this.keys.add(NamingCase.toLowerCamelHyphen(key));
        this.keys.add(NamingCase.toHyphenUpperCamel(key));
        this.keys.add(NamingCase.toLowerCamelHyphen(key));
        this.keys.add(NamingCase.toHyphenUpperUnderscore(key));
        this.keys.add(NamingCase.toCamelHyphen(key));
        this.keys.add(NamingCase.toCamelUnderscore(key));
    }

    @Override
    public Iterator<String> iterator() {
        return new LinkedList<>(keys).iterator();
    }

}
