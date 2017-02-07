/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.data;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;

/**
 *
 */
public class K8sCommonDataElement implements IK8sDataElementTimeseries {

    private final Date time;
    private final SortedMap<String, Object> values;

    public K8sCommonDataElement(Date time) {
        super();
        this.time = time;
        this.values = new TreeMap<String, Object>();
    }
    public K8sCommonDataElement(Date time, String key, String value) {
        this(time);
        this.values.put(key, value);
    }

    public Date getTime() {
        return time;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Map<String, Object> getColumns() {
        return values;
    }
}
