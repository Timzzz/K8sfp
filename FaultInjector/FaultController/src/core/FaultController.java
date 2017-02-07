/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.ArrayList;
import java.util.List;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

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
    static {
        execs.add(new ExecItem(new StressExec(), 60000));
    }
    
    private InfluxDB influxDB;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FaultController().execRandomly();
        
    }
   
    private void execRandomly(){
        int rInd = (int)((Math.random())*execs.size()+.25);
        ExecItem eItem = execs.get(rInd);
        sendStartToDb(eItem.fe);
        eItem.fe.execute(eItem.maxRuntime);
        sendStopToDb(eItem.fe);
    }
    
    private void connectToInflux() {
        
        /*InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig(
        "http://10.0.6.56:30343", "root", "root", null, null);
        
        influxDB = InfluxDBFactory.connect(
        conf.getConnectionUrl(), conf.getDbName(), conf.getPassword(), client);
        */
    }
    
    private void sendStartToDb(IFaultExecution exec) {
        
    }
    private void sendStopToDb(IFaultExecution exec) {
        
    }
    private void sendToDb(String name) {
        
    }
    
}
