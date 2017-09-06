package com.mondego.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;

import com.mondego.indexbased.SearchManager;
import com.mondego.interfaces.ThreadedChannelInterface;


public class ThreadedChannel<E> implements ThreadedChannelInterface<E>{

    //private ExecutorService executor;
    private ThreadPoolExecutor executor;
    
    private Class<Runnable> workerType;
    private Semaphore semaphore;
    private int thread_count;
    
    
    private static final Logger logger = LogManager
            .getLogger(ThreadedChannel.class);

    public ThreadedChannel(int nThreads, Class clazz) {
        
    	thread_count = nThreads;		
    	
    	//this.executor = Executors.newFixedThreadPool(nThreads);
    	
    	this.workerType = clazz;
   	
    	executor = new ThreadPoolExecutor(1, // core size
    		    100, // max size
    		    10*60, // idle timeout
    		    TimeUnit.SECONDS,
    		    new ArrayBlockingQueue<Runnable>(20));
        
        this.semaphore = new Semaphore(thread_count + 2);
        
        
        //System.out.println("for "+clazz.getName()+" the thread number is "+executor.getCorePoolSize());
        
    }
    
    public void update_thread(int nThread){
    	try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for(int i=0;i<(nThread - thread_count);i++)
    		semaphore.release();
    	
    	thread_count = nThread;
    	
    	executor.setCorePoolSize(thread_count);//newLimit is new size of the pool 
    	
 	  	
    }

    public void send(E e) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
    	
    	  if(workerType.getName().contains("QueryLineProcessor") ){
    		  SearchManager.inc_query_count();
    		  
    		  
    		  if(SearchManager.get_Query_count() % 1000 == 0){
    			  SearchManager.query_record();
    		  }
    		  
    	  }
    		  

                long startTime = System.nanoTime();

                final Runnable o = this.workerType.getDeclaredConstructor(e.getClass())
                .newInstance(e);
        try {
            
            logger.debug(SearchManager.NODE_PREFIX + " trying to get semaphore lock "
                          + " in " + startTime + " semaphore Available : "+ semaphore.availablePermits());
            
            semaphore.acquire();
            long estimatedTime = System.nanoTime() - startTime;          
            SearchManager.updateWaitTime(estimatedTime/1000, workerType.getName());
            
            
            
            logger.debug(SearchManager.NODE_PREFIX + " got semaphore lock "
                         + " in " + estimatedTime/1000+"   "+workerType.getName());           
            
        } catch (InterruptedException ex) {
            logger.error("Caught interrupted exception " + ex);
        }

        try {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        o.run();
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
