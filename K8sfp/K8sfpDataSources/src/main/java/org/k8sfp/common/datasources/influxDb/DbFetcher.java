/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.common.datasources.influxDb;
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

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.QueryResult.*;
import org.k8sfp.common.datasources.InfluxDbDataSourceConfig;

/**
 *
 */
public class DbFetcher {
    //private static final String containerQuery = "SELECT value, container_name FROM cpu_usage_total WHERE container_name !~ /\\/.*/ GROUP BY container_name ORDER BY DESC LIMIT 1";
	//private static final String cpuQuery = "SELECT value / 1000000 FROM %s WHERE container_name='%s' GROUP BY * ORDER BY DESC LIMIT %d";
	private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        static {
		utcDateFormat.setTimeZone((TimeZone.getTimeZone("UTC")));
	}   
	
        private final InfluxDbDataSourceConfig conf;
        
        public DbFetcher(InfluxDbDataSourceConfig conf) {
            this.conf= conf;
        }
        
	public List<DbEntry> GetData() {
		InfluxDB influxDB = InfluxDBFactory.connect(
                        conf.getConnectionUrl(), conf.getDbName(), conf.getPassword());
		String dbName = conf.getDbName();
		influxDB.createDatabase(dbName);

		List<String> container = new ArrayList<String>();

		getContainers(influxDB, dbName, container);
		for (String n : container) {
			System.out.println(n);
		}

		Map<String, List<DbEntry>> entries = GetValues(influxDB, dbName);
		List<DbEntry> joined = joinEntries(entries, null);
		return joined;
	}
	
	/**
	 * Joins entries since Influx DB cant join measurements
	 * @param entries
	 * @param measurement
	 */
	private static List<DbEntry> joinEntries(Map<String, List<DbEntry>> entries, String measurement) {
		List<DbEntry> jEntries = measurement == null ? null : entries.get(measurement);
		for(String key : entries.keySet()) {
			if(jEntries == null) {jEntries = entries.get(key); continue;}   // jEntries contains the final joined entries
			if(key.equals(measurement)) continue;
			List<DbEntry> e = entries.get(key);
			if(e == null) continue;
			for(int i=0; i<e.size(); ++i) { // iterate over all measurements
				DbEntry eToJoin = e.get(i);
				for(int j=0; j<jEntries.size(); ++j) {  // iterate over all rows in the measurement
					DbEntry joinTo = jEntries.get(j);
					if(eToJoin.getTime().equals(joinTo.getTime()) // join fields if the date is equal or before
                                                || eToJoin.getTime().before(joinTo.getTime())) {
						joinTo.merge(eToJoin);  // merge rows
					}
				}
			}
		}
		return jEntries;
	}
	
        private String getCpuQuery() {
            return conf.getRequestQuery();
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
		Query query = new Query(getCpuQuery(), dbName);
		QueryResult res = influxDB.query(query);
		Map<String, List<DbEntry>> entries = new HashMap<String, List<DbEntry>>();
		try {
			for (Result r : res.getResults()) {
                                if(r.getSeries() == null) continue;
				for (Series s : r.getSeries()) {
                                        if(s.getValues() == null) continue;
					for (List<Object> objList : s.getValues()) {
						if (objList != null && objList.size() >= 1) {
							Date d = parseTime(objList.get(0).toString());
							DbEntry e = new DbEntry(d, s.getName(), objList.get(1).toString());
                                                        for(int i=1; i<objList.size(); ++i) {
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

	/**
	 * Gets all container names available in the DB
	 * 
	 * @param influxDB
	 * @param dbName
	 * @param container
	 */
	private void getContainers(InfluxDB influxDB, String dbName, List<String> container) {
		Query query = new Query(conf.getContainerQuery(), dbName);
		QueryResult res = influxDB.query(query);
		for (Result r : res.getResults()) {
			for (Series s : r.getSeries()) {
				for (List<Object> objList : s.getValues()) {
					if (objList != null && objList.size() >= 2) {
						container.add(objList.get(2).toString());
					}
				}
			}
		}
	}
	
	private static Date parseTime(String ts) throws ParseException {
		String[] split = ts.split("[\\.Z]");
		if(split.length < 2) throw new ParseException("Format not valid", 0);
		long val = Long.parseLong(split[split.length-1]);
		val *= 1e-6;
		split[split.length-1] = "" + val;
		ts = split[0];
		for(int i=1; i<split.length; ++i) {
			ts += "." + split[i];
		}
		ts += "Z";
		return utcDateFormat.parse(ts);
	}
	
	private static void put(Map<String, List<DbEntry>> entries, String key, DbEntry val) {
		if (!entries.containsKey(key)) {
			entries.put(key, new ArrayList<DbEntry>());
		}
		entries.get(key).add(val);
	}
}
