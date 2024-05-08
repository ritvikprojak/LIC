package com.projak.lichfl.search.filters;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;
import com.projak.lichfl.search.utils.NHLPropertyReader;



/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
public class CustomSearchReqFilter extends PluginRequestFilter {

	/**
	 * Returns the names of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array that contains the names of the services.
	 */
	public String[] getFilteredServices() {
		return new String[] { "/p8/search" };
	}

	/**
	 * Filters a request that is submitted to a service.
	 * 
	 * @param callbacks
	 *            An instance of <code>PluginServiceCallbacks</code> that contains several functions that can be used by the
	 *            service. These functions provide access to plug-in configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the request. The service can access the invocation parameters from the
	 *            request. <strong>Note:</strong> The request object can be passed to a response filter to allow a plug-in to communicate 
	 *            information between a request and response filter.
	 * @param jsonRequest
	 *            A <code>JSONArtifact</code> that provides the request in JSON format. If the request does not include a <code>JSON Artifact</code>  
	 *            object, this parameter returns <code>null</code>.
	 * @return A <code>JSONObject</code> object. If this object is not <code>null</code>, the service is skipped and the
	 *            JSON object is used as the response.
	 */
	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		System.out.println("CustomSearchReqFilter.filter()");
		JSONArray criteria = (JSONArray) ((JSONObject) jsonRequest).get("searchCriteria");
		System.out.println("Before: "+criteria);
		JSONObject criterion = new JSONObject();
		String user = request.getRemoteUser();
		//get the access locations from api
		JSONObject accessLocations = getUserDetails(user);
		System.out.println("accessLocations: "+accessLocations);
		
		Set locationsSet = accessLocations.keySet();
		Iterator iterator = locationsSet.iterator();
		JSONArray values = new JSONArray();
		while (iterator.hasNext()) {
			String object = (String) iterator.next();
			values.add(object);
		}
		System.out.println("values: "+values);
		
		criterion.put("id", "ScanLocation");
		criterion.put("selectedOperator", "INANY");
		criterion.put("values", values);
		criterion.put("dataType", "xs:string");
		criterion.put("cardinality", "SINGLE");
		criteria.add(criterion);
		
		System.out.println("After: "+criteria);
		return null;
	}
	
	public static JSONObject getUserDetails(String userId) throws ClientProtocolException, IOException {
		
		JSONObject responseJson = null;
		
		try{
			CloseableHttpClient client = HttpClients.createDefault();
			System.out.println("CustomSearchReqFilter.getUserDetails() URL "+NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));
			HttpPost httpPost = new HttpPost(NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));

			JSONObject jobj = new JSONObject();

			jobj.put("userId", userId);

			StringEntity entity = new StringEntity(jobj.toString());

			httpPost.setEntity(entity);
			
			httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
			
			System.out.println("Header Set");
			
			try{
				CloseableHttpResponse response = client.execute(httpPost);
				String bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println(bodyAsString);
				
				responseJson = JSONObject.parse(bodyAsString);

				
				
			}catch (Exception e) {
				System.out.println("Exception in CloseableHttpResponse: "+e.getMessage());
			}finally {
				client.close();
			}
			
		}catch (Exception e) {
			System.out.println("Exception in CloseableHttpClient: "+e.getMessage());
		}

		return responseJson;
	}
}
