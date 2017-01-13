package org.k8sfp.interfaces;

/**
 * Failure Predictor instance
 */
public interface IK8sPredictor {
	/**
	 * Adds a new Datasource to the predictor.
	 * @param source
	 */
	public void addDataSource(IK8sDataSource source);
	
	
}
