package org.k8sfp.common.datasources;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sDataSourceConfig;

/**
 * Factory for creating Datasource instances.
 */
public class CommonDataSourceFactory {
	
	public static enum DataSourceType {
		InfluxDbSource, VersatileInfluxDbSource, CsvDatasource
	}
	
	private CommonDataSourceFactory() {
	}
	
	public static IK8sDataSource create(DataSourceType type, IK8sDataSourceConfig config) {
		try {
			IK8sDataSource res = null;
			switch (type) {
				case InfluxDbSource:
					res = new InfluxDbDataSource((InfluxDbDataSourceConfig) config);
					break;
				case VersatileInfluxDbSource:
					res = new VersatileInfluxDbDataSource((InfluxDbDataSourceConfig) config);
					break;
				case CsvDatasource:
					res = new CsvDataSource(config);
				default:
					break;
			}
			return res;
		} catch (Exception ex) {
			return null;
		}
	}
	
}
