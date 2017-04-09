package org.K8sFpTpFpEvaluator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.k8sfp.common.config.CsvDataSourceConfig;
import org.k8sfp.common.datasources.CommonDataSourceFactory;
import org.k8sfp.data.K8sArrayDataElement;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

public class TpFpEvaluator {
	
	public TpFpEvaluator() {
		try {
			fw = new FileWriter("out_tpfpevaluator.csv");
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	enum FpRes {
		TP, TN, FP, FN, NONE
	};
	
	private static final double THRESHOLD = .90;
	
	public static void main(String[] args) {
		new TpFpEvaluator().main();
	}
	
	public void main() {
		CsvDataSourceConfig config = new CsvDataSourceConfig("infile.csv", ",");
		IK8sTimeSeriesDataSource source = (IK8sTimeSeriesDataSource) CommonDataSourceFactory
		        .create(CommonDataSourceFactory.DataSourceType.CsvDatasource, config);
		
		List<IK8sDataElement> data = source.getData();
		List<FpRes> resList = createResList(data);
		
		write("TP\tTN\tFP\tFN\tNONE\n");
		
		int tp = 0, tn = 0, fp = 0, fn = 0, none = 0;
		for (FpRes res : resList) {
			switch (res) {
				case TP:
					tp++;
					break;
				case TN:
					tn++;
					break;
				case FP:
					fp++;
					break;
				case FN:
					fn++;
					break;
				case NONE:
					none++;
					break;
			}
			write(String.format("%s\t%s\t%s\t%s\t%s\n", tp, tn, fp, fn, none));
		}
		close();
	}
	
	private static List<FpRes> createResList(List<IK8sDataElement> data) {
		List<FpRes> resList = new ArrayList<FpRes>();
		for (int i = 0; i < data.size(); ++i) {
			K8sArrayDataElement it = (K8sArrayDataElement) data.get(i);
			String[] split = (String[]) it.getData()[1];
			double origVal = Double.parseDouble(split[1]);
			double arimaFc = Double.parseDouble(split[2]);
			double holtFc = Double.parseDouble(split[3]);
			
			double forecast = holtFc;
			
			boolean isTp = true;
			
			double maxVal = origVal;
			double minVal = origVal;
			
			for (int j = i; j < i + 5 && j < data.size(); j++) {
				K8sArrayDataElement it2 = (K8sArrayDataElement) data.get(i);
				String[] split2 = (String[]) it.getData()[1];
				double origVal2 = Double.parseDouble(split[1]);
				double arimaFc2 = Double.parseDouble(split[2]);
				double holtFc2 = Double.parseDouble(split[3]);
				
				maxVal = Math.max(origVal2, maxVal);
				minVal = Math.min(origVal2, minVal);
				
			}
			FpRes res = FpRes.NONE;
			if (forecast >= THRESHOLD && maxVal >= THRESHOLD) {
				res = FpRes.TP;
			}
			if (forecast < THRESHOLD && maxVal < THRESHOLD) {
				res = FpRes.TN;
			}
			if (forecast >= THRESHOLD && maxVal < THRESHOLD) {
				res = FpRes.FP;
			}
			if (forecast < THRESHOLD && maxVal >= THRESHOLD) {
				res = FpRes.FN;
			}
			resList.add(res);
		}
		return resList;
	}
	
	BufferedWriter bw = null;
	FileWriter fw = null;
	
	private void write(String line) {
		try {
			bw.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void close() {
		
		try {
			if (bw != null)
				bw.close();
			if (fw != null)
				fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
