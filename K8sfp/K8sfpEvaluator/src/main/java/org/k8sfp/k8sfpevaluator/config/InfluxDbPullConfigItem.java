package org.k8sfp.k8sfpevaluator.config;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("QueryItem")
public class InfluxDbPullConfigItem {
	@Expose
	public String query;
	@Expose
	public String dbName;
	@Expose
	public List<String> measureNames;
	@Expose
	public String dbConfigKey;
	
	public InfluxDbPullConfigItem(String query, String dbName, List<String> measureName, String dbConfigKey) {
		super();
		this.query = query;
		this.dbName = dbName;
		this.measureNames = measureName;
		this.dbConfigKey = dbConfigKey;
	}
	
	public InfluxDbPullConfigItem() {
	}
}
