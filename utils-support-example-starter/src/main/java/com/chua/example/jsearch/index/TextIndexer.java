package com.chua.example.jsearch.index;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 文本索引
 *
 * 索引文件结构：
 * 1、一个词的索引由=分割的三部分组成，第一部分是词，第二部分是这个词在多少个文档中出现过（上限1000），第三部分是倒排表
 * 2、倒排表由多个倒排表项目组成，倒排表项目之间使用|分割
 * 3、倒排表项目的组成又分为三部分，用_分割，第一部分是文档ID，第二部分是词频，第三部分是词的位置
 * 4、词的位置用:分割
 *
 * 例如:
 * the=1000=1_2_3:13|1_3_15:31:35
 * 表示词the的索引：
 * 词：the
 * 有1000个文档包含the这个词
 * 包含这个词的第一篇文档的ID是1，the的词频是2，出现the的位置分别是3和13
 * 包含这个词的第二篇文档的ID是1+1=2，the的词频是3，出现the的位置分别是是15、31和35
 *
 * ID为1的文档的内容为：
 * License perpetual the right patent rig Implement interfaces Space, or
 * Licensor implemen the applic foregoing hereunder extent of interest in
 * Lead's lic registered.《Java EE 7 Specification》【1213/1】
 *
 * ID为2的文档的内容为：
 * Specification Lead hereby grants you a fully-paid, non-exclusive,
 * ferable, worldwide, limited license (without the right to sublicense),
 * under Specification Lead's intellectual property rights to view, download,
 * use and reproduce the Specification only for the internal evaluation.
 * 《Java EE 7 Specification》【1213/2】
 *
 * @author 杨尚川
 */
public class TextIndexer implements Indexer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextIndexer.class);
    private String indexText = "data/index_text.txt";
    private String index = "data/index.txt";
    private int indexLengthLimit = 1000;

    public TextIndexer(){}

    public TextIndexer(String index, String indexText, int indexLengthLimit){
        this.index = index;
        this.indexText = indexText;
        this.indexLengthLimit = indexLengthLimit;
    }

    public int getIndexLengthLimit() {
        return indexLengthLimit;
    }

    public void setIndexLengthLimit(int indexLengthLimit) {
        this.indexLengthLimit = indexLengthLimit;
    }

    public String getIndexText() {
        return indexText;
    }

    public void setIndexText(String indexText) {
        this.indexText = indexText;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public void indexDir(String dir){
        indexDir(dir, SegmentationAlgorithm.PureEnglish);
    }

    @Override
    public void indexDir(String dir, SegmentationAlgorithm segmentationAlgorithm){
        try {
            long start = System.currentTimeMillis();
            Path indexTextPath = Paths.get(this.indexText);
            if(!Files.exists(indexTextPath.getParent())){
                indexTextPath.getParent().toFile().mkdirs();
            }
            Path indexPath = Paths.get(this.index);
            if(!Files.exists(indexPath.getParent())){
                indexPath.getParent().toFile().mkdirs();
            }
            //词 ->  [{文档ID,位置}, {文档ID,位置}]
            Map<String, Posting> index = new HashMap<>();
            AtomicInteger lineCount = new AtomicInteger();
            BufferedWriter writer = Files.newBufferedWriter(indexTextPath, Charset.forName("utf-8"));
            //将所有文本合成一个文件，每一行分配一个行号
            getFileNames(dir).forEach(file -> {
                try {
                    List<String> lines = Files.readAllLines(Paths.get(file));
                    if(lines.size()<1){
                        new File(file).delete();
                        LOGGER.error("empty file: {}", file);
                        return;
                    }
                    AtomicInteger i = new AtomicInteger();
                    lines.forEach(line -> {
                        try {
                            writer.append(line).append("《").append(Paths.get(file).getFileName().toString().split("\\.")[0]).append("》【").append(lines.size()+"/"+i.incrementAndGet()).append("】\n");
                            lineCount.incrementAndGet();
                            List<Word> words = WordSegmenter.seg(line, segmentationAlgorithm);
                            for(int j=0; j< words.size(); j++){
                                Word word = words.get(j);
                                //准备倒排表
                                index.putIfAbsent(word.getText(), new Posting());
                                //倒排表长度限制
                                if(index.get(word.getText()).size()< indexLengthLimit) {
                                    //一篇文档对应倒排表中的一项
                                    index.get(word.getText()).putIfAbsent(lineCount.get());
                                    index.get(word.getText()).get(lineCount.get()).addPosition(j+1);
                                }
                            }
                        } catch (IOException e) {
                            LOGGER.error("write index file error", e);
                        }
                    });

                } catch (IOException e) {
                    LOGGER.error("read text file error", e);
                }
            });
            writer.close();
            List<String> indices =
            index
                .entrySet()
                .stream()
                .sorted((a,b)->b.getValue().size()-a.getValue().size())
                .map(entry -> {
                    StringBuilder docs = new StringBuilder();
                    AtomicInteger lastDocId = new AtomicInteger();
                    entry.getValue().getPostingItems().stream().sorted().forEach(postingItem -> {
                        //保存增量
                        docs.append(postingItem.getDocId()-lastDocId.get()).append("_").append(postingItem.getFrequency()).append("_").append(postingItem.positionsToStr()).append("|");
                        lastDocId.set(postingItem.getDocId());
                    });
                    if (docs.length() > 1) {
                        docs.setLength(docs.length() - 1);
                        return entry.getKey() + "=" + entry.getValue().size() + "=" + docs.toString();
                    }
                    return entry.getKey() + "=0";
                })
                .collect(Collectors.toList());
            Files.write(indexPath, indices, Charset.forName("utf-8"));
            LOGGER.info("index finished："+this.index+" , cost ："+(System.currentTimeMillis()-start)+" ms");
        }catch (Exception e){
            LOGGER.error("index process error", e);
        }
    }

    private Set<String> getFileNames(String path){
        Set<String> fileNames = new HashSet<>();
        if(Files.isDirectory(Paths.get(path))) {
            LOGGER.info("process dir：" + path);
        }else{
            LOGGER.info("process file：" + path);
            fileNames.add(path);
            return fileNames;
        }
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String fileName = file.toFile().getAbsolutePath();
                    if (!fileName.endsWith(".txt")) {
                        LOGGER.info("filter non-txt file：" + fileName);
                        return FileVisitResult.CONTINUE;
                    }
                    fileNames.add(fileName);
                    return FileVisitResult.CONTINUE;
                }

            });
        }catch (IOException e){
            LOGGER.error("get file names failed", e);
        }
        return fileNames;
    }

    public static void main(String[] args) {
        Indexer textIndexer = new TextIndexer();
        textIndexer.indexDir("src/test/resources/it");
        textIndexer.indexDir("src/test/resources/cn", SegmentationAlgorithm.MaxNgramScore);
    }
}
