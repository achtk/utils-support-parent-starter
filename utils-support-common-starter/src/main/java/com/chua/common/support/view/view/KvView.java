package com.chua.common.support.view.view;

import com.chua.common.support.utils.StringUtils;

import java.util.Scanner;

/**
 * KV排版控件
 *
 * @author vlinux
 * @date 15/5/9
 */
public class KvView implements View {

    private final TableView tableView;

    public KvView() {
        this.tableView = new TableView(new TableView.ColumnDefine[]{
                new TableView.ColumnDefine(TableView.Align.RIGHT),
                new TableView.ColumnDefine(TableView.Align.RIGHT),
                new TableView.ColumnDefine(TableView.Align.LEFT)
        })
                .hasBorder(false)
                .padding(0);
    }

    public KvView(TableView.ColumnDefine keyColumnDefine, TableView.ColumnDefine valueColumnDefine) {
        this.tableView = new TableView(new TableView.ColumnDefine[]{
                keyColumnDefine,
                new TableView.ColumnDefine(TableView.Align.RIGHT),
                valueColumnDefine
        })
                .hasBorder(false)
                .padding(0);
    }

    public KvView add(final Object key, final Object value) {
        tableView.addRow(key, " : ", value);
        return this;
    }

    @Override
    public String draw() {
        String content = tableView.draw();
        StringBuilder sb = new StringBuilder();
        // 清理多余的空格
        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line != null) {
                //清理一行后面多余的空格
                line = StringUtils.stripEnd(line, " ");
            }
            sb.append(line).append('\n');
        }
        scanner.close();
        return sb.toString();
    }


}
