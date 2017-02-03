package org.k8sfp.springservice.core;

import java.util.List;
import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.common.datasources.InfluxDbDataSource;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.encog.EncogTimeSeriesPredictorConfig;
import org.k8sfp.TimeSeriesPredictorFactory;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sPredictor;
import org.k8sfp.interfaces.IK8sPredictorConfig;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;
import org.k8sfp.interfaces.IK8sTimeSeriesPredictor;
import org.k8sfp.interfaces.ITest;

public class SpringService {

    public static void main(String[] args) {
        System.out.println("Spring Service start");
//        InfluxDbDataSourceConfig config = new InfluxDbDataSourceConfig(
//                "http://172.17.0.1:8086", "root", "root", "cadvisor",
//                "cpu_usage_total", "sleepy_goldberg", 100,
//                InfluxDbDataSourceConfig.CONTAINER_QUERY,
//                InfluxDbDataSourceConfig.CPU_QUERY);
        InfluxDbDataSourceConfig config = new InfluxDbDataSourceConfig(
                "http://10.0.6.56:32197/", "root", "root", "k8s",
                100,
                InfluxDbDataSourceConfig.CPU_QUERY);

        IK8sPredictorConfig predictorConf = new EncogTimeSeriesPredictorConfig();

        IK8sTimeSeriesDataSource dSource = (IK8sTimeSeriesDataSource) CommonDataSourceFactory.create(
                CommonDataSourceFactory.DataSourceType.VersatileInfluxDbSource, config);
        IK8sTimeSeriesPredictor predictor = TimeSeriesPredictorFactory.create(
                TimeSeriesPredictorFactory.TimeSeriesPredictorTypes.Encog, predictorConf);

        predictor.addDataSource(dSource);

        List<IK8sDataElement> pred = predictor.predict(null, null, 10);

    }

}
