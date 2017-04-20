package org.k8sfp.common.datasources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.common.datasources.influxDb.DbEntry;
import org.k8sfp.common.datasources.influxDb.DbFetcher;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.interfaces.IK8sDataSourceConfig;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

public class InfluxDbDataSource implements IK8sTimeSeriesDataSource {
	private final InfluxDbDataSourceConfig conf;
	private final DbFetcher db;
	
	public InfluxDbDataSource(InfluxDbDataSourceConfig conf) {
		this.conf = conf;
		this.db = new DbFetcher(conf);
	}
	
	@Override
	public List<String> getColumnNames() {
		return null;
	}
	
	@Override
	public List<IK8sDataElement> getData() {
		return getData(null, null);
	}
	
	@Override
	public List<IK8sDataElement> getData(Date beginDate, Date endDate) {
		
		List<DbEntry> data = db.GetData();
		if (data == null)
			return null;
		return new ArrayList<IK8sDataElement>(data);
	}
	
	public void writeData(String measureName, List<IK8sDataElementTimeseries> data, List<String> fields) {
		this.db.writeData(measureName, data, fields);
	}
	
	@Override
	public IK8sDataSourceConfig getConfiguration() {
		return this.conf;
	}
	
}
