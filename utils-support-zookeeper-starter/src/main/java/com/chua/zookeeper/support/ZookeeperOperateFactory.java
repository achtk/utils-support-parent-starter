package com.chua.zookeeper.support;

import com.chua.common.support.operate.AbstractMethodOperateFactory;
import com.chua.common.support.router.Route;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * zk操作
 *
 * @author CH
 */
@Route
final class ZookeeperOperateFactory extends AbstractMethodOperateFactory {
    private final CuratorFramework curatorFramework;

    public ZookeeperOperateFactory(CuratorFramework curatorFramework) {
        super();
        this.curatorFramework = curatorFramework;
    }

    /**
     * 创建节点
     *
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
    public boolean crateNode(String path, CreateMode createMode, String data) {
        try {
            curatorFramework.create().withMode(createMode).forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteNode(String path) {
        try {
            curatorFramework.delete().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteChildrenIfNeededNode(String path) {
        try {
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 判断节点是否存在
     *
     * @param path 路径
     * @return true-存在  false-不存在
     */
    public boolean isExistNode(String path) {
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param path 路径
     * @return 2-节点不存在  | 1-是持久化 | 0-临时节点
     */
    public int isPersistentNode(String path) {
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                return 2;
            }

            if (stat.getEphemeralOwner() > 0) {
                return 1;
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();

            return 2;
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 路径
     * @return 节点数据，如果出现异常，返回null
     */
    public String getNodeData(String path) {
        try {
            byte[] bytes = curatorFramework.getData().forPath(path);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 注册节点数据变化事件
     *
     * @param path                 节点路径
     * @param curatorCacheListener 监听事件
     * @return 注册结果
     */
    public boolean register(String path, CuratorCacheListener curatorCacheListener) {
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, path);
        Listenable<CuratorCacheListener> listenable = curatorCache.listenable();
        try {
            listenable.addListener(curatorCacheListener);
            curatorCache.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 更新节点数据
     *
     * @param path     路径
     * @param newValue 新的值
     * @return 更新结果
     */
    public boolean updateNodeData(String path, String newValue) {
        //判断节点是否存在
        if (!isExistNode(path)) {
            return false;
        }

        try {
            curatorFramework.setData().forPath(path, newValue.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 开启事务
     *
     * @return 返回当前事务
     */
    public CuratorMultiTransaction startTransaction() {
        return curatorFramework.transaction();
    }


}
