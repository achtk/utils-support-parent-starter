/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chua.common.support.json.jsonpath;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 类型裁判
 * Used to specify generic type information in {@link ReadContext}
 *
 * <code>
 * TypeRef ref = new TypeRef<List<Integer>>() { };
 * </code>
 *
 * @param <T>
 * @author Administrator
 * @date 2023/08/31
 */
public abstract class BaseTypeRef<T> implements Comparable<BaseTypeRef<T>> {
    protected final Type type;

    protected BaseTypeRef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("No type info in TypeRef");
        }
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    /**
     * The only reason we define this method (and require implementation
     * of <code>Comparable</code>) is to prevent constructing a
     * reference without type information.
     */
    @Override
    public int compareTo(BaseTypeRef<T> o) {
        return 0;
    }
}
