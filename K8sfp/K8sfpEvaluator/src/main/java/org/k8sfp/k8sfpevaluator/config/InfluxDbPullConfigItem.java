package org.k8sfp.k8sfpevaluator.config;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("QueryItem")
public class InfluxDbPullConfigItem {
	@Expose
	public String query;
	@Expose
	public String dbName;
	@Expose
	public String measureName;
	@Expose
	public String dbConfigKey;
	@Expose
	public int limit;
	
	public InfluxDbPullConfigItem(String query, String dbName, String measureName, String dbConfigKey, int limit) {
		super();
		this.query = query;
		this.dbName = dbName;
		this.measureName = measureName;
		this.dbConfigKey = dbConfigKey;
	}
	
	public InfluxDbPullConfigItem() {
	}
}
