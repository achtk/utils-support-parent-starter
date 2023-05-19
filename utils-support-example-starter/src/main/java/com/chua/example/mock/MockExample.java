package com.chua.example.mock;

import com.chua.common.support.file.export.ExportFileBuilder;
import com.chua.common.support.file.export.ExportType;
import com.chua.common.support.mock.MockData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CH
 */
public class MockExample {
    public static void main(String[] args) throws IOException {

        List<User> rs = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            rs.add(MockData.createBean(User.class));
        }

        ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.xlsx")))
                .header(User.class)
                .type(ExportType.XLSX)
                .doRead(rs);
        System.out.println();
    }
}
