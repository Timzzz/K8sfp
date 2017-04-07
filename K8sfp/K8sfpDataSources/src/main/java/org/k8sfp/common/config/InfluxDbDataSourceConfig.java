package org.k8sfp.common.config;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.k8sfp.interfaces.IK8sDataSourceConfig;

import com.google.gson.annotations.Expose;

public class InfluxDbDataSourceConfig implements Serializable, IK8sDataSourceConfig {
	
	private static final long serialVersionUID = 3548170589593036300L;
	@Expose
	public String connectionUrl;
	@Expose
	private String user;
	@Expose
	private String password;
	
	private int limit;
	@Expose
	private boolean useProxy;
	
	@Expose
	private String dbName;
	
	private String requestQuery;
	private DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	public static final String CONTAINER_QUERY = "SELECT value, container_name FROM cpu_usage_total WHERE container_name !~ /\\/.*/ GROUP BY container_name ORDER BY DESC LIMIT 1";
	public static final String CPU_QUERY = "SELECT value / 1000000 FROM %1$s WHERE container_name='%2$s' AND container_name !~ /.*monitoring/ GROUP BY * ORDER BY DESC LIMIT %3$d";
	public static final String CPU_ALL_QUERY = "SELECT value, container_name, instance, machine FROM cpu_usage_total WHERE container_name !~ /\\/.*/ AND container_name !~ /.*monitoring/ ORDER BY DESC LIMIT %3$d";
	// public static final String CPU_ALL_QUERY = "SELECT value, container_name,
	// instance, machine FROM cpu_usage_per_cpu, cpu_usage_system,
	// cpu_usage_total, cpu_usage_user, fs_limit, fs_usage, load_average,
	// memory_usage, memory_working_set WHERE container_name !~ /\\/.*/ AND
	// container_name !~ /.*monitoring/ ORDER BY DESC LIMIT %3$d";
	
	/**
	 * @param connectionUrl
	 * @param user
	 * @param password
	 * @param dbName
	 * @param field
	 * @param container
	 * @param limit
	 * @param containerQuery
	 * @param cpuQuery
	 */
	public InfluxDbDataSourceConfig(String connectionUrl, String user, String password, String dbName, int limit,
	        String cpuQuery, boolean useProxy) {
		super();
		this.connectionUrl = connectionUrl;
		this.user = user;
		this.password = password;
		this.dbName = dbName;
		this.limit = limit;
		this.requestQuery = cpuQuery == null ? this.CPU_QUERY : cpuQuery;
		this.useProxy = useProxy;
	}
	
	public InfluxDbDataSourceConfig() {
		super();
	}
	/*
	 * public InfluxDbDataSourceConfig(String connectionUrl, String user, String
	 * password, String dbName, String cpuQuery,boolean useProxy) {
	 * this(connectionUrl, user, password, dbName, 100, cpuQuery, useProxy); }
	 */
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public void setRequestQuery(String requestQuery) {
		this.requestQuery = requestQuery;
	}
	
	public final String getRequestQuery() {
		return requestQuery;
	}
	
	public final DateFormat getUtcDateFormat() {
		return utcDateFormat;
	}
	
	public final String getConnectionUrl() {
		return connectionUrl;
	}
	
	public final String getUser() {
		return user;
	}
	
	public final String getPassword() {
		return password;
	}
	
	public final String getDbName() {
		return dbName;
	}
	
	public final int getLmit() {
		return limit;
	}
	
	public boolean isUseProxy() {
		return useProxy;
	}
	
}
