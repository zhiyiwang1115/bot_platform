package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private ReentrantLock lock = new ReentrantLock();
    //here the thread would always be sleeping unless some signal would
    //awake it and it will processing all bot code in the pool
    private Condition condition = lock.newCondition();
    private static Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId,botCode,input));
            //awake the rest of threads
            condition.signalAll();
        }finally {
            lock.unlock();
        }

    }

    void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }

    @Override
    public void run(){
        while(true){
            lock.lock();
            if(bots.isEmpty()){
                try {
                    //block the thread until it signaled
                    //producer would signal it when tasks come
                    //also it will release lock by default
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot); //time-consuming operation, so unlock lock first
            }
        }
    }
}
