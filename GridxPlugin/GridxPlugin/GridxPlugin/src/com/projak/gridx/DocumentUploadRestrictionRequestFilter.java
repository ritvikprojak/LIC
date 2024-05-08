package com.projak.gridx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.filenet.api.core.Factory;
import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/**
 * This sample filter modifies the
 * 
 * search request to demonstrate the capabilities:
 * <ol>
 * <li>To
 * 
 * add a request parameter. The set parameter will be accessed in the
 * 
 * SamplePluginResponseFilter to update some user response.</li>
 * 
 * </ol>
 * To prevent the results changes from always happening,
 * 
 * the logic will only take effect if the desktop's id is "sample".
 * 
 * 
 */

public class DocumentUploadRestrictionRequestFilter extends PluginRequestFilter {

	@Override
	public String[] getFilteredServices() {
		return new String[] { "/p8/addItem", "/p8/checkIn" };
	}

	@Override
	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest)
			throws Exception {

		String desktopId = request.getParameter("desktop");
		String template_name = request.getParameter("template_name");
		System.out.println("Template name" + template_name);
		System.out.println("Desktop Id" + desktopId);
		System.out.println("asdf123344" + request.getParameter("Content-Type"));
		JSONArray jsonarray = ((JSONArray) jsonRequest);


		JSONObject jsonartobj = (JSONObject) jsonarray.get(0);

		 DBUtil dbutil=new DBUtil();
		String loanNumber = null;
		String CIFID = null;
		String documentType = null;
		String natureOfDocument = null;
		boolean docExist = false;

		// if (desktopId != null &&
		// desktopId.equals(rs.getString("desktop_name"))) {
		if (desktopId != null) {
			JSONObject json=null;
			JSONArray jsonpropertiesarray = (JSONArray) jsonartobj.get("criterias");
			for (int i = 0; i < jsonpropertiesarray.size(); i++) {
				JSONObject jsonobjprop = (JSONObject) jsonpropertiesarray.get(i);
				if (jsonobjprop.get("name").toString().equalsIgnoreCase("HF_CIFID")) {
					
					if(jsonobjprop.get("value")!=null&&jsonobjprop.get("value")!="")
					{

						CIFID = jsonobjprop.get("value").toString();
	
					}
					else
					{

						CIFID="";
					}
				}
				if (jsonobjprop.get("name").toString().equalsIgnoreCase("HF_NatureOfDocument")) {
					
					if(jsonobjprop.get("value")!=null&&jsonobjprop.get("value")!="")
					{

					natureOfDocument = jsonobjprop.get("value").toString();
					}
					else
					{

						natureOfDocument="";
					}
				}
				if (jsonobjprop.get("name").toString().equalsIgnoreCase("HF_LOANNUMBER")) {
					
					if(jsonobjprop.get("value")!=null&&jsonobjprop.get("value")!="")
					{
						System.out.println("--1ln");

					loanNumber = jsonobjprop.get("value").toString();
					}
					else
					{
						System.out.println("e--11n");

						loanNumber="";
					}
				}
				if (jsonobjprop.get("name").toString().equalsIgnoreCase("HF_DocumentType")) {
					
					if(jsonobjprop.get("value")!=null&&jsonobjprop.get("value")!="")
					{
						System.out.println("--1dc");

					documentType = jsonobjprop.get("value").toString();
					}
					else
					{

						documentType="";
					}

				}
			}

			try {

				String docProperties = "loanNumber:" + loanNumber + "DocumentType:" + documentType + "NatureOfDocument:"
						+ natureOfDocument + "CIFID:" + CIFID;
				System.out.println("Document properties :" + docProperties);

				 docExist=dbutil.documentValidation(loanNumber, CIFID,
				 documentType, natureOfDocument);
				System.out.println("is Doc Exist" + docExist);

				System.out.println("docExist" + docExist);

				if (docExist) {
					JSONMessage errorMessage = new JSONMessage(10001, "Invalid, File already Exist  in the system",
							"Given  Details\n"+ docProperties, "Please selct diffrent values .", "", "");

					JSONArray jsonMessages = new JSONArray();

					jsonMessages.add(errorMessage);

					if (jsonRequest != null) {
						json = (JSONObject) ((JSONArray) jsonRequest).get(0);

						json.put("errors", jsonMessages);
						return json;
					}
				} else {
					System.out.println("file not Exist");

				}

				// }

			} catch (Exception e) {

				System.out.println("Exception in DocumentUploadRestrictionRequestFilter" + e.fillInStackTrace());

			}

		} // aks

		return null;
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
