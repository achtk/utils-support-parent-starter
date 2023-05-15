package com.chua.common.support.path;

import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

/**
 * 简单路劲访问器
 *
 * @author CH
 * @since 2021-09-29
 */
public class SimplePathVisitor extends SimpleFileVisitor<Path> implements FileVisitor<Path> {
    protected SimplePathVisitor() {
    }
}
