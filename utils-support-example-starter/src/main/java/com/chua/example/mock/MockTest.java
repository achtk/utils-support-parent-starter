package com.chua.example.mock;

import com.chua.common.support.file.export.ExportProperty;
import com.chua.common.support.mock.Mock;
import lombok.Data;

import java.util.Date;

/**
 * 测试实体
 *
 * @author CH
 */
@Data
public class MockTest {

//    @ExportProperty("头像")
//    @Mock(value = Mock.Type.URL, base = "头像")
//    private String url;

//    @ExportProperty("九日浮梁有约登高者以病不赴")
//    @Mock(value = Mock.Type.HANYU, base = "浮世光阴易红叶")
//    private String hanyu;

//    @ExportProperty("出师表译文")
//    @Mock(value = Mock.Type.HANYU_YIWEN, base = "出师表")
//    private String hanyuYiWen;

//
//    @ExportProperty("相似图片")
//    @Mock(value = Mock.Type.URL_LIKE, base = "#MockTest.url")
//    private String urlLike;

    @ExportProperty("姓名")
    @Mock(Mock.Type.NAME)
    private String name;

//    @ExportProperty("邮箱")
//    @Mock({Mock.Type.PHONE, Mock.Type.MAIL_SUFFIX})
//    private String email;

//    @ExportProperty("纬度")
//    @Mock(Mock.Type.LATITUDE)
//    private String latitude;
//    @ExportProperty("经度")
//    @Mock(Mock.Type.LONGITUDE)
//    private String longitude;

    @ExportProperty("当前位置")
    @Mock(Mock.Type.LOCATION)
    private String location;

//    @ExportProperty("ipv4")
//    @Mock(Mock.Type.IPV4)
//    private String ipv4;

    @ExportProperty("手机号")
    @Mock(Mock.Type.PHONE)
    private String phone;
    @ExportProperty("身份证号")
    @Mock(Mock.Type.CERT)
    private String card;
    @ExportProperty("当前城市")
    @Mock(Mock.Type.CITY)
    private String city;


    @ExportProperty(value = "年龄")
    @Mock(value = Mock.Type.AGE, base = "#MockTest.card")
    private String age;
    //
    @ExportProperty("性别")
    @Mock(value = Mock.Type.SEX, base = "#MockTest.card")
    private Integer sex;

    @ExportProperty(value = "生日", format = "yyyy-MM-dd")
    @Mock(value = Mock.Type.BIRTHDAY, base = "#MockTest.card")
    private Date birthday;
    //
//
    @Mock(Mock.Type.NATIONALITY)
    @ExportProperty("民族")
    private String nationality;
//
//    @ExportProperty("车牌")
//    @Mock(Mock.Type.PLATE)
//    private String plate;
//
//    @ExportProperty(value = "拼音")
//    @Mock(value = Mock.Type.NAME_PINYIN, base = "#MockTest.name")
//    private String pinyin;
//
//
//    @Cache(key = "#t.name+'-'+#m.name", timeout = "10s")
//    public String getUuid() {
//        return UUID.randomUUID().toString();
//    }


}
