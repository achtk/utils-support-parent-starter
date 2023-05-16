package com.chua.common.support.reflection.craft;

import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 字段
 *
 * @author CH
 */
public class FieldCraftTable implements CraftTable<FieldDescribe> {
    final List<FieldDescribe> describes = new LinkedList<>();
    public static final FieldDescribe VOID_FIELD_DESCRIBE = new VoidFieldDescribe();
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();

    public FieldCraftTable(Class<?> type) {
        ClassUtils.doWithFields(type, field -> {
            describes.add(FieldDescribe.of(field));
        });
    }

    @Override
    public void addDescribe(FieldDescribe describe) {
        hasNewDescribe.set(true);
        describes.add(describe);
    }

    @Override
    public boolean hasNewDescribe() {
        return hasNewDescribe.get();
    }

    @Override
    public void forEach(Consumer<FieldDescribe> describe) {
        describes.forEach(describe);
    }

    @Override
    public void forCreateEach(Consumer<FieldDescribe> describe) {
        for (FieldDescribe fieldDescribe : describes) {
            if (fieldDescribe.isCreate()) {
                describe.accept(fieldDescribe);
            }
        }
    }


    /**
     * 获取字段
     *
     * @param name 名称
     * @return 结果
     */
    public FieldDescribe get(String name) {
        if (null == name) {
            return VOID_FIELD_DESCRIBE;
        }

        for (FieldDescribe describe : describes) {
            if (describe.isMatch(name)) {
                return describe;
            }
        }

        return VOID_FIELD_DESCRIBE;
    }


    static final class VoidFieldDescribe extends FieldDescribe {

    }
}
