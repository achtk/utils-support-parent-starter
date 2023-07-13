package com.chua.example.poi;

/**
 * @author CH
 */
public class WordExample {

    private static final String SIGN = "#";

    public static void main(String[] args) throws Exception {
        SubjectWord subjectWord = new SubjectWord("Z://导入模板.docx");
        subjectWord.doAnalysis();
        System.out.println();
    }


}
