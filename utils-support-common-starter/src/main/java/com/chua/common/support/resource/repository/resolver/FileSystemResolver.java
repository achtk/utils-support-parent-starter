package com.chua.common.support.resource.repository.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.context.annotation.AutoInject;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.repository.FileSystemMetadata;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.UrlMetadata;
import com.chua.common.support.resource.resource.FileSystemResource;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 解释器
 *
 * @author CH
 */
@Spi("file")
public final class FileSystemResolver implements Resolver {

    @AutoInject
    private PathMatcher pathMatcher = PathMatcher.INSTANCE;

    @Override
    public List<Metadata> resolve(URL root, String name) {
        File file = new File(root.getFile());
        if (!pathMatcher.isPattern(name)) {
            File file1 = new File(file, name);
            if (file1.exists()) {
                return Collections.singletonList(new FileSystemMetadata(file1));
            }

            return Collections.emptyList();
        }

        List<Metadata> result = new LinkedList<>();

        String rootPath = file.getPath();
        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String path = StringUtils.startWithMove(file.toString().replace(rootPath, "").replace("\\", "/"), "/");
                    if (pathMatcher.match(name, path)) {
                        result.add(new FileSystemMetadata(file.toFile()));
                    }
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    String path = StringUtils.startWithMove(dir.toString().replace(rootPath, "").replace("\\", "/"), "/");
                    if (pathMatcher.match(name, path)) {
                        result.add(new FileSystemMetadata(dir.toFile()));
                    }
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
