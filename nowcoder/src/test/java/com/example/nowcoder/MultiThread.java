package com.example.nowcoder;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try{
            for(int i=0;i<10;i++){
                Thread.sleep(1000);
                //类似于redis队列，这里只是把数据放到了同步队列里
                q.put(String.valueOf(i));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        //从同步队列中取出所有的事件
        try {
            while (true) {
                //如果队列里面一直有东西就把东西取出来，如果没东西就卡着
                System.out.println(Thread.currentThread().getName() +":"+ q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


class MyThread extends Thread{
    private int tid;
    public MyThread(int tid){
        this.tid=tid;
    }

    @Override
    public void run() {
       try{
           for(int i=0;i<10;i++){
               Thread.sleep(1000);
               System.out.println(String.format("T%d:%d",tid,i));
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}


public class MultiThread {
    public static void testThread(){
        for(int i=0;i<10;i++){
            new MyThread(i).start();
        }

        for(int i=0;i<10;i++){
            final int tid=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        for(int i=0;i<10;i++){
                            Thread.sleep(1000);
                            System.out.println(String.format("T2%d:%d",tid,i));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    private static Object obj=new Object();

    private static void testSynchronized1(){
        synchronized (obj){
            try{
                for(int i=0;i<10;i++){
                    Thread.sleep(1000);
                    System.out.println(String.format("T3%d",i));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void testSynchronized2(){
        synchronized (new Object()){
            try{
                for(int i=0;i<10;i++){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4%d",i));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized(){
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }




//处理完事件放到queue里面去然后开另一个线程去处理
    public static void testBlockingQueue(){
        BlockingQueue<String> q=new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q),"Consumer1").start();
        new Thread(new Consumer(q),"Consumer2").start();

    }

    private static int counter=0;
    private static AtomicInteger atomicInteger=new AtomicInteger(0);


    public static void sleep(int mills){
        try{

            //Thread.sleep(new Random().nextInt());
            Thread.sleep(mills);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void testWithAtomic(){
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for(int j=0;j<10;j++){
                        //加1
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    public static void testWithoutAtomic(){
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for(int j=0;j<10;j++){
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }

    public static void testAtomic(){
        testWithAtomic();
        testWithoutAtomic();
    }

    private static ThreadLocal<Integer> threadLocalUserIds=new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal(){
        for(int i=0;i<10;i++){
            //开10个线程，每个线程中设置一个数finalI
            final int finalI=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserIds.set(finalI);
                    sleep(1000);
                    System.out.println("ThreadLocal:"+threadLocalUserIds.get());

                }
            }).start();
        }

        for(int i=0;i<10;i++){
            //开10个线程，每个线程中设置一个数finalI
            final int finalI=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //userId是线程先关的，存储空间里只有一个变量
                    userId=finalI;
                    sleep(1000);
                    System.out.println("NonThreadLocal:"+userId);

                }
            }).start();
        }

    }

    public static void testExecutor(){
        //很多任务都交给executorService来执行
        //ExecutorService service= Executors.newSingleThreadExecutor();
        //2个线程同时进行
        ExecutorService service=Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                //输出完10个数之后不会关闭该任务器；
                for(int i=0;i<10;i++){
                    sleep(1000);
                    System.out.println("Execute1"+i);
                }
            }
        });

        service.submit(new Runnable() {
            @Override
             public void run() {
                //输出完10个数之后不会关闭该任务器；
                for(int i=0;i<10;i++){
                    sleep(1000);
                    System.out.println("Execute2"+i);
                }
            }
        });

        service.shutdown();
        while(!service.isTerminated()){
            sleep(1000);
            System.out.println("Wait for termination");
        }
    }

    public static void testFuture(){
        ExecutorService service=Executors.newSingleThreadExecutor();
        Future<Integer> future=service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);
                return 1;
                //throw new IllegalArgumentException("异常");
            }
        });

        service.shutdown();

        try{
            //System.out.println(future.get());
            System.out.println(future.get(100,TimeUnit.MILLISECONDS));
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] argv){
        //testThread();
       // testSynchronized();
        //testBlockingQueue();
        //testAtomic();
        //testThreadLocal();
        //testExecutor();
        testFuture();
    }
}
