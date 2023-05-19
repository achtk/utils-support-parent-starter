package com.chua.example.database;


import com.chua.common.support.database.transfer.ReaderChannel;
import com.chua.common.support.database.transfer.Transfer;
import com.chua.common.support.database.transfer.WriterChannel;
import com.chua.common.support.database.transfer.file.*;
import com.chua.common.support.value.DataMapping;
import com.chua.common.support.value.Pair;
import com.chua.example.DataSourceUtils;
import com.chua.poi.support.database.transfer.file.XlsxReaderChannel;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 数据传输
 */
public class DataTransferExample {

    public static void main(String[] args) throws IOException {
        DataSource atguigudb = DataSourceUtils.createDefaultMysqlDataSource(DataSourceUtils.localMysqlUrl("atguigudb"));
        DataSource zxb2 = DataSourceUtils.createMysqlDataSource("192.168.110.100:3306/db_zxb2", "zxb2", "123456");

        DataMapping dataMapping = DataMapping.builder()
                .addMapping("序号", new Pair("id", "序号"))
                .addMapping("文件", new Pair("file_name", "文件"))
                .addMapping("url地址", new Pair("url", "url地址"))
                .build();
//        DataMapping dataMapping = DataMapping.builder()
//                .addMapping("id", new Pair("id", "序号"))
//                .addMapping("file_name", new Pair("file_name", "文件"))
//                .addMapping("url", new Pair("url", "url地址"))
//                .build();

//        WriterChannel writerChannel = new DataSourceWriterChannel(zxb2, new MybatisMetadata<>(OssLog.class));
//        WriterChannel jsonWriterChannel = new JsonWriterChannel("Z:/1.json");
//        WriterChannel xlsWriterChannel = new XlsWriterChannel("我的订单.xls");
//        WriterChannel xlsWriterChannel = new XlsWriterChannel("Z:/1.xls");
//        WriterChannel tsvWriterChannel = new TsvWriterChannel("Z:/1.tsv");
//        WriterChannel csvWriterChannel = new CsvWriterChannel("Z:/1.csv");
//        WriterChannel xmlWriterChannel = new XmlWriterChannel("Z:/1.xml");

//        ReaderChannel readerChannel = new DataSourceReaderChannel(atguigudb, new MybatisMetadata<>(OssLog.class));
        ReaderChannel csvReaderChannel = new CsvReaderChannel(Files.newOutputStream(Paths.get("Z://1.csv")));
        ReaderChannel tsvReaderChannel = new TsvReaderChannel(Files.newOutputStream(Paths.get("Z://1.tsv")));
        ReaderChannel sqlReaderChannel = new SqlReaderChannel(Files.newOutputStream(Paths.get("Z://1.sql")));
        ReaderChannel xmlReaderChannel = new XmlReaderChannel(Files.newOutputStream(Paths.get("Z://1.xml")));
        ReaderChannel htmlReaderChannel = new HtmlReaderChannel(Files.newOutputStream(Paths.get("Z://1.html")));
        ReaderChannel jsonReaderChannel = new JsonReaderChannel(Files.newOutputStream(Paths.get("Z://1.json")));
//        ReaderChannel xlsReaderChannel = new XlsReaderChannel(Files.newOutputStream(Paths.get("Z://1.xls")));
//        ReaderChannel xlsxReaderChannel = new XlsxReaderChannel(Files.newOutputStream(Paths.get("Z://1.xlsx")));

        Transfer transfer = Transfer.builder().dataMapping(dataMapping).build();
//        transfer.transferTo(tsvReaderChannel, csvReaderChannel, sqlReaderChannel, xmlReaderChannel, htmlReaderChannel, jsonReaderChannel);
    }
}
