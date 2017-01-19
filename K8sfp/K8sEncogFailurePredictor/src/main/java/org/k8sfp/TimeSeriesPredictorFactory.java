/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp;

import org.k8sfp.encog.EncogTimeSeriesPredictor;
import org.k8sfp.encog.EncogTimeSeriesPredictorConfig;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sPredictor;
import org.k8sfp.interfaces.IK8sPredictorConfig;
import org.k8sfp.interfaces.IK8sTimeSeriesPredictor;
import org.k8sfp.weka.WekaTimeSeriesPredictor;
import org.k8sfp.weka.WekaTimeSeriesPredictorConfig;

/**
 *
 */
public class TimeSeriesPredictorFactory {
    
    public enum TimeSeriesPredictorTypes{
        Encog,
        WekaTimeSeries
    }
    
    private TimeSeriesPredictorFactory(){}
    
    public static IK8sTimeSeriesPredictor create(TimeSeriesPredictorTypes type, IK8sPredictorConfig config){
        IK8sTimeSeriesPredictor res = null;
            switch(type) {
                case Encog:
                    res = new EncogTimeSeriesPredictor((EncogTimeSeriesPredictorConfig)config);
                    break;
                case WekaTimeSeries:
                    res = new WekaTimeSeriesPredictor((WekaTimeSeriesPredictorConfig)config);
                default: 
                    break;
            }
            return res;
    }
    
}
