package com.projak.gridx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginResponseUtil;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;

/**
 * Provides an abstract class that is extended to create a class implementing
 * each service provided by the plug-in. Services are actions, similar to
 * servlets or Struts actions, that perform operations on the IBM Content
 * Navigator server. A service can access content server application programming
 * interfaces (APIs) and Java EE APIs.
 * <p>
 * Services are invoked from the JavaScript functions that are defined for the
 * plug-in by using the <code>ecm.model.Request.invokePluginService</code>
 * function.
 * </p>
 * Follow best practices for servlets when implementing an IBM Content Navigator
 * plug-in service. In particular, always assume multi-threaded use and do not
 * keep unshared information in instance variables.
 */
public class CheckUserRolePermissionService extends PluginService {

	/**
	 * Returns the unique identifier for this service.
	 * <p>
	 * <strong>Important:</strong> This identifier is used in URLs so it must
	 * contain only alphanumeric characters.
	 * </p>
	 * 
	 * @return A <code>String</code> that is used to identify the service.
	 */
	public String getId() {
		return "RolePermisssionsService";
	}

	/**
	 * Returns the name of the IBM Content Navigator service that this service
	 * overrides. If this service does not override an IBM Content Navigator
	 * service, this method returns <code>null</code>.
	 * 
	 * @returns The name of the service.
	 */
	public String getOverriddenService() {
		return null;
	}

	/**
	 * Performs the action of this service.
	 * 
	 * @param callbacks
	 *            An instance of the <code>PluginServiceCallbacks</code> class
	 *            that contains several functions that can be used by the
	 *            service. These functions provide access to the plug-in
	 *            configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param response
	 *            The <code>HttpServletResponse</code> object that is generated
	 *            by the service. The service can get the output stream and
	 *            write the response. The response must be in JSON format.
	 * @throws Exception
	 *             For exceptions that occur when the service is running. If the
	 *             logging level is high enough to log errors, information about
	 *             the exception is logged by IBM Content Navigator.
	 */
	public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("Inside Delete Functionality Service");
		PluginLogger logger = callbacks.getLogger();

		JSONResponse js = new JSONResponse();
		JSONObject permissions = new JSONObject();

		response.setContentType("application/json");

		System.out.println("Inside userrole Functionality Service");

		//////////////////////////
		ResourceBundle rs = getResourceBundle();
		String userUrl = rs.getString("CE_URI");
		System.out.println("UserUrl : " + userUrl);
		logger.logInfo(logger, "HomeLoanViewerFilter", "URL is :" + userUrl);
		// String[] inputkey={"userId"};
		String[] inputkey = { "srNo" };
		String user = callbacks.getUserId();

		logger.logInfo(logger, "HomeLoanViewerFilter", "user id is :" + user);
		System.out.println("user id is :" + user);

		String[] inputValue = { user };
		logger.logInfo(logger, "HomeLoanViewerFilter",
				"userUrl " + userUrl + "inputkey" + inputkey + "inputValue" + inputValue);
		System.out.println("userUrl------" + userUrl + "inputkey" + inputkey + "inputValue" + inputValue);

		String userResponse = sendingGetRequest(userUrl, inputkey, inputValue, callbacks);
		System.out.println("userResponse" + userResponse);

		if (null != userResponse) {
			logger.logInfo(logger, "HomeLoanViewerFilter", "inside if loop");
			System.out.println("inside if loop");
			JSONObject userList = JSONObject.parse(userResponse);
			logger.logInfo(logger, "HomeLoanViewerFilter", "UserList : " + userList);
			System.out.println("UserList : " + userList);

			if (userList.containsKey("roleAccess")) {
				com.ibm.json.java.JSONObject object = (JSONObject) userList.get("roleAccess");

				// filter for View
				if (object.containsKey(rs.getString("DMS_Download_Group"))) {
					permissions.put(rs.getString("DMS_Download_Group"), true);

				} else {
					permissions.put(rs.getString("DMS_Download_Group"), false);

				}
				if (object.containsKey(rs.getString("DMS_Print_Group"))) {
					permissions.put(rs.getString("DMS_Print_Group"), true);

				} else {
					permissions.put(rs.getString("DMS_Print_Group"), false);

				}
			} else {
				permissions.put(rs.getString("DMS_Download_Group"), false);
				permissions.put(rs.getString("DMS_Print_Group"), false);

			}
		}
		System.out.println("userResponse" + permissions);

		try {

			logger.logInfo(logger, "HomeLoanDeleteService", "Check UserRolePermission Service");
			js.put("RESPONSE", permissions);
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "CheckUserRolePermissionService");
		} catch (Exception e) {
			logger.logError(logger, "HomeLoanDeleteService", "Exception in RolePermisssions" + e.getLocalizedMessage(),
					e.fillInStackTrace());
			js.put("message", "Fail");
			js.put("errorMessage", e.getMessage());
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "CheckUserRolePermissionService");
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

	private static String sendingGetRequest(String urlString, String[] key, String[] value,
			PluginServiceCallbacks callbacks) throws Exception {
		// By default it is GET request

		PluginLogger logger = callbacks.getLogger();
		logger.logInfo(logger, "HomeLoanViewerFilter", "Inside sendingGetRequestMethod");
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
			logger.logInfo(logger, "HomeLoanViewerFilter", "url in String :" + urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			int responseCode = con.getResponseCode();
			logger.logInfo(logger, "HomeLoanViewerFilter", "response code ----" + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			while ((output = in.readLine()) != null) {
				response = response + output;
			}
			in.close();
			logger.logInfo(logger, "HomeLoanViewerFilter", "response is ----" + response);
			return response;
		} catch (Exception e) {
			return "{}";
		}
	}
	
	
}
