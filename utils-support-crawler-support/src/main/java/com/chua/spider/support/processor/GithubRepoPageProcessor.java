package com.chua.spider.support.processor;

/**
 * @author CH
 */
public class GithubRepoPageProcessor  extendscom.chua.common.support.lang.spider.processor.example.GithubRepoPageProcessor implements PageProcessor{
@Override
public void process(Page page){
        super.process(new MagicOrginPage(page));
        }
        }
