/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.interfaces;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public interface IK8sDataElementTimeseries extends IK8sDataElement {
    public Map<String, Object> getColumns();
        public Date getTime();
}
