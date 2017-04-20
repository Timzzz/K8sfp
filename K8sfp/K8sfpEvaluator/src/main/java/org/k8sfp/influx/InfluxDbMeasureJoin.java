package org.k8sfp.influx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.common.datasources.InfluxDbDataSource;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.k8sfpevaluator.InfluxDbPull;
import org.k8sfp.k8sfpevaluator.XmlSerializer;
import org.k8sfp.k8sfpevaluator.config.InfluxDbMeasureJoinConfig;
import org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfig;

public class InfluxDbMeasureJoin {
	
	public final String DATE_KEY = "_DATE";
	private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	private static final String CONFIG_PATH = "./config/measureJoinConfig.conf";
	
	private InfluxDbDataSource db = null;
	
	public static void main(String[] args) {
		
		System.out.println("InfluxDbMeasureJoin start..");
		
		InfluxDbMeasureJoinConfig conf = getJoinConfig();
		InfluxDbPullConfig pullConf = InfluxDbPull.getPullConfig();
		
		if (conf == null || pullConf == null)
			return;
		new InfluxDbMeasureJoin().run(conf, pullConf);
	}
	
	public static InfluxDbMeasureJoinConfig getJoinConfig() {
		InfluxDbMeasureJoinConfig conf = XmlSerializer.deserialize(CONFIG_PATH, InfluxDbMeasureJoinConfig.class);
		if (conf == null) {
			System.out.println("Creating default Config.");
			XmlSerializer.serialize(CONFIG_PATH, InfluxDbMeasureJoinConfig.DEFAULT);
			return null;
		}
		return conf;
	}
	
	public void run(InfluxDbMeasureJoinConfig joinConf, InfluxDbPullConfig pullConf) {
		
		Date lastTimestamp = null;
		
		while (true) {
			List<IK8sDataElement> data;
			this.db = (InfluxDbDataSource) CommonDataSourceFactory
			        .create(CommonDataSourceFactory.DataSourceType.InfluxDbSource, joinConf.config);
			
			InfluxDbPull pull = new InfluxDbPull();
			data = pull.getMergedData(pullConf);
			List<IK8sDataElementTimeseries> dataTimeseries = data.stream()
			        .map(object -> (IK8sDataElementTimeseries) object).collect(Collectors.toList());
			Collections.reverse(dataTimeseries);
			
			if (dataTimeseries == null || dataTimeseries.size() <= 0) {
				
			} else {
				if (lastTimestamp == null && dataTimeseries.size() > 0) {
					this.db.writeData(joinConf.measureName, dataTimeseries, pullConf.keyList);
					lastTimestamp = dataTimeseries.get(dataTimeseries.size() - 1).getTime();
				} else {
					while (dataTimeseries.size() > 0) {
						// int lastIdx = dataTimeseries.size() - 1;
						Date curr = dataTimeseries.get(0).getTime();
						if (curr.equals(lastTimestamp) || curr.before(lastTimestamp)) {
							dataTimeseries.remove(0);
						} else {
							break;
						}
					}
					if (dataTimeseries.size() > 0) {
						this.db.writeData(joinConf.measureName, dataTimeseries, pullConf.keyList);
						lastTimestamp = dataTimeseries.get(dataTimeseries.size() - 1).getTime();
					}
				}
				
				try {
					Thread.sleep(joinConf.UpdateRateInMilliseconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
