/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.common.datasources.influxDb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.naming.directory.InvalidAttributeValueException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.QueryResult.*;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;

/**
 *
 */
public class DbFetcher {
    //private static final String containerQuery = "SELECT value, container_name FROM cpu_usage_total WHERE container_name !~ /\\/.*/ GROUP BY container_name ORDER BY DESC LIMIT 1";
    //private static final String cpuQuery = "SELECT value / 1000000 FROM %s WHERE container_name='%s' GROUP BY * ORDER BY DESC LIMIT %d";
    private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        utcDateFormat.setTimeZone((TimeZone.getTimeZone("UTC")));
    }

    private final InfluxDbDataSourceConfig conf;
    private InfluxDB influxDB;

    public DbFetcher(InfluxDbDataSourceConfig conf) {
        this.conf = conf;
        influxDB = authenticate(conf.isUseProxy());
    }

    /*public void updateConfig(boolean useProxy) {
    influxDB = authenticate(useProxy);
    }*/

    class Auth implements Authenticator {
        private final String username;
        private final String password;

        public Auth(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        }
    }

    private InfluxDB authenticate(boolean useProxy) {
        if(useProxy) {
        Authenticator proxyAuthenticator = new Auth("timzwietasch", "n2(rSR6oi");
            Builder client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.209.235", 8888)))
                    .proxyAuthenticator(proxyAuthenticator);
            InfluxDB influxDB = InfluxDBFactory.connect(
                conf.getConnectionUrl(), conf.getUser(), conf.getPassword(), client);
            return influxDB;
        } else {
            InfluxDB influxDB = InfluxDBFactory.connect(
                conf.getConnectionUrl(), conf.getUser(), conf.getPassword());
            return influxDB;
        }
    }

    public List<DbEntry> GetData() {
        //InfluxDB influxDB = authenticate(true);
        String dbName = conf.getDbName();
        System.out.println(String.format("DB: %s, Query: %s", conf.getDbName(), conf.getRequestQuery()));
        Map<String, List<DbEntry>> entries = GetValues(influxDB, dbName);
        List<DbEntry> joined = joinEntries(entries, null);
        return joined;
    }

    public boolean writeData(String measurement, List<IK8sDataElementTimeseries> data, List<String> fields) {
        try {

            BatchPoints batchPoints = BatchPoints
                    .database(conf.getDbName())
                    .consistency(ConsistencyLevel.ALL)
                    .build();

            for (IK8sDataElementTimeseries it : data) {
                Point.Builder point = Point.measurement(measurement).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                for (int i = 0; i < fields.size(); ++i) {
                    String key = fields.get(i);
                    if (!it.getColumns().containsKey(key)) {
                        //point.addField(key, "");
                    } else {
                        Object o = it.getColumns().get(key);
                        String str = (o == null ? "" : o).toString();
                        point.addField(key, str);
                    }
                }
                Point p = point.build();
                batchPoints.point(p);
            }
            influxDB.write(batchPoints);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Joins entries since Influx DB cant join measurements
     *
     * @param entries
     * @param measurement
     */
    private static List<DbEntry> joinEntries(Map<String, List<DbEntry>> entries, String measurement) {
    	if(entries == null) return null;
        List<DbEntry> jEntries = measurement == null ? null : entries.get(measurement);
        for (String key : entries.keySet()) {
            if (jEntries == null) {
                jEntries = entries.get(key);
                continue;
            }   // jEntries contains the final joined entries
            if (key.equals(measurement)) {
                continue;
            }
            List<DbEntry> e = entries.get(key);
            if (e == null) {
                continue;
            }
            for (int i = 0; i < e.size(); ++i) { // iterate over all measurements
                DbEntry eToJoin = e.get(i);
                for (int j = 0; j < jEntries.size(); ++j) {  // iterate over all rows in the measurement
                    DbEntry joinTo = jEntries.get(j);
                    if (eToJoin.getTime().equals(joinTo.getTime()) // join fields if the date is equal or before
                            || eToJoin.getTime().before(joinTo.getTime())) {
                        joinTo.merge(eToJoin);  // merge rows
                    }
                }
            }
        }
        return jEntries;
    }

    /**
     * Gets all values from the DB Returns Map [MeasureName] -> [DbEntry]
     *
     * @param influxDB
     * @param dbName
     * @return
     * @throws ParseException
     */
    private Map<String, List<DbEntry>> GetValues(InfluxDB influxDB, String dbName) {
        Query query = new Query(conf.getRequestQuery(), dbName);
        QueryResult res = influxDB.query(query);
        Map<String, List<DbEntry>> entries = new HashMap<String, List<DbEntry>>();
        try {
            for (Result r : res.getResults()) {
                if (r.getSeries() == null) {
                    continue;
                }
                for (Series s : r.getSeries()) {
                    if (s.getValues() == null) {
                        continue;
                    }
                    for (List<Object> objList : s.getValues()) {
                        if (objList != null && objList.size() >= 1) {
                            Date d = parseTime(objList.get(0).toString());
                            DbEntry e = new DbEntry(d, s.getName(), objList.get(1).toString());
                            for (int i = 1; i < objList.size(); ++i) {
                                e.getColumns().put(s.getColumns().get(i), objList.get(i));
                            }
                            put(entries, s.getName(), e);
                        }
                    }
                }
            }
            return entries;
        } catch (ParseException e1) {
            e1.printStackTrace();
            return null;
        }

    }

    private static Date parseTime(String ts) throws ParseException {
        if(ts.contains(".")) {
            String[] split = ts.split("\\.");
            ts = split[0] + "Z";
        }
        /*String[] split = ts.split("[\\.Z]");
        if (split.length < 2) {
        throw new ParseException("Format not valid", 0);
        }
        long val = Long.parseLong(split[split.length - 1]);
        val *= 1e-6;
        split[split.length - 1] = "" + val;
        ts = split[0];
        for (int i = 1; i < split.length; ++i) {
        ts += "." + split[i];
        }
        ts += "Z";*/
        return utcDateFormat.parse(ts);
    }

    private static void put(Map<String, List<DbEntry>> entries, String key, DbEntry val) {
        if (!entries.containsKey(key)) {
            entries.put(key, new ArrayList<DbEntry>());
        }
        entries.get(key).add(val);
    }
}
