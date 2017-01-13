package org.k8sfp.interfaces;

import java.util.Date;
import java.util.List;

/**
 * Time-Series Data Source
 */
public interface IK8sTimeSeriesDataSource extends IK8sDataSource {
	public List<IK8sDataElement> getData(Date beginDate, Date endDate);
}
