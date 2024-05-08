package com.projak.psb.searchutil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyReader {

	static Logger log = Logger.getLogger(PropertyReader.class.getName());
	
    private Properties properties = new Properties();

    private static final PropertyReader Instance = new PropertyReader();

    private PropertyReader() {
        try (InputStream ios = new FileInputStream("C:\\PageCountUpdater\\config.properties")) {
            properties.load(ios);
        } catch (IOException ex) {
            log.error("Error loading properties: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getProperty(final String key) {
        return getInstance().properties.getProperty(key);
    }

    public static PropertyReader getInstance() {
        return Instance;
    }
}
