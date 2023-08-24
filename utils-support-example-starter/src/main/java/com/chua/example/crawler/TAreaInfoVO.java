package com.chua.example.crawler;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TAreaInfoVO extends TAreaInfo {

    /**
     * 子级区域信息
     */
    private Set<TAreaInfoVO> child = new HashSet<>();

}
