/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.common.datasources;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.interfaces.special.IK8sVersatileDataSource;

/**
 *
 */
class VersatileInfluxDbDataSource extends InfluxDbDataSource implements 
        org.encog.ml.data.versatile.sources.VersatileDataSource, 
        IK8sVersatileDataSource {
    
    private List<IK8sDataElement> curr = null;
    private List<String[]> cols = new ArrayList<String[]>();
    
    private final Map<String, Integer> indexMap = new HashMap<String, Integer>();
    private int i = 0;
    
    public VersatileInfluxDbDataSource(InfluxDbDataSourceConfig conf) {
        super(conf);
    }
    
    
    @Override
    public List<IK8sDataElement> getData(Date beginDate, Date endDate) {
         List<IK8sDataElement> res = super.getData(beginDate, endDate);
         setCurrentData(res);
         return res;
    }
    
    public void setCurrentData(List<IK8sDataElement> curr){
        this.curr = curr;
        this.i = 0;
        
        this.indexMap.clear();
        this.cols.clear();
        int j=0;
        IK8sDataElementTimeseries el = ((IK8sDataElementTimeseries)curr.get(0));
        for(Map.Entry<String, Object> it : el.getColumns().entrySet()) {
            indexMap.put(it.getKey(), j++);
        }
        
        for(IK8sDataElement _it : curr) {
            IK8sDataElementTimeseries it = (IK8sDataElementTimeseries)_it;
            String[] arr = new String[it.getColumns().size()];
            arr = it.getColumns().values().toArray(arr);
            cols.add(arr);
        }
    }
    
    public String[] readLine() {
        if(cols.size() <= i) return null;
        return cols.get(i++);
//        IK8sDataElement e = curr.get(i);
//        String[] arr = new String[curr.size()];
//        return e.getColumns().values().toArray(arr);
    }

    public void rewind() {
        this.i = 0;
    }

    public int columnIndex(String string) {
        return indexMap.get(string);
    }

}
