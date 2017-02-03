package org.k8sfp.common.datasources.influxDb;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

public class InfluxDbExtractor {

    private InfluxDbDataSourceConfig conf;
    private DbFetcher db;
    private static final String FILE_PATH = "influxDb.log";
    private static final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS");
    
    private static String header = null;
    
    public static void main(String[] args) throws InterruptedException {
        
        InfluxDbDataSourceConfig config = new InfluxDbDataSourceConfig(
                "http://10.0.6.56:30343",
                "root", "root",
                "k8s",
                50,
                InfluxDbDataSourceConfig.CPU_ALL_QUERY);
        
        InfluxDbExtractor extractor = new InfluxDbExtractor();
        BufferedWriter writer = null;
        try {
            PrintWriter pw = new PrintWriter(FILE_PATH);
            pw.close();

            Date lastDate = null;
            List<String> lineBuffer = new ArrayList<String>();
            
            while (true) {
                //writer = new BufferedWriter(new FileWriter(FILE_PATH, false));
                
                lastDate = extractor.extract(config, lastDate, true, writer, lineBuffer);
                //writer.flush();
                
                while(lineBuffer.size() > 5000) {
                    lineBuffer.remove(0);
                }
                //writer.close();
                Thread.sleep(2000);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(InfluxDbExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
    }

    private Date extract(InfluxDbDataSourceConfig config, Date writeFromDate, boolean writeHeader,
            BufferedWriter writer, List<String> lineBuffer) throws IOException {
        
        Date lastDate = writeFromDate;
        db = new DbFetcher(config);
        List<DbEntry> data = db.GetData();
        if (data == null) {
            return writeFromDate;
        }

        if (writeFromDate != null) {
            for (int i = 0; i < data.size(); ++i) {
                DbEntry e = data.get(i);
                if (e.getTime().before(writeFromDate) ||
                        e.getTime().equals(writeFromDate)) {
                    data.remove(i--);
                }
            }
        }
        if (writeHeader && header == null) {
            for (int i = data.size() - 1; i >= 0; --i) { // Header
                DbEntry e = data.get(i);
                String str = "";
                for (Object obj : e.getValues().keySet()) {
                    str += obj + ";";
                }
                header = (str + "\n");
                break;
            }
        }

        for (int i = data.size() - 1; i >= 0; --i) { // Values
            DbEntry e = data.get(i);
            boolean first = true;
            String str = "";
            for (Object obj : e.getValues().values()) {
                if (first && obj instanceof Date) {
                    str += dFormat.format((Date) obj) + ";";
                    first = false;
                } else {
                    str += obj + ";";
                }
            }
            lineBuffer.add(str+"\n");
            if (lastDate == null || lastDate.before(e.getTime())) {
                lastDate = e.getTime();
            }
        }
        
        writer = new BufferedWriter(new FileWriter(FILE_PATH, false));
        
        writer.write(header);
        for(String line : lineBuffer) {
            writer.write(line);
        }
        writer.flush();
        writer.close();
        return lastDate;
    }

}
