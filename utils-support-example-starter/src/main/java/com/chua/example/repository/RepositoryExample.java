package com.chua.example.repository;


import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;

import java.util.List;

/**
 * @author CH
 */
public class RepositoryExample {

    public static void main(String[] args) {
//        testRepositoryClasspath();
//        testRepositoryFileSystem();
//        testRepositoryMulti();
        testRepositoryCompress("compress:C:\\Program Files\\Java\\jdk1.8.0_291\\1\\paddle.tar");
    }

    private static void testRepositoryCompress(String s) {
        Repository repository = Repository.of(s);
        List<Metadata> metadata = repository.getMetadata("*.dll");
        System.out.println(metadata);

    }

    private static void testRepositoryMulti() {
        Repository repository = Repository.classpath().add(Repository.current());
        Repository resolve = repository.resolve("mean_std.npz");
        System.out.println(resolve);
        Repository resolve1 = repository.resolve("*.dll");
        System.out.println(resolve);

    }

    private static void testRepositoryFileSystem() {
        Repository repository = Repository.current();
        Repository resolve = repository.resolve("*.dll");
        System.out.println(resolve);
    }

    private static void testRepositoryClasspath() {
        Repository repository = Repository.classpath();
        Repository resolve = repository.resolve("mean_std.npz");
        System.out.println(resolve);
    }
}
