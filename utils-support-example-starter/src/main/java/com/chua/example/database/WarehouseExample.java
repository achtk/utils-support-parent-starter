//package com.chua.example.database;
//
//import com.chua.common.support.database.DataSourceUtils;
//import com.chua.common.support.database.metadata.TypeMetadata;
//import com.chua.common.support.database.repository.MetadataRepository;
//import com.chua.common.support.database.warehouse.DataSourceWarehouse;
//import com.chua.common.support.database.warehouse.FileWarehouse;
//import com.chua.common.support.database.warehouse.Warehouse;
//import com.chua.common.support.database.warehouse.operator.DDLOperator;
//import com.chua.common.support.log.SysLog;
//import com.chua.example.support.http.Holiday;
//import com.chua.example.support.utils.MockUtils;
//
//import java.io.File;
//
//public class WarehouseExample {
//
//    public static void main(String[] args) {
//
//        //testDatabase();
//        testFile();
//    }
//
//    private static void testFile() {
//        Warehouse warehouse = new FileWarehouse(new File("Z:/1.xls"));
//        MetadataRepository<OssLog> repository = warehouse.packUp(OssLog.class);
//        repository.save(MockUtils.create(OssLog.class));
//
//    }
//
//    private static void testDatabase() {
//        Warehouse warehouse = new DataSourceWarehouse(DataSourceUtils.createH2FileDataSource("h2.db"));
//        DDLOperator ddlOperator = warehouse.getDDLOperator();
//        ddlOperator.createOrUpdate(TypeMetadata.of(Holiday.class));
//        ddlOperator.createOrUpdate(TypeMetadata.of(OssLog.class));
//        ddlOperator.createOrUpdate(TypeMetadata.of(SysLog.class));
//
//        MetadataRepository<Holiday> repository = warehouse.packUp(Holiday.class);
//        repository.save(MockUtils.create(Holiday.class));
//        Holiday holiday = repository.findById(1);
//        repository.removeById(4);
//
//        System.out.println();
//    }
//}
