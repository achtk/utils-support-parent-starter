package com.chua.common.support.collection;

import java.util.List;

/**
 * path map
 *
 * @author CH
 */
public class PathLinkedArray extends AbstractList implements PathArray {


    public PathLinkedArray(List<Object> list) {
        super(list);
    }


    @Override
    public Object get(int index) {
        return list.get(index);
    }
}
