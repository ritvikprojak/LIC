package com.projak.lichfl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;


import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.projak.lichfl.util.NHLPropertyReader;

public class FolderResponseService extends PluginResponseFilter {
	
	public static String FOLDER_NAME = "";
	public static String DOC_ID = "";
	public static JSONObject jobj = new JSONObject();
	
	public String[] getFilteredServices() {
		return new String[] { "/p8/openFolder","/p8/continueQuery" };
	}

	public void filter(String serverType, PluginServiceCallbacks callbacks,
			HttpServletRequest request, JSONObject jsonResponse)
			throws Exception {

		System.out.println("FolderResponseService.execute.entry()");

		System.out.println("FolderResponseService.filter()"+request.getRequestURL().toString());

		System.out.println("Folder Name&DOCID: "+FOLDER_NAME+"\t"+DOC_ID);
		
		String user = request.getRemoteUser();
		
		String folderName = request.getParameter("name");
		
		System.out.println("User - " + user);
		
		jobj = getUserDetails(user);

		JSONResultSetResponse response = (JSONResultSetResponse) jsonResponse;

		System.out.println("response - " + response.toString());

		JSONArray array = (JSONArray) response.get("rows");

		System.out.println("Rows Fetched: "+array);

		System.out.println("Printing User Response");
		
		System.out.println("JSON Parsed");
		
		if(folderName != null) {
			FOLDER_NAME = folderName;
			DOC_ID = request.getParameter("docid");
			System.out.println("Required Params: "+FOLDER_NAME+"\t"+DOC_ID);
		}else {
			folderName = FOLDER_NAME;
		}
		
		System.out.println(folderName);
		
		if (folderName.equalsIgnoreCase(NHLPropertyReader.getResourceBundle().getString("nhl.rootFolderName"))) {
			try {

				Set tt = jobj.keySet();

				JSONArray arr = new JSONArray();

				arr.addAll(tt);

				editResponse(response, array, arr);

			} catch (Exception e) {

				System.out
						.println("Error for Location Folder filter for userId - "
								+ user);

				System.out.println(e.fillInStackTrace());

				JSONMessage message = new JSONMessage(
						9860,
						"Error",
						"Error in fetching the Location Folders. Please Contact Administrator",
						null, null, e.getLocalizedMessage());

				response.addErrorMessage(message);

			}

		} else {

			System.out.println("Inside the Else Condition");
			
			String docId = request.getParameter("docid");

			System.out.println(docId);

			String template = docId.split(",")[0];

			if (template.equals("Location")) { // filtering by department
												// allowed in the particular
												// location

				try {

					JSONObject department = (JSONObject) jobj.get(folderName);

					Set tt = department.keySet();

					JSONArray arr = new JSONArray();

					arr.addAll(tt);

					editResponse(response, array, arr);

				} catch (Exception e) {

					System.out
							.println("Error for Department Folder filter for userId - "
									+ user);

					System.out.println(e.fillInStackTrace());

					JSONMessage message = new JSONMessage(
							9861,
							"Error",
							"Error in fetching the Department Folders. Please Contact Administrator",
							null, null, e.getLocalizedMessage());

					response.addErrorMessage(message);

				}

			} else if (template.equals("Department")) {

				try {

					String location = request.getParameter("userLocation");

					JSONObject department = (JSONObject) ((JSONObject) jobj
							.get(location)).get(folderName);

					Set tt = department.keySet();

					if (tt != null && !tt.contains("All")) { // If contains All
																// then to show
																// all the
																// document type
																// to the user

						JSONArray arr = new JSONArray();

						arr.addAll(tt);

						editResponse(response, array, arr);

					}

				} catch (Exception e) {

					System.out
							.println("Error for DocumentType Folder Filter for userId - "
									+ user);

					System.out.println(e.fillInStackTrace());

					JSONMessage message = new JSONMessage(
							9862,
							"Error",
							"Error for DocumentType Folder Filter. Please Contact Administrator",
							null, null, e.getLocalizedMessage());

					response.addErrorMessage(message);

				}

			} else if (template.equals("DocumentType")) {

				try {

					if (folderName.equalsIgnoreCase("Loan Proposal")) {

						try {

							Set tt = jobj.keySet();

							JSONArray arr = new JSONArray();

							arr.addAll(tt);

						//	editResponse(response, array, arr);

						} catch (Exception e) {

							System.out
									.println("Error for Location Folder filter inside Loan Proposal folder for userId - "
											+ user);

							System.out.println(e.fillInStackTrace());

							JSONMessage message = new JSONMessage(
									9860,
									"Error",
									"Error for Location Folder filter inside Loan Proposal folder. Please Contact Administrator",
									null, null, e.getLocalizedMessage());

							response.addErrorMessage(message);

						}

					} else {

						// TODO Other DOcument Class Entry
							
							String location = request.getParameter("userLocation");

							String department = request
									.getParameter("userDepartment");

							JSONObject departmentJSON = (JSONObject) ((JSONObject) jobj
									.get(location)).get(department);
							
							System.out.println("DepartmentJSON - " + departmentJSON);

							Set tt = departmentJSON.keySet();

							if (tt != null && !tt.contains("All")) {
								
								System.out.println("Doesnot contains All");
								
								JSONArray arr = (JSONArray) departmentJSON.get(folderName);
								
								if(!arr.contains("All")){
									editResponse(response, array, arr);
								}else{
									
									System.out.println("Contains all , sending back all the subtype folders");
									
								}

							} else { // If Contains all then show document subtype
										// present for all key

								JSONArray allJSON = (JSONArray) departmentJSON
										.get("All");
								
								if(!allJSON.contains("All")){
									editResponse(response, array, allJSON);
								}else{
									
									System.out.println("Contains all , sending back all the subtype folders");
									
								}

								

							}

					}

				} catch (Exception e) {

					System.out
							.println("Error for DocumentSubType Folder Filter for userId - "
									+ user);

					System.out.println(e.fillInStackTrace());

					JSONMessage message = new JSONMessage(
							9862,
							"Error",
							"Error for DocumentSubType Folder Filter. Please Contact Administrator",
							null, null, e.getLocalizedMessage());

					response.addErrorMessage(message);

				}

			}else if(template.equals("DocumentSubType")){
				
				try{
				
					
					String location = request.getParameter("userLocation");
					
					Set tt = jobj.keySet();
					
					JSONArray arr = new JSONArray();

					arr.addAll(tt);

					editDocResponse(response, array, arr);
					
				}catch(Exception e){
					
					System.out
					.println("Error while filtering documents based on location for userId - "
							+ user);

					System.out.println(e.fillInStackTrace());
		
					JSONMessage message = new JSONMessage(
							9862,
							"Error",
							"Error while filtering documents based on location. Please Contact Administrator",
							null, null, e.getLocalizedMessage());
		
					response.addErrorMessage(message);
					
				}
				
				
				
			}

		}

		
		System.out.println("FolderResponseService.execute.exit()");

	}
//array - rows from the response, tt - doc access locations
	private void editResponse(JSONResultSetResponse response, JSONArray array,
			JSONArray tt) {

		JSONArray removeArr = new JSONArray();

		for (int i = 0; i < array.size(); i++) {

			JSONObject row = (JSONObject) array.get(i);

			String name = (String) row.get("name");

			System.out.println(name);

			if (!tt.contains(name)) {

				removeArr.add(row);

			}

		}

		System.out.println(removeArr.size() + " to be removed from - "
				+ array.size() + " rows");

		array.removeAll(removeArr);

		System.out.println("Rows removed");

		System.out.println(response);
		
	}
	
