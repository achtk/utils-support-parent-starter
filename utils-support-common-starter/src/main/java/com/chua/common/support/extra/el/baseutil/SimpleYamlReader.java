package com.chua.common.support.extra.el.baseutil;

import com.chua.common.support.utils.IoUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleYamlReader
{
    static class Element
    {
        int     level;
        String  name;
        Object  value;
        int     type;
        Element parent;
    }

    static final int UNKNOWN      = 0;
    static final int STRING       = 1;
    static final int MAP          = 2;
    static final int LIST         = 3;
    static final int LIST_ELEMENT = 4;

    public static Map<String, Object> read(InputStream inputStream) throws IOException
    {
        List<Element> seqence   = new ArrayList<>();
        byte[]        content   = IoUtils.toByteArray(inputStream);
        LineHelper    helper    = new LineHelper(content);
        int           seqenceId = 0;
        String        currentLine;
        while ((currentLine = helper.currentLine()) != null)
        {
            if ("".equals(currentLine.trim()))
            {
                continue;
            }
            int level = 0;
            while (currentLine.charAt(level) == ' ')
            {
                level++;
            }
            Element element = new Element();
            element.level = level;
            if (currentLine.charAt(level) == '-')
            {
                element.type = LIST_ELEMENT;
                element.value = currentLine.trim().substring(2);
                int parentSeqenceId = seqenceId - 1;
                while (parentSeqenceId > -1)
                {
                    Element parent = seqence.get(parentSeqenceId);
                    if (parent.level < level)
                    {
                        if (parent.type == UNKNOWN)
                        {
                            parent.type = LIST;
                            parent.value = new LinkedList<Object>();
                        }
                        else if (parent.type == LIST)
                        {
                            ;
                        }
                        else
                        {
                            throw new IllegalArgumentException();
                        }
                        ((List<Object>) parent.value).add(element);
                        element.parent = parent;
                        break;
                    }
                    else if (parent.level >= level)
                    {
                        parentSeqenceId -= 1;
                    }
                }
                if (element.parent == null)
                {
                    throw new IllegalStateException();
                }
                seqence.add(seqenceId, element);
            }
            else if (currentLine.charAt(level) == '#')
            {
                seqenceId -= 1;
            }
            else if (currentLine.contains(":"))
            {
                String trim = currentLine.trim();
                int    i    = trim.indexOf(":");
                element.name = trim.substring(0, i);
                if (trim.endsWith(":"))
                {
                    element.type = UNKNOWN;
                }
                else
                {
                    element.type = STRING;
                    element.value = trim.substring(i + 1).trim();
                }
                if (level != 0)
                {
                    int parentSeqenceId = seqenceId - 1;
                    while (parentSeqenceId > -1)
                    {
                        Element parent = seqence.get(parentSeqenceId);
                        if (parent.level < level)
                        {
                            if (parent.type == UNKNOWN)
                            {
                                parent.type = MAP;
                                parent.value = new HashMap<String, Element>();
                            }
                            else if (parent.type == MAP)
                            {
                                ;
                            }
                            else
                            {
                                throw new IllegalArgumentException();
                            }
                            ((Map<String, Element>) parent.value).put(element.name, element);
                            element.parent = parent;
                            break;
                        }
                        else if (parent.level >= level)
                        {
                            parentSeqenceId -= 1;
                        }
                    }
                    if (element.parent == null)
                    {
                        throw new IllegalArgumentException();
                    }
                }
                seqence.add(seqenceId, element);
            }
            else
            {
                throw new IllegalArgumentException(currentLine);
            }
            seqenceId++;
        }
        Map<String, Object> map = new HashMap<>();
        for (Element element : seqence)
        {
            switch (element.type)
            {
                case UNKNOWN :
                {
                    element.value = "";
                    element.type = STRING;
                    map.put(path(element), "");
                    break;
                }
                case STRING : map.put(path(element), element.value); break;
                case LIST : map.put(path(element), new LinkedList<String>());break;
                case LIST_ELEMENT : ((List<String>) map.get(path(element.parent))).add((String) element.value);break;
                default:break;
            }
        }
        return map;
    }

    static String path(Element element)
    {
        List<String> list = new LinkedList<>();
        do
        {
            list.add(element.name);
        }
        while ((element = element.parent) != null);
        Collections.reverse(list);
        return list.stream().collect(Collectors.joining("."));
    }
    /**
     * 1、使用#开头的内容，后续一整行全部为注释
     * 2、只有K: 的情况，后续可以有两种情况。一种是缩进，那就带着是K这个对象的属性；一种是更少的缩进，那就代表这个K:是一个空字符串值
     * 3、如果是- 的情况，则不允许下级嵌套。即，自身的父级必须不是该类型。
     */
    /**
     *logging:
     *   config: classpath:logback-dev.xml
     * spring:
     *   jpa:
     *     hibernate:
     * #      ddl-auto: update
     *   datasource:
     *     url: jdbc:log4jdbc:mysql://127.0.0.1:3306/drg_medical?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false
     *     driver-class-name: net.sf.log4jdbc.DriverSpy
     *     hikari:
     *       username: root
     *       password:
     *   redis:
     *     host: 127.0.0.1
     *     port: 6379
     *   liquibase:
     *     change-log: classpath:/db/changelog/db.changelog-master.xml
     *     enabled: false
     */
    /**
     * 解析思路：
     * 1、按照读取的顺序，将每一行的原始数据读取后，放入到list中。
     * 2、如果是缩进行，首先寻找最近的行，如果其缩进不是自己的父类，则该行的状态变更为确定的状态。重复该过程，直到找到父类的行，指向到正确的行。
     * 3、
     */
    static class LineHelper
    {
        private byte[] src;
        private int    end   = 0;
        private int    index = 0;

        public LineHelper(byte[] src)
        {
            this.src = src;
        }

        public String currentLine()
        {
            if (index >= src.length - 1)
            {
                return null;
            }
            end = currentLine(index);
            String result;
            if (src[end] == '\n')
            {
                result = new String(src, index, end - index);
                index = end + 1;
            }
            else if (src[end] == '\r')
            {
                result = new String(src, index, end - index);
                index = end + 2;
            }
            else//结尾，直接没有换行符
            {
                result = new String(src, index, end - index + 1);
                index = end;
            }
            return result;
        }

        /**
         * 从index位置开始（包含）,找寻/n的坐标。并且返回
         *
         * @param index
         * @return
         */
        private int currentLine(int index)
        {
            for (int i = index; i < src.length; i++)
            {
                if (src[i] == '\n')
                {
                    if (i > index && src[i - 1] == '\r')
                    {
                        return i - 1;
                    }
                    else
                    {
                        return i;
                    }
                }
            }
            return src.length - 1;
        }
    }

    public static void main(String[] args) throws IOException
    {
        Map<String, Object> map = SimpleYamlReader.read(new FileInputStream(new File("/Users/linbin/代码空间/baseutil/src/test/resources/test.yml")));
        map.forEach((name, value) -> {
            System.out.println(name + ":" + value);
        });
    }
}
