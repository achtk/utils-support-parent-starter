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
public class User {

    @ExportProperty("姓名")
    @Mock(Mock.Type.NAME)
    private String name;
    @ExportProperty("身份证号")
    @Mock(Mock.Type.CERT)
    private String card;
    @ExportProperty(value = "年龄")
    @Mock(value = Mock.Type.AGE, base = "User.card")
    private String age;
    @ExportProperty("性别")
    @Mock(value = Mock.Type.SEX, base = "User.card")
    private Integer sex;

    @ExportProperty(value = "生日", format = "yyyy/MM/dd")
    @Mock(value = Mock.Type.BIRTHDAY, base = "User.card")
    private Date birthday;

    @ExportProperty("手机号")
    @Mock(Mock.Type.PHONE)
    private String phone;

}
