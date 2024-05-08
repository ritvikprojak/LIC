package com.projak.lic.foldercreation.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

public class PropertyReader {

	private Properties properties = null;
//
//	private static final PropertyReader Instance;

	public PropertyReader() {

		try {
			
			properties = new Properties();
			InputStream ios = PropertyReader.class.getResourceAsStream("/application.properties");
			properties.load(ios);

		} catch (Exception ex) {
			
		}

	}

	public static String getProperty(final String key) {
		
		PropertyReader reader = new PropertyReader();
		
		String t = reader.properties.getProperty(key);
		
		return t;
	}
	
}
