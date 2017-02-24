package com.lanmo.canary.netty;

/**
 * Created by bowen on 2017/1/17.
 */
public interface Codec {

    /**
     * 解码---byte到某个具体的实体类
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T decode(byte[] bytes, Class<T> clazz);


    /**
     * 对象转bytes
     * @param data
     * @return
     */
    <T> byte[] encode(T data);

}
