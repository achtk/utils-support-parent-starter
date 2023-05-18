package com.chua.example.el;

import com.chua.common.support.extra.el.expression.Expression;
import com.chua.common.support.utils.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.utils.Preconditions.assertEquals;


/**
 * @author CH
 */
public class Example {

    public static void main(String[] args) {
        //[] 获取元素支持
        el1();
        el2();
        el3();
        //四则运算支持
        al1();
        //对象属性获取和类属性获取支持
        ol1();
    }

    private static void ol1() {
        //对象属性获取和类属性获取支持

    }

    private static void al1() {
        //四则运算支持
        Expression lexer1 = Expression.parse("1+4/2");
        assertEquals(3, lexer1.calculate(null)); //对于不需要注入参数计算的场合，入参可以直接为null，不影响运算
        Expression lexer2 = Expression.parse("5-(4-1)");
        assertEquals(2, lexer2.calculate(null));
        Expression lexer3 = Expression.parse("1*2-1");
        assertEquals(1, lexer3.calculate(null));
    }

    private static void el3() {
        //获取Map键值
        Map < String, String > map = new HashMap < String, String > ();
        map.put("1", "12");
        Map < String, Object > vars = new HashMap < String, Object > ();
        vars.put("map", map);
        vars.put("age", "1");
        Expression lexer = Expression.parse("map['1']");
        assertEquals("12", lexer.calculate(vars));
        Expression lexer2 = Expression.parse("map[age]");
        assertEquals("12", lexer2.calculate(vars));
    }

    private static void el2() {
        //获取List中元素
        List< String > list = new ArrayList< String >();
        list.add("1212");
        list.add("13");
        Map < String, Object > vars = new HashMap < String, Object > ();
        vars.put("list", list);
        Expression lexer = Expression.parse("list[1]");
        assertEquals("13", lexer.calculate(vars));
    }

    /**
     * [] 获取元素支持
     */
    public static void el1() {
        //获取数组元素
        int[] array = new int[]
                {
                        1, 2, 3, 4
                };
        Map< String, Object > vars = new HashMap<>();
        vars.put("array", array);
        Expression lexer = Expression.parse("array[2]");//使用静态方法parse解析字符串形式的EL表达式，得到一个表达式实例。该表达式是一个并发安全的实例，可以供后续反复的并发的调用。一次生成即可，无需反复生成。
        assertEquals(3, lexer.calculate(vars));//外部参数通过Map的形式传递。使用方法calculate根据给定的参数计算得到表达式的值
    }
}
