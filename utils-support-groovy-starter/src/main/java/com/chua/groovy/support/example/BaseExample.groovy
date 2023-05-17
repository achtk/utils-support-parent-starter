package com.chua.groovy.support.example

/**
 * 基础信息
 * @author CH* @since 2021/2/22
 * @version 1.0.0
 */
class BaseExample {
    static void main(String[] args) {
        def list = [1, 2, 3, 4] as String[]
        //默认使用LinkedHashMap类型
        def map = [a: 1, b: 2]
        //一元集合
        println "一元集合: " + list + "(" + list.getClass().getName() + ")"
        //二元集合
        println "二元集合: " + map
        //闭包
        list.each {
            println "闭包元素: " + it
        }
        //区间
        println "区间" + ('a'..'z').collect()
    }
}
