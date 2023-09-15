/*
 * Copyright 2023 zoukang, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chua.proxy.support.route.locator;

import com.chua.proxy.support.bootstrap.LifeCycle;
import com.chua.proxy.support.route.Route;
import reactor.core.publisher.Flux;


/**
 * 路线定位器
 *
 * @author CH
 */
public interface RouteLocator extends LifeCycle {

    /**
     * 获取路线
     *
     * @param path 路径
     * @return {@link Flux}<{@link Route}>
     */
    Flux<Route> getRoutes(String path);

    /**
     * 开始
     */
    @Override
    default void start() {
    }

    /**
     * 停止
     */
    @Override
    default void stop() {
    }

}
