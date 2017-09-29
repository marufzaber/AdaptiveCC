package com.mondego.indexbased;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;





public class MonitorListener extends Thread{
	
	private String file_to_read = "config.txt";
	private String file_to_write = "run_environment.txt";
	
	private Socket socket = null;
	String host = "localhost";
    int port = 3001;
    
    long sleeptime = 1000;
    
    
	
    public long getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(long sleeptime) {
		
		
		this.sleeptime = sleeptime;
		//System.out.println("Sleep time updated to "+this.sleeptime);

	}

	BufferedWriter bw = null;
	public void run(){	
		
		
		PrintWriter pwrite = null;  //opening channel to write sending message on socket
	    BufferedReader receiveRead = null;
    	try {
			socket = new Socket("127.0.0.1", port);
			pwrite = new PrintWriter(socket.getOutputStream(), true);  //opening channel to write sending message on socket
		    receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // opening channel to read from socket
		    SearchManager.setMode("ASCC");
	
		    while(!SearchManager.completed){
		    
		    	
		    	/*try {

		    		Thread.sleep(sleeptime);
					   
				   } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					   e.printStackTrace();
				 }*/
		    	
		    	
		    	System.out.println("before waiting");
		    	
		        synchronized (SearchManager.notifier) {
		    	     
		    	     SearchManager.notifier.wait(30000);
		        }
			    	
		        System.out.println("after waiting"+SearchManager.get_Query_count());
		    	

		         
		    /*  System.out.println("SENDING: "+ Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt())));	
		       	
			  */
		    	
		    	
		    	
		    
		    	pwrite.println(Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt()))+
	    	    		" Query Count : "+SearchManager.get_Query_count()); // sending to server 
			   
			   SearchManager.flushWaitAndRunTime(); 			   
			   pwrite.flush(); // flush the data 			   
			   String current_configuration = receiveRead.readLine();
			   if(current_configuration.equals("SKIP"))continue;			   
			    
			   System.out.println("received : "+current_configuration); 
			   
			   SearchManager.update_thread_count(process_monitor_info(current_configuration));
		        
		    }
		    
		    System.out.println("out");
		       
		    
		
		    pwrite.println("FINISHED");		
    	} 
    	
    	
    	catch (IOException e) {
			// TODO Auto-generated catch block
    		SearchManager.setMode("SCC");
    		System.out.println("Monitor was not found, setting to SCC mode");
 		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		try {
    			if((socket != null))
				   socket.close();
				pwrite.close();
				receiveRead.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
    	}
	}
	
	
	
	private int[] process_monitor_info(String config){
		int [] current_configuration = new int[5];
		
		if(config != null){
    		String [] split = config.split(" ");
    		
    		for(int i=0;i<5;i++){
  				current_configuration[i] = Integer.parseInt(split[i]);
			}
    	}
		
		return current_configuration;
	}
	
}
