package com.chua.common.support.task.arrange.async.worker;


import com.chua.common.support.task.arrange.Worker;
import lombok.EqualsAndHashCode;

/**
 * 对依赖的wrapper的封装
 *
 * @author wuweifeng wrote on 2019-12-20
 * @version 1.0
 */
@EqualsAndHashCode
public class DependWrapper {
    private Worker<?, ?> dependWrapper;
    /**
     * 是否该依赖必须完成后才能执行自己.<p>
     * 因为存在一个任务，依赖于多个任务，是让这多个任务全部完成后才执行自己，还是某几个执行完毕就可以执行自己
     * 如
     * 1
     * ---3
     * 2
     * 或
     * 1---3
     * 2---3
     * 这两种就不一样，上面的就是必须12都完毕，才能3
     * 下面的就是1完毕就可以3
     */
    private boolean must = true;

    public DependWrapper(Worker<?, ?> dependWrapper, boolean must) {
        this.dependWrapper = dependWrapper;
        this.must = must;
    }

    public DependWrapper() {
    }

    public Worker<?, ?> getDependWrapper() {
        return dependWrapper;
    }

    public void setDependWrapper(Worker<?, ?> dependWrapper) {
        this.dependWrapper = dependWrapper;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    @Override
    public String toString() {
        return "DependWrapper{" +
                "dependWrapper=" + dependWrapper +
                ", must=" + must +
                '}';
    }
}
