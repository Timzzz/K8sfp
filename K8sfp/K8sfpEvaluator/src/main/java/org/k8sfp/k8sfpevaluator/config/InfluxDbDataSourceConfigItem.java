package org.k8sfp.k8sfpevaluator.config;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;

import com.google.gson.annotations.Expose;

public class InfluxDbDataSourceConfigItem {
	@Expose
	public InfluxDbDataSourceConfig config;
	
	@Expose
	public String dbConfigKey;
	
	public InfluxDbDataSourceConfigItem(String dbConfigKey, InfluxDbDataSourceConfig config) {
		super();
		this.config = config;
		this.dbConfigKey = dbConfigKey;
	}
	
	public InfluxDbDataSourceConfigItem() {
	}
}
