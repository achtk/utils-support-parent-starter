package com.chua.example.poi;

/**
 * @author CH
 */
public class WordExample {

    private static final String SIGN = "#";

    public static void main(String[] args) throws Exception {
        SubjectWord subjectWord = new SubjectWord("Z://2023年7月13日初中数学作业.docx");
        subjectWord.doAnalysis();
        System.out.println();
    }


}
