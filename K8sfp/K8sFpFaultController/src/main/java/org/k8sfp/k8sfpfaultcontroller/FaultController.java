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

    //public static final String BYTEMAN_PATH = "/home/tim/packages/byteman-download-3.0.6";
    public static final String BYTEMAN_PATH = "byteman-download-3.0.6";

    private static List<ExecItem> execs = new ArrayList<ExecItem>();
    private static List<String> fields = new ArrayList<String>();
    static {
        //execs.add(new ExecItem(new StressExec(), 60));
        execs.add(new ExecItem(new BytemanMemInjectExec(), -1));
        fields.add("pod_name");
        fields.add("failure");
        fields.add("started");
    }

    private static DbFetcher influxDB;
    private static String hostname = null;
    private static String influxUrlPort = null;
    private static String influxTableName = null;
    private static final String MEASURE_NAME = "fpi";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        if(args.length < 3) {
            System.out.println("USAGE: Program <HOSTNAME> <URL:PORT to DB> <TABLE_NAME> <WAIT_TIME>"
                    + "\nByteman folder must be present in working dir.");
            return;
        }
        hostname = getArrayVal(args, 0, "testhost");
        influxUrlPort = getArrayVal(args, 1, "10.0.6.56:30343");
        influxTableName = getArrayVal(args, 2, "k8sfp");
        String waitTime = getArrayVal(args, 3, "1000");

        int wTime = Integer.parseInt(waitTime);
        Thread.sleep(wTime);

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
        //FaultController.sendStartToDb(eItem.fe.getName());
        eItem.fe.execute(eItem.maxRuntime);
    }

    private void connectToInflux() {
    	boolean useProxy = false;
        InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig(
            "http://" + influxUrlPort, "root", "root", influxTableName, 100, "", useProxy);
        try {
            FaultController.influxDB = new DbFetcher(conf);
        } catch (Exception ex) {
            System.out.println("Caught Failure:");
            ex.printStackTrace();
        }
    }

    public static void sendStartToDb(String execName) {
        sendToDb(execName, true);
    }
    public static void sendStopToDb(String execName) {
        sendToDb(execName, false);
    }
    private static void sendToDb(String name, boolean started) {
        K8sCommonDataElement e = new K8sCommonDataElement(new Date());
        e.getColumns().put("pod_name", hostname);
        e.getColumns().put("failure", name);
        e.getColumns().put("started", started);

        List<IK8sDataElementTimeseries> data = new ArrayList<IK8sDataElementTimeseries>();
        data.add(e);

        try {
            influxDB.writeData(MEASURE_NAME, data, fields);
        } catch (Exception ex) {
            System.out.println("Caught Failure:");
            ex.printStackTrace();
        }
    }

}
