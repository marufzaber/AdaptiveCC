package com.mondego.indexbased;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class MonitorListener extends Thread{
	
	private String file_to_read = "config.txt";
	private String file_to_write = "run_environment.txt";
	
	private Socket socket = null;
	String host = "localhost";
    int port = 40005;
    
    
	
    BufferedWriter bw = null;
	public void run(){	
		
		
		PrintWriter pwrite = null;  //opening channel to write sending message on socket
	    BufferedReader receiveRead = null;
    	try {
			socket = new Socket("127.0.0.1", port);
			pwrite = new PrintWriter(socket.getOutputStream(), true);  //opening channel to write sending message on socket
		    receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // opening channel to read from socket
            
		    
		    
		   
		
		    while(!SearchManager.completed){
		    	//System.out.println("entered here.......");
	    		SearchManager.setMode("ASCC");

		    	try {
					   Thread.sleep(100);
				   } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					   e.printStackTrace();
				 }
		    	/*****     
		      System.out.println("SENDING: "+ Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
	    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt())));	
		       	******/
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
			  
			   
			   //process_monitor_info(current_configuration);
			
			   
			   /*  if((receiveMessage = receiveRead.readLine()) != null) //receive from server 
			   { 
			    System.out.println("received : "+receiveMessage); // displaying at DOS prompt 
			   }*/
			  
			
			
		  /* // SearchManager.update_thread_count(current_configuration);
		    
		    
		    long elapsed = System.nanoTime() - SearchManager.getStartTime();           
            int elapsed_time = (int)Math.floor(elapsed/10000000);          
            if(elapsed_time % 10 == 0){   
            	OutputStream os = socket.getOutputStream();
    	        OutputStreamWriter osw = new OutputStreamWriter(os);
    	        bw = new BufferedWriter(osw);
    	        
            	send_data_to_monitor();
            	
            	//SearchManager.flushWaitAndRunTime();  
            	//listenMonitor();
            }*/
		}
		
		    pwrite.println("FINISHED");
		
    	} 
    	
    	
    	catch (IOException e) {
			// TODO Auto-generated catch block
			
    		System.out.println("tocu tocusssss ");
    		SearchManager.setMode("SCC");
    		
    		e.printStackTrace();
		}
    	
    	
    	
    	finally{
    		try {
    			if((socket != null))
				   socket.close();
				pwrite.close();
				receiveRead.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	/*private void listenMonitor(){
		
        try{
        	
        	
        	InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            
            
            String config = br.readLine();
        	System.out.println("reading "+ config);
   
        	if(config != null){
        		String [] split = config.split(" ");
        		for(int i=0;i<5;i++){
					current_configuration[0] = Integer.parseInt(split[i]);
				}
        	}
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
        
	}*/
			
	/*private boolean readFromFile(){
		boolean update = false;
		try (BufferedReader br = new BufferedReader(new FileReader(file_to_read))) {
			String sCurrentLine;
			if ((sCurrentLine = br.readLine()) != null) {
				String split[] = sCurrentLine.split(" ");
				if(split[split.length-1].equals("DIRTY")){
					for(int i=0;i<5;i++){
						current_configuration[0] = Integer.parseInt(split[i]);
					}
					update = true;
				}
				else{
					System.out.println("File already have been read");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return update;
	}
	*/
	
	
	private void send_data_to_monitor(){
    	
		
       
        try{
	       	
	        //Send the message to the server
	        
	
	        
	        bw.write(Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt()))); 
    	    
    	    System.out.println(Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
    	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt()))); 
	        
	        
	        bw.flush();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
    }
	
	/*private void writeInFile(){
    	
    	boolean isFileUnlocked = false;
    	try {
    		FileUtils.touch(new File(file_to_write));
    	    isFileUnlocked = true;
    	} catch (IOException e) {
    	    isFileUnlocked = false;
    	}

    	if(isFileUnlocked){
    		BufferedWriter out = null;
        	try{
        	    FileWriter fstream = new FileWriter("run_environment.txt", false); //true tells to append data.
        	    out = new BufferedWriter(fstream);
        	    out.write(Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt()))+
        	    		" DIRTY"); 
        	    
        	    System.out.println(Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_wt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQlq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQbq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getQcq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getVcq_avg_rt()))+
        	    		" "+Double.parseDouble(String.format("%.2f",SearchManager.getRcq_avg_rt()))+
        	    		" DIRTY");    	         	    
        	}
        	catch (IOException e){
        	    System.err.println("Error: " + e.getMessage());
        	}
        	finally{
        		try{
    	    	    if(out != null) {
    	    	        out.close();
    	    	    }
        		}
        		catch (Exception e) {
        			e.printStackTrace();
    			}
        	}
    	}  	
    	else {
    	    // Do stuff you need to do with a file that IS locked
    		System.out.println("File is currently opened");
    	}  	
    }*/

	

}
