package com.chua.example.wrapper;

import com.chua.common.support.file.export.ExportType;
import com.chua.common.support.mock.MockData;
import com.chua.common.support.wrapper.CollectionWrapper;
import com.chua.common.support.wrapper.Wrapper;
import com.chua.example.mock.User;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CH
 */
public class WrapperExample {

    public static void main(String[] args) throws FileNotFoundException {
        List<User> rs = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            rs.add(MockData.createBean(User.class));
        }
        CollectionWrapper<User> wrapper = Wrapper.of(rs);
        wrapper.writeTo(new FileOutputStream("E://1.tsv"), ExportType.TSV);
        System.out.println();
    }
}
