/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.k8sfpevaluator.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 *
 */
@XStreamAlias("InfluxDbPull")
public class InfluxDbPullConfig implements Serializable {
	
	private static final long serialVersionUID = -8413809772483977201L;
	
	@XStreamOmitField
	public static InfluxDbPullConfig DEFAULT;
	static {
		InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig("http://10.0.11.61:32601", "root", "root", null,
		        100, null, true);
		InfluxDbDataSourceConfigItem cItem = new InfluxDbDataSourceConfigItem("default", conf);
		List<InfluxDbDataSourceConfigItem> cItems = new ArrayList<>();
		cItems.add(cItem);
		
		List<String> keys = new ArrayList<>();
		keys.add("cpuusage");
		InfluxDbPullConfigItem pullItem = new InfluxDbPullConfigItem(
		        "SELECT value as cpuusage, pod_name as host FROM \"cpu/usage_rate\" WHERE pod_name =~ /edge-8vsjk.*/ ORDER BY DESC LIMIT 100",
		        "k8s", keys, "default");
		ArrayList<InfluxDbPullConfigItem> pullItems = new ArrayList<>();
		pullItems.add(pullItem);
		
		ArrayList<String> keyList = new ArrayList<>();
		keyList.add("_DATE");
		keyList.add("host");
		keyList.add("cpuusage");
		
		DEFAULT = new InfluxDbPullConfig(false, cItems, pullItems, keyList);
	}
	
	// @Expose
	// public boolean useProxy;
	
	@XStreamAlias("DBConfigList")
	public static class ConfList {
		@XStreamImplicit
		public InfluxDbDataSourceConfigItem[] conf;
	}
	
	@Expose
	public List<InfluxDbDataSourceConfigItem> conf;
	@Expose
	public List<InfluxDbPullConfigItem> queries;
	@Expose
	public List<String> keyList;
	
	public InfluxDbPullConfig(boolean useProxy, List<InfluxDbDataSourceConfigItem> conf,
	        ArrayList<InfluxDbPullConfigItem> queries, List<String> keyList) {
		super();
		// this.useProxy = useProxy;
		this.conf = conf;// conf.toArray(new InfluxDbDataSourceConfigItem[0]);
		this.queries = queries;// queries.toArray(new
		                       // InfluxDbPullConfigItem[0]);
		this.keyList = keyList;// keyList.toArray(new String[0]);
	}
	
	public InfluxDbPullConfig() {
	}
}
