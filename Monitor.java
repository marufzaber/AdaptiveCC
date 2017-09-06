import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;




public class Monitor {
    
    private static int qlq_count;
    private static int qbq_count;
    private static int qcq_count;
    private static int vcq_count;
    private static int rcq_count;
    private static int query_processed;
    private static int denom;
    
    
    private static double current_score;
    private static double [] wait_time =  new double[5];
    private static double [] run_time = new double[5];
    private static double [] percentage_wait_time_change = new double[5];
    
    private static boolean empty = true;
    private static ArrayList<String> results = new ArrayList<String>();
    
    private static Socket socket;
    private static int port;
    private static ServerSocket serverSocket;
    
    
    
    public static void main(String[] args) {
        
        qlq_count = qbq_count = qcq_count = vcq_count = rcq_count = 1;
        current_score = Long.MAX_VALUE;
        
        port = 40005;
        query_processed = 0;
        denom = 1;
        
        
        int cores = Runtime.getRuntime().availableProcessors();
       
        
        
        try{
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintWriter pwrite = new PrintWriter(os, true);
            
            while(true)
            {
                String run_environment = br.readLine();
                System.out.println("Just read : "+run_environment);
                
                
                
                if(run_environment != null){
                    
                    if(run_environment.equals("FINISHED"))
                        break;
                    
                    SCCInputProcessor(run_environment);
                }
                
                else empty = true;
                
                if(!empty){
                    
                    System.out.println("Old score "+current_score +"  New Score : "+getScore());
                    
                    if(getScore() != current_score && (getScore() != 0)){
                        current_score = getScore();
                        
                        double cpu_usage = getCPUUsage();
                        if(cpu_usage <= (cores * 100.0)){
                            
                        	
                            updateConfig(indexOfMax());
                            
                            if((qlq_count +qbq_count+
                                    qcq_count + vcq_count + rcq_count) < (2*cores)){
                            
                                pwrite.println(qlq_count + " "+qbq_count+" "+
                                           qcq_count +" "+ vcq_count +" "+ rcq_count);
                                System.out.println("Written : "+qlq_count + " "+qbq_count+" "+
                                               qcq_count +" "+ vcq_count +" "+ rcq_count);
                            }
                            else{
                            	pwrite.println("SKIP");
                            	System.out.println("\n\nSkipping cause number threads is greater than core\n");
                            }
                            	
                            pwrite.flush();
                        }
                        
                        else{
                        	
                        	 pwrite.println("SKIP");
                             pwrite.flush();
                             System.out.println("\n\nSkipping CPU Load : "+getCPUUsage()+"\n\n");
                        	
                        }
                    }
                    
                    else{
                        
                        pwrite.println("SKIP");
                        pwrite.flush();
                        System.out.println("\n\nSkipping cause the score is same or greater"+"\n\n");
                    }
                }
            }
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        finally{
            try{
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    
    private static void SCCInputProcessor(String sCurrentLine){
        if (sCurrentLine != null) {
            empty = false;
            if(!results.contains(sCurrentLine)){
                results.add(sCurrentLine);
                
                String [] rt = sCurrentLine.split(" ");
                
                denom = Integer.parseInt(rt[rt.length-1]) - query_processed;
                query_processed = Integer.parseInt(rt[rt.length-1]);
                
                System.out.println("query process in the interval "+denom);
                System.out.print("percentage change in waittime : ");
                for(int i=0;i<5;i++){
                	percentage_wait_time_change[i] = (Double.parseDouble(rt[i]) - wait_time[i]) / wait_time[i];
                	System.out.print((percentage_wait_time_change[i]*100)+"  ");
                	wait_time[i] = Double.parseDouble(rt[i]);
                }
                
                System.out.println();
                
                for(int i=5;i<10;i++){
                    run_time[i-5] = Double.parseDouble(rt[i]);
                }
                
                
            }
        }
        else{
            empty = true;
        }
    }
    
    
    private static double getCPUUsage(){
        ExecuteShellCommand com = new ExecuteShellCommand();
        
        String load = com.executeCommand("cat ~/proc/loadavg");
        
        //String test = com.executeCommand("sysctl -n vm.loadavg");
        
        String [] cpu_load_avg = load.split(" ");
        
       // double load_avg =  Double.parseDouble(cpu_load_avg[1]);
        
        //System.out.println("CPU LOAD Vaibhav : "+load_avg);
        
        
        
        String cpu_by_processes = com.executeCommand("ps -A -o %cpu");
        

        String split[] = cpu_by_processes.split(" ");
        double cpu_load = 0;
        for(int i=2;i<split.length;i++){
            
            if(split[i].length()>0){
                // System.out.println(split[i]);
                cpu_load += Double.parseDouble(split[i]);
                
            }
        }
        
        
        return cpu_load;
        
    }
    
    private static void updateConfig(int pool){
        switch(pool){
            case 0:
                qlq_count++;
                break;
            case 1:
                qbq_count++;
                break;
            case 2:
                qcq_count++;
                break;
            case 3:
                vcq_count++;
                break;
            case 4:
                rcq_count++;
                break;
            default:
        }
    }
    
    private static int indexOfMax() {
        double max = percentage_wait_time_change[0];
        int maxIndex = 0;
        for (int i = 1; i < (percentage_wait_time_change.length-1); i++) {
            if (percentage_wait_time_change[i] > max) {
                maxIndex = i;
                max = wait_time[i];
            }
        }
        if(maxIndex == (wait_time.length -1)) return maxIndex;
        return maxIndex+1;
    }
    
    public static double getScore(){
        double score = 0;
        double [] weight = getRatio();
        for(int i=0;i<5;i++){
            score+=(weight[i] * wait_time[i]);
        }
        return score ;
    }
    
    public static double [] getRatio(){
        double [] ratio = {0,0,0,0,0};
        double denominator = 0;
        
        for(int i=0;i<5;i++){
            ratio[i]=run_time[i];
            denominator += Math.pow(ratio[i], 2.0);
        }
        denominator = Math.sqrt(denominator);
        
        if(denominator != 0){
            for(int i=0;i<5;i++){
                ratio[i]/=denominator;
            }
        }
        return ratio;
    }
}

class ExecuteShellCommand {
    
    public String executeCommand(String command) {
        
        StringBuffer output = new StringBuffer();
        
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
            new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + " ");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}



