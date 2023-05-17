package com.chua.groovy.support.example
/**
 * @author CH* @since 2021/2/22
 * @version 1.0.0
 */
class HttpExample {

    static void main(String[] args) {
        def url = "https://www.baidu.com";
        println url.toURL().text
    }
}
