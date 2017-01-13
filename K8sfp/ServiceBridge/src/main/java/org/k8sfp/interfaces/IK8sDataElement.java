package org.k8sfp.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents a data point from a Data Source.
 */
public interface IK8sDataElement {
	public Map<String, Object> getColumns();
        public Date getTime();
}
