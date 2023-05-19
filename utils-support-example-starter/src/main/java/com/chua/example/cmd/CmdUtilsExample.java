package com.chua.example.cmd;

import com.chua.common.support.utils.CmdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class CmdUtilsExample {

    public static void main(String[] args) {
        log.info(CmdUtils.exec("pandoc --help"));
    }
}
