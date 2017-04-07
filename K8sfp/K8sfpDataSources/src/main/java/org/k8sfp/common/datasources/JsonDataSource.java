/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.common.datasources;

import java.util.Date;
import java.util.List;

import org.k8sfp.common.config.JsonDataSourceConfig;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataSourceConfig;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

/**
 *
 */
class JsonDataSource implements IK8sTimeSeriesDataSource {
	
	private final JsonDataSourceConfig conf;
	
	public JsonDataSource(JsonDataSourceConfig conf) {
		this.conf = conf;
	}
	
	@Override
	public List<IK8sDataElement> getData(Date beginDate, Date endDate) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public List<String> getColumnNames() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public List<IK8sDataElement> getData() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public IK8sDataSourceConfig getConfiguration() {
		return this.conf;
	}
	
}
