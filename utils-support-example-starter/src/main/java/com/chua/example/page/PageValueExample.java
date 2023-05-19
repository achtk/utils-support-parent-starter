package com.chua.example.page;

import com.chua.common.support.lang.page.Page;
import com.chua.common.support.lang.page.PageMemData;
import com.chua.common.support.lang.page.PageValue;
import com.chua.common.support.mock.MockData;
import com.chua.example.mock.MockTest;

/**
 * @author CH
 */
public class PageValueExample {

    public static void main(String[] args) {
        PageValue<MockTest> pageValue = PageValue.newBuilder(MockTest.class).addPageData(PageMemData.of(MockData.createListBean(MockTest.class))).build();
        Page<MockTest> query = pageValue.query(2, 10);
    }
}
