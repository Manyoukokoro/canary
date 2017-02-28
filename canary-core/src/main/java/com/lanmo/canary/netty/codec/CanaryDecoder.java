package com.lanmo.canary.netty.codec;

import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.common.utils.ReflectionUtils;
import com.lanmo.canary.netty.Codec;
import com.lanmo.canary.netty.impl.ProtostuffCodec;
import com.lanmo.canary.netty.message.BaseMsg;
import com.lanmo.canary.netty.message.MsgHeader;
import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.netty.message.ResponseMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

/**
 * Created by bowen on 2017/2/24.
 */
public class CanaryDecoder extends ByteToMessageDecoder {

    private Logger logger= LoggerFactory.getLogger(CanaryDecoder.class);
    //默认解码
    private Codec codec=new ProtostuffCodec();

    //魔数的长度
    private int magicLength = CanaryConstants.magicLength;

    //消息体总长度的标识的长度
    private int contentAllLengthByteLength = CanaryConstants.contentAllLengthByteLength;

    //消息header中header长度标识的长度
    private int contentHeaderLengthByteLength = CanaryConstants.contentHeaderLengthByteLength;

    //非消息体的长度 2 + 4
    private int unBodySliceStartLength = CanaryConstants.unBodySliceStartLength;



    public CanaryDecoder(){

    }

    public CanaryDecoder(Codec codec){
        this.codec=codec;
    }

    public CanaryDecoder(int magicLength,int contentAllLengthByteLength,int contentHeaderLengthByteLength,int unBodySliceStartLength){
        this.magicLength=magicLength;
        this.contentAllLengthByteLength=contentAllLengthByteLength;
        this.contentHeaderLengthByteLength=contentHeaderLengthByteLength;
        this.unBodySliceStartLength=unBodySliceStartLength;
    }


    public CanaryDecoder(int magicLength,int contentAllLengthByteLength,int contentHeaderLengthByteLength,int unBodySliceStartLength,Codec codec){
        this.magicLength=magicLength;
        this.contentAllLengthByteLength=contentAllLengthByteLength;
        this.contentHeaderLengthByteLength=contentHeaderLengthByteLength;
        this.unBodySliceStartLength=unBodySliceStartLength;
        this.codec=codec;
    }


    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.debug("receive msg is {}",in.toString(CharsetUtil.UTF_8));
        if(!checkMsgLength(in)){
            return;
        }
        //获取整个body的长度
        int contentAllLength = (int) in.getUnsignedInt(in.readerIndex() + magicLength);
        //减去 contentLength 所占用的长度 就是body的真实长度
        int contentLength = contentAllLength - contentAllLengthByteLength;
        // frameLength 整个消息应该的长度
        int frameLength = contentLength + contentAllLengthByteLength + magicLength;

        logger.info("contentAllLength {},contentLength {},frameLength {}",contentAllLength,contentLength,frameLength);
        if (frameLength > in.readableBytes()) {
            //数据还不够,返回
            return;
        }

        if (frameLength < unBodySliceStartLength) {
            in.skipBytes(frameLength);
            throw new Exception("发生了突发异常,消息可能已经被其它线程使用");
        }

        BaseMsg msg = decodeMsg(in, contentLength);
        if (msg != null) {
            in.readerIndex(frameLength);
            logger.debug("get message success");
            out.add(msg);
        }
    }

    //检查消息体长度是否合法
    private boolean checkMsgLength(ByteBuf in){
        //获得可读数据的长度
        if(in.readableBytes()<unBodySliceStartLength){
            logger.error("this msg length less than 6 ,msg length is {}",in.readableBytes());
            return false;
        }
        //返回指定索引位置的无符号byte值（返回类型是short）
        //This method does not modify {@code readerIndex}
        short magic=in.getUnsignedByte(in.readerIndex());
        logger.info("magic is {}",magic);
        return true;
    }

    private BaseMsg decodeMsg(ByteBuf in,int contentLength){
        ByteBuf realBuf=in.slice(unBodySliceStartLength,contentLength);
        //得出header的长度
        int headerLength=realBuf.readInt();
        logger.info("header length is {}",headerLength);
        //拿出header测信息
        ByteBuf headerBuf=realBuf.slice(realBuf.readerIndex(),headerLength);
        byte[] header = new byte[headerLength];
        headerBuf.readBytes(header);
        MsgHeader msgHeader = decodeMessageHeader(header);
        BaseMsg baseMsg = ensureMsg(realBuf, msgHeader, headerLength + contentHeaderLengthByteLength);
        //计算器-1，-1后如果计算器和初始值1相同，则会释放内存
//        realBuf.release();
        return baseMsg;

    }

    /**
     * @param in
     * @param msgHeader
     * @param start     headerLength标识占4位 + header内容，即headerLength
     * @return
     */
    private BaseMsg ensureMsg(ByteBuf in, MsgHeader msgHeader, int start) {

        if (msgHeader.getMsgType() == CanaryConstants.REQUEST_MSG) {
            RequestMsg msg = new RequestMsg();
            msg.setSendTime(System.currentTimeMillis());
            msg.setMsgBody(in.slice(start, msgHeader.getLength()));
            msg.setMsgHeader(msgHeader);
            int bodyLength = msgHeader.getLength();
            if (bodyLength > 0) {
                byte[] body = new byte[bodyLength];
                msg.getMsgBody().readBytes(body);
                Object consumerBean = codec.decode(body, ReflectionUtils.forName(msgHeader.getClz()));
                msg.setConsumerBean(consumerBean);
            }
            return msg;
        } else if (msgHeader.getMsgType() == CanaryConstants.RESPONSE_MSG) {
            ResponseMsg msg = new ResponseMsg();
            msg.setMsgBody(in.slice(start, msgHeader.getLength()));
            msg.setMsgHeader(msgHeader);
            int bodyLength = msgHeader.getLength();
            if (bodyLength > 0) {
                byte[] body = new byte[bodyLength];
                msg.getMsgBody().readBytes(body);
                String targetClz = msgHeader.getClz();
                if(!CanaryConstants.NULL_RESULT_CLASS.equals(msg.getMsgHeader().getClz())) {
                    Object response = codec.decode(body, ReflectionUtils.forName(targetClz));
                    msg.setResponse(response);
                }
            }
            return msg;
        }

        throw new CanaryException("unknown msgType");
    }

    //codec
    private MsgHeader decodeMessageHeader(byte[] header) {
        return codec.decode(header, MsgHeader.class);
    }



}
