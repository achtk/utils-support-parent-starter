package com.alibaba.json.support.hsf;

import java.lang.reflect.Method;

public interface MethodLocator {
    Method findMethod(String[] types);
}
