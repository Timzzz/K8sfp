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
    private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static void main(String[] args) {

        int limit = 15000;

        InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig(
                "http://10.0.11.63:32601", "root", "root", null, 100, null);

        IK8sTimeSeriesDataSource ds = (IK8sTimeSeriesDataSource) CommonDataSourceFactory.create(
                CommonDataSourceFactory.DataSourceType.InfluxDbSource, conf);

        List<String> keyList = new ArrayList<String>();
        keyList.add("_DATE");
        keyList.add("host");
        String key = "";

        conf.setDbName("k8s");
        key = "cpuusage";
        keyList.add(key);
        conf.setRequestQuery(String.format("SELECT value as cpuusage, pod_name FROM \"cpu/usage_rate\" WHERE pod_name =~ /edge.*/  ORDER BY DESC LIMIT %s", limit));
        List<IK8sDataElement> data1 = ds.getData();

        conf.setDbName("k8s");
        key = "memusage";
        keyList.add(key);
        conf.setRequestQuery(String.format("SELECT value as memusage, pod_name FROM \"memory/usage\" WHERE pod_name =~ /edge.*/  ORDER BY DESC LIMIT %s", limit));
        List<IK8sDataElement> data2 = ds.getData();

        conf.setDbName("k8sfp");
        key = "log";
        keyList.add(key);
        conf.setRequestQuery(String.format("SELECT host, value as log FROM kiekerlogs ORDER BY DESC LIMIT %s", limit));
        List<IK8sDataElement> data = ds.getData();

        List<IK8sDataElement> res = combineMeasurements(data, data1);
        res = combineMeasurements(res, data2);
        writeToCsv(res, "eventlog.csv", keyList);

        System.out.println("Done.");

    }

    private static List<IK8sDataElement> combineMeasurements(List<IK8sDataElement> data, List<IK8sDataElement> data1) {
        List<IK8sDataElement> res = new ArrayList<IK8sDataElement>();
        for (IK8sDataElement it : data) {
            IK8sDataElementTimeseries itt = (IK8sDataElementTimeseries) it;
            IK8sDataElementTimeseries toCombine = null;
            for (IK8sDataElement it2 : data1) {  // data must be ordered
                IK8sDataElementTimeseries itt2 = (IK8sDataElementTimeseries) it2;
                if (itt2.getTime().before(itt.getTime())) {
                    /*if(itt2.getTime().getMonth() <= itt.getTime().getMonth() &&
                    itt2.getTime().getDay()<= itt.getTime().getDay() &&
                    itt2.getTime().getHours()<= itt.getTime().getHours() &&
                    itt2.getTime().getMinutes()<= itt.getTime().getMinutes()&&
                    itt2.getTime().getSeconds()<= itt.getTime().getSeconds()) {*/
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

            }
            res.add(itt);
        }
        return res;
    }

    private static void writeToCsv(List<IK8sDataElement> list, String path, List<String> keyList) {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(path, false)));
            IK8sDataElementTimeseries first = (IK8sDataElementTimeseries) list.get(0);
            for (String key : keyList) {
                writer.print(key + " ");
            }
            writer.println();

            for (int i = 0; i < list.size(); ++i) {
                IK8sDataElementTimeseries it = (IK8sDataElementTimeseries) list.get(i);
                List<String> values = new ArrayList<String>();
                for (String key : keyList) {
                    Object s = "null";
                    if (it.getColumns().containsKey(key)) {
                        s = it.getColumns().get(key);
                    }
                    if (s instanceof Date) {
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
