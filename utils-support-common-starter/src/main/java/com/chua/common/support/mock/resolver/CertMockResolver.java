package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.utils.CardUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * 身份证
 *
 * @author CH
 */
@Spi("cert")
public class CertMockResolver implements MockResolver {

    /**
     * 随机生成地级市、盟、自治州代码 3-4
     */
    static final String[] CITY = {"01", "02", "03", "04", "05", "06", "07", "08",
            "09", "10", "21", "22", "23", "24", "25", "26", "27", "28"};
    /**
     * 随机生成省、自治区、直辖市代码 1-2
     */
    static final String[] PROVINCE = {"11", "12", "13", "14", "15", "21", "22", "23",
            "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
            "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
            "63", "64", "65", "71", "81", "82"};

    static final String[] CHECK = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "X"};
    /**
     * 随机生成县、县级市、区代码 5-6
     */
    static final String[] COUNTY = {"01", "02", "03", "04", "05", "06", "07", "08",
            "09", "10", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"};

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom();
    }


    public static String getRandom() {

        String province = PROVINCE[new Random().nextInt(PROVINCE.length - 1)];

        String city = CITY[new Random().nextInt(CITY.length - 1)];

        String county = COUNTY[new Random().nextInt(COUNTY.length - 1)];
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE,
                date.get(Calendar.DATE) - new Random().nextInt(365 * 100));
        String birth = dft.format(date.getTime());
        String no = new Random().nextInt(999) + "";
        String card17 = province + city + county + birth + no;
        char checkCode18 = CardUtils.getCheckCode18(card17);
        return card17 + checkCode18;
    }
}
