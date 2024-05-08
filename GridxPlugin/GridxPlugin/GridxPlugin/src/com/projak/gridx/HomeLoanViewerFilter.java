package com.projak.gridx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONViewoneBootstrapResponse;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Provides an abstract class that is extended to create a filter for responses
 * from a particular service. The response from the service is provided to the
 * filter in JSON format before it is returned to the web browser. The filter
 * can then modify that response, and the modified response is returned to the
 * web browser.
 */
public class HomeLoanViewerFilter extends PluginResponseFilter {

	/**
	 * Returns an array of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array of names of the services. These are
	 *         the servlet paths or Struts action names.
	 */
	
	private static final String[] filteredServices = new String[] { "/p8/getViewoneBootstrap" };
	
	public String[] getFilteredServices() {
		return filteredServices;
	}

	/**
	 * Filters the response from the service.
	 * 
	 * @param serverType
	 *            A <code>String</code> that indicates the type of server that
	 *            is associated with the service. This value can be one or more
	 *            of the following values separated by commas:
	 *            <table border="1">
	 *            <tr>
	 *            <th>Server Type</th>
	 *            <th>Description</th>
	 *            </tr>
	 *            <tr>
	 *            <td><code>p8</code></td>
	 *            <td>IBM FileNet P8</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>cm</code></td>
	 *            <td>IBM Content Manager</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>od</code></td>
	 *            <td>IBM Content Manager OnDemand</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>cmis</code></td>
	 *            <td>Content Management Interoperability Services</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>common</code></td>
	 *            <td>For services that are not associated with a particular
	 *            server</td>
	 *            </tr>
	 *            </table>
	 * @param callbacks
	 *            An instance of the
	 *            <code>{@link com.ibm.ecm.extension.PluginServiceCallbacks PluginServiceCallbacks}</code>
	 *            class that contains functions that can be used by the service.
	 *            These functions provide access to plug-in configuration and
	 *            content server APIs.
	 * @param request
	 *            An <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param jsonResponse
	 *            The <code>JSONObject</code> object that is generated by the
	 *            service. Typically, this object is serialized and sent as the
	 *            response. The filter modifies this object to change the
	 *            response that is sent.
	 * @throws Exception
	 *             For exceptions that occur when the service is running.
	 *             Information about the exception is logged as part of the
	 *             client logging and an error response is automatically
	 *             generated and returned.
	 */
	public void filter(String serverType, PluginServiceCallbacks callbacks,
			HttpServletRequest request, JSONObject jsonResponse)
			throws Exception {
		if (jsonResponse instanceof JSONViewoneBootstrapResponse) {
			PluginLogger logger = callbacks.getLogger();
			JSONViewoneBootstrapResponse jvbr = (JSONViewoneBootstrapResponse) jsonResponse;
			System.out.println("jvbr is ------------- " + jvbr);
			logger.logInfo(logger, "HomeLoanViewerFilter", "JVBR is ===="
					+ jvbr);
			Map<String, String> params = getQueryParams(callbacks
					.getServerBaseUrl() + jvbr.getGetContentUrl());
			String docType = params.get("template_name");
			String documentType = docType.substring(3, docType.length());
			logger.logInfo(logger, "HomeLoanViewerFilter",
					"document type is ====" + documentType);
			System.out.println("Document Type is " + documentType);
			ResourceBundle rs = getResourceBundle();
			String userUrl = rs.getString("CE_URI");
			System.out.println("UserUrl : " + userUrl);
			logger.logInfo(logger, "HomeLoanViewerFilter", "URL is :" + userUrl);
			// String[] inputkey={"userId"};
			String[] inputkey = { "srNo" };
			String user = callbacks.getUserId();

			logger.logInfo(logger, "HomeLoanViewerFilter", "user id is :"
					+ user);
			System.out.println("user id is :" + user);

			String[] inputValue = { user };
			logger.logInfo(logger, "HomeLoanViewerFilter", "userUrl " + userUrl
					+ "inputkey" + inputkey + "inputValue" + inputValue);
			System.out.println("userUrl------"+ userUrl
					+ "inputkey" + inputkey + "inputValue" + inputValue);

			String userResponse = sendingGetRequest(userUrl, inputkey,
					inputValue, callbacks);

			
			try {
				if (null != userResponse && !(userResponse.contains("NOT_FOUND"))) {
					logger.logInfo(logger, "HomeLoanViewerFilter",
							"inside if loop");
					System.out.println("inside if loop");
					JSONObject userList = JSONObject.parse(userResponse);
					logger.logInfo(logger, "HomeLoanViewerFilter",
							"UserList : " + userList);
					System.out.println("UserList : " + userList);

					JSONObject object = (JSONObject) userList.get("roleAccess");
					
					//filter for View
					if (object.containsKey("FN_DMS_View")) {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"Print rights present");
						System.out.println("Print rights present");
						String jsonArray = (object.get("FN_DMS_View"))
								.toString();
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"checking if array contains dms print --"
										+ jsonArray.contains("FN_DMS_View"));
						System.out.println("checking if array contains dms print --"
								+ jsonArray.contains("FN_DMS_View"));


						if (jsonArray.equalsIgnoreCase("ALL")) {
							logger.logInfo(logger, "HomeLoanViewerFilter",
									"Document will be visible ---1");
							System.out.println("Document will be visible ---1");
//							jvbr.setViewOneParameter("printButtons", "true");
						} else {
							JSONArray printArray = (JSONArray) object
									.get("FN_DMS_View");
							Iterator<?> iterator = printArray.iterator();
							Set<String> set = new HashSet<String>();
							while (iterator.hasNext()) {
								JSONObject jsonObject = (JSONObject) iterator
										.next();
								for (Object forKey : jsonObject.keySet()) {
									String keys = forKey.toString();
									set.add(keys);
								}
							}
							boolean result = set.contains(documentType);
							if (result) {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is true Document will be visible ---");
							} else {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is false Document will not be visible ---");
								jvbr.setGetContentUrl("");
								jvbr.setColdTemplateUrl("");
								jvbr.put("documentId", "");
							}
						}
					} else {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"View rights not present Document will not be visible --");
						jvbr.setGetContentUrl("");
						jvbr.setColdTemplateUrl("");
						jvbr.put("documentId", "");
					}
					
					// Filter for Print
					if (object.containsKey("FN_DMS_Print")) {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"Print rights present");
						String jsonArray = (object.get("FN_DMS_Print"))
								.toString();
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"checking if array contains dms print --"
										+ jsonArray.contains("FN_DMS_Print"));

