package com.lanmo.canary.server.handler;

import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.netty.message.ResponseMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by bowen on 2017/1/20.
 *
 * 表明可被多个线程 安全的共享使用
 */
@ChannelHandler.Sharable
public class ServerHandler extends AbstractServerHandler  {

    private final static Logger logger= LoggerFactory.getLogger(ServerHandler.class);

    List<RequestHandle> requestHandleList=new ArrayList<RequestHandle>();

    public ServerHandler(List<RequestHandle> requestHandleList) {
        this.requestHandleList = requestHandleList;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Channel channel=ctx.channel();
        if(msg instanceof RequestMsg){
            RequestMsg requestMsg= (RequestMsg) msg;
            //获取消费者信息
            Object consumerBean=requestMsg.getConsumerBean();
            logger.debug("receive consumer info {}",consumerBean.toString());
            ResponseMsg responseMsg=new ResponseMsg();
            responseMsg.setReceiveTime(System.currentTimeMillis());
            Object result=null;

            boolean requestHandled=false;
            //处理消息 查找那个Handle 能处理这个消息
                for (RequestHandle requestHandle : requestHandleList) {
                    if (requestHandle.canHandle(consumerBean)) {
                        requestHandled = true;
                        result = requestHandle.handleReq(requestMsg.getMsgHeader(), channel, consumerBean);
                        break;
                    }
                }

                if (!requestHandled) {
                    logger.warn("消息类型:{} 未发现可以处理的requestHandle.", consumerBean == null ? "null" :
                            consumerBean.getClass().getCanonicalName());
                }
            //发送返回结果
                sendResponse(requestMsg.getMsgId(), channel, result);
        }else if(msg instanceof ResponseMsg){
             //receive the callback ResponseMessage 回调信息--暂时 还未处理
            ResponseMsg responseMsg = (ResponseMsg) msg;
            //find the transport
        }
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        try {
            super.channelActive(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ChannelFuture f = ctx.writeAndFlush(new LTime());
//        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void setRequestHandleList(RequestHandle... requestHandleList) {
        this.requestHandleList = Arrays.asList(requestHandleList);
    }

}
