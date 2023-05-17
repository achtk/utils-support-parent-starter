package com.chua.common.support.protocol.client;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.router.Router;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.StringUtils;

/**
 * 客户端
 *
 * @author CH
 */
public abstract class AbstractClient<T> implements Client<T> {

    protected final ClientOption clientOption;
    protected final Profile profile;
    protected String url;
    protected long timeout;
    protected NetAddress netAddress;
    final Router router;

    protected AbstractClient(ClientOption clientOption) {
        this.clientOption = clientOption;
        this.profile = new TypeHashMap(this.clientOption.ream());
        profile.addProfile(BeanMap.of(clientOption));
        this.router = Router.create(profile.getString("scan-package", this.getClass().getPackage().getName()));
        this.router.addRouter(this);
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    protected Profile describe() {
        return profile;
    }


    /**
     * 允许池化
     *
     * @return 允许池化
     */
    protected boolean allowPooling() {
        return true;
    }

    @Override
    public Router getRouter() {
        return router;
    }


    @Override
    public void connect(String url,
                        long timeout) {
        this.url = url;
        this.timeout = timeout;
        if (StringUtils.isNullOrEmpty(url)) {
            this.url = profile.getString("url");
        }
        this.netAddress = NetAddress.of(this.url);
        profile.addProfile("host", netAddress.getHost());
        profile.addProfile("port", netAddress.getPort());
        afterPropertiesSet();
        connectClient();
        try {
            this.router.addRouter(this.getClient());
        } catch (Exception ignored) {
        }
    }

    /**
     * 获取客户端
     */
    public abstract void connectClient();

    /**
     * 获取客户端
     * @throws Exception ex
     * @return 客户端
     */
    public abstract T getClient() throws Exception;

    /**
     * 获取客户端
     *
     * @param client 客户端
     */
    public abstract void closeClient(T client);


}
