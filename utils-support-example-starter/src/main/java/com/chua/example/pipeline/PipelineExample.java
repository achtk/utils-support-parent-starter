package com.chua.example.pipeline;

import com.chua.common.support.lang.pipeline.PipelineBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class PipelineExample {


    public static void main(String[] args) {
        Date execute = PipelineBuilder.<Date>newBuilder()
                .next(d -> {
                    log.info(d.toString());
                    return new Date();
                }).next(d -> {
                    log.info(d.toString());
                    return new Date();
                }).next(d -> {
                    log.info(d.toString());
                    return new Date();
                }).next(d -> {
                    log.info(d.toString());
                    return new Date();
                }).execute(new Date());

    }
}
