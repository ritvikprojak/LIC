package com.projak.lichfl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;
import com.projak.lichfl.util.NHLPropertyReader;

public class LocationDetails extends PluginService {

	public String getId() {
		return "LocationDetails";
	}

	public String getOverriddenService() {
		return null;
	}

	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		System.out.println("LocationDetails.execute.entry()");

		String user = request.getRemoteUser();

		System.out.println("User - " + user);

		PrintWriter writer = response.getWriter();

		JSONResponse responseJ = new JSONResponse();

		JSONObject jobj = new JSONObject();
		
		try{
			
			jobj = getUserLocationDetails(user);
			
		}catch(Exception e){
			
			System.out.println("Exception in fetching user location details");
			
			System.out.println(e.fillInStackTrace());
			
		}

		System.out.println("JSON Parsed");

		responseJ.put("Message", jobj);

		try {

			writer.write(responseJ.toString());

		} catch (Exception e) {

			System.out.println(e.fillInStackTrace());

		} finally {

			writer.close();

		}

		System.out.println("LocationDetails.execute.exit()");

	}

	public static JSONObject getUserLocationDetails(String userId) throws ClientProtocolException, IOException {
		System.out.println("LocationDetails.getUserLocationDetails() URL: "+NHLPropertyReader.getResourceBundle().getString("nhl.location.api"));
		
		JSONObject responseJson = null;
		
		String url = NHLPropertyReader.getResourceBundle().getString("nhl.location.api")+userId;
		
		System.out.println("URL: "+url);
		
		try(CloseableHttpClient client = HttpClients.createDefault()){
			

			HttpGet httpGet = new HttpGet(url);

			JSONObject jobj = new JSONObject();

			try(CloseableHttpResponse response = client.execute(httpGet)){
				
				String bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println(bodyAsString);
				
				responseJson = JSONObject.parse(bodyAsString);

				client.close();
				
			}
			
		}

		return responseJson;
	}
	
	
}
