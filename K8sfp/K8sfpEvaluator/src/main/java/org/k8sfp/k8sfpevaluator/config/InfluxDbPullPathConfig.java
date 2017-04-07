package org.k8sfp.k8sfpevaluator.config;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class InfluxDbPullPathConfig implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 8449876130610529193L;
	
	@Expose
	public String filePath;
	
	@XStreamOmitField
	public static final InfluxDbPullPathConfig DEFAULT = new InfluxDbPullPathConfig("./config/default.conf");
	
	public InfluxDbPullPathConfig(String filePath) {
		this.filePath = filePath;
	}
	
	public InfluxDbPullPathConfig() {
	}
	
}
