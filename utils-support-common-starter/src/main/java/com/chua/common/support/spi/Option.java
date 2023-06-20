package com.chua.common.support.spi;

import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Option(T value, String label, List<Option> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    private T value;

    private String label;

    private List<Option> children;

}