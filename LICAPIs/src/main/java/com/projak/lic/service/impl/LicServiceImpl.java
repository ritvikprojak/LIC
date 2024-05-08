package com.projak.lic.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projak.lic.service.LicService;
import com.projak.lic.utility.PropertyReader;


@Service
public class LicServiceImpl implements LicService {

	private static final Logger logger = LoggerFactory.getLogger(LicServiceImpl.class);
	

	static String scheme = null;
	static String host = null;
	static String employeeDetails = null;
	static String locationDetails = null;
	static String loanDetails = null;
	static String userDetails = null;
	static String docList=null;
	static String srNoAP = null;
	static String filterAP = null;
	static String numberAP = null;
	
	@Override
	public String getEmployeeDetails(String srNo) {
		
		logger.info("Inside getEmployeeDetails method of LicServiceImpl");
		
		scheme = PropertyReader.getProperty("scheme");
		host = PropertyReader.getProperty("host");
		employeeDetails = PropertyReader.getProperty("path1");
		srNoAP = PropertyReader.getProperty("params1");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			logger.info("Inside try block of getEmployeeDetails method :");
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme)
			.setHost(host)
			.setPath(employeeDetails)
			.setParameter(srNoAP, srNo);
			
			URI uri = null;
			
			try {
				uri = builder.build();
				logger.info("uri is :"+uri);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
				logger.error("Exception caught in getEmployeeDetails method of LicServiceImpl ::"+ e1.getLocalizedMessage(),e1.fillInStackTrace());
			}
			
			HttpGet httpget = new HttpGet(uri);
			
			
			logger.info("http URL is:" + httpget.getURI());
			
