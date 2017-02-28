package com.lanmo.canary.netty.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lanmo.canary.netty.Codec;

import java.io.IOException;

/**
 * Created by bowen on 2017/1/17.
 * jackson的解码和编码
 */
public class JacksonCodec implements Codec{

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    public <T> T decode(byte[] bytes, Class<T> clz) {
        try {
            return objectMapper.readValue(bytes, 0, bytes.length, clz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encode(Object data) {
        try {
//            logger.info(objectMapper.writeValueAsString(data));
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
