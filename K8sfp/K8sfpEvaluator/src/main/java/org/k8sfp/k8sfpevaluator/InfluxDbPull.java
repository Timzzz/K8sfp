/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.k8sfpevaluator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

/**
 *
 */
public class InfluxDbPull {

    public final String DATE_KEY = "_DATE";
    private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        int limit = 100;
        String dbName = "mydb";
        String queryStr = "SHOW MEASUREMENTS";
        queryStr = String.format(queryStr, limit);

        InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig(
                "http://10.0.6.56:30343", "root", "root", dbName,
                limit,
                queryStr);

        IK8sTimeSeriesDataSource ds = (IK8sTimeSeriesDataSource) CommonDataSourceFactory.create(
                CommonDataSourceFactory.DataSourceType.InfluxDbSource, conf);

        conf.setDbName("mydb");
        conf.setRequestQuery(String.format("SELECT host, value as log FROM kiekerlogs ORDER BY DESC LIMIT %s", limit));
        List<IK8sDataElement> data = ds.getData();

        conf.setDbName("k8s");
            conf.setRequestQuery(String.format("SELECT value as cpuusage, pod_name FROM \"cpu/usage\" WHERE pod_name =~ /edgeinflux.*/  ORDER BY DESC LIMIT 1000", limit));
        List<IK8sDataElement> data1 = ds.getData();

        List<IK8sDataElement> res = new ArrayList<IK8sDataElement>();
        
        for (IK8sDataElement it : data) {
            IK8sDataElementTimeseries itt = (IK8sDataElementTimeseries) it;
            IK8sDataElementTimeseries toCombine = null;
            for (IK8sDataElement it2 : data1) {  // data must be ordered
                IK8sDataElementTimeseries itt2 = (IK8sDataElementTimeseries) it2;
                if (itt2.getTime().before(itt.getTime())) {
                    toCombine = itt2;
                    break;
                } else {
                    continue;
                }
            }
            if (toCombine != null) {
                for (String key : toCombine.getColumns().keySet()) {
                    if (!itt.getColumns().containsKey(key)) {
                        itt.getColumns().put(key, toCombine.getColumns().get(key));
                    }
                }
                res.add(itt);
            }
            
        }
        writeToCsv(res, "test.csv");

    }

    private static void writeToCsv(List<IK8sDataElement> list, String path) {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(path, false)));
            IK8sDataElementTimeseries first = (IK8sDataElementTimeseries) list.get(0);
            List<String> keyList = new ArrayList<String>();
            /*for (String s : first.getColumns().keySet()) {  // create order
            keyList.add(s);
            }*/
            keyList.add("_DATE");
            keyList.add("cpuusage");
            keyList.add("host");
            keyList.add("log");
            
            for (String key : keyList) {
                writer.print(key + " ");
            }
            writer.println();
            
            for (int i = 0; i < list.size(); ++i) {
                IK8sDataElementTimeseries it = (IK8sDataElementTimeseries) list.get(i);
                List<String> values = new ArrayList<String>();
                for (String key : keyList) {
                    Object s = "null";
                    if(it.getColumns().containsKey(key)) s = it.getColumns().get(key);
                    if(s instanceof Date) {
                        s = utcDateFormat.format(s);
                    }
                    writer.print(s + " ");
                }
                writer.println();
            }
        } catch (IOException ex) {
            Logger.getLogger(InfluxDbPull.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }
}
