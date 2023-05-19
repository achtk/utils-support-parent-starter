package com.chua.common.support.lang.page;

import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.PageUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 分页数据
 *
 * @author CH
 */
public class PageValue<T> {
    /**
     * 分页数据
     */
    private final List<PageData<T>> data = new LinkedList<>();

    private PageValue() {
    }

    /**
     * 初始化
     *
     * @param target 类型
     * @param <T>    类型
     * @return 结果
     */
    public static <T> PageValueBuilder<T> newBuilder(Class<T> target) {
        return new PageValueBuilder<>(target);
    }

    public static class PageValueBuilder<T> {

        private final PageValue<T> pageValue = new PageValue<>();
        private Class<T> target;

        public PageValueBuilder(Class<T> target) {
            this.target = target;
        }

        public PageValueBuilder<T> addPageData(PageData<T> pageData) {
            pageValue.data.add(pageData);
            return this;
        }


        public PageValue<T> build() {
            return pageValue;
        }
    }

    /**
     * 数据查询
     *
     * @param sql      sql
     * @param page     页码
     * @param pageSize 每页数量
     * @return 结果
     */
    public Page<T> query(String sql, int page, int pageSize) {
        return null;
    }

    /**
     * 数据查询
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 结果
     */
    public Page<T> query(int page, int pageSize) {
        Page<T> rs = Page.<T>builder().build();
        List<T> tpl = new LinkedList<>();
        int sum = 0;
        for (PageData<T> pageData : data) {
            Page<T> tPage = pageData.find(page, pageSize);
            List<T> data1 = tPage.getData();
            sum += Math.max(tPage.getTotal(), data1.size());
            tpl.addAll(data1);
        }

        rs.setCurrent(page);
        rs.setSize(pageSize);
        rs.setPages(PageUtils.totalPage(sum, pageSize));
        rs.setTotal(sum);
        rs.setData(CollectionUtils.page(page - 1, pageSize, tpl));

        return rs;
    }

}
