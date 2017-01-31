/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiekerlogpusher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;

/**
 *
 * @author tim
 */
public class KiekerLogPusher {
    
    //curl -XPOST --proxy timzwietasch:"<pwd>".168.209.235:8888 'http://10.0.6.56:30343/write?db=mydb' 
    //-d 'kiekerlogs,host=testhost log="test msg"'
    //
    private static final String[] queryArr = new String[]{
        "curl",
        "-XPOST",
        "",//"--proxy",
        "",//"timzwietasch:n2(rSR6oi@192.168.209.235:8888",
        "http://%1$s/write?db=%2$s", // URL, DBNAME 
        "-d",
        "%1$s,%2$s"  // MEASUREMENTNAME, VALUES
    };
    private static final String valueFormat = "host=%s value=\"%s\"";
    
    private static String[] getQuery(String url, String dbName, String measurement, String values) {
        String[] a = queryArr.clone();
        a[4] = String.format(a[4], url, dbName);
        a[6] = String.format(a[6], measurement, values);
        return a;
    }
    private static String getValueStr(String host, String log) {
        return String.format(valueFormat, host, log.trim());
    }
    
    private static void sendToDb(String[] query){
        StringBuffer output = new StringBuffer();
        try {
            ProcessBuilder pb=new ProcessBuilder(query);
            final Process shell = pb.start();
        } catch (IOException ex) {
            Logger.getLogger(KiekerLogPusher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String getArrayVal(String[] array, int pos, String valIfNull) {
        if(array.length <= pos) return valIfNull;
        return array[pos];
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        KiekerLogPusher m = new KiekerLogPusher();
        
        String host = getArrayVal(args, 0, "testhost");
        String pathName = getArrayVal(args, 1, "/tmp/");
        String url = getArrayVal(args, 2, "10.0.6.56:30343");   // 172.16.22.5:8086
        String dbName = getArrayVal(args, 3, "mydb");
        String measurement = getArrayVal(args, 4, "kiekerlogs");
        int sleepTime = 2000;
        
        if(args.length < 5) {
            System.out.println("USAGE: prgram <Hostname> <pathToFile> <URL:Port to InfluxDb> <DB Name> <Measurement Name>");
            for(String s : args) {
                System.out.println("PARAM: " + s);
            }
            //return;
        }
        
        while(true) {
            try {
                List<String> content = m.readFiles(pathName);
                if(content != null && content.size() > 0) 
                    System.out.println("Writing " + content.size());
                if(content != null) {
                    for(String c : content) {
                        String v = getValueStr(host, c);
                        String[] q = getQuery(url, dbName, measurement, v);
                        sendToDb(q);
                    }
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(KiekerLogPusher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private List<String> readFiles(String path){
        List<String> res = null;
        File[] files = new File(path).listFiles();
        if(files == null) return res;
        for(File f : files) {
            if(f.isDirectory() && f.getName().contains("kieker")) {
                File[] files2 = f.listFiles();
                if(files2 == null) continue;
                for(File f2 : files2) {
                    if(f2.isFile()&& f2.getName().contains("kieker") && f2.getName().endsWith(".dat")) {
                        res = readFile(f2);
                        if(res != null && res.size() > 0) {
                            return res;
                        }
                    }
                    
                }
            }
        }
        return res;
    }
    
    private List<String> readFile(File f) {
        List<String> res = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = null;
            while((line = br.readLine()) != null) {
                res.add(line);
            }
            PrintWriter writer = new PrintWriter(f);    // clear file
            writer.print("");
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KiekerLogPusher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KiekerLogPusher.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(KiekerLogPusher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return res;
    }
    
}
