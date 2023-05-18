package com.chua.poi.support.template;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.word.parse.excel.ExcelMapParse;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.lang.template.Template;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.IoUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.afterturn.easypoi.util.PoiElUtil.EMPTY;
import static cn.afterturn.easypoi.util.PoiElUtil.FOREACH;

/**
 * word
 *
 * @author CH
 */
@Spi({"doc", "docx"})
public class WordTemplate implements Template {
    protected Map body;
    public static final String START_STR = "${";
    public static final String END_STR = "}";

    private static final Pattern P;

    static {
        P = Pattern.compile("\\$\\{(.*?)\\}");
    }


    @Override
    @SneakyThrows
    public void resolve(InputStream inputStream, OutputStream outputStream, Map<String, Object> templateData) {
        this.initialData(templateData);
        OPCPackage opcPackage = OPCPackage.open(inputStream);
        String extension;
        try (InputStream stream = IoUtils.copy(inputStream)) {
            extension = IoUtils.getMimeType(stream);
        }
        XWPFDocument xwpfDocument = null;
        if ("docx".equals(extension)) {
            xwpfDocument = new XWPFDocument(opcPackage);
        }
        analysisDocument(xwpfDocument, outputStream);
    }

    /**
     * 初始化参数
     *
     * @param templateData 数据
     * @param <T>          类型
     */
    private <T> void initialData(Map<String, T> templateData) {
        this.body = templateData;
    }

    /**
     * 解析文件
     *
     * @param xwpfDocument doc
     * @param outputStream 输出
     */
    private void analysisDocument(XWPFDocument xwpfDocument, OutputStream outputStream) {
        //段落
        analysisParagraphs(xwpfDocument.getParagraphs());
        analysisHeaderAndFoot(xwpfDocument);
        analysisTables(xwpfDocument);
        try {
            xwpfDocument.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析表格
     *
     * @param xwpfDocument doc
     */
    private void analysisTables(XWPFDocument xwpfDocument) {
        List<XWPFTable> tables = xwpfDocument.getTables();
        for (XWPFTable table : tables) {
            analysisTable(table);
        }
    }

    /**
     * 解析表格
     *
     * @param table table
     */
    private void analysisTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        analysisTableRows(table, rows);
        table.removeRow(1);
    }

    /**
     * 解析表格行
     *
     * @param table table
     * @param rows  rows
     */
    private void analysisTableRows(XWPFTable table, List<XWPFTableRow> rows) {
        for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
            XWPFTableRow row = rows.get(i);
            analysisTableRow(table, row, i);
        }
    }

    /**
     * 解析表格行
     *
     * @param table table
     * @param row   row
     * @param index 索引
     */
    private void analysisTableRow(XWPFTable table, XWPFTableRow row, int index) {
        if (index == 0) {
            analysisTableHeader(row);
            return;
        }

        analysisTableBody(table, row);
    }

    /**
     * 解析表格行body
     *
     * @param table table
     * @param row   row
     */
    protected void analysisTableBody(XWPFTable table, XWPFTableRow row) {
        List<XWPFTableCell> tableCells = row.getTableCells();
        List<String> cellNames = new LinkedList<>();
        for (XWPFTableCell tableCell : tableCells) {
            cellNames.add(tableCell.getParagraphArray(0).getText());
        }

        String pattern = analysisPattern(cellNames);
        if (null == pattern) {
            return;
        }

        Collection rs = analysisRs(pattern);
        analysisTableBodyValues(table, rs, cellNames, analysisPrefix(pattern));
    }

    /**
     * 前缀
     *
     * @param pattern 表达式
     * @return 前缀
     */
    private String analysisPrefix(String pattern) {
        pattern = pattern.substring(START_STR.length(), pattern.length() - END_STR.length());
        List<String> strings = new ArrayList<>(Splitter.on('.').trimResults().omitEmptyStrings().splitToList(pattern));
        return Joiner.on('.').join(strings.subList(0, strings.size() - 1));
    }

