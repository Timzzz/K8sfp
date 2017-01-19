/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.weka;

import org.k8sfp.interfaces.IK8sPredictorConfig;

/**
 *
 */
public class WekaTimeSeriesPredictorConfig implements IK8sPredictorConfig {
    
    private String filePath = "/home/tim/repos/K8sfp/scripts/fix_hists.arff";
    private String fieldsToForecast = "value";
    private String timestampField = "date";
    private int forecastCount = 10;
    
    public String getFilePath() {
        return filePath;
    }

    public String getFieldsToForecast() {
        return fieldsToForecast;
    }

    public String getTimestampField() {
        return timestampField;
    }

    public int getForecastCount() {
        return forecastCount;
    }
    
    
    
}
