package org.k8sfp.interfaces;

import java.util.Date;
import java.util.List;

public interface IK8sTimeSeriesPredictor extends IK8sPredictor {
	/**
	 * Predicts a number of datapoints by using the data-source information
	 * within the given time-span.
	 * @param begin
	 * @param end
	 * @param count
	 * @return
	 */
	public List<IK8sDataElement> predict(Date begin, Date end, int count);
}
