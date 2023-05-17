package com.chua.common.support.lang.area;

import com.chua.common.support.binary.ByteSourceInputStream;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.line.TsvFile;
import com.chua.common.support.io.CompressInputStream;
import com.chua.common.support.lang.treenode.TreeNode;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.ResourceProvider;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * 区域
 *
 * @author CH
 */
@Data
public class Area {
    /**
     * 行政区划
     */
    private long code;
    /**
     * 名称
     */
    private String name;
    /**
     * 级别1-5,省市县镇村
     */
    private int level;
    /**
     * 父级区划代码
     */
    private long pcode;
    /**
     * 简称
     */
    private String shortName;
    /**
     * 组合名
     */
    private String mergerName;
    /**
     * 拼音
     */
    private String pinyin;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;

    /**
     * 初始化
     *
     * @param max 最大数量
     * @return 区划
     */
    public static TreeNode<Area> createTree(int max) {
        List<TreeNode<Area>> tpl = new LinkedList<>();
        try (CompressInputStream compressInputStream = new CompressInputStream(ResourceProvider.of(
                "classpath:area.xz").getResource(),
                "area.tsv")) {
            TsvFile tsvFile = new TsvFile(ResourceFileConfiguration.builder().byteSource(new ByteSourceInputStream(compressInputStream)).build());

            Class<?> type = Area.class;
            Field[] declaredFields = type.getDeclaredFields();
            List<Field> fields = new LinkedList<>();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fields.add(field);
            }

            tsvFile.line(it -> {
                Area area = new Area();
                for (int i = 0; i < it.length; i++) {
                    String s = it[i];
                    Field field = fields.get(i);
                    try {
                        field.set(area, Converter.convertIfNecessary(s, field.getType()));
                    } catch (IllegalAccessException ignored) {
                    }
                }

                TreeNode<Area> item = new TreeNode<>();
                item.setValue(area.getName());
                item.setPid(String.valueOf(area.getPcode()));
                item.setId(String.valueOf(area.getCode()));
                item.setExt(area);
                tpl.add(item);
                return -1 != max && tpl.size() > max;
            }, 1);

            return TreeNode.transfer(tpl);
        } catch (Throwable ignored) {
        }
        return TreeNode.empty();
    }

    /**
     * 初始化
     *
     * @return 区划
     */
    public static List<Area> create() {
        List<Area> rs = new LinkedList<>();
        try (CompressInputStream compressInputStream = new CompressInputStream(ResourceProvider.of(
                "classpath:area.xz").getResource(),
                "area.tsv")) {
            TsvFile tsvFile = new TsvFile(ResourceFileConfiguration.builder().byteSource(new ByteSourceInputStream(compressInputStream)).build());

            Class<?> type = Area.class;
            Field[] declaredFields = type.getDeclaredFields();
            List<Field> fields = new LinkedList<>();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fields.add(field);
            }

            tsvFile.line(it -> {
                Area area = new Area();
                for (int i = 0; i < it.length; i++) {
                    String s = it[i];
                    Field field = fields.get(i);
                    try {
                        field.set(area, Converter.convertIfNecessary(s, field.getType()));
                    } catch (IllegalAccessException ignored) {
                    }
                }

                rs.add(area);
                return false;
            }, 1);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }
}
