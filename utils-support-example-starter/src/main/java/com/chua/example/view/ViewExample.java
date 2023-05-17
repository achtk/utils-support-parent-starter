package com.chua.example.view;

import com.chua.common.support.view.view.LadderView;
import com.chua.common.support.view.view.TreeView;
import com.chua.common.support.view.view.TreeViewNode;

/**
 * @author CH
 */
public class ViewExample {

    public static void main(String[] args) {
//        TableView tableView = new TableView(3);
//        tableView.hasBorder(true);
//        tableView.addRow("head1", "head2", "head3");
//        tableView.addRow("body1", "body222222", "body3");
//
//        System.out.println(tableView.draw());
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
}
