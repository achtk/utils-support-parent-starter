package com.chua.common.support.reflection.marker;

import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.chua.common.support.reflection.craft.FieldCraftTable.VOID_FIELD_DESCRIBE;


/**
 * 字段
 *
 * @author CH
 */
public class FieldBenchFactory implements BenchFactory<FieldDescribe> {
    final List<FieldDescribe> describes = new LinkedList<>();
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();

    public FieldBenchFactory(Class<?> type) {
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
