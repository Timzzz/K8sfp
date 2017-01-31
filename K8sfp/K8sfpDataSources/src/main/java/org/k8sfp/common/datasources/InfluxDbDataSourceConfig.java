package org.k8sfp.common.datasources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.k8sfp.interfaces.IK8sDataSourceConfig;

public class InfluxDbDataSourceConfig implements IK8sDataSourceConfig {

    private final String connectionUrl;
    private final String user;
    private final String password;
    private final String dbName;
    private final String fieldRequest;
    private final String containerName;
    private final int limit;
    private final String requestQuery;
    private final String containerRequestQuery;
    private DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    public static final String CONTAINER_QUERY = "SELECT value, container_name FROM cpu_usage_total WHERE container_name !~ /\\/.*/ GROUP BY container_name ORDER BY DESC LIMIT 1";
    public static final String CPU_QUERY = "SELECT value / 1000000 FROM %1$s WHERE container_name='%2$s' AND container_name !~ /.*monitoring/ GROUP BY * ORDER BY DESC LIMIT %3$d";
    public static final String CPU_ALL_QUERY = "SELECT value, container_name, instance, machine FROM cpu_usage_total WHERE container_name !~ /\\/.*/ AND container_name !~ /.*monitoring/ ORDER BY DESC LIMIT %3$d";
    //public static final String CPU_ALL_QUERY = "SELECT value, container_name, instance, machine FROM cpu_usage_per_cpu, cpu_usage_system, cpu_usage_total, cpu_usage_user, fs_limit, fs_usage, load_average, memory_usage, memory_working_set WHERE container_name !~ /\\/.*/ AND container_name !~ /.*monitoring/ ORDER BY DESC LIMIT %3$d";
    
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
    public InfluxDbDataSourceConfig(String connectionUrl, String user, String password, String dbName,
            String field, String container, int limit,
            String containerQuery, String cpuQuery) {
        super();
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.fieldRequest = field;
        this.containerName = container;
        this.limit = limit;
        this.requestQuery = cpuQuery == null ? this.CPU_QUERY : cpuQuery;
        this.containerRequestQuery = containerQuery == null ? this.CONTAINER_QUERY : containerQuery;
        
    }

    public final String getContainerQuery() {
        return containerRequestQuery;
    }

    public final String getRequestQuery() {
        return String.format(requestQuery, getField(),
                    getContainer(), getLmit());
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

    public final String getField() {
        return fieldRequest;
    }

    public final String getContainer() {
        return containerName;
    }

    public final int getLmit() {
        return limit;
    }

}
