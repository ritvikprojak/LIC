package com.projak.gridx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;






public class DBUtil {

	private static Connection dbConnection = null;
	ResourceBundle rs =null;

	public Connection getDBConnection(String url,String dbuser,String password) throws Exception
	{

		if(dbConnection == null || dbConnection.isClosed()){

				Class.forName(rs.getString("DATABASE_DRIVER"));
				
				dbConnection = DriverManager.getConnection(url,dbuser,password);
				System.out.println("DB Connection Established");
                System.out.println("DbConnection status"+dbConnection.isClosed());
		}

		return dbConnection;
	}
	
	public  Connection getConfigDBConnection() throws Exception
	{

		return getDBConnection(rs.getString("CONFIG_DB_URL"),rs.getString("CONFIG_DB_USERNAME"),rs.getString("CONFIG_DB_PASSWORD"));
	}
	public Boolean documentValidation(String loanNumber, String CIFID, String documentType, String natureOfDocument) {
		Statement stmt = null;
		 rs = getResourceBundle();
		ResultSet rst = null;
		Boolean docExist = false;
		int count = 0;
		String sqlQuery = rs.getString("SQlQuery");
		System.out.println("prop file sqlsqlQuery" + sqlQuery);
		sqlQuery = sqlQuery.replace("[LOANNUMBER]", loanNumber).replace("[CIFID]", CIFID)
				.replace("[DocumentType]", documentType).replace("[NatureOfDocument]", natureOfDocument);

		System.out.println("Final Query" + sqlQuery);

		try {
			
			getConfigDBConnection();
			stmt=dbConnection.createStatement();
			rst = stmt.executeQuery(sqlQuery);
			while (rst.next()) {
				count = rst.getInt(1);
				System.out.println("Final Query Document Count" + count);

				if (count > 0)
					docExist = true;
			}
			
		} catch (SQLException e) {
			System.out.println("Error Occured in While  Connecting database" + e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error Occured in While  excuting the query" + e.toString());
			System.out.println(e);

		}
		finally {

			if (stmt != null)
				stmt = null;
			if (dbConnection != null)
				try {
					dbConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		return docExist;
	}
	
	
	public static ResourceBundle getResourceBundle() {
		ResourceBundle rsbundle = null;
		FileInputStream fis = null;
		try {
			System.out.println("Inside resource bundle");
			fis = new FileInputStream("/EDMS/Configuration/config.properties");
			// fis=new
			// FileInputStream("C:\\Users\\p8demo\\git\\homeloan\\HomeLoanPlugin\\Resources\\config.properties");
			System.out.println("File Found");
			rsbundle = new PropertyResourceBundle(fis);
			fis.close();
			return rsbundle;
		} catch (FileNotFoundException e) {
			e.fillInStackTrace();
			System.out.println(e.fillInStackTrace());
		} catch (IOException e) {
			e.fillInStackTrace();
			System.out.println(e.fillInStackTrace());
		} finally {
			fis = null;
		}
		return rsbundle;
	}
}