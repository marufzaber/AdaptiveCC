import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigGenerator {
    
    private static final String  prefix =
    "NODE_PREFIX=NODE\n" +
    "QUERY_DIR_PATH=${NODE_PREFIX}/query\n"+
    "OUTPUT_DIR=${NODE_PREFIX}/output\n"+
    "DATASET_DIR_PATH=input/dataset\n"+
    "IS_GEN_CANDIDATE_STATISTICS=false\n"+
    "IS_STATUS_REPORTER_ON=true\n"+
    "LOG_PROCESSED_LINENUMBER_AFTER_X_LINES=50\n"+
    "MIN_TOKENS=65\n"+
    "MAX_TOKENS=500000\n"+
    "IS_SHARDING=true\n"+
    "SHARD_MAX_NUM_TOKENS=65,100,300,500000\n"+
    "BTSQ_THREADS=4\n"+
    "BTIIQ_THREADS=4\n"+
    "BTFIQ_THREADS=4\n";
    
    public static void main(String[] args) {
        
        // TODO Auto-generated method stub
        BufferedWriter bw = null;
        String [] config = null;
        FileWriter fw = null;
        try{
            
            File file = new File("sourcerer-cc.properties");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(prefix);
            
            
            try (BufferedReader br = new BufferedReader(new FileReader("/Users/demigorgan/SourcererCC/clone-detector/config.txt"))) {
                String sCurrentLine;
                if ((sCurrentLine = br.readLine()) != null) {
                    config = sCurrentLine.split(" ");
                    
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            
            
            
            String append =
            "QLQ_THREADS="+config[0]+"\n"+
            "QBQ_THREADS="+config[1]+"\n"+
            "QCQ_THREADS="+config[2]+"\n"+
            "VCQ_THREADS="+config[3]+"\n"+
            "RCQ_THREADS="+config[4]+"\n";
            
            
            System.out.println(append);
            
            bw.write(append);
        }
        catch(Exception e){
            e.printStackTrace();
        }	
        finally{
            try {
                if (bw != null) bw.close();
                if (fw != null)fw.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }	
    }
}
