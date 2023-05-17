package com.chua.common.support.file.line;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.LineFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.univocity.parsers.common.IterableResult;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.csv.CsvParser;
import com.chua.common.support.file.univocity.parsers.csv.CsvParserSettings;
import com.chua.common.support.resource.ResourceConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

/**
 * csv
 *
 * @author CH
 */
@Spi("csv")
public class CsvFile extends AbstractResourceFile implements LineFile<String[]> {

    final CsvParserSettings settings = new CsvParserSettings();

    public CsvFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
        if (Projects.isWindows()) {
            settings.getFormat().setLineSeparator("\n");
        } else {
            settings.getFormat().setLineSeparator("\r\n");
        }
    }

    @Override
    public void line(Function<String[], Boolean> line, int skip) throws IOException {
        int count = 0;

        try (InputStreamReader reader = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            CsvParser parser = new CsvParser(settings);
            IterableResult<String[], ParsingContext> iterate = parser.iterate(reader);
            for (String[] next : iterate) {
                if (count++ < skip) {
                    continue;
                }
                Boolean apply = line.apply(next);
                if (apply) {
                    parser.stopParsing();
                    break;
                }
            }
        }
    }

}
