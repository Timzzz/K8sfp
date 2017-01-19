/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.data;

import java.util.Date;
import java.util.List;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;

/**
 *
 */
public class TimeSeriesFilter {
    
    public static void filter(List<IK8sDataElement> list, int minDistanceInSeconds) {
        Date last = null;
        for(int i=0; i<list.size(); ++i) {
            IK8sDataElementTimeseries it = (IK8sDataElementTimeseries)list.get(i);
            Date curr = it.getTime();
            if(last == null){
                last = curr;
                continue;
            } else {
              long diffInMs = curr.getTime() - last.getTime();
              if(diffInMs < minDistanceInSeconds*1000) {
                  list.remove(i--);
                  continue;
              }
            }
        }
    }
    
}
