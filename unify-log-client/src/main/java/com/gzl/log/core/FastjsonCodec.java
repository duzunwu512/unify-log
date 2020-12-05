package com.gzl.log.core;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.codec.RedisCodec;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FastjsonCodec
 */
public class FastjsonCodec implements RedisCodec<String, Object> {

    private Charset charset = StandardCharsets.UTF_8;


    public FastjsonCodec() {}


    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        return charset.decode(byteBuffer).toString();
    }

    @SneakyThrows
    @Override
    public Object decodeValue(ByteBuffer byteBuffer) {
        return JSON.parseObject(new ByteArrayInputStream(byteBuffer.array()), Object.class);
    }

    @Override
    public ByteBuffer encodeKey(String s) {
        return charset.encode(s);
    }

    @Override
    public ByteBuffer encodeValue(Object o) {

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bytes);
            os.writeObject(o);
            return ByteBuffer.wrap(bytes.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}