import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class UpdateObjectTypeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8824851516614537710L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.out.println("Inside UpdateObjectTypeServlet.doPost!!!!");
		
		System.out.println("Debugger - 34");

		String objectType = request.getPathInfo().substring(1);

		System.out.println("Object Type : " + objectType);

		InputStream requestInputStream = request.getInputStream();

		JSONObject jsonRequest = JSONObject.parse(requestInputStream);

		String requestMode = jsonRequest.get("requestMode").toString();

		JSONArray requestProperties = (JSONArray) jsonRequest.get("properties");

		JSONArray responseProperties = new JSONArray();

		JSONObject clientContext = (JSONObject) jsonRequest.get("clientContext");

		String userId = clientContext.get("userid").toString();

		System.out.println("Logged in UserId - " + userId);

		JSONArray propertyData = getPropertyData(objectType, request.getLocale(), userId);

//		System.out.println("sampleEDService.UpdateObjectTypeServlet: clientContext=" + clientContext);

//		System.out.println("sampleEDService.UpdateObjectTypeServlet: Cookie=" + request.getHeader("Cookie"));

//		System.out.println(jsonRequest.toString());

		try {

			for (int j = 0; j < requestProperties.size(); j++) {

				JSONObject requestProperty = (JSONObject) requestProperties.get(j);

				String value = String.valueOf(requestProperty.get("value"));

				if (value.equals("error")) {

					sendErrorResponse(response, "EDS error details for logging.", "Example of an error from EDS.");

					return;

				}
			}

			if (requestMode.equals("initialNewObject")) {

				for (int i = 0; i < propertyData.size(); i++) {

					JSONObject overrideProperty = (JSONObject) propertyData.get(i);

					String overridePropertyName = overrideProperty.get("symbolicName").toString();

					if (overrideProperty.containsKey("initialValue")) {

						for (int j = 0; j < requestProperties.size(); j++) {

							JSONObject requestProperty = (JSONObject) requestProperties.get(j);
							String requestPropertyName = requestProperty.get("symbolicName").toString();
							if (overridePropertyName.equals(requestPropertyName)) {

								Object initialValue = overrideProperty.get("initialValue");
								requestProperty.put("value", initialValue);

							}

						}

					}

				}

			}

			for (int i = 0; i < propertyData.size(); i++) {

				JSONObject overrideProperty = (JSONObject) propertyData.get(i);
				if (requestMode.equals("initialNewObject") || requestMode.equals("initialExistingObject")
						|| requestMode.equals("inProgressChanges")) {

					if (overrideProperty.containsKey("dependentOn")) {

						if (requestMode.equals("inProgressChanges")) {

							String dependentOn = overrideProperty.get("dependentOn") != null
									? overrideProperty.get("dependentOn").toString()
									: "";
							String dependentValue = overrideProperty.get("dependentValue") != null
									? overrideProperty.get("dependentValue").toString()
									: "";

							for (int j = 0; j < requestProperties.size(); j++) {

								JSONObject requestProperty = (JSONObject) requestProperties.get(j);
								String requestPropertyName = requestProperty.get("symbolicName").toString();
								if (requestPropertyName.equals(dependentOn)) {

									Object value = requestProperty.get("value");
									String valueStr = (value != null) ? value.toString() : "";
									if (dependentValue.equals(valueStr)) {
										responseProperties.add(overrideProperty);
									}

								}

							}

						}

					} else {

						if (requestMode.equals("initialNewObject") && overrideProperty.containsKey("initialValue")) {

							overrideProperty.put("value", overrideProperty.get("initialValue"));

						}

						responseProperties.add(overrideProperty);

					}

				}

			}

			JSONObject jsonResponse = new JSONObject();

			jsonResponse.put("properties", responseProperties);

			PrintWriter writer = response.getWriter();

			jsonResponse.serialize(writer);

		} catch (Exception ex) {

		}

	}

	private JSONArray getPropertyData(String objectType, Locale locale, String userId) throws IOException {

//		InputStream propertyDataStream = this.getClass()
//				.getResourceAsStream(objectType.replace(' ', '_') + "_PropertyData_" + locale.toString() + ".json");
//
//		if (propertyDataStream == null) {
//
//			propertyDataStream = this.getClass()
//					.getResourceAsStream(objectType.replace(' ', '_') + "_PropertyData.json");
//
//		}

		JSONArray jsonPropertyData = new JSONArray();

		JSONObject jobj1 = new JSONObject();

		jobj1.put("symbolicName", "Office");
		
		jsonPropertyData.add(jobj1);
		
		JSONObject jobj2 = new JSONObject();
		
		jobj2.put("symbolicName", "Confidential");

		jobj2.put("initialValue", true);
		
		JSONObject job = new JSONObject();
		
		job.put("displayName", "confidential");
		
		JSONObject jobb = new JSONObject();
		
		jobb.put("value", "false");
		
		jobb.put("displayName", "No");
		
		JSONArray arr = new JSONArray();
		
		arr.add(jobb);
		
		jobb = new JSONObject();
		
		jobb.put("value", "true");
		
		jobb.put("displayName", "Yes");
		
		arr.add(jobb);
		
		job.put("choiceList", arr);
		
		jobj2.put("choices", job);
		
		jsonPropertyData.add(jobj2);

		for (Object object : jsonPropertyData) {

			JSONObject jobj = (JSONObject) object;
			String sName = jobj.get("symbolicName") != null ? jobj.get("symbolicName").toString() : "";
			if (sName.equalsIgnoreCase("Office")) {
				try {
					System.out.println("Symbolic Name - Office");
					JSONObject userObj = getUserDetails(userId);
					if (userObj.get("status").toString().equalsIgnoreCase("Success")) {

						String baseLocation = userObj.get("baseLocation").toString();
						String attachLocation = userObj.get("attachLocation").toString();
						JSONArray locations = locationArray(attachLocation);
						jobj.put("initialValue", baseLocation);
						JSONObject choices = new JSONObject();
						choices.put("displayName", "Offices");
						choices.put("choices", locations);
						jobj.put("choiceList", choices);

					}

				} catch (Exception ex) {
					System.out.println("Error on EDS Update Services : " + ex.getMessage());
				}
			}
//			else if(sName.equalsIgnoreCase("PublishtoOtherDepartment")) {
//				System.out.println("Symbolic Name - PublishtoOtherDepartment");
//				jobj.put("initialValue", false);
//				JSONArray choiceList = new JSONArray();
//				JSONObject choiceObject = new JSONObject();
//				choiceObject.put("value", "False");
//				choiceObject.put("displayName", "No");
//				choiceList.add(choiceObject);
//				JSONObject _choiceObject = new JSONObject();
//				_choiceObject.put("value", "True");
//				_choiceObject.put("displayName", "Yes");
//				choiceList.add(_choiceObject);
//				JSONObject choices = new JSONObject();
//				choices.put("displayName", "Publish");
//				choices.put("choices", choiceList);
//				jobj.put("choiceList", choices);
//				
//			}
			
//			System.out.println("Jobj - "+jobj.toString());
			
		}
		
		System.out.println(jsonPropertyData.toString());

		return jsonPropertyData;
	}

	private JSONArray locationArray(String _locations) {

		JSONArray array = new JSONArray();

		String[] locations = _locations.substring(1, _locations.length() - 1).trim().split(",");

		for (String string : locations) {
			JSONObject jobj = new JSONObject();
			jobj.put("value", string);
			jobj.put("displayName", string);
			array.add(jobj);
		}

		return array;

	}

	private void sendErrorResponse(HttpServletResponse response, String errorMessage, String userMessage)
			throws IOException {

		response.setStatus(500);
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("errorMessage", errorMessage);
		jsonResponse.put("userMessage", userMessage);
		System.out.println(" " + jsonResponse.serialize());
		PrintWriter writer = response.getWriter();
		writer.write(jsonResponse.serialize());

	}

	public JSONObject getUserDetails(String userId) throws IOException {

		System.out.println("Inside getUserDetails method ");

		InputStream propertyStream = this.getClass().getResourceAsStream("application.properties");

		Properties prop = new Properties();

		prop.load(propertyStream);

		String _url = prop.getProperty("url.location");

		_url = _url + "=" + userId;

		System.out.println("URL - " + _url);

		URL url = new URL(_url);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);

		try {
			con.connect();

			InputStream stream = con.getInputStream();

			JSONObject jobj = JSONObject.parse(stream);

			return jobj;
		} catch (Exception ex) {

			System.out.println("Exception on fetching user details");

		} finally {
			con.disconnect();
		}

		return new JSONObject();

	}

	public static void main(String args[]) {

		String str = "[ BANER ]";

		String[] _str = str.substring(1, str.length() - 1).trim().split(",");

		System.out.println(_str[0]);

	}

}
