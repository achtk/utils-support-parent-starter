package com.chua.example.poi;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author CH
 */
public class SubjectWord implements InitializingAware, AutoCloseable {
    private static final String DEFAULT_SIGN = "#";

    private static final Map<String, StreamSource> CACHE = new ConcurrentHashMap<>();
    private boolean isResult;
    private final XWPFDocument xwpfDocument;
    private String signWord = DEFAULT_SIGN;
    private final FileInputStream stream;
    final LongAdder index = new LongAdder();
    final Set<Subject> sign = new LinkedHashSet<>();
    final Map<Integer, Subject> rSign = new ConcurrentHashMap<>();
    final Map<Integer, String> title = new HashMap<>();
    @Getter
    final Map<String, Set<Subject>> text = new LinkedHashMap<>();

    final AtomicInteger subjectIndex = new AtomicInteger(0);
    private static final String RESULT_SIGN = "参考答案及解析";

    public SubjectWord(File wordFile, String signWord) throws Exception {
        this.stream = new FileInputStream(wordFile);
        this.signWord = signWord;
        this.xwpfDocument = new XWPFDocument(stream);
    }

    public SubjectWord(File wordFile) throws Exception {
        this(wordFile, DEFAULT_SIGN);
    }

    public SubjectWord(String wordFile) throws Exception {
        this(new File(wordFile), DEFAULT_SIGN);
    }

