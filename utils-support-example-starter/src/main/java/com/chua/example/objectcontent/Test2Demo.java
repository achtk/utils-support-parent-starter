package com.chua.example.objectcontent;


import com.chua.common.support.objects.scanner.annotations.AutoService;
import com.chua.common.support.objects.scanner.annotations.AutoValue;

import javax.annotation.Resource;

/**
 * 测试演示
 *
 * @author CH
 * @since 2023/09/02
 */
@AutoService
public class Test2Demo {


    @AutoValue("user.home")
    private String userHome;

    @Resource
    private TestDemo testDemo;


}
