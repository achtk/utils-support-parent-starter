package com.chua.example.describe;

import com.chua.common.support.reflection.describe.GenericDescribe;
import com.chua.common.support.reflection.describe.GenericTypeAttribute;
import com.chua.common.support.reflection.describe.TypeDescribe;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CH
 */
public class TypeDescribeExample {

    public static void main(String[] args) {
        TypeDescribe typeDescribe = new TypeDescribe(Demo.class);
        GenericDescribe genericDescribe = typeDescribe.getActualTypeArguments();
        GenericTypeAttribute typeAttribute = genericDescribe.getType(Demo.class);
        System.out.println();
    }

    class Demo extends AbstractMap<List<Map<Integer, List<Float>>>, List<Demo>> {

        @Override
        public Set<Entry<List<Map<Integer, List<Float>>>, List<Demo>>> entrySet() {
            return null;
        }
    }
}
