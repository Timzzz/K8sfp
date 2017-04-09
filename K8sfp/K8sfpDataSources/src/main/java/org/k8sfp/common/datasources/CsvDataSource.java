package org.k8sfp.common.datasources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.directory.InvalidAttributeValueException;

import org.k8sfp.common.config.CsvDataSourceConfig;
import org.k8sfp.data.K8sArrayDataElement;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataSourceConfig;
import org.k8sfp.interfaces.IK8sTimeSeriesDataSource;

public class CsvDataSource implements IK8sTimeSeriesDataSource {
	
	private CsvDataSourceConfig config;
	private static final DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	
	public CsvDataSource(IK8sDataSourceConfig config) throws InvalidAttributeValueException {
		super();
		if (!(config instanceof CsvDataSourceConfig))
			throw new InvalidAttributeValueException();
		this.config = (CsvDataSourceConfig) config;
	}
	
	@Override
	public List<String> getColumnNames() {
		return null;
	}
	
	@Override
	public List<IK8sDataElement> getData() {
		return readFile();
	}
	
	@Override
	public List<IK8sDataElement> getData(Date beginDate, Date endDate) {
		return null;
	}
	
	@Override
	public IK8sDataSourceConfig getConfiguration() {
		return null;
	}
	
	/**
	 * @TODO: Dirty Approach
	 * @return
	 */
	private List<IK8sDataElement> readFile() {
		List<IK8sDataElement> ret = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(config.getFilepath()))) {
			String line = br.readLine();
			String[] headers = line.split(config.getDelimiter());
			line = br.readLine();
			
			while (line != null) {
				
				String[] split = line.split(config.getDelimiter());
				if (split == null || split.length == 0)
					continue;
				K8sArrayDataElement data = new K8sArrayDataElement(new Object[] { headers, split });
				ret.add(data);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
}
