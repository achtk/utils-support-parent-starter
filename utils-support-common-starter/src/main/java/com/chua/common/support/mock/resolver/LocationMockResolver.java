package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.area.Area;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.lang.treenode.TreeNode;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.List;

/**
 * 地区
 *
 * @author CH
 */
@Spi("location")
public class LocationMockResolver implements MockResolver {

    private static final TreeNode<Area> TREE_NODE = Area.createTree(3000);
    private static final List<TreeNode<Area>> DATA = TREE_NODE.toNodeList();

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        if (DATA.isEmpty()) {
            return "北京市";
        }
        return CollectionUtils.getRandom(DATA).getCascade().replace("-", StringUtils.defaultString(mock.base(), ""));
    }
}
