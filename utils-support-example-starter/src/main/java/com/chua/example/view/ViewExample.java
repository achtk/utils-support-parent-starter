package com.chua.example.view;

import com.chua.common.support.view.view.*;

/**
 * @author CH
 */
public class ViewExample {

    public static void main(String[] args) {
        gauss();
        table();
        tableSmall();
        bar();
        tree();
    }

    private static void tableSmall() {
        SmallTableView tableView = new SmallTableView();
        tableView.addRow("head1", "head2", "head3");
        tableView.addRow("body1", "body222222", "body3");
        System.out.println(tableView.draw());
    }

    private static void gauss() {
        GaussView gaussView = new GaussView(3, 8, 1000);
        System.out.println(gaussView.draw());
    }

    private static void bar() {
        Double[] dou = new Double[]{1.7, 2.9, 5.0, 6.6, 1.0, 6.9, 17.9, 8.2, 7.9, 10.0};
        HistogramView histogramView = new HistogramView(dou);
        System.out.println(histogramView.draw());
    }

    private static void tree() {
        LadderView ladderView = new LadderView();
        ladderView.addItem("1");
        ladderView.addItem("2");
        ladderView.addItem("3");
        ladderView.addItem("4");
        ladderView.addItem("5");
        System.out.println(ladderView.draw());

        TreeView treeView = new TreeView(true, TreeViewNode.newBuilder("title1")
                .addChildren(TreeViewNode.newBuilder("title1-1"))
                .addChildren(TreeViewNode.newBuilder("title1-2")
                        .addChildren(TreeViewNode.newBuilder("title1-2-1"))
                        .addChildren(TreeViewNode.newBuilder("title1-2-2"))
                )
                .addChildren(TreeViewNode.newBuilder("title1-3"))
        );
        System.out.println(treeView.draw());
    }

    private static void table() {
        TableView tableView = new TableView(3);
        tableView.hasBorder(true);
        tableView.addRow("head1", "head2", "head3");
        tableView.addRow("body1", "body222222", "body3");

        System.out.println(tableView.draw());
    }
}
