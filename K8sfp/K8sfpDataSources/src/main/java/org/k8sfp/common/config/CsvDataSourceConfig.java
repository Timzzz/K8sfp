package org.k8sfp.common.config;

import java.io.Serializable;

import org.k8sfp.interfaces.IK8sDataSourceConfig;

public class CsvDataSourceConfig implements Serializable, IK8sDataSourceConfig {
	private static final long serialVersionUID = 3881199362607119322L;
	private String filepath;
	private String delimiter;
	
	public CsvDataSourceConfig(String filepath, String delimiter) {
		super();
		this.filepath = filepath;
		this.delimiter = delimiter;
	}
	
	public String getFilepath() {
		return this.filepath;
	}
	
	public String getDelimiter() {
		return this.delimiter;
	}
	
}
