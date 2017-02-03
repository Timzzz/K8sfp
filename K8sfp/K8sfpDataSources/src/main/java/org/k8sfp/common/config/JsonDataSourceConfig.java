/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.common.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.k8sfp.interfaces.IK8sDataSourceConfig;

/**
 *
 */
public class JsonDataSourceConfig implements IK8sDataSourceConfig {
    
    private final String filePath;
    private final DateFormat dateFormat;
    private final String dateRowName;

    public JsonDataSourceConfig(String filePath, DateFormat dateFormat, String dateRowName) {
        this.filePath = filePath;
        this.dateFormat = dateFormat;
        this.dateRowName = dateRowName;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public String getDateRowName() {
        return dateRowName;
    }

}
