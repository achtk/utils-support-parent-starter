package com.chua.common.support.printer;

import com.chua.common.support.tree.BinaryTreeNode;
import com.chua.common.support.tree.support.DefaultBinaryTreeNodePrinter;
import com.chua.common.support.tree.support.DefaultBinaryTreePrinter;
import lombok.extern.slf4j.Slf4j;

import static com.chua.common.support.printer.Printer.Type.OUT;


/**
 * 二叉树打印器
 *
 * @author CH
 */
@Slf4j
@SuppressWarnings("ALL")
public class BinaryTreePrinter implements Printer<BinaryTreeNode> {

    @Override
    public String print(BinaryTreeNode binaryTreeNode, Type type) {

        DefaultBinaryTreePrinter binaryTreePrinter = new DefaultBinaryTreePrinter();
        String stringBuffer = binaryTreePrinter.print(binaryTreeNode, new DefaultBinaryTreeNodePrinter());

        if (OUT == type) {
            return stringBuffer;
        }

        if (type == Type.SYSTEM) {
            System.out.println(stringBuffer);
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug(stringBuffer.toString());
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace(stringBuffer.toString());
            return null;
        }

        log.info(stringBuffer.toString());
        return null;
    }
}
