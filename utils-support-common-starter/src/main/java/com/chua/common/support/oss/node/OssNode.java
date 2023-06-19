package com.chua.common.support.oss.node;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * node
 * @author CH
 */
@Data
@AllArgsConstructor
public class OssNode {

    private String id;

    private String parent;
    private String name;

    private LocalDateTime lastModified;

    private boolean isFile;

    private boolean hasChildren;
}