	private void editDocResponse(JSONResultSetResponse response, JSONArray array,
			JSONArray tt) {
		
		System.out.println("Inside editDocResponse");

		JSONArray removeArr = new JSONArray();

		for (int i = 0; i < array.size(); i++) {

			JSONObject row = (JSONObject) array.get(i);

			JSONObject attr = (JSONObject) row.get("attributes");

			System.out.println("Attributes - " + attr);
			
			if(attr.keySet().contains("ScanLocation")){
				
				System.out.println("ScanLocation Key Present");
				
				String name = (String) ((JSONArray) attr.get("ScanLocation")).get(0);
				
				if (!tt.contains(name)) {

					removeArr.add(row);

				}
				
			}else{
				
				System.out.println("ScanLocation Key Absent");
				
				array.clear();
				return;
				
			}

		}

		System.out.println(removeArr.size() + " to be removed from - "
				+ array.size() + " rows");

		array.removeAll(removeArr);

		System.out.println("Rows removed");

		System.out.println(response);
		
	}
	public static JSONObject getUserDetails(String userId) throws ClientProtocolException, IOException {
		System.out.println("FolderResponseService.getUserDetails() URL="+NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));
		JSONObject responseJson = null;
		
		try(CloseableHttpClient client = HttpClients.createDefault()){
			

			HttpPost httpPost = new HttpPost(NHLPropertyReader.getResourceBundle().getString("nhl.user.api"));

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
	
	public static void main(String[] args) {

		String path = "D:\\IBM\\response\\sumit.json";

		// System.out.println(path);

		FileInputStream stream = null;

		try {
			stream = new FileInputStream(new File(path));

			JSONObject jobj = JSONObject.parse(stream);

			// System.out.println(jobj.keySet());
			
			jobj = (JSONObject) jobj.get("KOLKATA BO");
			
			jobj = (JSONObject) jobj.get("Credit Appraisal");
			
			JSONArray jarr = (JSONArray) jobj.get("Loan Proposal");

			Set tt = jobj.keySet();

			JSONArray arr = new JSONArray();

			arr.addAll(tt);

			System.out.println(jarr.toString());

		} catch (Exception e) {
			System.out.println("User data not found");
			System.out.println(e.fillInStackTrace());
			return;
		}

	}
	

}
