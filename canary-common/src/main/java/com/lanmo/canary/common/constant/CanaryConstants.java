package com.lanmo.canary.common.constant;

/**
 * Created by bowen on 2017/2/24.
 */
public class CanaryConstants {

    /**
     * 协议头的魔术位 16进制的 AD =255 BF=191
     */
    public static final byte[] MAGIC_CODE_BYTE = new byte[]{(byte) 0xAD, (byte) 0xBF};

    /*---------消息类型开始-----------*/
    public static final int REQUEST_MSG = 1;

    public static final int RESPONSE_MSG = 2;

    /**
     * 客户端HANDLER 标示
     */
    public static final String CLIENT_HANDLER = "COM.LANMO.CANARY.CLIENT_HANDLER";

    /**
     * null返回结果
     */
    public static final String NULL_RESULT_CLASS = "null";

    //魔数的长度
    public static int magicLength = 2;

    //消息体总长度的标识的长度
    public static int contentAllLengthByteLength = 4;

    //消息header中header长度标识的长度
    public static int contentHeaderLengthByteLength = 4;

    //非消息体的长度 2 + 4
    public static int unBodySliceStartLength = 6;
    //默认编码解码协议
    public static String DEFAULT_CODEC_TYPE="protostuff";

}
