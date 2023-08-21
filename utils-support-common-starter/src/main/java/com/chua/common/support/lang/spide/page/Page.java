package com.chua.common.support.lang.spide.page;

import com.chua.common.support.http.HttpStatus;
import com.chua.common.support.lang.spide.pipeline.ResultItems;
import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.selector.Selectable;

import java.util.List;

/**
 * 页面信息
 * @author CH
 */
public interface Page {
    /**
     * 查询请求
     * @return 请求
     */
    Request getSpiderRequest();

    /**
     * 是否下载成功
     * @return  是否下载成功
     */
    boolean isDownloadSuccess();

    /**
     * 添加字段
     * @param key key
     * @param field 值
     */
    void putField(String key, Object field);
    /**
     * 状态
     * @return 状态
     */
    HttpStatus getStatusCode();

    /**
     * 结果
     * @return 结果
     */
    ResultItems getResultItems();

    /**
     * 请求
     * @return 请求
     */
    List<Request> getTargetRequests();

    /**
     * 是否跳过
     * @param b 是否跳过
     */
    void setSkip(boolean b);

    /**
     * 选择器
     * @return 选择器
     */
    Selectable getHtml();

    /**
     * 追加地址
     * @param all 地址
     */
    void addTargetRequests(List<String> all);
}
