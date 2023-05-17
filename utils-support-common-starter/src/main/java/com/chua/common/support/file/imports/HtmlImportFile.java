package com.chua.common.support.file.imports;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * 导入html文件
 *
 * @author CH
 */
@Spi("html")
public class HtmlImportFile extends AbstractImportFile {

    public HtmlImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @SneakyThrows
    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        Document document = Jsoup.parse(inputStream, "UTF-8", "");
        Elements headers = document.selectXpath("//table/thead/tr").get(0).children();
        Elements body = document.selectXpath("//table/tbody/tr");
        JsonArray jsonArray = new JsonArray();
        headers.forEach((SafeConsumer<Element>) element -> jsonArray.add(element.text()));

        int size = body.size();
        for (int i = 0; i < size; i++) {
            Element element = body.get(i);
            JsonArray item = new JsonArray(element.children().stream().map(Element::text).collect(Collectors.toList()));
            listener.accept(doAnalysis(jsonArray, type, item));
            boolean end = listener.isEnd(i);
            if (end) {
                break;
            }
        }
    }

    @Override
    public void afterPropertiesSet() {

    }
}

