package com.lanmo.canary.client.future;

import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.netty.message.BaseMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;

/**
 * Created by bowen on 2017/1/22.
 * 异步执行方法
 */
@Data
public class MsgFuture<V> implements Future<V> {


    private Logger logger = LoggerFactory.getLogger(MsgFuture.class);

    private int timeout;

    private volatile Object result;

    private long sendTime;

    private transient ReentrantLock reentrantLock = new ReentrantLock();

    private transient Condition condition = reentrantLock.newCondition();


    public MsgFuture(BaseMsg request, int timeout){
        this.timeout=timeout;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return this.result != null;
    }

    public V get() throws InterruptedException {
        logger.debug("sendTime:{}",sendTime);
        return get(timeout, TimeUnit.MILLISECONDS);
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException {
        Long now=System.currentTimeMillis();
        //转为毫秒值
        timeout=unit.toMillis(timeout);
        //已经耗费的使用时间
        Long past=now-sendTime;
        //剩余可执行时间
        Long left=timeout-past;

        if(left<=0){
            if(isDone()){
                return (V) this.result;
            }else {
                throw new CanaryException("超时了");
            }
        }else {
           //执行操作
            reentrantLock.lock();
            try {
                logger.debug("await "+left+" ms");
                condition.await(left,TimeUnit.MILLISECONDS);
            }finally {
                reentrantLock.unlock();
            }
            if(isDone()){
                return (V)this.result;
            }
            //还可以等待
            try {
                //等待后重新获取
                return get(left, TimeUnit.MILLISECONDS);
            }catch (InterruptedException e){
                throw e;
            }
        }
    }
}
