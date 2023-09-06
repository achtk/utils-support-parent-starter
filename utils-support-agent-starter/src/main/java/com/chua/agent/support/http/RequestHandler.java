package com.chua.agent.support.http;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.json.JSONObject;

import java.io.IOException;

/**
 * 请求处理
 *
 * @author CH
 */
public interface RequestHandler<Req, Res> extends Constant {

    /**
     * 处理连接
     *
     * @param request  请求
     * @param response 响应
     * @throws IOException ex
     */
    void handle(Req request, Res response) throws IOException;

    /**
     * Req
     *
     * @return Req
     */
    default Req getRequest() {
        return null;
    }

    /**
     * JSONObject
     *
     * @return Req
     */
    default JSONObject getParameter() {
        return new JSONObject();
    }

    /**
     * 路径
     *
     * @return 路径
     */
    default String[] getPath() {
        return null;
    }
}
