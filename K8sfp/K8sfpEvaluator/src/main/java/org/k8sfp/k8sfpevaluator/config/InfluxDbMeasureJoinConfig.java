package org.k8sfp.k8sfpevaluator.config;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;

import com.google.gson.annotations.Expose;

public class InfluxDbMeasureJoinConfig {
	
	public static InfluxDbMeasureJoinConfig DEFAULT = new InfluxDbMeasureJoinConfig("JoinedMeasures", 100000, 10);
	
	static {
		InfluxDbDataSourceConfig conf = new InfluxDbDataSourceConfig("http://10.0.11.61:32601", "root", "root", "k8sfp",
		        100, null, true);
		DEFAULT.config = conf;
	}
	
	// @Expose
	// public String databaseName;
	
	@Expose
	public String measureName;
	
	@Expose
	public InfluxDbDataSourceConfig config;
	
	/**
	 * Rate to write joined Measures
	 */
	@Expose
	public int UpdateRateInMilliseconds;
	
	/**
	 * Limit for Query Requests
	 */
	@Expose
	public int QueryWriteLimit;
	
	public InfluxDbMeasureJoinConfig(String measureName, int updateRateInMilliseconds, int queryWriteLimit) {
		super();
		this.measureName = measureName;
		this.UpdateRateInMilliseconds = updateRateInMilliseconds;
		this.QueryWriteLimit = queryWriteLimit;
	}
	
}