    @Override
    public void afterPropertiesSet() {
        List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            boolean isBold = isTextBold(paragraph);
            if (isBold) {
                registerTitle(paragraph);
                continue;
            }

            int i = findDot(paragraph);
            if (isPass(i)) {
                continue;
            }


            convertIndex(i, paragraph);
            if (!isResult) {
                registerSubject(paragraph);
                continue;
            }
            registerSubjectResult(paragraph);
            System.out.println();
        }
    }

    /**
     * 結果
     *
     * @param paragraph 段落
     */
    private void registerSubjectResult(XWPFParagraph paragraph) {
        String text1 = parseParagraph(paragraph);
        if (text1.startsWith(subjectIndex.get() + ".") || text1.startsWith(subjectIndex.get() + "．")) {
            Subject subject = new Subject();
            subject.setSeq(subjectIndex.get());
            subject.setTitle(text1.substring((subjectIndex.get() + "").length() + 1));
            text.computeIfAbsent(RESULT_SIGN, it -> new LinkedHashSet<>())
                    .add(subject);
            rSign.put(subjectIndex.get(), subject);
            return;
        }
        Subject subject = rSign.get(subjectIndex.get());
        if (StringUtils.isNotEmpty(text1)) {
            subject.getResult().add(text1);
        }
        checkImage(paragraph, subject);
    }

    /**
     * 注冊題目
     *
     * @param paragraph 段落
     */
    private void registerSubject(XWPFParagraph paragraph) {
        String text1 = parseParagraph(paragraph);
        if (text1.startsWith(subjectIndex.get() + ".") || text1.startsWith(subjectIndex.get() + "．")) {
            Subject subject = new Subject();
            subject.setSeq(subjectIndex.get());
            subject.setTitle(text1.substring((subjectIndex.get() + "").length() + 1));
            text.computeIfAbsent(title.get(index.intValue()), it -> new LinkedHashSet<>())
                    .add(subject);
            sign.add(subject);
            return;
        }
        Subject subject = CollectionUtils.find(sign, subjectIndex.get() - 1);
        if (StringUtils.isNotEmpty(text1)) {
            subject.getResult().add(text1);
        }
        checkImage(paragraph, subject);
    }

    /**
     * 检查图片
     *
     * @param paragraph 段落
     * @param subject   题目
     */
    private void checkImage(XWPFParagraph paragraph, Subject subject) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun run : runs) {
            List<XWPFPicture> embeddedPictures = run.getEmbeddedPictures();
            checkPicture(embeddedPictures, subject);
        }
    }

    /**
     * 检查图片
     *
     * @param embeddedPictures 嵌入图片
     * @param subject          题目
     */
    private void checkPicture(List<XWPFPicture> embeddedPictures, Subject subject) {
        for (XWPFPicture embeddedPicture : embeddedPictures) {
            subject.getResult().add(StringUtils.str(embeddedPicture.getPictureData().getData(), StandardCharsets.UTF_8));
        }
    }

    /**
     * 段落解析
     *
     * @param xwpfParagraph 段落
     * @throws DocumentException 计息异常
     */
    public String parseParagraph(XWPFParagraph xwpfParagraph) {
        CTP ctp = xwpfParagraph.getCTP();
        String xmlText = ctp.xmlText();
        StringBuilder sb = new StringBuilder();
        //得到根节点的值
        SAXReader saxReader = new SAXReader();
        //将String类型的字符串转换成XML文本对象
        Document doc = null;
        try {
            doc = saxReader.read(new ByteArrayInputStream(xmlText.getBytes()));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = doc.getRootElement();
        List<Element> elements = root.elements();
        for (Element element : elements) {
            String asXML = element.asXML();
            if (asXML.startsWith("<w:r")) {
                if (asXML.contains("<w:em w:val=\"dot\"/>")) {
                    sb.append("<ruby class=\"underdot\">").append(element.getStringValue()).append("<rt></rt></ruby>");
                } else {
                    sb.append(element.getStringValue());
                }
                continue;
            }
            if (asXML.startsWith("<m:oMath")) {
                //xml转 mathml
                String mathml = convertOMML2MML(asXML);
                //mathml转latx
                String latex = convertMML2Latex(mathml);
                sb.append(doAnalysis(latex));
            }
        }
        return sb.toString();
    }

    /**
     * 解析文本
     *
     * @param latex latex表达式
     * @return 文本
     */
    private String doAnalysis(String latex) {
        return signWord + latex + signWord;
    }

    /**
     * 处理序号转化
     *
     * @param i         字符串中题目需序号
     * @param paragraph 段落
     * @return 实际题目序号
     */
    private void convertIndex(int i, XWPFParagraph paragraph) {
        String text1 = paragraph.getText();
        if (RESULT_SIGN.equals(text1)) {
            this.subjectIndex.incrementAndGet();
            this.isResult = true;
            return;
        }
        if (i != -1) {
            int i1 = NumberUtils.toInt(text1.substring(0, i), 0);
            if (i1 > 0) {
                subjectIndex.set(i1);
                return;
            }
        }
    }


    /**
     * Description: xsl转换器</p>
     *
     * @param s           公式xml字符串
     * @param xslPath     转换器路径
     * @param uriResolver xls依赖文件
     * @return
     */
    public String xslConvert(String s, String xslPath, URIResolver uriResolver) {
        TransformerFactory tFac = TransformerFactory.newInstance();
        if (uriResolver != null) {
            tFac.setURIResolver(uriResolver);
        }
        StreamSource xslSource = new StreamSource(SubjectWord.class.getResourceAsStream(xslPath));
        try {
            StringWriter writer = new StringWriter();
            Transformer t = tFac.newTransformer(xslSource);
            Source source = new StreamSource(new StringReader(s));
            Result result = new StreamResult(writer);
            t.transform(source, result);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Description: 将mathml转为latx </p>
     *
     * @param mml mathml字符串
     * @return
     */
    public String convertMML2Latex(String mml) {
        mml = mml.substring(mml.indexOf("?>") + 2, mml.length()); //去掉xml的头节点
        //设置xls依赖文件的路径
        URIResolver r = (href, base) -> {
            InputStream inputStream = WordExample.class.getResourceAsStream("/conventer/TexTablet/" + href);
            return new StreamSource(inputStream);
        };
        String latex = xslConvert(mml, "/conventer/TexTablet/mmltex.xsl", r);
        if (latex != null && latex.length() > 1) {
            latex = latex.substring(1, latex.length() - 1);
        }
        return latex;
    }

    /**
     * <p>Description: office xml转为mathml </p>
     *
     * @param xml 公式xml
     * @return
     */
    public String convertOMML2MML(String xml) {
        // 进行转换的过程中需要借助这个文件,一般来说本机安装office就会有这个文件,找到就可以
        return xslConvert(xml, "/conventer/OMML2MML.XSL", null);
    }

    private Node getChildNode(Node node, String nodeName) {
        if (!node.hasChildNodes()) {
            return null;
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (nodeName.equals(childNode.getNodeName())) {
                return childNode;
            }
            childNode = getChildNode(childNode, nodeName);
            if (childNode != null) {
                return childNode;
            }
        }
        return null;
    }

    private boolean isPass(int i) {
        return i == -1 && subjectIndex.get() == 0;
    }

    private int findDot(XWPFParagraph paragraph) {
        String text1 = paragraph.getText();
        int i = text1.indexOf(".");
        if (i == -1) {
            i = text1.indexOf("．");
        }
        return i;
    }

    private void registerTitle(XWPFParagraph paragraph) {
        index.increment();
        title.put(index.intValue(), paragraph.getText());
    }

    private boolean isTextBold(XWPFParagraph paragraph) {
        if (RESULT_SIGN.equals(paragraph.getText())) {
            this.isResult = true;
            return true;
        }
        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun run : runs) {
            if (run.isBold()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void close() throws Exception {
        IOUtils.closeQuietly(stream);
    }

    public void doAnalysis() {
        afterPropertiesSet();
    }

    @Data
    static class Subject {
        /**
         * 序号
         */
        private Integer seq;

        private String title;
        private List<String> result = new LinkedList<>();
    }
}
