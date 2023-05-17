package com.chua.groovy.support.example
//package com.chua.tools.groovy.example
//
//import groovy.json.JsonBuilder
//import groovy.json.JsonOutput
//
///**
// * @author CH* @since 2021/2/22
// * @version 1.0.0
// */
//class JsonExample {
//
//    private static final JsonExample JSON_EXAMPLE = new JsonExample();
//
//    static void main(String[] args) {
//        //输出Json
//        println JsonOutput.prettyPrint(JsonOutput.toJson([new Person(JSON_EXAMPLE, 1), new Person(JSON_EXAMPLE, 2)]))
//        //构建Json
//        def jsonBuilder = new JsonBuilder();
//        jsonBuilder.name{
//            capital  "demo"
//            value "1", "2"
//        }.toString()
//        println JsonOutput.prettyPrint(jsonBuilder.toString())
//    }
//
//    /**
//     * 测试对象
//     */
//    class Person {
//        String name
//        Integer age
//
//        Person(String name = IdUtils.createUuid(), Integer age) {
//            this.age = age
//            this.name = name;
//        }
//
//        def increaseAge(Integer years) {
//            this.name += years
//        }
//
//        def invokeMethod(String name, Object args) {
//            return "the methos is ${name}, the params is ${args}"
//        }
//
//        def methodMissing(String name, Object args) {
//            return "the methos is ${name} is missing"
//        }
//    }
//
//}
