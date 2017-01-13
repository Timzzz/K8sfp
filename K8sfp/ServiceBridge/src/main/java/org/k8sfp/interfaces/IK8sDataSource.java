package org.k8sfp.interfaces;

import java.util.List;

/**
 * Real-Time Data Source Interface
 */
public interface IK8sDataSource {
	public List<String> getColumnNames();
	public List<IK8sDataElement> getData();
}
