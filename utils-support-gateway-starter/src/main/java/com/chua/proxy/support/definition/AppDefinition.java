package com.chua.proxy.support.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用程序定义
 *
 * @author CH
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppDefinition {

    private String id;

    private String key;

    private String secret;

}
