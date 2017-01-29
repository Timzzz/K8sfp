/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.k8sfp.data.K8sArrayDataElement;
import org.k8sfp.data.K8sCommonDataElement;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementArray;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sTimeSeriesPredictor;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 */
public class WekaTimeSeriesPredictor implements IK8sTimeSeriesPredictor {
    
    private IK8sDataSource source;
    private final WekaTimeSeriesPredictorConfig conf;
    private Instances data;

    public WekaTimeSeriesPredictor(WekaTimeSeriesPredictorConfig conf) {
        this.conf = conf;
    }
    
    private static List<IK8sDataElement> predictInstances(WekaTimeSeriesPredictorConfig conf, Instances instances) {
        try {
            WekaForecaster forecaster = new WekaForecaster();
            forecaster.setFieldsToForecast(conf.getFieldsToForecast());
            forecaster.setBaseForecaster(new GaussianProcesses());

            forecaster.getTSLagMaker().setTimeStampField(conf.getTimestampField()); // date time stamp
            forecaster.getTSLagMaker().setMinLag(1);
            forecaster.getTSLagMaker().setMaxLag(12); // monthly data
            forecaster.getTSLagMaker().setAddMonthOfYear(true);
            forecaster.getTSLagMaker().setAddQuarterOfYear(true);
            forecaster.buildForecaster(instances, System.out);
            forecaster.primeForecaster(instances);
            List<List<NumericPrediction>> forecast = forecaster.forecast(conf.getForecastCount(), System.out);

            /*for(int i=0; i<wine.size(); ++i) {
          Instance inst = wine.get(i);
          double v = inst.value(6);
          Attribute a = inst.attribute(6);
          String q = a.getDateFormat();
          String sv = inst.stringValue(6);
          System.out.println(v);
      }*/
            List<IK8sDataElement> predictions = new ArrayList<IK8sDataElement>();
            for (int i = 0; i < 12; i++) {
                List<NumericPrediction> predsAtStep = forecast.get(i);
                List<Object> values = new ArrayList<Object>();
                for (int j = 0; j < 2; j++) {
                    NumericPrediction predForTarget = predsAtStep.get(j);
                    values.add(predForTarget.predicted());
                    System.out.print("" + predForTarget.predicted() + " ");
                }
                System.out.println();
                predictions.add(new K8sArrayDataElement(values.toArray()));
            }
            return predictions;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private void createWekaInstances(){
        try {
            String pathToWineData = conf.getFilePath();
            /*data = new Instances(new BufferedReader(new FileReader(
                    pathToWineData)));
            */
            // load CSV
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File(pathToWineData));
    Instances data = loader.getDataSet();
 
    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);
    saver.setFile(new File(pathToWineData + ".arff"));
    saver.setDestination(new File(pathToWineData));
    saver.writeBatch();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WekaTimeSeriesPredictor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WekaTimeSeriesPredictor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<IK8sDataElement> predict(Date begin, Date end, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addDataSource(IK8sDataSource source) {
        this.source = source;
    }

    public List<IK8sDataElement> predict(int beginIndex, int amount, int forecastCount) {
        List<IK8sDataElement> res = null;
        if(data == null) {
            createWekaInstances();
        }
        Instances dataRange = new Instances(data, beginIndex, amount);
        res = predictInstances(conf, dataRange);
        return res;
    }

}
