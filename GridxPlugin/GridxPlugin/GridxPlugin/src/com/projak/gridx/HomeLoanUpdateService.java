package com.projak.gridx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.filenet.api.collection.DocumentSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
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
public class HomeLoanUpdateService extends PluginService {

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
		return "HomeLoanUpdateService";
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
		PluginLogger logger = callbacks.getLogger();

		JSONResponse js = new JSONResponse();
		response.setContentType("application/json");

		String repositoryId = request.getParameter("repositoryId");
		String jsonString = request.getParameter("updateData");
		logger.logInfo(logger, "HomeLoanUpdateService", "Repository Id is ="
				+ repositoryId);
		logger.logInfo(logger, "HomeLoanUpdateService", "json object is  =   "
				+ jsonString);

		Subject subject = callbacks.getP8Subject(repositoryId);
		logger.logInfo(logger, "HomeLoanUpdateService", "Subject is = "
				+ subject);
		UserContext.get().pushSubject(subject);

		try {
			logger.logInfo(logger, "HomeLoanUpdateService", "Inside Try");
			JSONArray jsonArr = new JSONArray(jsonString);
			logger.logInfo(logger, "HomeLoanUpdateService", "Json Array is = "
					+ jsonArr);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				logger.logInfo(logger, "HomeLoanUpdateService", "jsonObj is = "
						+ jsonObj);
				System.out.println("jsonobj is " + jsonObj);
				logger.logInfo(logger, "HomeLoanUpdateService", "Id is = "
						+ jsonObj.getString("Id"));
				logger.logInfo(
						logger,
						"HomeLoanUpdateService",
						"Objectore is = "
								+ callbacks.getP8ObjectStore(repositoryId));
				com.filenet.api.core.Document doc = Factory.Document
						.fetchInstance(
								callbacks.getP8ObjectStore(repositoryId),
								jsonObj.getString("Id"), null);
				logger.logInfo(logger, "HomeLoanUpdateService",
						"Document is = " + doc);
				// Return document properties.
				com.filenet.api.property.Properties props = doc.getProperties();
				logger.logInfo(logger, "HomeLoanUpdateService",
						"Properties are = " + props);

				System.out.println("Debugging check - 1");

				Iterator itr = jsonObj.keys();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					if (!key.equalsIgnoreCase("rowId")
							&& !key.equalsIgnoreCase("Id")) {
						if (key.equalsIgnoreCase("HF_DocumentDate")) {
							Date date = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
									.parse((String) jsonObj.get(key));
							logger.logInfo(logger, "HomeLoanUpdateService",
									"Date Property : " + key);
							props.putValue(key, date);
						} else if (key.equalsIgnoreCase("HF_DocumentNumber")) {
							logger.logInfo(logger, "HomeLoanUpdateService",
									"Int Property : " + key);
							//props.putValue(key,
							//		String.valueOf(jsonObj.getInt(key)));
							props.putValue(key,(String) jsonObj.get(key));
						} else {
							logger.logInfo(logger, "HomeLoanUpdateService",
									"String Property : " + key);
							props.putValue(key, (String) jsonObj.get(key));
						}
					}
				}
				logger.logInfo(
						logger,
						"HomeLoanUpdateService",
						"Property Updated of Document with Id = "
								+ jsonObj.getString("Id"));
				// Save and update property cache.
				doc.save(RefreshMode.REFRESH);

				System.out.println("Updaing child documents");
				DocumentSet docSet = doc.get_ChildDocuments();

				System.out.println("Child Set empty - " + docSet.isEmpty());

				if (!docSet.isEmpty()) {
					Iterator docItr = docSet.iterator();
					while (docItr.hasNext()) {
						Document docChild = (Document) docItr.next();
						itr = jsonObj.keys();
						while (itr.hasNext()) {
							String key = (String) itr.next();
							if (!key.equalsIgnoreCase("rowId")
									&& !key.equalsIgnoreCase("Id")) {
								if (key.equalsIgnoreCase("HF_DocumentDate")) {
									Date date = new SimpleDateFormat(
											"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
											.parse((String) jsonObj.get(key));
									logger.logInfo(logger,
											"HomeLoanUpdateService",
											"Child Date Property : " + key);
									docChild.getProperties()
											.putValue(key, date);
								} else if (key
										.equalsIgnoreCase("HF_DocumentNumber")) {
									logger.logInfo(logger,
											"HomeLoanUpdateService",
											"Child Int Property : " + key);
									/*docChild.getProperties()
											.putValue(
													key,
													String.valueOf(jsonObj
															.getInt(key)));*/
									docChild.getProperties()
									.putValue(
											key,(String) jsonObj.get(key));
								} else {
									logger.logInfo(logger,
											"HomeLoanUpdateService",
											"Child String Property : " + key);
									docChild.getProperties().putValue(key,
											(String) jsonObj.get(key));
								}
							}
						}
						docChild.save(RefreshMode.REFRESH);
						System.out.println("Child doc updated - "+docChild.get_Name());
					}
				}

			}
			logger.logInfo(logger, "HomeLoanUpdateService",
					"Document properties updated successfully");
			js.put("message", "Success");
			PluginResponseUtil.writeJSONResponse(request, response, js,
					callbacks, "HomeLoanUpdateService");
		} catch (Exception e) {
			logger.logError(
					logger,
					"HomeLoanUpdateService",
					"Exception in Update Properties service = "
							+ e.getLocalizedMessage(), e.fillInStackTrace());
			js.put("message", "Fail");
			js.put("errorMessage", e.getMessage());
			PluginResponseUtil.writeJSONResponse(request, response, js,
					callbacks, "HomeLoanUpdateService");
		}
	}

	public static void main(String[] args) throws JSONException, ParseException {

		JSONArray array = new JSONArray(
				"[{\"HF_FileType\":\"OD FILE\",\"Id\":\"{9070A57A-0000-CB11-B05B-0F5F1C17A8A2}\",\"rowId\":2}]");

		for (int i = 0; i < array.length(); i++) {
			JSONObject jsonObj = array.getJSONObject(i);
			// Iterator keys = jobj.keys();
			Iterator itr = jsonObj.keys();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				if (!key.equalsIgnoreCase("rowId")
						&& !key.equalsIgnoreCase("Id")) {
					if (key.equalsIgnoreCase("HF_DocumentDate")) {
						Date date = new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
								.parse((String) jsonObj.get(key));
						// props.putValue(key, date);
						System.out.println("Date : " + date);
					} else if (key.equalsIgnoreCase("HF_DocumentNumber")) {
						System.out.println("Int : " + jsonObj.getInt(key));
						// props.putValue(key,
						// String.valueOf(jsonObj.getInt(key)));
					} else {
						System.out.println("String : " + jsonObj.get(key));
						// props.putValue(key, jsonObj.getInt(key));
					}
				}
			}
		}

	}
}
