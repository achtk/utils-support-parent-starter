package com.chua.common.support.view.view;


import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 阶梯缩进控件
 *
 * @author vlinux
 * @date 15/5/8
 */
public class LadderView implements View {

    /**
     * 分隔符
     */
    private static final String LADDER_CHAR = "`-";

    /**
     * 缩进符
     */
    private static final String STEP_CHAR = " ";

    /**
     * 缩进长度
     */
    private static final int INDENT_STEP = 2;

    private final List<String> items = new ArrayList<String>();


    @Override
    public String draw() {
        final StringBuilder builder = new StringBuilder();
        int deep = 0;
        for (String item : items) {

            // 第一个条目不需要分隔符
            if (deep == 0) {
                builder
                        .append(item)
                        .append("\n");
            }

            // 其他的需要添加分隔符
            else {
                builder
                        .append(StringUtils.repeat(STEP_CHAR, deep * INDENT_STEP))
                        .append(LADDER_CHAR)
                        .append(item)
                        .append("\n");
            }

            deep++;

        }
        return builder.toString();
    }

    /**
     * 添加一个项目
     *
     * @param item 项目
     * @return this
     */
    public LadderView addItem(String item) {
        items.add(item);
        return this;
    }

}
