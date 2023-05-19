package com.chua.xjar.support.io.xjar;

/**
 * 记录可过滤的加密器
 *
 * @author Payne 646742615@qq.com
 * 2018/11/23 20:38
 */
public abstract class XEntryEncryptor<E> extends XWrappedEncryptor implements XEncryptor, XEntryFilter<E> {
    protected final XEntryFilter<E> filter;
    protected final XEntryFilter<E> dependsFilter;
    protected final XNopEncryptor xNopEncryptor = new XNopEncryptor();

    protected XEntryEncryptor(XEncryptor xEncryptor) {
        this(xEncryptor, null, null);
    }

    protected XEntryEncryptor(XEncryptor xEncryptor, XEntryFilter<E> filter, XEntryFilter<E> dependsFilter) {
        super(xEncryptor);
        this.filter = filter;
        this.dependsFilter = dependsFilter;
    }

    @Override
    public boolean filtrate(E entry) {
        if (null != dependsFilter) {
            return dependsFilter.filtrate(entry);
        }
        return filter == null || filter.filtrate(entry);
    }
}
