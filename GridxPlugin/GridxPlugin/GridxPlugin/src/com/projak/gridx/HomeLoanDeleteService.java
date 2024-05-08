package com.projak.gridx;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class HomeLoanDeleteService  extends PluginService {

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
		return "HomeLoanDeleteService";
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
	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("Inside Delete Functionality Service");
		PluginLogger logger = callbacks.getLogger();
		
		JSONResponse js = new JSONResponse();
		response.setContentType("application/json");
		
		System.out.println("Inside Delete Functionality Service");
		
		String repositoryId=request.getParameter("repositoryId");
		String jsonString=request.getParameter("deleteData");
		logger.logInfo(logger, "HomeLoanDeleteService", "Repository Id is ="+repositoryId);
		logger.logInfo(logger, "HomeLoanDeleteService", "json object is  =   "+jsonString);
		Subject subject = callbacks.getP8Subject(repositoryId);
		UserContext.get().pushSubject(subject);
		
		try {
			JSONArray jsonArr = new JSONArray(jsonString);
	        for(int i = 0; i < jsonArr.length(); i++) {
	            JSONObject jsonObj = (JSONObject) jsonArr.get(i);
	            logger.logInfo(logger, "HomeLoanDeleteService", "json obj = "+jsonObj);
	            System.out.println("json obj = "+jsonObj.toString());
	           // logger.info(jsonObj);
	            //com.filenet.api.core.Document doc = Factory.Document.getInstance(callbacks.getP8ObjectStore(repositoryId), ClassNames.DOCUMENT, new Id(jsonObj.getString("Id")));
	            // Delete Document.
	            //doc.delete();
	            // Save the changes.
	            //doc.save(RefreshMode.REFRESH );
	            com.filenet.api.core.Document doc = Factory.Document.fetchInstance(callbacks.getP8ObjectStore(repositoryId), new Id(jsonObj.getString("Id")), null);
	            VersionSeries vs=doc.get_VersionSeries();
	            
	            logger.logInfo(logger, "HomeLoanDeleteService", "Deleting Document with Version Series Id "+vs.get_Id().toString());
	            vs.delete();
	            vs.save(RefreshMode.NO_REFRESH);
	            logger.logInfo(logger, "HomeLoanDeleteService", "Deleted Document with ID = "+jsonObj.getString("Id"));
	        }
			logger.logInfo(logger, "HomeLoanDeleteService", "Document/s Deleted Successfully");
	        js.put("message", "Success");
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "HomeLoanDeleteService");
		}catch(Exception e) {
			logger.logError(logger, "HomeLoanDeleteService", "Exception in Delete Document Service = "+e.getLocalizedMessage(), e.fillInStackTrace());
			js.put("message", "Fail");
			js.put("errorMessage",e.getMessage());
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "HomeLoanDeleteService");
		}

	}
}
