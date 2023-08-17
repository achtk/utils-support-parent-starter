package com.chua.common.support.crypto;

import java.nio.charset.StandardCharsets;

/**
 * 空
 *
 * @author CH
 */
public class NoneCodec implements Codec {
    @Override
    public Codec accessKey(String accessKey) {
        return this;
    }

    @Override
    public Codec secretKey(String secretKey) {
        return this;
    }

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return content;
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return content;
    }

    @Override
    public String decodeHex(String content) {
        return content;
    }

    @Override
    public byte[] decode(String content) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decode(String content, byte[] key) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decode(byte[] content, String key) {
        return content;
    }

    @Override
    public byte[] decode(byte[] content) {
        return content;
    }

    @Override
    public byte[] decode(String content, String key) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String decodeHex(String content, String key) {
        return content;
    }

    @Override
    public byte[] encode(String bytes) {
        return bytes.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String encodeHex(String content) {
        return content;
    }

    @Override
    public byte[] encode(String content, byte[] key) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encode(byte[] content, String key) {
        return content;
    }

    @Override
    public byte[] encode(String content, String key) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encode(byte[] content) {
        return content;
    }

    @Override
    public String encodeHex(String content, String key) {
        return content;
    }
}
