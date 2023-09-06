package com.chua.example.mapping;

import com.chua.common.support.mapping.annotations.MappingAddress;
import com.chua.common.support.mapping.annotations.MappingRequest;

import java.util.List;

/**
 * 成语
 *
 * @author CH
 */
@MappingAddress("https://route.showapi.com")
public interface Idiom {
    /**
     * 查询成语
     *
     * @param page 页码
     * @param row  每页数量
     * @param name 成语
     * @return 成语
     */
    @MappingRequest(value = "GET /1196-1?keyword=${name}&page=${page}&rows=${row}&showapi_appid=1191705&showapi_sign=882fe29fdbee429d9ac55e8d234ffa40", jsonPath = "$.data")
    List<IdiomQuery> query(int page, int row, String name);
}
