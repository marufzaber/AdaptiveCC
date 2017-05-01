package com.mondego.models;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mondego.indexbased.WordFrequencyStore;
import com.mondego.indexbased.SearchManager;


public class ThreadedChannel<E> {

    private ExecutorService executor;
    private Class<Runnable> workerType;
    private Semaphore semaphore;
    private static final Logger logger = LogManager
            .getLogger(ThreadedChannel.class);

    public ThreadedChannel(int nThreads, Class clazz) {
        this.executor = Executors.newFixedThreadPool(nThreads);
        this.workerType = clazz;
        this.semaphore = new Semaphore(nThreads + 6);
    }

    public void send(E e) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
               
                long startTime = System.nanoTime();

                final Runnable o = this.workerType.getDeclaredConstructor(e.getClass())
                .newInstance(e);
        try {
            
            logger.debug(SearchManager.NODE_PREFIX + " trying to get semaphore lock "
                          + " in " + startTime);
            
            semaphore.acquire();
            long estimatedTime = System.nanoTime() - startTime;
            
            logger.debug(SearchManager.NODE_PREFIX + " got semaphore lock "
                         + " in " + estimatedTime/1000);

            
        } catch (InterruptedException ex) {
            logger.error("Caught interrupted exception " + ex);
        }

        try {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        o.run();

                        //System.out.println("Available memory in JVM @ "+ System.currentTimeMillis()+" : "+ Runtime.getRuntime().freeMemory());
                        //System.out.println("Available processors @ "+ System.currentTimeMillis()+" : "+ Runtime.getRuntime().availableProcessors());
                        //System.out.println("Total Memory in Use @ "+ System.currentTimeMillis()+" : "+ Runtime.getRuntime().totalMemory());

                    } finally {
                        semaphore.release();
                    }
                }
            });
        } catch (RejectedExecutionException ex) {
            semaphore.release();
        }
    }

    public void shutdown() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("inside catch, shutdown");
        }
    }
}
