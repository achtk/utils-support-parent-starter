package com.chua.common.support.extra.el.baseutil.bytecode.structure;

public class ExceptionHandler
{
    /**
     * start_pc和end_pc为异常处理字节码在code[]的索引值。当程序计数器在[start_pc, end_pc)内时，表示异常会被该ExceptionHandler捕获
     */
    private int start_pc;
    private int end_pc;
    /**
     * handler_pc表示ExceptionHandler的起点，为code[]的索引值。
     */
    private int handler_pc;
    /**
     * catch_type为CONSTANT_Class类型常量项的索引，表示处理的异常类型。如果该值为0，则该ExceptionHandler会在所有异常抛出时会被执行，可以用来实现finally代码
     */
    private int catch_type;
}