						if (jsonArray.equalsIgnoreCase("ALL")) {
							logger.logInfo(logger, "HomeLoanViewerFilter",
									"Print button will be visible ---");
							jvbr.setViewOneParameter("printButtons", "true");
						} else {
							JSONArray printArray = (JSONArray) object
									.get("FN_DMS_Print");
							Iterator<?> iterator = printArray.iterator();
							Set<String> set = new HashSet<String>();
							while (iterator.hasNext()) {
								JSONObject jsonObject = (JSONObject) iterator
										.next();
								for (Object forKey : jsonObject.keySet()) {
									String keys = forKey.toString();
									set.add(keys);
								}
							}
							boolean result = set.contains(documentType);
							if (result) {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is true Print button will be visible ---");
								System.out.println(
										"result is true Print button will be visible ---");
								jvbr.setViewOneParameter("printButtons", "true");
							} else {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is false Print button will not be visible ---");
								System.out.println(
										"result is false Print button will not be visible ---");
								jvbr.setViewOneParameter("printButtons",
										"false");
							}
						}
					} else {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"Print rights not present button will not be visible --");
						System.out.println(
								"Print rights not present button will not be visible --");
						jvbr.setViewOneParameter("printButtons", "false");
					}

					// Filter for Annotate
					if (object.containsKey("FN_DMS_Annotate")) {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"Annotate rights present");
						String jsonArray = (object.get("FN_DMS_Annotate"))
								.toString();
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"checking if array contains dms annotate --"
										+ jsonArray.contains("FN_DMS_Annotate"));
						System.out.println(
								"checking if array contains dms annotate --");

						if (jsonArray.equalsIgnoreCase("ALL")) {
							logger.logInfo(logger, "HomeLoanViewerFilter",
									"Annotate buttons will be visible ---");
							System.out.println(
									"Annotate buttons will be visible ---");
							jvbr.setAnnotationHideButtons("");
						} else {
							JSONArray annotateArray = (JSONArray) object
									.get("FN_DMS_Annotate");
							Iterator<?> iterator = annotateArray.iterator();
							Set<String> set = new HashSet<String>();
							while (iterator.hasNext()) {
								JSONObject jsonObject = (JSONObject) iterator
										.next();
								for (Object forKey : jsonObject.keySet()) {
									String keys = forKey.toString();
									set.add(keys);
								}
							}
							boolean result = set.contains(documentType);
							if (result) {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is true annotate buttons will be visible ---");
								System.out.println("result is true annotate buttons will be visible ---");		
								jvbr.setAnnotationHideButtons("");

							} else {
								logger.logInfo(logger, "HomeLoanViewerFilter",
										"result is false annotate buttons will not be visible ---");
								jvbr.setAnnotationHideButtons("restore,save,select,line,arrow,text,solidText,note,highlight,Transparent highlight,hyperlink,highlightPoly,rectangle,Square,redact,redactPoly,poly,openPoly,oval,circle,freehand,stamp,ruler,angle,show");
							}
						}
					} else {
						logger.logInfo(logger, "HomeLoanViewerFilter",
								"Annotate rights not present buttons will not be visible --");
						System.out.println("Annotate rights not present buttons will not be visible --");		

						jvbr.setAnnotationHideButtons("restore,save,select,line,arrow,text,solidText,note,highlight,Transparent highlight,hyperlink,highlightPoly,rectangle,Square,redact,redactPoly,poly,openPoly,oval,circle,freehand,stamp,ruler,angle,show");
					}
				} else {
					logger.logInfo(
							logger,
							"HomeLoanViewerFilter",
							this
									+ "  \n\nv1BootstrapPlugin --> HomeLoanViewerFilter--> filter User response Is Null");
                      System.out.println("filter User response Is Null");				}
			} catch (Exception exc) {
				logger.logError(this, "filter", exc);
			}
		}
	}

	public static Map<String, String> getQueryParams(String url) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}
					params.put(key, value);
				}
			}
			return params;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	private static String sendingGetRequest(String urlString, String[] key,
			String[] value, PluginServiceCallbacks callbacks) throws Exception {
		// By default it is GET request

		PluginLogger logger = callbacks.getLogger();
		logger.logInfo(logger, "HomeLoanViewerFilter",
				"Inside sendingGetRequestMethod");
		String response = "";
		try {
			String parameters = "";
			for (int i = 0; i < key.length; i++) {
				if (i == 0) {
					parameters = key[i] + "=" + value[i];
				} else {
					parameters = parameters + "&&" + key[i] + "=" + value[i];
				}
			}
			urlString = urlString + parameters;
			URL url = new URL(urlString);
			logger.logInfo(logger, "HomeLoanViewerFilter", "url in String :"
					+ urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			int responseCode = con.getResponseCode();
			logger.logInfo(logger, "HomeLoanViewerFilter", "response code ----"
					+ responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String output;
			while ((output = in.readLine()) != null) {
				response = response + output;
			}
			in.close();
			logger.logInfo(logger, "HomeLoanViewerFilter", "response is ----"
					+ response);
			return response;
		} catch (Exception e) {
			return "{}";
		}
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