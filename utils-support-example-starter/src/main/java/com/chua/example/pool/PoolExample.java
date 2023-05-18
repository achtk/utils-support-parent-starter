package com.chua.example.pool;

import com.chua.common.support.pool.BoundBlockingPool;
import com.chua.common.support.pool.Pool;
import com.chua.common.support.pool.ReflectObjectFactory;
import com.chua.common.support.thread.ExecutorCounter;
import com.chua.common.support.thread.ThreadProvider;
import com.chua.common.support.thread.ThreadTask;
import com.chua.common.support.utils.RandomUtils;
import com.google.common.base.Strings;

/**
 * 对象池例子
 *
 * @author CH
 * @since 2022-05-23
 */
public class PoolExample {
    public static void main(String[] args) {
        //创建一个阻塞的对象池
        //参数1: 对象池数量
        //参数2: 检验器， 用于检验对象是否有效
        //参数3: 对象生成器, 用于初始化对象池数量时，生成对象
        Pool<TestEntity> pool = new BoundBlockingPool<>(new ReflectObjectFactory<>(TestEntity.class));
        int count = 0;
        ThreadProvider threadProvider = ThreadProvider.of("test");
        ExecutorCounter executorCounter = threadProvider.coreSize(10).newFixedThreadExecutor();
        executorCounter.forEach(1000, new ThreadTask() {
            @Override
            public Object execute(Object value) {
                //获取对象
                TestEntity testEntity = pool.getObject();
                if (Strings.isNullOrEmpty(testEntity.getSuccess())) {
                    testEntity.setSuccess(RandomUtils.randomString(10));
                }
                System.out.println(testEntity);
                //回收对象
//                testEntity.close();
                return null;
            }
        });

        executorCounter.allOfComplete();

        //关闭对象池
        pool.close();
    }
}
