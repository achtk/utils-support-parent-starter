package com.chua.example.string;

import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class StringExample {

    public static void main(String[] args) {
      log.info("{}", StringUtils.safeEquals("233", "331"));
      log.info("{}", StringUtils.safeEquals("233", "233"));
    }
}
