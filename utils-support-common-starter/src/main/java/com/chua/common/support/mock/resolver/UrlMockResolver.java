package com.chua.common.support.mock.resolver;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.spi.condition.PingCondition;
import com.chua.common.support.utils.RandomUtils;

import java.util.Map;

/**
 * url
 * @author CH
 */
@Spi("url")
@SpiCondition(onCondition = PingCondition.class)
public class UrlMockResolver implements MockResolver{

    private static final String IMAGE_ADDRESS = "https://image.so.com/i?q=%s&inact=0";

    private static final Map<String, JsonArray> CACHE = new ConcurrentReferenceHashMap<>(512);

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        String address = String.format(IMAGE_ADDRESS, mock.base());
        JsonArray list = CACHE.computeIfAbsent(address, s -> {
            HttpResponse execute = HttpClient.get().url(address).newInvoker().execute();
            String content = execute.content(String.class);
            String indexStr = "<script type=\"text/data\" id=\"initData\">";
            String substring = content.substring(content.indexOf(indexStr) + indexStr.length());
            substring = substring.substring(0, substring.indexOf("</script>"));
            JsonObject jsonObject = Json.getJsonObject(substring);
            return jsonObject.getJsonArray("list");
        });
        int size = list.size();
        int index = RandomUtils.randomInt(0, size);
        JsonObject jsonObject1 = list.getJsonObject(index);
        return jsonObject1.getString("thumb");
    }
}
