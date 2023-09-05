package com.chua.example.objectcontent;


import com.chua.common.support.objects.scanner.annotations.AutoService;
import com.chua.common.support.objects.scanner.annotations.AutoValue;

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

    private final TestDemo testDemo;


    public Test2Demo(TestDemo testDemo) {
        this.testDemo = testDemo;
    }
}
