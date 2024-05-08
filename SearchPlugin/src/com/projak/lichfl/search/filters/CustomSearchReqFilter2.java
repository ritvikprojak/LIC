package com.projak.lichfl.search.filters;

import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
public class CustomSearchReqFilter2 extends PluginRequestFilter {

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
		JSONObject jsonObject = (JSONObject) jsonRequest;
		JSONArray criteria = (JSONArray) jsonObject.get("searchCriteria");
		String criterias = request.getParameter("criterias");
		System.out.println("jsonRequest: "+jsonObject);
		System.out.println("Criteria: "+criteria);
		System.out.println("Search Criteria: "+criterias);// [{"name":"DocumentTitle","operator":"STARTSWITH","values":["test",""]}]
		/*[{"name":"DocumentTitle","operator":"STARTSWITH","values":["test",""]},
		{"name":"ScanLocation","operator":"INANY","values":["CORPORATE OFFICE","MUMBAI BO"]}]*/
		JSONArray jsonArray = new JSONArray();
		jsonArray = JSONArray.parse(criterias);
		//jsonArray.parse(criterias);//Test_nov25
		System.out.println("JSONArray size:: "+jsonArray.size()+"\n"+jsonArray);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject object = (JSONObject) jsonArray.get(i);
			JSONArray array = (JSONArray) object.get("values");
			if(array.size()>0){
				System.out.println("array"+array);
				array.remove(0);
				array.add(0, "Test_nov25");
				
			}
			//array.remove(0);
			//System.out.println("array:"+array.get(0));
			
		}
		System.out.println("modified criteria to: "+jsonArray.toString());
		PluginRequestUtil.setRequestParameter(request, "criterias", jsonArray.toString());
		
		
		
		return null;
	}
}
