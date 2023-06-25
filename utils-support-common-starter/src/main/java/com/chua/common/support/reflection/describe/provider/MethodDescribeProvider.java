package com.chua.common.support.reflection.describe.provider;

import com.chua.common.support.collection.CollectionProvider;
import com.chua.common.support.lang.loader.Loadable;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 方法提供者
 *
 * @author CH
 */
public class MethodDescribeProvider extends LinkedList<MethodDescribe>
        implements CollectionProvider<MethodDescribe, MethodDescribeProvider>,
        DescribeProvider,
        Loadable<MethodDescribeProvider> {

    static final MethodDescribeProvider EMPTY = new MethodDescribeProvider();

    final Map<MethodDescribe, AtomicInteger> loaded = new ConcurrentHashMap<>();

    /**
     * 初始化
     *
     * @return 初始化
     */
    public static MethodDescribeProvider empty() {
        return EMPTY;
    }

    @Override
    public MethodDescribeProvider addChain(MethodDescribe methodDescribe) {
        add(methodDescribe);
        return this;
    }

    @Override
    public MethodDescribeProvider addChains(Collection<MethodDescribe> v) {
        addAll(v);
        return this;
    }

    @Override
    public MethodDescribeProvider get() {
        return this;
    }

    @Override
    public <T> T execute(Object entity, Class<T> target, Object... args) {
        MethodDescribe methodDescribe = findMethodDescribe(args, true);
        if (null == methodDescribe) {
            methodDescribe = findMethodDescribe(args, false);
        }

        if (null == methodDescribe) {
            return null;
        }

        return methodDescribe.invoke(entity, args).getValue(target);
    }

    @Override
    public <T> T executeSelf(Class<T> target, Object... args) {
        MethodDescribe methodDescribe = findMethodDescribe(args, true);
        if (null == methodDescribe) {
            methodDescribe = findMethodDescribe(args, false);
        }

        if (null == methodDescribe) {
            return null;
        }

        return methodDescribe.invoke(methodDescribe.entity(), args).getValue(target);
    }

    @Override
    public TypeDescribe isChainSelf(Object... args) {
        return new TypeDescribe(executeSelf(Object.class, args));
    }

    /**
     * 检索方法
     *
     * @param args 参数
     * @return 方法
     */
    private MethodDescribe findMethodDescribe(Object[] args, boolean requireNone) {
        List<MethodDescribe> prob = new LinkedList<>();
        for (MethodDescribe methodDescribe : this) {
            doAnalysis(prob, methodDescribe, args, requireNone);
        }

        if (prob.isEmpty()) {
            return null;
        }

        if (prob.size() == 1) {
            MethodDescribe methodDescribe = prob.get(0);
            loaded.computeIfAbsent(methodDescribe, it -> new AtomicInteger(0)).incrementAndGet();
            return methodDescribe;
        }

        return prob.stream().findFirst().get();

    }


    /**
     * 分析方法
     *
     * @param prob           结果
     * @param methodDescribe 方法描述
     * @param args           参数
     * @param requireNone    是否全部不为空
     */
    private void doAnalysis(List<MethodDescribe> prob, MethodDescribe methodDescribe, Object[] args, boolean requireNone) {
        boolean hasNoneValue = false;
        ParameterDescribe[] parameterDescribes = methodDescribe.parameterDescribes();
        if (parameterDescribes.length == 0) {
            hasNoneValue = true;
        }

        if(parameterDescribes.length != args.length) {
            return;
        }

        for (ParameterDescribe parameterDescribe : parameterDescribes) {
            Object value = createParameter(parameterDescribe, args);
            if (null == value && requireNone) {
                break;
            }

            hasNoneValue = true;
        }

        if (!hasNoneValue) {
            return;
        }

        prob.add(methodDescribe);
    }

    /**
     * 创建参数
     *
     * @param parameterDescribe 字段描述
     * @param args              参数
     * @return 结果
     */
    private Object createParameter(ParameterDescribe parameterDescribe, Object[] args) {
        int length = args.length;
        int index = parameterDescribe.index();
        if (index < length) {
            if (ClassUtils.isEquals(parameterDescribe.returnClassType(), args[index])) {
                return parameterDescribe.createValue(args[index]);
            }
        }

        for (Object arg : args) {
            if (ClassUtils.isEquals(parameterDescribe.returnClassType(), arg)) {
                return parameterDescribe.createValue(arg);
            }
        }

        return null;
    }

}
