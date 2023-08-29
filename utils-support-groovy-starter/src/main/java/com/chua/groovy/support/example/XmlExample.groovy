package com.chua.groovy.support.example

import com.chua.common.support.file.xml.Xml
import groovy.xml.MarkupBuilder
import groovy.xml.XmlSlurper;
/**
 * 基础信息
 * @author CH* @since 2021/2/22
 * @version 1.0.0
 */
class XmlExample {
    static void main(String[] args) {
        //解析XML
//        parseXML();
        //创建XML
//        createXML();
        Xml.toJsonObject(getXml());
    }
    /**
     * 获取XML
     * @return xml字符串
     */
    static String getXml() {
        '''
            <response version-api="2.0">
                <value>
                    <books id="1" classification="android">
                        <book available="20" id="1">
                            <title>疯狂Android讲义</title>
                            <author id="1">李刚</author>
                        </book>
                        <book available="14" id="2">
                           <title>第一行代码</title>
                           <author id="2">郭林</author>
                       </book>
                       <book available="13" id="3">
                           <title>Android开发艺术探索</title>
                           <author id="3">任玉刚</author>
                       </book>
                       <book available="5" id="4">
                           <title>Android源码设计模式</title>
                           <author id="4">何红辉</author>
                       </book>
                   </books>
                   <books id="2" classification="web">
                       <book available="10" id="1">
                           <title>Vue从入门到精通</title>
                           <author id="4">李刚</author>
                       </book>
                   </books>
               </value>
            </response>
        '''
    }
    /**
     * 解析XML
     */
    static void parseXML() {
        def xmlParser = new XmlSlurper();
        def response = xmlParser.parseText(getXml());
        println "获取[books[0].book[0].title]的值" + response.value.books[0].book[0].title.text()
        println "获取[books[0].book[0].author]的值" + response.value.books[0].book[0].author.text()
        println "获取[books[1].book[0]]的属性available的值" + response.value.books[1].book[0].@available
    }
    /**
     * 创建XML
     */
    static void createXML() {
        def pw = new StringWriter()
        def xmlMarker = new MarkupBuilder(pw);
        //创建 <root type="demo"></root>
        xmlMarker.root(type: 'demo') {
            //创建 <element id="1"></element>
            name(id: 1) {
                //创建 <text id="1">value</text>
                text(id: 1, 'value')
            }
            //创建 <element id="2" />
            name(id: 2)
        }
        println pw
    }
}
