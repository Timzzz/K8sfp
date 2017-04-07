package org.k8sfp.common.datasources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.common.datasources.influxDb.DbEntry;
import org.k8sfp.common.datasources.influxDb.DbFetcher;
import org.k8sfp.interfaces.IK8sDataElement;
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
		// TODO Auto-generated method stub
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
		
		/*
		 * InfluxDB influxDB = InfluxDBFactory.connect(conf.getConnectionUrl(),
		 * conf.getUser(), conf.getPassword()); String dbName =
		 * conf.getDbName(); influxDB.createDatabase(dbName);
		 *
		 * BatchPoints batchPoints = BatchPoints .database(dbName) .tag("async",
		 * "true") .retentionPolicy("autogen")
		 * .consistency(ConsistencyLevel.ALL) .build(); Point point1 =
		 * Point.measurement("cpu") .time(System.currentTimeMillis(),
		 * TimeUnit.MILLISECONDS) .addField("idle", 90L) .addField("user", 9L)
		 * .addField("system", 1L) .build(); Point point2 =
		 * Point.measurement("disk") .time(System.currentTimeMillis(),
		 * TimeUnit.MILLISECONDS) .addField("used", 80L) .addField("free", 1L)
		 * .build(); batchPoints.point(point1); batchPoints.point(point2);
		 * influxDB.write(batchPoints); Query query = new
		 * Query("SELECT idle FROM cpu", dbName); influxDB.query(query);
		 * influxDB.deleteDatabase(dbName); return null;
		 */
	}
	
	@Override
	public IK8sDataSourceConfig getConfiguration() {
		return this.conf;
	}
	
}
