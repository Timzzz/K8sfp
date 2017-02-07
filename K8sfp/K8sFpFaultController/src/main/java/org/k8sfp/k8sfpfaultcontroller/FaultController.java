/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.k8sfpfaultcontroller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.common.datasources.influxDb.DbFetcher;
import org.k8sfp.data.K8sCommonDataElement;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;

public class FaultController {
    
    private static class ExecItem{
        public IFaultExecution fe;
        public int maxRuntime;
        public ExecItem(IFaultExecution fe, int maxRuntime) {
            this.fe = fe;
            this.maxRuntime = maxRuntime;
        }
    }
    
    private static List<ExecItem> execs = new ArrayList<ExecItem>();
    private static List<String> fields = new ArrayList<String>();
    static {
        execs.add(new ExecItem(new StressExec(), 60));
        fields.add("pod_name");
        fields.add("failure");
        fields.add("started");
    }
    
    private DbFetcher influxDB;
    private static String hostname = null;
    private static String urlPort = null;
    private static String tableName = null;
    private static final String MEASURE_NAME = "fpi";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("USAGE: Program <HOSTNAME> <URL:PORT to DB> <TABLE_NAME>");
            //return;
        }
        hostname = getArrayVal(args, 0, "testhost");
        urlPort = getArrayVal(args, 1, "10.0.6.56:30343");
        tableName = getArrayVal(args, 2, "k8sfp");
        
        new FaultController()._main();
        
    }
    
    private static String getArrayVal(String[] array, int pos, String valIfNull) {
        if(array.length <= pos) return valIfNull;
        return array[pos];
    }
    
    private void _main() {
        connectToInflux();
        execRandomly();
    }
    
    private void execRandomly(){
        int rInd = (int)((Math.random())*(execs.size()-1)+.25);
        ExecItem eItem = execs.get(rInd);
        sendStartToDb(eItem.fe);
        eItem.fe.execute(eItem.maxRuntime);
        sendStopToDb(eItem.fe);
    }
    
    private void connectToInflux() {
        
        InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig(
            "http://" + urlPort, "root", "root", tableName, null);
        this.influxDB = new DbFetcher(conf);
        this.influxDB.updateConfig(true);
        
    }
    
    private void sendStartToDb(IFaultExecution exec) {
        sendToDb(exec.getName(), true);
    }
    private void sendStopToDb(IFaultExecution exec) {
        sendToDb(exec.getName(), false);
    }
    private void sendToDb(String name, boolean started) {
        K8sCommonDataElement e = new K8sCommonDataElement(new Date());
        e.getColumns().put("pod_name", hostname);
        e.getColumns().put("failure", name);
        e.getColumns().put("started", started);
        
        List<IK8sDataElementTimeseries> data = new ArrayList<IK8sDataElementTimeseries>();
        data.add(e);
        
        influxDB.writeData(MEASURE_NAME, data, fields);
    }
    
}
