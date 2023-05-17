package com.chua.example.converter;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.unit.name.NamingCase;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author CH
 */
@Slf4j
public class ConverterExample {

    public static void main(String[] args) throws IOException {
        log.info("'1' => {}", Converter.convertIfNecessary("1", Long.class));
        log.info("'1f' => {}", Converter.convertIfNecessary("1f", Long.class));
        log.info("'1d' => {}", Converter.convertIfNecessary("1d", Long.class));
        log.info("'1M' => {}", Converter.convertIfNecessary("1M", Long.class));
        log.info("'壹十壹' => {}", Converter.convertIfNecessary("壹十壹", Long.class));
        log.info("'1' => {}", Converter.convertIfNecessary("1", BigDecimal.class));

        log.info("toHyphenLowerCamel => {}", NamingCase.toHyphenLowerCamel("1"));
        log.info("String -> Integer => {}", Converter.convertIfNecessary("1", Integer.class));
    }
}
