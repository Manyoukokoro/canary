package com.lanmo.canary.netty.codec;

import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.netty.Codec;
import com.lanmo.canary.netty.impl.ProtostuffCodec;
import com.lanmo.canary.netty.message.BaseMsg;
import com.lanmo.canary.netty.message.MsgHeader;
import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.netty.message.ResponseMsg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by bowen on 2017/2/24.
 */
public class CanaryEncoder extends MessageToByteEncoder {

    private Codec codec=new ProtostuffCodec();


    public CanaryEncoder(){
    }

    public CanaryEncoder(Codec codec){
           this.codec=codec;
    }


    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ByteBuf byteBuf=null;
        if(out==null){
            out=ctx.alloc().buffer();
        }
        try {
            if(msg instanceof BaseMsg){
                byteBuf=encodeMsg(ctx,(BaseMsg) msg,out);
            }
        }finally {
            if(byteBuf!=null){
                //release()方法总是把引用计数器设为0，因此可以即刻让所有活跃的引用失效
                byteBuf.release();
            }
        }
    }

    private ByteBuf encodeMsg(ChannelHandlerContext ctx, BaseMsg baseMsg, ByteBuf out) throws Exception {
        ByteBuf byteBuf = readAll(ctx, baseMsg);
        //总长度 = 4+传输信息的可读长度(4 是预留给存储 totalLength信息的位置)
        int totalLength = 4 + byteBuf.readableBytes();
        checkCapacity(out, totalLength);
        //写入魔数信息 writerIndex 向前移动2位
        out.writeBytes(CanaryConstants.MAGIC_CODE_BYTE);
        //写入总长度信息 writerIndex 向前移动4位 所以非body长度=2+4 (解码的时候需要用到)
        out.writeInt(totalLength);
        //写入body的信息
        out.writeBytes(byteBuf, byteBuf.readerIndex(), byteBuf.readableBytes());

        return byteBuf;
    }

    //检查可容纳的字节数
    private void checkCapacity(ByteBuf out, int totalLength) {
        if (out.capacity() < totalLength + 2) {
            //扩展容量
            out.capacity(totalLength + 2);
        }
    }

    private ByteBuf readAll(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception{
        //分配buffer 线程安全的
        ByteBuf byteBuf=ctx.alloc().buffer();
        //获得header
        MsgHeader header=baseMsg.getMsgHeader();
        //获取body实例
        byte[] body=null;
        int bodyLength=0;
        if(baseMsg instanceof RequestMsg){
            // 请求
            body=codec.encode(((RequestMsg) baseMsg).getConsumerBean());
            bodyLength=body.length;
        }else if(baseMsg instanceof ResponseMsg){
            //响应
            body=codec.encode(((ResponseMsg) baseMsg).getResponse());
            bodyLength=body.length;
        }else if(baseMsg.getMsgBody()!=null){
            //bodyLength 设置成 baseMsg 的可读长度
            bodyLength=baseMsg.getMsgBody().readableBytes();
        }
        //在header中标示出 body的长度
        header.setLength(bodyLength);
        //对header进行编码
        byte[] headerByte = codec.encode(header);
        //writeInt 在当前writerIndex位置写入int值，然后writerIndex加4
        byteBuf.writeInt(headerByte.length);
        //从当前writerIndex位置开始，从指定的源ByteBuf或者byte[]传送数据到当前ByteBuf。
        //如果输入参数中提供了srcIndex和length，那么从srcIndex开始读出length长的字节。
        //当前ByteBuf的writerIndex值根据已写入的字节数增长
        byteBuf.writeBytes(headerByte);

        //byteBuf 中加入body的信息
        if(baseMsg instanceof RequestMsg|| baseMsg instanceof ResponseMsg){
            byteBuf.writeBytes(body);
        }else if (baseMsg.getMsgBody() != null) {
            byteBuf.writeBytes(baseMsg.getMsgBody(), baseMsg.getMsgBody().readerIndex(), baseMsg.getMsgBody().readableBytes());
        }

        return byteBuf;
    }
}
