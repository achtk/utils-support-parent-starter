package com.chua.common.support.shell;

import com.chua.common.support.json.Json;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * ShellTable
 */
@Data
@NoArgsConstructor
public class ShellTable {

    private List<String> head = new LinkedList<>();
    private List<Collection<String>> rows = new LinkedList<>();

    public ShellTable(String... head) {
        this.head.addAll(Arrays.asList(head));
    }

    public ShellTable(List<String> head) {
        this.head = head;
    }

    /**
     * 行
     *
     * @param columns columns
     */
    public void addRow(String... columns) {
        rows.add(Arrays.asList(columns));
    }

    /**
     * 行
     *
     * @param columns columns
     */
    public void addRows(Collection<Collection<String>> columns) {
        rows.addAll(columns);
    }

    /**
     * 行
     *
     * @param columns columns
     */
    public void addRow(Collection<String> columns) {
        rows.add(columns);
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