			CloseableHttpResponse response = null;
		
			
			try {
				
				response = httpclient.execute(httpget);
				logger.info("Http response is :"+response);
				
				 String responseString = "";
				    
				    HttpEntity responseHttpEntity = response.getEntity();
				    
				    InputStream content = responseHttpEntity.getContent();

				    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				    String line;

				    while ((line = buffer.readLine()) != null) {
				        responseString += line;
				    }
				    logger.info("Response String is: " + responseString);
				    EntityUtils.consume(responseHttpEntity);
				
				return responseString.toString();
			    
			}
			catch(IOException e) {
				logger.error("Exception caught in getEmployeeDetails method of LicServiceImpl "+e.getLocalizedMessage(), e.fillInStackTrace());
				e.printStackTrace();
			}
			finally {
			    response.close();
			}
		return null;
	}
		
	catch(Exception ex){
			logger.error("Exception caught in getEmployeeDetails method of LicServiceImpl: "+ex.getLocalizedMessage(), ex.fillInStackTrace());
			ex.printStackTrace();

			return "{}";
		}
	}

	@Override
	public String getEmployeeLocationLocations(String srNo, String filter) {
		
		logger.info("Inside getEmployeeLocationLocations method of LicServiceImpl class");
		
		scheme = PropertyReader.getProperty("scheme");
		host = PropertyReader.getProperty("host");
		locationDetails = PropertyReader.getProperty("path2");
		srNoAP = PropertyReader.getProperty("params1");
		filterAP = PropertyReader.getProperty("params2");
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try {
		
			logger.info("inside try block of getEmployeeLocationLocations method");
			
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme)
			.setHost(host)
			.setPath(locationDetails)
			.setParameter(srNoAP, srNo)
			.setParameter(filterAP, filter);
			
			URI uri = null;
			
			try {
				uri = builder.build();
				logger.info("uri is :"+uri);
			}
			catch (URISyntaxException e1) {
				e1.printStackTrace();
				logger.error("Exception caught in getEmployeeLocationLocations method :"+ e1.getLocalizedMessage(), e1.fillInStackTrace());
			}
			
			HttpGet httpget = new HttpGet(uri);
			
			logger.info("http URL " + httpget.getURI());
			CloseableHttpResponse response = null;
		
			try {
				response = httpClient.execute(httpget);
				logger.info("HTTP response is :"+response);
				
				String responseString="";
				HttpEntity responseHttpEntity = response.getEntity();
				InputStream content = responseHttpEntity.getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				
				String line ;
				
				while((line=buffer.readLine()) != null) {
					responseString+=line;
				}
				
				logger.info("response in string format : " + responseString);
			    
				EntityUtils.consume(responseHttpEntity);
				return responseString;
				
			}
			catch(IOException e){
				logger.error("Exception caught in getEmployeeLocationLocations method of LicServiceImpl :"+e.getLocalizedMessage(), e.fillInStackTrace());
				e.printStackTrace();
			}
			finally {
				response.close();
			}
		
		return null;
		}
		catch(Exception ex){
			logger.error("Exception caught in getEmployeeLocationLocations method of LicServiceImpl class :"+ex.getLocalizedMessage(), ex.fillInStackTrace());
			ex.printStackTrace();

			return "{}";
		}
		
	}

	@Override
	public String getLoanDetails(String number, String filter) {
		
		logger.info("Inside getLoanDetails method of LicServiceImpl class");
		
		scheme = PropertyReader.getProperty("scheme");
		host = PropertyReader.getProperty("host");
		loanDetails = PropertyReader.getProperty("path3");
		numberAP = PropertyReader.getProperty("params3");
		filterAP = PropertyReader.getProperty("params2");
		
		CloseableHttpClient httpClient = HttpClients.createDefault(); 
		
		
		try {
			logger.info("Inside try block of getLoanDetails method :");
			 URIBuilder builder = new URIBuilder();
			 builder.setScheme(scheme)
			 .setHost(host)
			 .setPath(loanDetails)
			 .setParameter(numberAP, number)
			 .setParameter(filterAP, filter);
			 
			 URI uri = null;
			 
			 try {
					uri = builder.build();
					logger.info("uri is :"+uri);
				} catch (URISyntaxException e1) {
					
					e1.printStackTrace();
					logger.error("Exception occured in getLoanDetails method of licServiceImpl:"+ e1.getLocalizedMessage(), e1.fillInStackTrace());
				}
			 
			 HttpGet httpGet = new HttpGet(uri);
			 logger.info("http URL"+ httpGet.getURI());
			 CloseableHttpResponse response = null;
			 
			 try {
				 response= httpClient.execute(httpGet);
				 logger.info("HTTP response is: "+ response);
				 
				 String responseString = "";
				 HttpEntity responseHttpentity = response.getEntity();
				 InputStream content = responseHttpentity.getContent();
				 BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				 
				 String line;
				 
				 while((line=buffer.readLine())!=null) {
					 responseString += line;
					 
				 }
				 
				 logger.info("response in string is :"+responseString);
				 EntityUtils.consume(responseHttpentity);;
				 return responseString;
				 
			 }
			 catch(IOException e) {
				 logger.error(e.getLocalizedMessage(), e.fillInStackTrace());
				 e.printStackTrace();
			 }
			 finally {
				 response.close();
				 logger.info("Inside finally block !!!");
			 }
			
			 return null;
		
		}
		
		catch(Exception ex) {
			logger.error("Exception caught in getLoanDetails method of LicServiceImpl :"+ex.getLocalizedMessage(), ex.fillInStackTrace());
			ex.printStackTrace();

			return "{}";
		}
	}

	@Override
	public String getUserDetails(String srNo) {
		
		logger.info("Inside getUserDetails method of LicServiceImpl class");
		
		scheme = PropertyReader.getProperty("scheme");
		host = PropertyReader.getProperty("host");
		userDetails = PropertyReader.getProperty("path4");
		srNoAP = PropertyReader.getProperty("params1");
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try {
			logger.info("Inside try block of getUserDetails method ");
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme)
			.setHost(host).setPath(userDetails)
			.setParameter(srNoAP, srNo);
			
			URI uri = null;
			try {
				
				uri = builder.build();
				logger.info("uri is :"+uri);
			}
			catch(URISyntaxException e1) {
				e1.printStackTrace();
				logger.error("Exception caught in getUserDetails method of LicServiceImpl :"+ e1.getLocalizedMessage(), e1.fillInStackTrace());
			}
			
			HttpGet httpGet = new HttpGet(uri);
			logger.info("http URL is:"+httpGet.getURI());
			CloseableHttpResponse response = null;
			
			try {
				response = httpClient.execute(httpGet);
				logger.info("HTTP response is :"+ response);
				String responseString = "";
				HttpEntity responseHttpEntity = response.getEntity();
				InputStream content = responseHttpEntity.getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				
				String line="";
				
				while((line=buffer.readLine())!= null) {
					responseString += line;
				}
				
				logger.info("response in String is :"+ responseString);
				EntityUtils.consume(responseHttpEntity);
				return responseString;
			}
			catch(IOException e) {
				logger.error("Exception caught ingetUserDetails method of LicServiceImpl :"+e.getLocalizedMessage(), e.fillInStackTrace());
			}
			finally {
				response.close();
			}
			return null;
		}
		catch(Exception ex) {
			logger.error("Exception caught in getUserDetails method of LiceServiceImpl class:"+ex.getLocalizedMessage(), ex.fillInStackTrace());
			ex.printStackTrace();
			return "{}";
		}
		
		
	}

	@Override
	public String getDocList() {
		logger.info("Inside getDocList method of LicServiceImpl class");
		
		scheme = PropertyReader.getProperty("scheme");
		host = PropertyReader.getProperty("host");
		docList = PropertyReader.getProperty("path5");
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try {
			logger.info("Inside try block of getDocList method ");
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme)
			.setHost(host).
			setPath(docList);
			
			URI uri = null;
			try {
				
				uri = builder.build();
				logger.info("uri is :"+uri);
			}
			catch(URISyntaxException e1) {
				e1.printStackTrace();
				logger.error("Exception caught in getDocList method of LicServiceImpl :"+ e1.getLocalizedMessage(), e1.fillInStackTrace());
			}
			
			HttpGet httpGet = new HttpGet(uri);
			logger.info("http URL is:"+httpGet.getURI());
			CloseableHttpResponse response = null;
			
			try {
				response = httpClient.execute(httpGet);
				logger.info("HTTP response is :"+ response);
				String responseString = "";
				HttpEntity responseHttpEntity = response.getEntity();
				InputStream content = responseHttpEntity.getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				
				String line="";
				
				while((line=buffer.readLine())!= null) {
					responseString += line;
				}
				
				logger.info("response in String is :"+ responseString);
				EntityUtils.consume(responseHttpEntity);
				return responseString;
			}
			catch(IOException e) {
				logger.error("Exception caught in getDocList method of LicServiceImpl :"+e.getLocalizedMessage(), e.fillInStackTrace());
			}
			finally {
				response.close();
			}
			return null;
		}
		catch(Exception ex) {
			logger.error("Exception caught in getDocList method of LiceServiceImpl class:"+ex.getLocalizedMessage(), ex.fillInStackTrace());
			ex.printStackTrace();
			return "{}";
		}
	}

	

}
