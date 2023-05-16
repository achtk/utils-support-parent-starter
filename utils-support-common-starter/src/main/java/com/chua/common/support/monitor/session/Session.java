package com.chua.common.support.monitor.session;

import java.io.Serializable;
import java.util.List;

/**
 * 会话
 *
 * @author CH
 */
public interface Session {
    /**
     * 修改前数据
     *
     * @return 修改前数据
     */
    List<Serializable[]> getBeforeData();

    /**
     * 待修改数据
     *
     * @return 待修改数据
     */
    List<Serializable[]> getModifyData();

    /**
     * 受影响根目录
     *
     * @return 受影响根目录
     */
    String root();

    /**
     * 受影响名称
     *
     * @return 受影响名称
     */
    String change();

    /**
     * 会话
     *
     * @return 会话
     */
    Session getSession();
}
