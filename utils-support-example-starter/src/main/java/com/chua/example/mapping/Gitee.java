package com.chua.example.mapping;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.mapping.annotation.MappingAddress;
import com.chua.common.support.mapping.annotation.MappingRequest;

import java.util.List;

/**
 * @author CH
 */
@Extension("jsoup")
@MappingAddress("https://gitee.com/explore/payment-dev?lang=Java")
public interface Gitee {


    @MappingRequest("//div[@class='ui relaxed divided items explore-repo__list']//div[@class='item']")
    GiteeTitle[] test();
}
