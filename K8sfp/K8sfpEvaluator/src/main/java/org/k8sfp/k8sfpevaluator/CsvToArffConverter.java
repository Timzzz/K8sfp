/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.k8sfpevaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 */
public class CsvToArffConverter {
    
    private static String timeFormat = "yyyy-MM-dd HH:mm:ss";
    
    public static void main(String[] args) {
        try {
            
            String filePath = args[0]; //"/home/tim/repos/K8sfp/scripts/fix_hists.csv";
            File f = new File(filePath);
            BufferedReader rd = new BufferedReader(new FileReader(filePath));
            PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(filePath + ".arff")));
            
            wr.println("@relation " + f.getName());
            
            String header = rd.readLine();
            String[] split = header.split(",");
            for(String s : split) {
                if(s.equals("time")) {
                    wr.println("@attribute " + s + " date \"" + timeFormat + "\"");
                } else if(s.equals("container_name")) {
                    wr.println("@attribute " + s + " string");
                } else {
                    wr.println("@attribute " + s + " numeric");
                }
            }
            wr.println("");
            wr.println("@data");
            wr.println("");
            
            String line = null;
            while((line = rd.readLine()) != null) {
                wr.println(line);
            }
            
            wr.close();
            rd.close();
            
            /*CSVLoader loader = new CSVLoader();
            //loader.setDateAttributes("0");
            loader.setDateFormat("yyyy-MM-dd HH:mm:ss");
            loader.setSource(new File(pathToWineData));
            Instances data = loader.getDataSet();
            
            //data.replaceAttributeAt(
            //        new Attribute("dateTime","yyyy-MM-dd HH:mm:ss"), 0);
            
            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(pathToWineData + ".arff"));
            saver.writeBatch();*/
            
        } catch (IOException ex) {
            Logger.getLogger(CsvToArffConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}