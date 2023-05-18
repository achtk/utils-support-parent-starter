//package com.chua.example.database;
//
//import com.chua.common.support.database.api.Api;
//import com.chua.common.support.database.api.ApiRepository;
//import com.chua.common.support.json.Json;
//import com.chua.common.support.json.JsonObject;
//import com.chua.example.support.utils.DataSourceUtils;
//
///**
// * 单数据查询
// * =========================================================
// * <pre>
// * GET
// *
// * {
// *      "XxlJobInfo":{}
// * }
// *
// * 多数据查询
// * =========================================================
// * GET
// *
// * {
// *      "XxlJobInfo[]":{
// *      }
// * }
// *
// * 条件查询
// * =========================================================
// * GET {
// *      "XxlJobInfo":{
// *          "@count":1,                                     //每页数量
// *          "@page": 2                                      //第几页
// *          "id": 1                                         //ID = 1
// *          "@group": "id"                                  //根据id分组
// *          "@order": "id"                                  //根据id排序
// *          "/XxlJobInfo2/id": "id"                         //根据id关联XxlJobInfo2/id     [@->,@-<]
// *      },
// *      "XxlJobInfo2":{
// *      }
// * }
// * 支持多数据库查询
// * =========================================================
// * 支持多数据库分表查询
// * =========================================================
// * {
// *      "TOrder[]":{
// *          "sysTime": "[2019-06-14, 2019-08-14]"           //区间查询
// *      }
// * }
// * </pre>
// *
// * @author CH
// * @see com.chua.example.support.jsonapi.TOrder
// * @see com.chua.example.support.jsonapi.TOrder20190614
// * @see com.chua.example.support.jsonapi.XxlJobInfo
// * @see com.chua.example.support.jsonapi.XxlJobLog
// * @see com.chua.api.json.support.open.OpenMapping
// */
//public class ApiExample {
//
//    public static void main(String[] args) {
//        ApiRepository repository = ApiRepository.builder()
//                .writerWithDefaultPrettyPrinter(true)
//                .addMapping(DataSourceUtils.createDefaultMysqlDataSource("localhost:3306/sakila"))
//                .addMapping(DataSourceUtils.createDefaultMysqlDataSource("localhost:3306/atguigudb"))
//                .addMapping(DataSourceUtils.createDefaultMysqlDataSource("localhost:3306/htgfhf"))
//                .addMapping(DataSourceUtils.createMysqlDataSource("jdbc:mysql://192.168.110.100:3306/yunshang_auditoria?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true", "yunshang_auditoria", "yunshang_auditoria(iFto-?2=u"))
//                .build();
//
//        Api api = repository.createApi();
//
//        System.out.println(api.get(new JsonObject().add("sys_log", new JsonObject())));
//        System.out.println(api.get(Json.getJsonObject("{'sys_log[]':{'@count': 2}}")));
//    }
//}
