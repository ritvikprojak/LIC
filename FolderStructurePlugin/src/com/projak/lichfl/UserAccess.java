package com.projak.lichfl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;
import com.projak.lichfl.util.NHLPropertyReader;

public class UserAccess extends PluginService {

	public String getId() {
		return "UserAccess";
	}

	public String getOverriddenService() {
		return null;
	}

	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		System.out.println("UserAccess.execute.entry()");

		String user = request.getRemoteUser();

		System.out.println("User - " + user);

		PrintWriter writer = response.getWriter();

		JSONResponse responseJ = new JSONResponse();

		JSONObject jobj = new JSONObject();
		
		try{
			
			jobj = getUserDetails(user);
			
		}catch(Exception e){
			
			System.out.println("Exception in fetching user details");
			
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

		System.out.println("UserAccess.execute.exit()");

	}

	public static JSONObject getUserDetails(String userId) throws ClientProtocolException, IOException {
		
		JSONObject responseJson = null;
		
		try(CloseableHttpClient client = HttpClients.createDefault()){
			
			System.out.println("UserAccess.getUserDetails() URL "+NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));
			HttpPost httpPost = new HttpPost(NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));
		//	HttpPost httpPost = new HttpPost("http://10.0.5.81:8080/edms_uat/getNHLDocAccessDtl");

			JSONObject jobj = new JSONObject();

			jobj.put("userId", userId);

			StringEntity entity = new StringEntity(jobj.toString());

			httpPost.setEntity(entity);
			
			httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
			
			System.out.println("Header Set");
			
			try(CloseableHttpResponse response = client.execute(httpPost)){
				
				String bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println(bodyAsString);
				
				responseJson = JSONObject.parse(bodyAsString);

				client.close();
				
			}
			
		}

		return responseJson;
	}
	
	
}
