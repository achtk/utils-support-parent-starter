package com.chua.common.support.spi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 下拉选项对象
 *
 * @author haoxr
 * @since 2022/1/22
 */
@Data
@NoArgsConstructor
public class Option<T> {

    public Option(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public Option(T value, String label, String type) {
        this.value = value;
        this.label = label;
        this.type = type;
    }

    public Option(T value, String label, List<Option> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    public Option(T value, String label, String type, List<Option> children) {
        this.value = value;
        this.label = label;
        this.type = type;
        this.children = children;
    }

    private T value;

    private String label;

    private String type;

    @Setter
    @Accessors(chain = true)
    private transient Class<?> impl;

    private List<Option> children;

}