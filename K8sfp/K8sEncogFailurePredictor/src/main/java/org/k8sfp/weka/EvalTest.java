/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.weka;

import java.io.*;


import java.util.List;

import weka.core.Instances;

import weka.classifiers.functions.GaussianProcesses;

import weka.classifiers.evaluation.NumericPrediction;

import weka.classifiers.timeseries.WekaForecaster;


import weka.classifiers.timeseries.eval.TSEvaluation;

/**
 * http://weka.8497.n7.nabble.com/Fwd-Simple-questions-about-forecaster-API-td33447.html
 * @author tim
 */
public class EvalTest {

  public static void main(String[] args) {

    

    try {

      // path to the Australian wine data included with the time series forecasting

      // package

      String pathToWineData = weka.core.WekaPackageManager.PACKAGES_DIR.toString()

        + File.separator + "timeseriesForecasting" + File.separator + "sample-data"

        + File.separator + "wine.arff";


      // load the wine data

      Instances wine = new Instances(new BufferedReader(new FileReader(pathToWineData)));


      // new forecaster

      WekaForecaster forecaster = new WekaForecaster();


      // set the targets we want to forecast. This method calls

      // setFieldsToLag() on the lag maker object for us

      forecaster.setFieldsToForecast("Fortified,Dry-white");


      // default underlying classifier is SMOreg (SVM) - we'll use

      // gaussian processes for regression instead

      forecaster.setBaseForecaster(new GaussianProcesses());


      forecaster.getTSLagMaker().setTimeStampField("Date"); // date time stamp

      forecaster.getTSLagMaker().setMinLag(1);

      forecaster.getTSLagMaker().setMaxLag(12); // monthly data


      // add a month of the year indicator field

      forecaster.getTSLagMaker().setAddMonthOfYear(true);


      // add a quarter of the year indicator field

      forecaster.getTSLagMaker().setAddQuarterOfYear(true);


      // a new evaluation object (evaluation on the training data)

      TSEvaluation eval = new TSEvaluation(wine, 0);


      // generate and evaluate predictions for up to 12 steps ahead

      eval.setHorizon(12);


      // prime with enough data to cover our maximum lag

      eval.setPrimeWindowSize(12);

      eval.evaluateForecaster(forecaster, System.out);

      System.out.println(eval.toSummaryString());


    } catch (Exception ex) {

      ex.printStackTrace();

    }

  } 

}
