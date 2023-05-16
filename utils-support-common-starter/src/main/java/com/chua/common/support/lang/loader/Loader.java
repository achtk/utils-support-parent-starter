package com.chua.common.support.lang.loader;

/**
 * 加载器
 *
 * @author CH
 */
public interface Loader<T> {
    /**
     * 获取一个准备好的对象<br>
     * 通过准备逻辑准备好被加载的对象，然后返回。在准备完毕之前此方法应该被阻塞
     *
     * @return 加载完毕的对象
     */
    T get();
}
