package com.projak.lic.utility;

import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

	private Properties properties = null;

	private static final PropertyReader Instance;

	private PropertyReader() {

		try {

			properties = new Properties();
			InputStream ios = this.getClass().getResourceAsStream("/application.properties");
			properties.load(ios);

		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}

	}

	public static String getProperty(final String key) {
		return getInstance().properties.getProperty(key);
	}

	public static PropertyReader getInstance() {
		return PropertyReader.Instance;
	}

	static {
		Instance = new PropertyReader();
	}

}
