/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.k8sfpevaluator;

import java.util.List;
import org.k8sfp.common.config.InfluxDbDataSourceConfig;

/**
 *
 */
public class InfluxDbPullConfig {
    
    public class InfluxDbPullConfigItem {
        public String query;
        public String tableName;
        public InfluxDbPullConfigItem(String query, String tableName) {
            this.query = query;
            this.tableName = tableName;
        }
    }
    
    public InfluxDbDataSourceConfig conf;
    public List<String> outputColumns;
    public List<InfluxDbPullConfigItem> queries;

    public InfluxDbPullConfig(InfluxDbDataSourceConfig conf, List<String> outputColumns, List<InfluxDbPullConfigItem> queries) {
        this.conf = conf;
        this.outputColumns = outputColumns;
        this.queries = queries;
    }
    
}
