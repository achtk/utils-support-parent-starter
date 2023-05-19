package com.chua.zookeeper.support.lock;

import com.chua.common.support.lang.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zk lock
 *
 * @author CH
 */
@Slf4j
public class ZookeeperDistributeLock implements Lock, Watcher {

    private ZooKeeper zk = null;
    /**
     * 根节点
     */
    private String ROOT_LOCK = "/locks";
    /**
     * 竞争的资源
     */
    private String lockName;
    /**
     * 等待的前一个锁
     */
    private String WAIT_LOCK;
    /**
     * 当前锁
     */
    private String CURRENT_LOCK;
    /**
     * 计数器
     */
    private CountDownLatch countDownLatch;
    private List<Exception> exceptionList = new ArrayList<Exception>();

    public ZookeeperDistributeLock(String lockName, int sessionTimeout, String connect) {
        this.lockName = lockName;
        try {
            // 连接zookeeper
            zk = new ZooKeeper(connect, sessionTimeout, this);
            Stat stat = zk.exists(ROOT_LOCK, false);
            if (stat == null) {
                // 如果根节点不存在，则创建根节点
                zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean lock(int timeout) {
        if (exceptionList.size() > 0) {
            throw new IllegalArgumentException(exceptionList.get(0));
        }

        try {
            if (this.tryLock()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} {}获得了锁", Thread.currentThread().getName(), lockName);
                }
                return true;
            } else {
                // 等待锁
                waitForLock(WAIT_LOCK, timeout);
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unlock() {
        try {
            log.info("释放锁 {}", CURRENT_LOCK);
            zk.delete(CURRENT_LOCK, -1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (lockName.contains(splitStr)) {
                throw new IllegalArgumentException("锁名有误");
            }

            // 创建临时有序节点
            CURRENT_LOCK = zk.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info(" {} 已经创建", CURRENT_LOCK);
            // 取所有子节点
            List<String> subNodes = zk.getChildren(ROOT_LOCK, false);
            // 取出所有lockName的锁
            List<String> lockObjects = new ArrayList<String>();
            for (String node : subNodes) {
                String node1 = node.split(splitStr)[0];
                if (node1.equals(lockName)) {
                    lockObjects.add(node);
                }
            }
            Collections.sort(lockObjects);
            if (log.isDebugEnabled()) {
                log.debug("{} 的锁是 {}", Thread.currentThread().getName(), CURRENT_LOCK);
            }

            // 若当前节点为最小节点，则获取锁成功
            if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockObjects.get(0))) {
                return true;
            }

            // 若不是最小节点，则找到自己的前一个节点
            String prevNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
            WAIT_LOCK = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryLock(long timeout, TimeUnit unit) {
        try {
            if (this.tryLock()) {
                return true;
            }
            return waitForLock(WAIT_LOCK, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 等待锁
     */
    private boolean waitForLock(String prev, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(ROOT_LOCK + "/" + prev, true);

        if (stat != null) {
            if (log.isDebugEnabled()) {
                log.debug("{} 等待锁 {}/{}", Thread.currentThread().getName(), ROOT_LOCK, prev);
            }
            this.countDownLatch = new CountDownLatch(1);
            // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
            this.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);
            this.countDownLatch = null;
            if (log.isDebugEnabled()) {
                log.debug("{} 等到了锁", Thread.currentThread().getName());
            }
        }
        return true;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
