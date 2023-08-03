package com.chua.common.support.query;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.lang.profile.DelegateProfile;
import com.chua.common.support.lang.profile.value.ProfileValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.query.Operations.EQUAL;

/**
 * 表单化查询
 * @author CH
 */
@Data
@AllArgsConstructor
public class FormQuery {
    /**
     * 名称
     */
    private String name;
    /**
     * 操作符
     */
    private Operations operations;
    /**
     * 值
     */
    private Object value;

    /**
     * 转化
     * @param obj 对象
     * @return 表单化查询条件
     */
    public static List<FormQuery> transFrom(Object obj) {
        if(null == obj) {
            return Collections.emptyList();
        }

        BeanMap beanMap = BeanMap.create(obj);
        if(beanMap.isEmpty()) {
            return Collections.emptyList();
        }

        DelegateProfile profile = new DelegateProfile();
        profile.addProfile("default", beanMap);
        ProfileValue profileValue = profile.getProfile().get("default");
        if(null == profileValue) {
            return Collections.emptyList();
        }
        List<FormQuery> rs = new LinkedList<>();
        for (String key : profileValue.keys()) {
            Object object = profile.getObject(key);
            if(null == object) {
                continue;
            }
            rs.add(new FormQuery(key, EQUAL, obj));
        }
        return rs;
    }
}