    /**
     * 获取数据
     *
     * @param pattern 表达式
     * @return 数据
     */
    private Collection analysisRs(String pattern) {
        pattern = pattern.substring(START_STR.length(), pattern.length() - END_STR.length());
        List<String> strings = Splitter.on('.').trimResults().omitEmptyStrings().splitToList(pattern);
        int size = strings.size();
        BeanMap beanMap = BeanMap.of(body);
        for (int i = 0; i < size - 1; i++) {
            String string = strings.get(i);
            Object o = beanMap.get(string);
            if (i == size - 2) {
                try {
                    return (Collection) o;
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
            beanMap = BeanMap.of(o);
        }
        return Collections.emptyList();
    }

    /**
     * 分析数据
     *
     * @param table     table
     * @param rs        结果
     * @param cellNames 列
     * @param pre       前缀
     */
    private void analysisTableBodyValues(XWPFTable table, Collection rs, List<String> cellNames, String pre) {
        for (Object r : rs) {
            BeanMap beanMap = BeanMap.of(r);
            analysisTableBodyValues(table, beanMap, cellNames, pre);
        }
    }

    /**
     * 分析数据
     *
     * @param table     table
     * @param beanMap   结果
     * @param cellNames 列
     * @param pre       前缀
     */
    private void analysisTableBodyValues(XWPFTable table, BeanMap beanMap, List<String> cellNames, String pre) {
        XWPFTableRow row = table.createRow();
        for (int i = 0; i < cellNames.size(); i++) {
            String cellName = cellNames.get(i);
            StringBuffer sb = new StringBuffer();
            Matcher matcher = P.matcher(cellName);
            Object replaceValue = null;
            if (matcher.find()) {
                String group = matcher.group();
                replaceValue = replaceValue(group, beanMap, pre);
                if (!(replaceValue instanceof InputStream)) {
                    matcher.appendReplacement(sb, replaceValue.toString());
                }
            }

            if (!(replaceValue instanceof InputStream)) {
                row.getCell(i).setText(sb.toString());
                continue;
            }
            XWPFTableCell imageCell = row.getCell(i);
            List<XWPFParagraph> paragraphs = imageCell.getParagraphs();
            XWPFParagraph newPara = paragraphs.get(0);
            XWPFRun imageCellRunn = newPara.createRun();
            try {
                imageCellRunn.addPicture((InputStream) replaceValue, XWPFDocument.PICTURE_TYPE_PNG, "default.png", Units.toEMU(600), Units.toEMU(300));
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * pattern
     *
     * @param cellNames 单元格
     * @return 结果
     */
    private String analysisPattern(List<String> cellNames) {
        for (String cellName : cellNames) {
            Matcher matcher = P.matcher(cellName);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    /**
     * 解析表格行header
     *
     * @param row row
     */
    private void analysisTableHeader(XWPFTableRow row) {
        List<XWPFTableCell> tableCells = row.getTableCells();
        for (XWPFTableCell tableCell : tableCells) {
            analysisParagraphs(tableCell.getParagraphs());
        }
    }

    /**
     * 解析头尾
     *
     * @param xwpfDocument doc
     */
    private void analysisHeaderAndFoot(XWPFDocument xwpfDocument) {
        List<XWPFHeader> headerList = xwpfDocument.getHeaderList();
        for (XWPFHeader xwpfHeader : headerList) {
            for (int i = 0; i < xwpfHeader.getListParagraph().size(); i++) {
                parseThisParagraph(xwpfHeader.getListParagraph().get(i));
            }
        }
        List<XWPFFooter> footerList = xwpfDocument.getFooterList();
        for (XWPFFooter xwpfFooter : footerList) {
            for (int i = 0; i < xwpfFooter.getListParagraph().size(); i++) {
                parseThisParagraph(xwpfFooter.getListParagraph().get(i));
            }
        }
    }

    /**
     * 分析段落
     *
     * @param paragraphs 段落
     */
    private void analysisParagraphs(List<XWPFParagraph> paragraphs) {
        for (XWPFParagraph paragraph : paragraphs) {
            if (paragraph.getText().contains(START_STR)) {
                try {
                    parseThisParagraph(paragraph);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 分析文本
     *
     * @param paragraph 文本
     */
    private void parseThisParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        Map<String, List<XWPFRun>> tpl = new HashMap<>(runs.size());
        List<XWPFRun> runsTpl = new ArrayList<>(runs.size());

        for (XWPFRun run : runs) {
            runsTpl.add(run);
            String text = run.getText(0).trim();
            if (text.endsWith(END_STR)) {
                StringBuilder sb = new StringBuilder();
                for (XWPFRun xwpfRun : runsTpl) {
                    sb.append(xwpfRun.getText(0));
                }
                tpl.put(sb.toString(), runsTpl);
                runsTpl = new ArrayList<>();
            }
        }

        for (Map.Entry<String, List<XWPFRun>> entry : tpl.entrySet()) {
            String paragraphText = entry.getKey();
            List<XWPFRun> value = entry.getValue();
            Matcher matcher = P.matcher(paragraphText);
            StringBuffer sb = new StringBuffer();
            XWPFRun xwpfRun = null;
            Object replaceValue = null;
            while (matcher.find()) {
                String group = matcher.group();
                xwpfRun = analysisXwpfRun(group, value);
                replaceValue = replaceValue(group, body, null);

                if (!(replaceValue instanceof InputStream)) {
                    matcher.appendReplacement(sb, replaceValue.toString());
                }
            }

            for (XWPFRun run : value) {
                run.setText("", 0);
            }

            if (null == xwpfRun) {
                xwpfRun = value.get(0);
            }
            String text = sb.toString();
            if (replaceValue instanceof InputStream) {
                try {
                    xwpfRun.addPicture((InputStream) replaceValue, Document.PICTURE_TYPE_PNG, text, Units.toEMU(450), Units.toEMU(300));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            xwpfRun.setText(text);
        }

    }

    /**
     * 获取run
     *
     * @param group group
     * @param runs  runs
     * @return run
     */
    private XWPFRun analysisXwpfRun(String group, List<XWPFRun> runs) {
        String newGroup = group.substring(START_STR.length(), group.length() - END_STR.length());
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (newGroup.equals(text)) {
                return run;
            }
        }
        return null;
    }


    /**
     * 替换值
     *
     * @param group 表达式
     * @param body  环境
     * @param pre   前缀
     * @return 值
     */
    private Object replaceValue(String group, Map<String, Object> body, String pre) {
        String substring = group.substring(START_STR.length(), group.length() - END_STR.length());
        if (null != pre) {
            substring = substring.substring(pre.length() + 1);
        }
        ExpressionParser expressionParser = ServiceProvider.of(ExpressionParser.class).getNewExtension("el");
        expressionParser.setVariable(body);
        try {
            return expressionParser.parseExpression(substring).getDefaultValue("");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据条件改变值
     */
    private void changeValues(XWPFParagraph paragraph, XWPFRun currentRun, String currentText, List<Integer> runIndex) throws Exception {
        // 判断是不是迭代输出
        if (currentText.contains(FOREACH) && currentText.startsWith(START_STR)) {
            currentText = currentText.replace(FOREACH, EMPTY).replace(START_STR, EMPTY).replace(END_STR, EMPTY);
            String[] keys = currentText.replaceAll("\\s{1,}", " ").trim().split(" ");
            List list = (List) PoiPublicUtil.getParamsValue(keys[0], body);
            list.forEach(obj -> {
                if (obj instanceof ImageEntity) {
                    currentRun.setText("", 0);
                    ExcelMapParse.addAnImage((ImageEntity) obj, currentRun);
                } else {
                    PoiPublicUtil.setWordText(currentRun, obj.toString());
                }
            });
        } else {
            Object obj = PoiPublicUtil.getRealValue(currentText, body);
            // 如果是图片就设置为图片
            if (obj instanceof ImageEntity) {
                currentRun.setText("", 0);
                ExcelMapParse.addAnImage((ImageEntity) obj, currentRun);
            } else {
                currentText = obj.toString();
                PoiPublicUtil.setWordText(currentRun, currentText);
            }
        }

        for (int k = 0; k < runIndex.size(); k++) {
            paragraph.getRuns().get(runIndex.get(k)).setText("", 0);
        }
        runIndex.clear();
    }

    /**
     * 替换值
     *
     * @param text word中的文本
     * @return 替换的值
     */
    private String replace(String text) {
        return text;
    }

}
