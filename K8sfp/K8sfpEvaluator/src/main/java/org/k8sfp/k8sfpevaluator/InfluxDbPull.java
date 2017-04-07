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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;
import org.k8sfp.k8sfpevaluator.config.InfluxDbDataSourceConfigItem;
import org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfig;
import org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfigItem;
import org.k8sfp.k8sfpevaluator.config.InfluxDbPullPathConfig;

/**
 *
 */
public class InfluxDbPull {
	
	public final String DATE_KEY = "_DATE";
	private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	private static final String CONFIG_PATH = "./config/selection.conf";
	
	public static void main(String[] args) {
		System.out.println("InfluxDbPull start..");
		
		InfluxDbPullPathConfig pathConf = XmlSerializer.deserialize(CONFIG_PATH, InfluxDbPullPathConfig.class);
		if (pathConf == null || pathConf.filePath == null) {
			pathConf = InfluxDbPullPathConfig.DEFAULT;
			XmlSerializer.serialize(CONFIG_PATH, InfluxDbPullPathConfig.DEFAULT);
		}
		InfluxDbPullConfig conf = XmlSerializer.deserialize(pathConf.filePath, InfluxDbPullConfig.class);
		if (conf == null) {
			System.out.println("Creating default Config.");
			XmlSerializer.serialize(pathConf.filePath, InfluxDbPullConfig.DEFAULT);
		}
		
		new InfluxDbPull().Run(conf);// conf);
		
		System.out.println("Done.");
		
	}
	
	private void Run(InfluxDbPullConfig pullConfig) {
		
		List<String> keyList = pullConfig.keyList;
		
		HashMap<String, IK8sTimeSeriesDataSource> connections = new HashMap<>();
		for (InfluxDbDataSourceConfigItem it : pullConfig.conf) {
			connections.put(it.dbConfigKey, (IK8sTimeSeriesDataSource) CommonDataSourceFactory
			        .create(CommonDataSourceFactory.DataSourceType.InfluxDbSource, it.config));
		}
		
		List<List<IK8sDataElement>> dataAll = new ArrayList<List<IK8sDataElement>>();
		for (InfluxDbPullConfigItem item : pullConfig.queries) {
			List<IK8sDataElement> data = null;
			String DbKey = item.dbConfigKey;
			IK8sTimeSeriesDataSource ds = connections.get(DbKey);
			InfluxDbDataSourceConfig dsConf = (InfluxDbDataSourceConfig) ds.getConfiguration();
			dsConf.setDbName(item.dbName);
			keyList.add(item.measureName);
			dsConf.setRequestQuery(item.query);
			data = ds.getData();
			dataAll.add(data);
		}
		List<IK8sDataElement> dataMerged = dataAll.get(0);
		for (int i = 1; i < dataAll.size(); ++i) {
			dataMerged = combineMeasurements(dataMerged, dataAll.get(i));
		}
		writeToCsv(dataMerged, "eventlog.csv", keyList);
	}
	
	private static List<IK8sDataElement> combineMeasurements(List<IK8sDataElement> data, List<IK8sDataElement> data1) {
		List<IK8sDataElement> res = new ArrayList<IK8sDataElement>();
		for (IK8sDataElement it : data) {
			IK8sDataElementTimeseries itt = (IK8sDataElementTimeseries) it;
			IK8sDataElementTimeseries toCombine = null;
			for (IK8sDataElement it2 : data1) { // data must be ordered
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
				
			}
			res.add(itt);
		}
		return res;
	}
	
	private void Run2() {
		int limit = 10;
		boolean useProxy = true;
		InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig("http://10.0.11.61:32601", "root", "root", null,
		        100, null, useProxy);
		IK8sTimeSeriesDataSource ds = (IK8sTimeSeriesDataSource) CommonDataSourceFactory
		        .create(CommonDataSourceFactory.DataSourceType.InfluxDbSource, conf);
		
		List<String> keyList = new ArrayList<String>();
		List<IK8sDataElement> data1 = null;
		List<IK8sDataElement> data2 = null;
		List<IK8sDataElement> datanodecpu = null;
		
		keyList.add("_DATE");
		keyList.add("host");
		String key;
		
		// conf.setDbName("k8s");
		// conf.setRequestQuery(String.format("SHOW MEASUREMENTS", limit));
		// data1 = ds.getData();
		
		conf.setDbName("k8s");
		key = "cpuusage";
		keyList.add(key);
		conf.setRequestQuery(String.format(
		        "SELECT value as cpuusage, pod_name as host FROM \"cpu/usage_rate\" WHERE pod_name =~ /edge-8vsjk.*/ ORDER BY DESC LIMIT %s",
		        limit));
		data1 = ds.getData();
		
		conf.setDbName("k8s");
		key = "memusage";
		keyList.add(key);
		conf.setRequestQuery(String.format(
		        "SELECT value as memusage, pod_name FROM \"memory/usage\" WHERE pod_name =~ /edge-8vsjk.*/  ORDER BY DESC LIMIT %s",
		        limit));
		data2 = ds.getData();
		
		conf.setDbName("k8s");
		key = "nodecpu";
		keyList.add(key);
		conf.setRequestQuery(String.format(
		        "SELECT value as nodecpu FROM \"cpu/node_utilization\" WHERE nodename =~ /.*61/ ORDER BY DESC LIMIT %s",
		        limit));
		datanodecpu = ds.getData();
		
		conf = new InfluxDbDataSourceConfig("http://10.0.11.61:31070", "root", "root", null, 100, null, useProxy);
		conf.setDbName("kieker");
		ds = (IK8sTimeSeriesDataSource) CommonDataSourceFactory
		        .create(CommonDataSourceFactory.DataSourceType.InfluxDbSource, conf);
		
		// key = "log";
		// keyList.add(key);
		// conf.setRequestQuery(String.format("SELECT hostname as host,
		// operation_signature as log FROM operation_execution WHERE hostname =~
		// /.*edge.*/ ORDER BY DESC LIMIT 0"));
		// List<IK8sDataElement> data = ds.getData();
		
		conf.setDbName("locust");
		key = "locustusers";
		keyList.add(key);
		conf.setRequestQuery(
		        String.format("SELECT user_count as locustusers FROM user_count ORDER BY DESC LIMIT %s", limit));
		List<IK8sDataElement> datalocustUsers = ds.getData();
		
		List<IK8sDataElement> res = data1; // combineMeasurements(data, data1);
		res = combineMeasurements(res, data2);
		res = combineMeasurements(res, datanodecpu);
		res = combineMeasurements(res, datalocustUsers);
		writeToCsv(res, "eventlog.csv", keyList);
	}
	
	private static void writeToCsv(List<IK8sDataElement> list, String path, List<String> keyList) {
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(path, false)));
			IK8sDataElementTimeseries first = (IK8sDataElementTimeseries) list.get(0);
			for (String key : keyList) {
				writer.print(key + "\t");
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
					writer.print(s + "\t");
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
