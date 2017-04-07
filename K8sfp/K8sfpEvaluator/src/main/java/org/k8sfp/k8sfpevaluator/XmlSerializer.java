package org.k8sfp.k8sfpevaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.k8sfp.common.config.InfluxDbDataSourceConfig;
import org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class XmlSerializer {
	
	private static XStream xstreamRead = new XStream(new JettisonMappedXmlDriver());
	private static XStream xstreamWrite = new XStream(new JsonHierarchicalStreamDriver());
	static {
		xstreamWrite.processAnnotations(InfluxDbDataSourceConfig.class);
		xstreamWrite.processAnnotations(InfluxDbPullConfig.class);
		xstreamWrite.setMode(XStream.NO_REFERENCES);
		// xstreamWrite.addImplicitCollection(InfluxDbPullConfigItem.class,
		// "queries");
		
		xstreamRead.processAnnotations(InfluxDbDataSourceConfig.class);
		xstreamRead.processAnnotations(InfluxDbPullConfig.class);
		xstreamRead.setMode(XStream.NO_REFERENCES);
		// xstreamRead.addImplicitCollection(InfluxDbPullConfigItem.class,
		// "queries");
		
		xstreamWrite.alias("InfluxDbPull", org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfig.class);
		xstreamRead.alias("InfluxDbPull", org.k8sfp.k8sfpevaluator.config.InfluxDbPullConfig.class);
	}
	
	public static void serialize(String outPath, Object obj) {
		
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		String xml = gson.toJson(obj);
		
		// String xml = xstreamRead.toXML(obj);
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), "utf-8"))) {
			writer.write(xml);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> T deserialize(String outPath, Class<T> type) {
		
		String everything = null;
		try (BufferedReader br = new BufferedReader(new FileReader(outPath))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (java.lang.NullPointerException ex) {
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			return gson.fromJson(everything, type);
			// return (T) xstreamRead.fromXML(new File(outPath));
		} catch (com.thoughtworks.xstream.io.StreamException ex) {
			return null;
		} catch (java.lang.NullPointerException ex) {
			return null;
		} catch (Exception ex) {
			throw ex;
			// return null;
		}
	}
	
}
