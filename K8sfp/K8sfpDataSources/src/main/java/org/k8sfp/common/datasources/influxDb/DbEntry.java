/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.common.datasources.influxDb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;

/**
 *
 */
public class DbEntry implements IK8sDataElementTimeseries {

    private final Date time;
    public final String DATE_KEY = "_DATE";
    private final SortedMap<String, Object> values;

    public DbEntry(Date time, String key, String value) {
        super();
        this.time = time;
        this.values = new TreeMap<String, Object>();
        this.values.put(key, value);
        this.values.put(DATE_KEY, time);//(double)(time.getHours()*100+time.getMinutes()));
    }

    public Date getTime() {
        return time;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void merge(DbEntry e) {
        for (String k : e.getValues().keySet()) {
            this.values.put(k, e.getValues().get(k));
        }
    }

    public Map<String, Object> getColumns() {
        return values;
    }
}
